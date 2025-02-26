package ca.cihi.cims.content.icd;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.ContextUtils;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class MultiYearRealizationTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	@Autowired
	ClassService classService;

	@Autowired
	NonContextOperations nco;

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Test
	public void testCreateConceptMultiYear() {

		String testCode = "A02";
		String newCode = "A02.98";
		String languageCode = "ENG";
		String baseClassification = "ICD-10-CA";
		String text = "I am A02.4 and I am new";

		ContextIdentifier contextId = finder.findIfAvail(baseClassification, CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", testCode));

		IcdTabular parent = iterator.next();

		IcdTabular child = IcdTabular.create(context, newCode, IcdTabular.CATEGORY);
		child.setParent(parent);
		child.setUserDescription(languageCode, text);

		context.persist();
		context.realizeChangeContext(false);

		// Lets test that this change propagated to the newer years
		Collection<ContextIdentifier> openBaseContexts = provider.findOpenBaseContextIdentifiers(baseClassification);
		Collection<ContextIdentifier> newerContexts = ContextUtils.returnNewerContexts(contextId, openBaseContexts);

		for (ContextIdentifier c : newerContexts) {

			LOGGER.info("Checking " + c.getVersionCode());

			context = provider.findContext(c);
			iterator = context.find(icdTab, icdTab.eq("code", newCode));
			child = iterator.next();

			assertTrue(child.getUserDescription(languageCode).equalsIgnoreCase(text));

		}
	}

	@Test
	@Ignore
	public void testRemoveMultiYear() {

		// Ignored. Can't find suitable concept to remove, unless you create a dummy one first.

		String testCode = "A02.3";

		ContextIdentifier contextId = finder.findIfAvail("ICD-10-CA", CIMSTestConstants.TEST_VERSION);
		ContextAccess context = provider.createChangeContext(contextId, null);

		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		Iterator<IcdTabular> iterator = context.find(icdTab, icdTab.eq("code", testCode));

		long elementId = iterator.next().getElementId();

		nco.remove(context.getContextId(), elementId);

		context.realizeChangeContext(false);

	}

}
