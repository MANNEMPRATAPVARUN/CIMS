package ca.cihi.cims.service;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
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
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.transformation.IcdXmlGenerator;
import ca.cihi.cims.transformation.XmlGenerator;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class TransformServiceTest {

	private static final Log LOGGER = LogFactory.getLog(TransformServiceTest.class);

	private static final String CLASSIFICATION = "ICD-10-CA";
	private static final String LANGUAGE = "ENG";
	private static final String CODE = "code";
	private static final String SPACE_PATTERN = "\\s+";

	@Autowired
	private TransformationService transformationService;

	private ContextAccess ctxtx;
	private XmlGenerator xmlGenerator;
	private Long runId;
	private List<String> languages;

	private Iterator<IcdTabular> findByCode(String code) {
		Ref<IcdTabular> icdTab = ref(IcdTabular.class);
		return ctxtx.find(icdTab, icdTab.eq(CODE, code));
	}

	@Before
	public void setUp() {
		ctxtx = transformationService.getContextProvider().findContext(
				ContextDefinition.forVersion(CLASSIFICATION, CIMSTestConstants.TEST_VERSION));
		xmlGenerator = new IcdXmlGenerator();
		runId = Long.valueOf(-1);
		languages = new ArrayList<String>();
		languages.add(LANGUAGE);
	}

	@Test
	public void testCategoryPresentation() {

		Iterator<IcdTabular> iterator800 = findByCode("800");
		IcdTabular c800IcdTabular = iterator800.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, c800IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtml800 = c800IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtml800.indexOf("class=\"cat1\"") != -1);

		Iterator<IcdTabular> iteratorC07 = findByCode("D65");
		IcdTabular c07IcdTabular = iteratorC07.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, c07IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlC07 = c07IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtmlC07.indexOf("class=\"cat1\"") != -1);

		Iterator<IcdTabular> iteratorA15 = findByCode("A15");
		IcdTabular a15IcdTabular = iteratorA15.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, a15IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlA15 = a15IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtmlA15.indexOf("class=\"cat1\"") != -1);

		Iterator<IcdTabular> iteratorA152 = findByCode("A15.2");
		IcdTabular a152IcdTabular = iteratorA152.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, a152IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlA152 = a152IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtmlA152.indexOf("class=\"cat2\"") != -1);

		Iterator<IcdTabular> iteratorA1520 = findByCode("A15.20");
		IcdTabular a1520IcdTabular = iteratorA1520.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, a1520IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlA1520 = a1520IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtmlA1520.indexOf("class=\"code\"") != -1);

		Iterator<IcdTabular> iteratorA154 = findByCode("A15.4");
		IcdTabular a154IcdTabular = iteratorA154.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, a154IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlA154 = a1520IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtmlA154.indexOf("class=\"code\"") != -1);

		Iterator<IcdTabular> iterator80001 = findByCode("8000/1");
		IcdTabular c80001IcdTabular = iterator80001.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, c80001IcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtml80001 = c80001IcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertTrue(presentationHtml80001.indexOf("class=\"code\"") != -1);

	}

	@Test
	public void testFindVersionCodes() {

		final Collection<String> versions = transformationService.getContextProvider().findVersionCodes("CCI");

		Assert.assertNotNull(versions);
	}

	@Test
	public void testTabularData() {
		String chapterCode16 = "16";
		Iterator<IcdTabular> iteratorC16 = findByCode(chapterCode16);
		Assert.assertTrue(iteratorC16.hasNext());
		IcdTabular concept = iteratorC16.next();
		transformationService.transformConcept(concept, ctxtx, true);

		Assert.assertNotNull(concept.getPresentationHtml(LANGUAGE));
		Assert.assertNull(concept.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testTransformBlock() {
		// Test Block1 (also)
		String block1Code = "D10-D36";
		Iterator<IcdTabular> iteratorB1 = findByCode(block1Code);
		Assert.assertTrue(iteratorB1.hasNext());
		IcdTabular block1 = iteratorB1.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, block1, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlB1 = block1.getPresentationHtml(LANGUAGE);
		String partExpectedHtmlB1 = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"bl1\" colspan=\"3\"><a name=\"D10-D36\">Benign neoplasms";
		Assert.assertNotNull(presentationHtmlB1);
		Assert.assertTrue(presentationHtmlB1.replaceAll(SPACE_PATTERN, "").indexOf(
				partExpectedHtmlB1.replaceAll(SPACE_PATTERN, "")) != -1);
		Assert.assertNull(block1.getShortPresentationHtml(LANGUAGE));

		// Test Block2
		String block2Code = "C76-C80";
		Iterator<IcdTabular> iteratorB2 = findByCode(block2Code);
		Assert.assertTrue(iteratorB2.hasNext());
		IcdTabular block2 = iteratorB2.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, block2, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlB2 = block2.getPresentationHtml(LANGUAGE);
		String partExpectedHtmlB2 = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"bl2\" colspan=\"3\"><a name=\"C76-C80\">Malignant neoplasms of ill-defined, secondary and unspecified sites";
		Assert.assertNotNull(presentationHtmlB2);
		Assert.assertTrue(presentationHtmlB2.replaceAll(SPACE_PATTERN, "").indexOf(
				partExpectedHtmlB2.replaceAll(SPACE_PATTERN, "")) != -1);
		Assert.assertNull(block2.getShortPresentationHtml(LANGUAGE));

		// Test Block3
		String block3Code = "C00-C14";
		Iterator<IcdTabular> iteratorB3 = findByCode(block3Code);
		Assert.assertTrue(iteratorB3.hasNext());
		IcdTabular block3 = iteratorB3.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, block3, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlB3 = block3.getPresentationHtml(LANGUAGE);
		String partExpectedHtmlB3 = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"bl3\" colspan=\"3\"><a name=\"C00-C14\">Malignant neoplasms of lip, oral cavity and pharynx";
		Assert.assertNotNull(presentationHtmlB3);
		Assert.assertTrue(presentationHtmlB3.replaceAll(SPACE_PATTERN, "").indexOf(
				partExpectedHtmlB3.replaceAll(SPACE_PATTERN, "")) != -1);
		Assert.assertNull(block3.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testTransformCategory() {
		// Test CAT1 (includes)
		String cat1Code = "A02";
		Iterator<IcdTabular> iteratorCat1 = findByCode(cat1Code);
		Assert.assertTrue(iteratorCat1.hasNext());
		IcdTabular cat1 = iteratorCat1.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cat1, runId, languages,
				xmlGenerator, ctxtx, true);
		String presentationHtmlCat1 = cat1.getPresentationHtml(LANGUAGE);
		String partExpectedHtmlCat1 = "<td class=\"cat1\" colspan=\"2\">Other salmonella infections";
		Assert.assertTrue(presentationHtmlCat1.replaceAll(SPACE_PATTERN, "").indexOf(
				partExpectedHtmlCat1.replaceAll(SPACE_PATTERN, "")) != -1);
		Assert.assertNull(cat1.getShortPresentationHtml(LANGUAGE));

		// Test CAT2
		String cat2Code = "A15.2";
		Iterator<IcdTabular> iteratorCat2 = findByCode(cat2Code);
		Assert.assertTrue(iteratorCat2.hasNext());
		IcdTabular cat2 = iteratorCat2.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cat2, runId, languages,
				xmlGenerator, ctxtx, true);
		String presentationHtmlCat2 = cat2.getPresentationHtml(LANGUAGE);
		String expectedHtmlCat2 = "<tr><td colspan=\"4\" height=\"15px\"/></tr><tr><td class=\"cat2\"><a name=\"A15.2\">A15.2</a> <img align=\"texttop\" src=\"img/icd/cleaf.gif\" height=\"15\" width=\"15\"/></td><td class=\"cat2\" colspan=\"2\">Tuberculosis of lung, confirmed histologically</td><td/></tr><tr><td colspan=\"4\" height=\"3px\"/></tr><tr valign=\"top\"><td class=\"include\"/><td class=\"includelabel\">Includes:</td><td class=\"include\">Conditions listed in <a  href=\"javascript:navigateFromDynaTree('/39/46/1357/1371/1381');\">A15.0</a>, confirmed histologically<br/></td><td/></tr>";
		Assert.assertNotNull(cat2.getShortPresentationHtml(LANGUAGE));

		// Test code - CAT2 (includes, excludes, url, brace)
		String cCode = "A15.4";
		Iterator<IcdTabular> iteratorCode = findByCode(cCode);
		Assert.assertTrue(iteratorCode.hasNext());
		IcdTabular code = iteratorCode.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, code, runId, languages,
				xmlGenerator, ctxtx, true);
		String presentationHtmlCode = code.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlCode);
		Assert.assertFalse(presentationHtmlCode.indexOf("Includes") == -1);
		Assert.assertFalse(presentationHtmlCode.indexOf("Excludes") == -1);
		Assert.assertFalse(presentationHtmlCode.indexOf("bracket_03.gif") == -1);
		Assert.assertNotNull(code.getShortPresentationHtml(LANGUAGE));
	}

	@Test
	public void testTransformChapter() {
		XmlGenerator xmlGenerator = new IcdXmlGenerator();
		Long runId = Long.valueOf(-1);
		List<String> languages = new ArrayList<String>();
		languages.add(LANGUAGE);

		// Test Chapter
		String chapterCode22 = "05";
		Iterator<IcdTabular> iteratorC22 = findByCode(chapterCode22);
		Assert.assertTrue(iteratorC22.hasNext());
		IcdTabular chapter22 = iteratorC22.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, chapter22, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlC22 = chapter22.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlC22);

		/*
		 * // Test Chapter String chapterCode = "13"; Iterator<IcdTabular> iteratorC = findByCode(chapterCode);
		 * Assert.assertTrue(iteratorC.hasNext()); IcdTabular chapter = iteratorC.next();
		 * transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, chapter, runId,
		 * languages, xmlGenerator, ctxtx); String presentationHtmlC = chapter.getPresentationHtml(LANGUAGE);
		 * Assert.assertNotNull(presentationHtmlC); SortedSet<IcdTabular> children = chapter.getSortedBlocks();
		 * Assert.assertEquals(children.size(), 19);
		 * 
		 * // Test Chapter String chapterCode05 = "05"; Iterator<IcdTabular> iterator05 = findByCode(chapterCode05);
		 * Assert.assertTrue(iterator05.hasNext()); IcdTabular chapter05 = iterator05.next();
		 * transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, chapter05, runId,
		 * languages, xmlGenerator, ctxtx); String presentationHtmlC05 = chapter05.getPresentationHtml(LANGUAGE);
		 * Assert.assertNotNull(presentationHtmlC05); Assert.assertFalse(presentationHtmlC05
		 * .equalsIgnoreCase("ERROR: please check the data and fix it")); children = chapter05.getSortedBlocks();
		 * Assert.assertEquals(children.size(), 11);
		 */

	}

	@Test
	public void testTransformChpfront() {

		XmlGenerator xmlGenerator = new IcdXmlGenerator();
		Long runId = Long.valueOf(-1);
		List<String> languages = new ArrayList<String>();
		languages.add(LANGUAGE);

		// Test Chapter (includes, excludes, also, url)
		String chapterCode = "02";
		Iterator<IcdTabular> iteratorC = findByCode(chapterCode);
		Assert.assertTrue(iteratorC.hasNext());
		IcdTabular chapter = iteratorC.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, chapter, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlCF = chapter.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlCF);
		Assert.assertFalse(presentationHtmlCF.equalsIgnoreCase("ERROR: please check the data and fix it"));

	}

	@Test
	public void testTransformNoteTable() {
		String cat1Code = "M99";
		Iterator<IcdTabular> iteratorCat1 = findByCode(cat1Code);
		Assert.assertTrue(iteratorCat1.hasNext());
		IcdTabular cat1 = iteratorCat1.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cat1, runId, languages,
				xmlGenerator, ctxtx, true);
		String presentationHtmlCat1 = cat1.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlCat1);
	}

	@Test
	public void testTransformOthers() {

		// Test url in excludes label and ulist label
		String codeA09 = "A09";
		Iterator<IcdTabular> iteratorA09 = findByCode(codeA09);
		Assert.assertTrue(iteratorA09.hasNext());
		IcdTabular A09 = iteratorA09.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, A09, runId, languages,
				xmlGenerator, ctxtx, true);
		String presentationHtmlA09 = A09.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlA09);
		Assert.assertFalse(presentationHtmlA09.indexOf(">K52.9</a>") == -1);

		// Test url in userDesc
		String codeG553 = "G55.3";
		Iterator<IcdTabular> iteratorG553 = findByCode(codeG553);
		Assert.assertTrue(iteratorG553.hasNext());
		IcdTabular icdTabularG553 = iteratorG553.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabularG553, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlG553 = icdTabularG553.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlG553);
		Assert.assertTrue(presentationHtmlG553.indexOf("M48\">M48.-&#134;</a>") == -1);

		// Test conceptDetails for pupups
		String codeO103 = "O10.3";
		Iterator<IcdTabular> iteratorO103 = findByCode(codeO103);
		Assert.assertTrue(iteratorO103.hasNext());
		IcdTabular tableOutputO103 = iteratorO103.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, tableOutputO103, runId,
				languages, xmlGenerator, ctxtx, true);
		String shortPresentationHtml = tableOutputO103.getShortPresentationHtml(LANGUAGE);
		Assert.assertNotNull(shortPresentationHtml);
		Assert.assertFalse(shortPresentationHtml.indexOf("Includes") == -1);

		// Test anchor
		String anCode = "I42.9";
		Iterator<IcdTabular> iteratorAn = findByCode(anCode);
		Assert.assertTrue(iteratorAn.hasNext());
		IcdTabular anIcdTabular = iteratorAn.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, anIcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlAn = anIcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlAn);

		// Test url in user desc
		String usCode = "A17.0";
		Iterator<IcdTabular> iteratorUS = findByCode(usCode);
		Assert.assertTrue(iteratorUS.hasNext());
		IcdTabular usIcdTabular = iteratorUS.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, usIcdTabular, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlUS = usIcdTabular.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlUS);
		Assert.assertTrue(presentationHtmlUS.indexOf("#G01\">G01*</a>)") == -1);

		// Test url in user desc
		String usCode2 = "9044/3";
		Iterator<IcdTabular> iteratorUS2 = findByCode(usCode2);
		Assert.assertTrue(iteratorUS2.hasNext());
		IcdTabular usIcdTabular2 = iteratorUS2.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, usIcdTabular2, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlUS2 = usIcdTabular2.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlUS2);
		Assert.assertTrue(presentationHtmlUS2.indexOf("#8964/3\">8964/3</a>)") == -1);

		// Test Dagger (url)
		String daggerCode = "A17.9";
		Iterator<IcdTabular> iteratorDagger = findByCode(daggerCode);
		Assert.assertTrue(iteratorDagger.hasNext());
		IcdTabular dagger = iteratorDagger.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, dagger, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlDagger = dagger.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlDagger);
		Assert.assertFalse(presentationHtmlDagger.indexOf(">G99.8*</a>") == -1);

		// Test url in userDesc
		String daggerCode2 = "M03.0";
		Iterator<IcdTabular> iteratorDagger2 = findByCode(daggerCode2);
		Assert.assertTrue(iteratorDagger2.hasNext());
		IcdTabular dagger2 = iteratorDagger2.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, dagger2, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlDagger2 = dagger2.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlDagger2);
		Assert.assertFalse(presentationHtmlDagger2.indexOf(">A39.8&#134;</a>") == -1);

		// Test url in userDesc
		String codeL405 = "L40.5";
		Iterator<IcdTabular> iteratorL405 = findByCode(codeL405);
		Assert.assertTrue(iteratorL405.hasNext());
		IcdTabular icdTabularL405 = iteratorL405.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabularL405, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlL405 = icdTabularL405.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlL405);
		Assert.assertTrue(presentationHtmlL405.indexOf("M09.0\">M09.0*</a>)") == -1);

	}

	@Test
	public void testTransformTableOutput() {
		// Test table output (popup, excludes)
		String tableCode = "V83";
		Iterator<IcdTabular> iteratorT = findByCode(tableCode);
		Assert.assertTrue(iteratorT.hasNext());
		IcdTabular tableOutput = iteratorT.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, tableOutput, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlTable = tableOutput.getPresentationHtml(LANGUAGE);

		Assert.assertNotNull(presentationHtmlTable);
		Assert.assertFalse(presentationHtmlTable.indexOf("<a name=") == -1);

		String tableCode2 = "O08";
		Iterator<IcdTabular> iteratorT2 = findByCode(tableCode2);
		Assert.assertTrue(iteratorT2.hasNext());
		IcdTabular tableOutput2 = iteratorT2.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, tableOutput2, runId,
				languages, xmlGenerator, ctxtx, true);
		String presentationHtmlTable2 = tableOutput2.getPresentationHtml(LANGUAGE);
		Assert.assertNotNull(presentationHtmlTable2);
		Assert.assertFalse(presentationHtmlTable2.indexOf("<a name=") == -1);

		// ShortPresentationHTML should be created for O0801 instead of
		// LongPresentationHTML since it is presented in a table output.
		String tableCode3 = "O08.01";
		Iterator<IcdTabular> iteratorT3 = findByCode(tableCode3);
		Assert.assertTrue(iteratorT3.hasNext());
		IcdTabular tableOutput3 = iteratorT3.next();
		transformationService.transformConcept(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, tableOutput3, runId,
				languages, xmlGenerator, ctxtx, true);
		String shortPresentationHtml = tableOutput3.getShortPresentationHtml(LANGUAGE);
		Assert.assertNotNull(shortPresentationHtml);
		Assert.assertNotNull(tableOutput3.getPresentationHtml(LANGUAGE));
	}

}