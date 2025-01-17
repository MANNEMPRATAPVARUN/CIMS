package ca.cihi.cims.service;

import static ca.cihi.cims.util.CollectionUtils.asSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.ContentViewerModel;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.filter.ThreadLocalCurrentContext;

/**
 * Test to see differences in XML generated during UI "save" and migration
 * 
 * http://jira.cihi.ca/browse/CSRE-822
 * 
 * @author adenysenko
 * 
 */
@Ignore
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class MigratedIndexXmlIntegrationTest {

	@Autowired
	private ViewService viewService;
	@Autowired
	private LookupService lookupService;
	@Autowired
	private ContextProvider contextProvider;
	@Autowired
	private ThreadLocalCurrentContext context;
	@Autowired
	private ClassificationService classificationService;
	@Autowired
	private ChangeRequestService changeRequestService;

	// ---------------------------------------------------------

	private User getChangeRequestUser(long changeRequestId) {
		ChangeRequestDTO dto = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		User user = dto.getUserAssignee();
		user.setUserId(dto.getAssigneeUserId());
		user.setRoles(asSet(SecurityRole.ROLE_ADMINISTRATOR));
		return user;
	}

	private void processChangeRequest(long changeRequestId, PrintStream out) throws Exception {
		out.println("Change request: " + changeRequestId);
		out.println("=============================================");
		ContextIdentifier contextId = setContextChangeRequest(changeRequestId);
		User user = getChangeRequestUser(changeRequestId);

		Set<Language> languages = classificationService.getChangeRequestLanguages();
		assertEquals(1, languages.size());

		processChildren(null, null, contextId, user, languages.iterator().next(), out);

	}

	private void processChildren(String conceptId, String conceptType, ContextIdentifier contextId, User user,
			Language lang, PrintStream out) throws Exception {
		boolean isIndex = StringUtils.contains(conceptType, "Index");
		if (isIndex) {
			IndexModel model = classificationService.getIndexById(Long.parseLong(conceptId), lang);
			if (classificationService.isIndexEditableShallow(model)) {
				Index entity = model.getEntity();
				String originalXml = entity.getIndexRefDefinition(lang.getCode());
				try {
					classificationService.saveIndex(new OptimisticLock(Long.MIN_VALUE), new ErrorBuilder(
							new BeanPropertyBindingResult(model, "model")), user, model, lang);
					String changedXml = entity.getIndexRefDefinition(lang.getCode());
					if (!StringUtils.equals(originalXml, changedXml)) {
						out.println("\t" + "Concept: " + conceptId + ": " + lang + ": " + model.getDescription());
						out.println("\t Original: " + originalXml);
						out.println("\t Saved   : " + changedXml);
						out.println("\t" + "=============================================");
					}
				} catch (Exception ex) {
					out.println("\t" + "Concept: " + conceptId + ": " + lang + ": " + model.getDescription());
					ex.printStackTrace(out);
				}
			}
		}
		if (isIndex || conceptType == null) {
			List<ContentViewerModel> children = viewService.getTreeNodes(conceptId, contextId.getBaseClassification(),
					contextId.getContextId(), lang.getCode(), null);
			for (ContentViewerModel child : children) {
				processChildren(child.getConceptId(), child.getConceptType(), contextId, user, lang, out);
			}
		}
	}

	private ContextIdentifier setContextChangeRequest(long changeRequestId) {
		ContextIdentifier contextId = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		contextId.setRequestId(changeRequestId);
		ContextAccess access = contextProvider.findContext(contextId);
		ContextAccess spy = Mockito.spy(access);
		Mockito.doNothing().when(spy).persist();
		context.makeCurrentContext(spy);
		return contextId;
	}

	@Test
	public void test_Suppliment_Xml() {
		long changeRequestId = 691L;
		setContextChangeRequest(changeRequestId);
		User user = getChangeRequestUser(changeRequestId);
		Language lang = classificationService.getChangeRequestLanguages().iterator().next();

		SupplementModel model = classificationService.getSupplementById(371156, lang);
		model.setDescription(model.getDescription() + "1");
		String xmlOld = model.getEntity().getSupplementDefinition(lang.getCode());
		System.err.println(xmlOld);

		ErrorBuilder result = new ErrorBuilder(new BeanPropertyBindingResult(model, "model"));
		classificationService.saveSupplement(new OptimisticLock(Long.MIN_VALUE), result, user, model, lang);
		assertFalse(result.hasErrors());

		String xmlNew = model.getEntity().getSupplementDefinition(lang.getCode());
		System.out.println(xmlNew);

		assertEquals(xmlOld, xmlNew);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDifferences() throws Exception {
		LoggerContext logContext = (LoggerContext) LogManager.getContext(false);
		Collection<LoggerConfig> loggerConfigs = logContext.getConfiguration().getLoggers().values();
		for (LoggerConfig logConfig: loggerConfigs) {
			logConfig.setLevel(Level.ALL);
		}

		Set<Long> changeRequests = asSet( //
		// 157L /* CCI english */
		// , 169L /* CCI french */
		// 269L /* ICD English - check AGAIN */
		270L /* ICD French */
		// 273L /* CCI french after SPEC chars fix */
		);
		PrintStream out = new PrintStream(new File("C://Eclipse projects//indexes.txt"));
		for (Long changeRequestId : changeRequests) {
			processChangeRequest(changeRequestId, out);
		}
	}

	@Test
	public void testXml() throws Exception {
		long changeRequestId = 273L;
		setContextChangeRequest(changeRequestId);
		User user = getChangeRequestUser(changeRequestId);
		Language lang = classificationService.getChangeRequestLanguages().iterator().next();

		IndexModel model = classificationService.getIndexById(1610947, lang);
		Index entity = model.getEntity();
		String originalXml = entity.getIndexRefDefinition(lang.getCode());
		classificationService.saveIndex(new OptimisticLock(Long.MIN_VALUE), new ErrorBuilder(
				new BeanPropertyBindingResult(model, "model")), user, model, lang);
		String changedXml = entity.getIndexRefDefinition(lang.getCode());
		assertEquals(originalXml, changedXml);
	}

}
