package ca.cihi.cims.transformation;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.dal.jdbc.ContextFinder;
import ca.cihi.cims.service.BaseTransformationService;
import ca.cihi.cims.service.TransformationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class XslTransformerIntegrationTest {
	final static String xml_hearder = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	@Autowired
	private TransformationService transformationService;

	@Autowired
	private BaseTransformationService baseTransformService;

	@Test
	public void testGenerateRubricXml() throws Exception {
		ContextAccess context = provider.findContext(ContextDefinition.forVersion("CCI", "2015"));
		// List<TransformationError> errors = new ArrayList<TransformationError>();
		Ref<CciTabular> cciTab = ref(CciTabular.class);

		// List<FindCriterion> criteria = new ArrayList<FindCriterion>();

		// criteria.add(cciTab.eq("typeCode", "Rubric"));
		// criteria.add(cciTab.eq("code", "1.AA.35.^^"));

		Iterator<CciTabular> results = context.find(cciTab, cciTab.eq("code", "1.HN.87.^^"));
		// 1.HV.80.^^, 1.HN.87.^^
		while (results.hasNext()) {
			CciTabular cci = results.next();

			XmlGenerator xmlGenerator = new CciXmlGenerator();

			// String generatedxml= xmlGenerator.generateXml("CCI", "2015", cci, errors,
			// transformationService.getDtdFile(), "FRA", isContainedInTable, context);

			Long runId = baseTransformService.getRunId();
			final Collection<String> languageList = new ArrayList<String>();
			languageList.add("FRA");
			transformationService
					.transformConcept("CCI", "2015", cci, runId, languageList, xmlGenerator, context, true);

			// System.err.println(generatedxml);
		}
	}

}
