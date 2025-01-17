package ca.cihi.cims.content.cci;

import static ca.cihi.cims.bll.query.FindCriteria.*;

import java.util.Iterator;
import java.util.SortedSet;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class CciTabularTest {

	@Autowired
	private ContextProvider provider;

	@Autowired
	JdbcTemplate template;

	@Autowired
	ContextFinder finder;

	private final Logger LOGGER = LogManager.getLogger(CciTabularTest.class);

	private ContextAccess buildContext() {
		ContextIdentifier contextId = finder.findIfAvail("CCI", CIMSTestConstants.TEST_VERSION);
		if (contextId == null) {
			LOGGER.warn("Test skipping due to lack of test data.");
			return null;
		}

		return provider.findContext(contextId);
	}

	@Test
	public void testAttributes() {
		ContextAccess context = buildContext();

		Iterator<CciAttribute> attribs = context.find(ref(CciAttribute.class));

		CciAttribute attrib = attribs.next();
		CciReferenceAttribute refAttrib = attrib.getReferenceAttribute();

		if (refAttrib == null) {
			return;
		}

		CciAttributeType attribType = refAttrib.getType();
		if (attribType == null) {
			return;
		}
	}

	@Test
	public void testFindChildrenWithValidation() {

		ContextAccess context = buildContext();
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> rubrics = context.find(cciTab, cciTab.eq("typeCode", CciTabular.RUBRIC));

		if (!rubrics.hasNext()) {
			return;
		}
		CciTabular rubric = rubrics.next();

		SortedSet<CciTabular> children = rubric.getChildrenWithValidations();

		for (CciTabular child : children) {
			Assert.assertNotNull(child.getValidations().isEmpty());
		}
	}

	@Test
	@Ignore
	// Not a test
	public void testFindSections() {

		ContextAccess context = buildContext();
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> cci = context.find(cciTab, cciTab.eq("typeCode", CciTabular.SECTION));

		while (cci.hasNext()) {
			CciTabular section = cci.next();
			LOGGER.debug(section.getCode());
		}

	}

	@Test
	public void testInvasivenessLevelConcepts() {

		ContextAccess context = buildContext();
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Iterator<CciTabular> concepts = context.find(cciTab, cciTab.eq("code", "1.PG.87.LA-XX-E"));

		CciTabular concept = concepts.next();

		LOGGER.debug(concept.getCode() + " " + concept.getInvasivenessLevel().getCode());
	}

	@Test
	@Ignore
	// Dont run me
	public void testInvasivenessLevelConcepts1() {

		ContextAccess context = buildContext();
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		Ref<CciInvasivenessLevel> cciI = ref(CciInvasivenessLevel.class);

		Iterator<CciTabular> concepts = context.find(cciTab, cciTab.link("invasivenessLevel", cciI));

		while (concepts.hasNext()) {
			CciTabular concept = concepts.next();
			LOGGER.debug(concept.getCode() + " " + concept.getInvasivenessLevel().getCode());
		}
	}

}
