package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.jdbc.ContextFinder;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.IdCodeDescription;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class ConceptServiceTest {

	@Autowired
	private ConceptService conceptService;

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private ContextFinder finder;

	private ContextIdentifier contextId;

	private ContextAccess context;

	@Before
	public void setUp() {
		String baseClassification = "CCI";
		String version = CIMSTestConstants.TEST_VERSION;

		contextId = finder.findIfAvail(baseClassification, version);

	}

	@Test
	public void testGetAsteriskList() {
		context = contextProvider.findContext(ContextDefinition.forVersion(
				"ICD-10-CA", CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> meRef = ref(IcdTabular.class);
		Long elementId = context.findOne(meRef, meRef.eq("code", "03"))
				.getElementId();

		assertNotNull(elementId);
		List<AsteriskBlockInfo> asteriskList = conceptService.getAsteriskList(
				elementId, context.getContextId().getContextId());
		assertNotNull(asteriskList);
		assertTrue(asteriskList.size() > 0);
	}

	@Test
	public void testGetBlockList() {

		// Test getBlockList for ICD chapters
		context = contextProvider.findContext(ContextDefinition.forVersion(
				CIMSConstants.ICD_10_CA, CIMSTestConstants.TEST_VERSION));

		Ref<IcdTabular> meRef = ref(IcdTabular.class);
		Long elementId = context.findOne(meRef, meRef.eq("code", "03"))
				.getElementId();
		assertNotNull(elementId);
		List<AsteriskBlockInfo> blockList = conceptService.getBlockList(
				ca.cihi.cims.CIMSConstants.ICD_10_CA, elementId, context
						.getContextId().getContextId());
		assertNotNull(blockList);
		assertTrue(blockList.size() > 0);

		// Test getBlockList for CCI
		context = contextProvider.findContext(ContextDefinition.forVersion(
				CIMSConstants.CCI, CIMSTestConstants.TEST_VERSION));
		Ref<CciTabular> aRef = ref(CciTabular.class);
		elementId = context.findOne(aRef, aRef.eq("code", "1")).getElementId();
		assertNotNull(elementId);
		blockList = conceptService.getBlockList(ca.cihi.cims.CIMSConstants.CCI,
				elementId, context.getContextId().getContextId());
		assertNotNull(blockList);
		assertTrue(blockList.size() > 0);
	}

	@Test
	public void testGetCCIClassID() {
		Long classId = conceptService
				.getCCIClassID("ConceptVersion", "Section");
		assertNotNull(classId);
		assertTrue(classId > 0);
	}

	@Test
	public void testGetICDClassID() {
		Long classId = conceptService.getICDClassID("ConceptVersion", "Block");
		assertNotNull(classId);
		assertTrue(classId > 0);
	}

	@Test
	public void testGetRefAttributePerType() {
		List<IdCodeDescription> list = conceptService.getRefAttributePerType(
				contextId.getContextId(), Language.ENGLISH, null);
		for (IdCodeDescription item : list) {
		}
		assertTrue(list.size() > 0);
	}

	@Test
	public void testIsValidCode() {

		context = contextProvider.findContext(ContextDefinition.forVersion(
				"ICD-10-CA", CIMSTestConstants.TEST_VERSION));

		// Chapter concept is not a valid Code
		Ref<IcdTabular> meRef = ref(IcdTabular.class);
		Long elementId = context.findOne(meRef, meRef.eq("code", "05"))
				.getElementId();
		assertNotNull(elementId);
		assertFalse(conceptService.isValidCode(elementId, context
				.getContextId().getContextId()));

		// Block concept is not a valid code
		Long elementId2 = context.findOne(meRef, meRef.eq("code", "C00-C14"))
				.getElementId();
		assertNotNull(elementId2);
		assertFalse(conceptService.isValidCode(elementId2, context
				.getContextId().getContextId()));

		// Category concept with children is not a valid code
		Long elementId3 = context.findOne(meRef, meRef.eq("code", "C00"))
				.getElementId();
		assertNotNull(elementId3);
		assertFalse(conceptService.isValidCode(elementId3, context
				.getContextId().getContextId()));

		// Category concept without children is a valid code
		Long elementId4 = context.findOne(meRef, meRef.eq("code", "C00.0"))
				.getElementId();
		assertNotNull(elementId4);
		assertTrue(conceptService.isValidCode(elementId4, context
				.getContextId().getContextId()));
	}

}
