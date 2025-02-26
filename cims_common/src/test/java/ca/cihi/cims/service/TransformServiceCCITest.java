package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.transformation.CciXmlGenerator;
import ca.cihi.cims.transformation.XmlGenerator;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformServiceCCITest {

	private static final Log LOGGER = LogFactory.getLog(TransformServiceCCITest.class);

	private static final String CLASSIFICATION = "CCI";
	private static final String CODE = "code";
	private static final String LANGUAGE = "ENG";

	@Autowired
	private TransformationService transformationService;
	@Autowired
	private BaseTransformationService baseTransformService;

	private ContextAccess ctxtx;
	private Long runId;

	private Iterator<CciTabular> findCciTabular(String code) {
		Ref<CciTabular> cciTab = ref(CciTabular.class);
		return ctxtx.find(cciTab, cciTab.eq(CODE, code));
	}

	@Before
	public void setUp() {
		ctxtx = transformationService.getContextProvider().findContext(
				ContextDefinition.forVersion(CLASSIFICATION, CIMSTestConstants.TEST_VERSION));
		runId = Long.valueOf(-1);
	}

	@Test
	public void testBlockData() {
		LOGGER.debug("TransformServiceCCITest.testBlockData()...");

		String blockCode1AA = "1AA-1ZZ";
		Iterator<CciTabular> iteratorCode1 = findCciTabular(blockCode1AA);
		Assert.assertTrue(iteratorCode1.hasNext());
		CciTabular concept = iteratorCode1.next();
		transformationService.transformConcept(concept, ctxtx, true);

		// at least have the recorcds of "START TRANSFORMATION" and "END TRANSFORMATION"
		Assert.assertNotNull(concept.getPresentationHtml(LANGUAGE));
		Assert.assertNull(concept.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testCode() {
		LOGGER.debug("TransformServiceCCITest.testCode()...");

		String blockCode1AA = "1.AA.35.HA-C1";
		Iterator<CciTabular> iteratorCode1 = findCciTabular(blockCode1AA);
		Assert.assertTrue(iteratorCode1.hasNext());
		CciTabular code = iteratorCode1.next();
		transformationService.transformConcept(code, ctxtx, true);
		// at least have the recorcds of "START TRANSFORMATION" and "END TRANSFORMATION"
		// Assert.assertTrue(transformationService.getAllErrors(runId).size() > 1);
		Assert.assertNotNull(code.getShortPresentationHtml("FRA"));
		Assert.assertNull(code.getPresentationHtml("FRA"));
	}

	@Test
	public void testGroupData() {
		LOGGER.debug("TransformServiceCCITest.testBlockData()...");

		String blockCode1AA = "2.BA.^^.^^";
		Iterator<CciTabular> iteratorCode1 = findCciTabular(blockCode1AA);
		Assert.assertTrue(iteratorCode1.hasNext());
		CciTabular concept = iteratorCode1.next();
		transformationService.transformConcept(concept, ctxtx, true);

		// at least have the recorcds of "START TRANSFORMATION" and "END TRANSFORMATION"
		Assert.assertNotNull(concept.getPresentationHtml(LANGUAGE));
		Assert.assertNull(concept.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testRubWithMExtent() {
		LOGGER.debug("TransformServiceCCITest.testRubWithMExtent()...");

		String rubCode1AA = "1.FE.53.^^";// "6.LA.50.^^";
		Iterator<CciTabular> iteratorCode1 = findCciTabular(rubCode1AA);
		Assert.assertTrue(iteratorCode1.hasNext());
		transformationService.transformTabularData(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, iteratorCode1,
				runId, ctxtx);
		// at least have the recorcds of "START TRANSFORMATION" and "END TRANSFORMATION"
		Assert.assertTrue(baseTransformService.getAllErrors(runId).size() > 1);
	}

	@Test
	public void testRubWithSL() {
		LOGGER.debug("TransformServiceCCITest.testRubWithSL()...");

		String rubCode1AA = "1.AN.26.^^";
		Iterator<CciTabular> iteratorCode1 = findCciTabular(rubCode1AA);
		Assert.assertTrue(iteratorCode1.hasNext());
		transformationService.transformTabularData(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, iteratorCode1,
				runId, ctxtx);
		// at least have the recorcds of "START TRANSFORMATION" and "END TRANSFORMATION"
		Assert.assertTrue(baseTransformService.getAllErrors(runId).size() > 1);
	}

	@Test
	public void testSectionData() {
		LOGGER.debug("TransformServiceCCITest.testSectionData()...");

		String sectionCode1 = "1";
		Iterator<CciTabular> iteratorCode1 = findCciTabular(sectionCode1);
		Assert.assertTrue(iteratorCode1.hasNext());
		CciTabular concept = iteratorCode1.next();
		transformationService.transformConcept(concept, ctxtx, true);

		// at least have the recorcds of "START TRANSFORMATION" and "END TRANSFORMATION"
		Assert.assertNotNull(concept.getPresentationHtml(LANGUAGE));
		Assert.assertNull(concept.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testTransformCodeList() {
		String rubricCode = "1.AA.52.^^";
		Iterator<CciTabular> iteratorRubric = findCciTabular(rubricCode);
		Assert.assertTrue(iteratorRubric.hasNext());
		CciTabular rubric = iteratorRubric.next();

		List<String> languages = new ArrayList<String>();
		languages.add(LANGUAGE);
		XmlGenerator xmlGenerator = new CciXmlGenerator();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, rubric, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlRub = rubric.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlRub);
		System.out.println("presentatyionhtmlrub:" + presentationHtmlRub);
		Assert.assertFalse(presentationHtmlRub.indexOf("<a  name=\"1.AA.52.SZ\">1.AA.52.SZ</a>") == -1);
		Assert.assertNull(rubric.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testTransformRubric() {
		String rubricCode = "1.AA.80.^^";
		Iterator<CciTabular> iteratorRubric = findCciTabular(rubricCode);
		Assert.assertTrue(iteratorRubric.hasNext());
		CciTabular rubric = iteratorRubric.next();

		List<String> languages = new ArrayList<String>();
		languages.add(LANGUAGE);
		XmlGenerator xmlGenerator = new CciXmlGenerator();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, rubric, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlRub = rubric.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlRub);
		Assert.assertNull(rubric.getShortPresentationHtml(LANGUAGE));
	}
}