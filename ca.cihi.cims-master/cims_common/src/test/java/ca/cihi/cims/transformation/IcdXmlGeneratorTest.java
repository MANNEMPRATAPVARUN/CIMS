package ca.cihi.cims.transformation;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.TabularConceptInfo;
import ca.cihi.cims.model.TransformationError;
import ca.cihi.cims.service.TransformationService;

/**
 * Test class of XmlGenerator.
 * 
 * @author wxing
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class IcdXmlGeneratorTest {

	private static final Log LOGGER = LogFactory.getLog(IcdXmlGeneratorTest.class);

	@Autowired
	private TransformationService transformationService;

	private XmlGenerator xmlGenerator;
	private IcdTabular icdTabular;
	private ContextAccess context;
	private TabularConceptInfo tabularConceptInfo;

	private static final String LANGUAGE = "ENG";
	private static final String CLASSIFICATION = "ICD-10-CA";
	private static final String DTD_FILE = "/dtd/cihi_cims.dtd";
	private static final String SPACE = "\\s+";

	@Before
	public void setUp() {
		xmlGenerator = new IcdXmlGenerator();
		context = transformationService.getContextProvider().findContext(
				ContextDefinition.forVersion(CLASSIFICATION, CIMSTestConstants.TEST_VERSION));
		tabularConceptInfo = new TabularConceptInfo();

		tabularConceptInfo.setBlockList(new ArrayList<AsteriskBlockInfo>());
		tabularConceptInfo.setAsteriskList(new ArrayList<AsteriskBlockInfo>());
		tabularConceptInfo.setValidCode(false);
		tabularConceptInfo.setCode("A15.4");
		tabularConceptInfo.setConceptCodeWithDecimalDagger("A15.4");
		tabularConceptInfo.setNestingLevel(2);
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setCanadianEnhancement(false);

		icdTabular = new MockIcdTabular();
		icdTabular.setUserDescription(LANGUAGE, "No user description set");
		icdTabular.setShortDescription(LANGUAGE, "Anencephaly and other serious impediments");
	}

	@Test
	public void testAddXrefInUserDesc() {
		tabularConceptInfo.setCode("8000/1");
		tabularConceptInfo.setTypeCode(IcdTabular.CODE);
		tabularConceptInfo.setNestingLevel(1);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("8000/1");
		icdTabular.setUserDescription(LANGUAGE, "No user description set (A01)");
		icdTabular.setShortDescription("ENG", "Anencephaly and other serious impediments");

		String partOfExpectedXml = "<CODE>8000/1</CODE><PRESENTATION_CODE>8000/1</PRESENTATION_CODE>"
				+ "<TYPE_CODE>CODE</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>No user description set (&lt;a href=\"#A01\"&gt;A01&lt;/a&gt;)</USER_DESC>"
				+ "<CONCEPT_CODE_WITH_DECIMAL_DAGGAR>8000/1</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>8000/1</CONCEPT_CODE_WITH_DECIMAL>"
				+ "<CONCEPT_DETAIL><CLOB/></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"/><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		LOGGER.debug("testIcdXsl resultHtml:" + xmlString);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());

		tabularConceptInfo.setTypeCode(IcdTabular.CODE);
		tabularConceptInfo.setNestingLevel(1);
		icdTabular.setUserDescription(LANGUAGE, "No user description set (M72.5*)");
		partOfExpectedXml = "<CODE>8000/1</CODE><PRESENTATION_CODE>8000/1</PRESENTATION_CODE>"
				+ "<TYPE_CODE>CODE</TYPE_CODE><PRESENTATION_TYPE_CODE>CODE</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>No user description set (&lt;a href=\"#M72.5\"&gt;M72.5*&lt;/a&gt;)</USER_DESC>"
				+ "<CONCEPT_CODE_WITH_DECIMAL_DAGGAR>8000/1</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>8000/1</CONCEPT_CODE_WITH_DECIMAL>"
				+ "<CONCEPT_DETAIL><CLOB/></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"/><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testChapterConcept() {
		tabularConceptInfo.setCode("01");
		tabularConceptInfo.setTypeCode(IcdTabular.CHAPTER);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("01");
		tabularConceptInfo.setNestingLevel(0);

		Ref<IcdTabular> meRef = ref(IcdTabular.class);
		Long elementId = context.findOne(meRef, meRef.eq("code", "01")).getElementId();
		icdTabular.setElementId(elementId);
		icdTabular.setUserDescription(LANGUAGE, "Oat cell carcinoma (C34.-)");
		icdTabular.setContextAccess(context);

		List<AsteriskBlockInfo> blockList = new ArrayList<AsteriskBlockInfo>();
		AsteriskBlockInfo blockInfo = new AsteriskBlockInfo();
		blockInfo.setCode("A00-A09");
		blockList.add(blockInfo);
		tabularConceptInfo.setBlockList(blockList);

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.indexOf("<BLOCK code=\"A00-A09\"") != -1);
	}

	@Test
	public void testGenerateXmlFail() {
		List<TransformationError> errors = new ArrayList<TransformationError>();

		// The xml generation should fail and record an error if there are
		// invalid xml characters in the xml string
		icdTabular.setIncludeXml(LANGUAGE, "<qualifierlist type=\"includes\"><include>A & B</include></qualifierlist>");
		String docString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(docString.isEmpty());
		Assert.assertTrue(errors.size() == 1);

		// The xml generation should fail and record an error if there are
		// unmatched tags in the xml string
		errors = new ArrayList<TransformationError>();
		icdTabular.setIncludeXml(LANGUAGE, "<qualifierlist type=\"includes\"><a><b>test</a></qualifierlist>");
		docString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(docString.isEmpty());
		Assert.assertTrue(errors.size() == 1);
	}

	@Test
	public void testGenerateXmlForSpecialBlock() {

		tabularConceptInfo.setCode("8000/1");
		tabularConceptInfo.setTypeCode(IcdTabular.BLOCK);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("8000/1");
		tabularConceptInfo.setNestingLevel(2);
		icdTabular.setUserDescription(LANGUAGE, "No user description set");
		icdTabular.setShortDescription(LANGUAGE, "Anencephaly and other serious impediments");

		final String partOfExpectedXml = "<CODE>8000/1</CODE><PRESENTATION_CODE>8000/1</PRESENTATION_CODE>"
				+ "<TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CATEGORY1</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>No user description set</USER_DESC>"
				+ "<CONCEPT_CODE_WITH_DECIMAL_DAGGAR>8000/1</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>8000/1</CONCEPT_CODE_WITH_DECIMAL>"
				+ "<CONCEPT_DETAIL><CLOB/></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"/><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);

		// Assert.assertTrue(xmlString.replaceAll(SPACE,
		// "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGenerateXmlSuccess() {
		icdTabular.setIncludeXml(LANGUAGE, "<qualifierlist type=\"includes\">" + "<include>" + "<!-- *** BRACE *** -->"
				+ "<brace cols=\"3\">" + "<label>Tuberculosis of lymph nodes:</label>"
				+ "<segment bracket=\"right\" size=\"03\">" + "<item>" + "<ulist>" + "<listitem>hilar</listitem>"
				+ "<listitem>mediastinal</listitem>" + "<listitem>tracheobronchial</listitem>" + "</ulist>" + "</item>"
				+ "</segment>" + "<segment>" + "<item>confirmed bacteriologically and histologically</item>"
				+ "</segment>" + "</brace>" + "</include>" + "</qualifierlist>");
		icdTabular.setExcludeXml(LANGUAGE, "<qualifierlist type=\"excludes\">" + "<exclude>"
				+ "<label>specified as primary ( <xref refid=\"A157\">A15.7</xref>)</label>" + "</exclude>"
				+ "</qualifierlist>");
		icdTabular.setNote(LANGUAGE,
				"<qualifierlist type=\"note\"><note><label>A group of disorders characterized by the"
						+ "combination of persistently aggressive, dissocial or defiant behaviour with overt and"
						+ "marked symptoms of depression, anxiety or other emotional upsets. The criteria for both"
						+ "conduct disorders of childhood (F9l.-) and emotional disorders of childhood (F93.-) or an"
						+ "adult-type neurotic diagnosis (F40-F48) or a mood disorder (F30-F39) must be met.</label>"
						+ "</note></qualifierlist>");

		icdTabular
				.setDefinitionXml(LANGUAGE,
						"<qualifierlist type=\"definition\"><definition><label>A definition test</label></definition></qualifierlist>");

		icdTabular
				.setCodeAlsoXml(
						LANGUAGE,
						"<qualifierlist type=\"also\"><also><label>Use additional code to identify infectious agent.</label></also></qualifierlist>");

		final String partOfExpectedXml = "<CODE>A15.4</CODE><PRESENTATION_CODE>A15.4</PRESENTATION_CODE>"
				+ "<TYPE_CODE>CATEGORY</TYPE_CODE><PRESENTATION_TYPE_CODE>CATEGORY2</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>No user description set</USER_DESC>"
				+ "<CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A15.4</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A15.4</CONCEPT_CODE_WITH_DECIMAL>"
				+ "<CONCEPT_DETAIL><CLOB><qualifierlist type=\"definition\"><definition><label>A definition test</label></definition></qualifierlist><qualifierlist type=\"note\"><note><label>A group of disorders characterized by thecombination of persistently"
				+ "aggressive, dissocial or defiant behaviour with overt andmarked symptoms of depression,anxiety or other emotional upsets. The criteria"
				+ " for bothconduct disorders of childhood (F9l.-) and emotional disorders of childhood (F93.-) or anadult-type neurotic "
				+ "diagnosis (F40-F48) or a mood disorder (F30-F39) must be met.</label></note></qualifierlist><qualifierlist type=\"includes\">"
				+ "<include><!-- *** BRACE *** --><brace cols=\"3\"><label>Tuberculosis of lymph nodes:</label>"
				+ "<segment bracket=\"right\" size=\"03\"><item><ulist mark=\"bullet\"><listitem>hilar</listitem><listitem>mediastinal</listitem><listitem>tracheobronchial</listitem></ulist>"
				+ "</item></segment><segment bracket=\"right\" size=\"05\"><item>confirmed bacteriologically and histologically</item></segment></brace></include></qualifierlist>"
				+ "<qualifierlist type=\"also\"><also><label>Use additional code to identify infectious agent.</label></also></qualifierlist><qualifierlist type=\"excludes\">"
				+ "<exclude><label>specified as primary ( <xref refid=\"A157\">A15.7</xref>)</label></exclude></qualifierlist></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\"/><ASTERISK_LIST hasAsterisk=\"false\"/></concept>";

		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		LOGGER.debug("resultHtml:" + xmlString);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGenerateXmlSuccessTable() {
		icdTabular.setTableOutput(LANGUAGE, "<table cols=\"3\" colwidth=\"200pt 95pt 95pt\"><thead><tr><td>"
				+ "<xref refid=\"S72.4\">S72.4</xref> Fracture" + "</td><td>Fer</td><td>Ouverte</td></tr></thead>"
				+ "<tbody><tr><td><xref refid=\"S72.40\">S72.40</xref>   Fracture"
				+ "</td><td><xref refid=\"S72.400\">S72.400</xref>"
				+ "<phrase format=\"emblem\">o</phrase></td><td><xref refid=\"S72.401\">S72.401</xref>"
				+ "<phrase format=\"emblem\">o</phrase></td></tr></tbody></table>");

		final String partOfExpectedXml = "<CODE>A15.4</CODE><PRESENTATION_CODE>A15.4</PRESENTATION_CODE><TYPE_CODE>CATEGORY</TYPE_CODE><PRESENTATION_TYPE_CODE>CATEGORY2</PRESENTATION_TYPE_CODE><CA_ENHANCEMENT_FLAG>false</CA_ENHANCEMENT_FLAG><USER_DESC>No user description set</USER_DESC><CONCEPT_CODE_WITH_DECIMAL_DAGGAR>A15.4</CONCEPT_CODE_WITH_DECIMAL_DAGGAR><CONCEPT_CODE_WITH_DECIMAL>A15.4</CONCEPT_CODE_WITH_DECIMAL>"
				+ "<CONCEPT_DETAIL><CLOB><table cols=\"3\" colwidth=\"200pt 95pt 95pt\" type=\"portrait\"><thead><tr><td><a name=\"S72.4\">S72.4</a> Fracture</td><td>Fer</td><td>Ouverte</td></tr></thead><tbody><tr><td><a name=\"S72.40\">S72.40</a>   Fracture</td><td><a name=\"S72.400\">S72.400</a><phrase format=\"emblem\">o</phrase></td><td><a name=\"S72.401\">S72.401</a><phrase format=\"emblem\">o</phrase></td></tr></tbody></table></CLOB></CONCEPT_DETAIL><BLOCK_LIST hasBlock=\"false\" /><ASTERISK_LIST hasAsterisk=\"false\" /></concept>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		LOGGER.debug("resultHtml:" + xmlString);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGreaterThanSign() {
		icdTabular
				.setIncludeXml(
						LANGUAGE,
						"<qualifierlist type=\"includes\"><include><label>Kidney damage with normal or increased GFR (&ge;90 mL/min)</label> </include> </qualifierlist>");
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);

		Assert.assertNotNull(xmlString);
		Assert.assertTrue(errors.isEmpty());

	}

	@Test
	public void testUrlInUserDesc() {

		tabularConceptInfo.setCode("8042/3");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(1);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("8042/3");

		icdTabular.setUserDescription(LANGUAGE, "Oat cell carcinoma (C34.-)");
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.indexOf("(&lt;a href=\"#C34\"&gt;C34.-&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("8147/3");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(1);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("8147/3");
		icdTabular.setUserDescription(LANGUAGE, "Basal cell adenocarcinoma (C07.-, C08.-)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString
				.indexOf("(&lt;a href=\"#C07\"&gt;C07.-&lt;/a&gt;, &lt;a href=\"#C08\"&gt;C08.-&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("8160/0");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(1);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("8160/0");
		icdTabular.setUserDescription(LANGUAGE, "Bile duct adenoma (D13.4, D13.5)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString
				.indexOf("(&lt;a href=\"#D13.4\"&gt;D13.4&lt;/a&gt;, &lt;a href=\"#D13.5\"&gt;D13.5&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("8941/3");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(1);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("8941/3");
		icdTabular.setUserDescription(LANGUAGE, "Carcinoma in pleomorphic adenoma (C07, C08.-)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString
				.indexOf("(&lt;a href=\"#C07\"&gt;C07&lt;/a&gt;, &lt;a href=\"#C08\"&gt;C08.-&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("B57.0");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(2);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("B57.0");
		icdTabular.setUserDescription(LANGUAGE, "Acute Chagas' disease with heart involvement (I41.2*, I98.1*)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString
				.indexOf("(&lt;a href=\"#I41.2\"&gt;I41.2*&lt;/a&gt;, &lt;a href=\"#I98.1\"&gt;I98.1*&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("M01.6");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(2);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("M01.6");
		icdTabular.setUserDescription(LANGUAGE, "Arthritis in mycoses (B35-B49&#134;)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString
				.indexOf("(&lt;a href=\"#B35\"&gt;B35&lt;/a&gt;-&lt;a href=\"#B49\"&gt;B49&amp;#134;&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("M07.4");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(2);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("M07.4");
		icdTabular.setUserDescription(LANGUAGE, "Arthropathy in Crohn's disease [regional enteritis] (K50.-&#134;)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.indexOf("(&lt;a href=\"#K50\"&gt;K50.-&amp;#134;&lt;/a&gt;)") != -1);

		tabularConceptInfo.setCode("M14.2");
		tabularConceptInfo.setTypeCode(IcdTabular.CATEGORY);
		tabularConceptInfo.setNestingLevel(2);
		tabularConceptInfo.setConceptCodeWithDecimalDagger("M14.2");
		icdTabular.setUserDescription(LANGUAGE,
				"Diabetic arthropathy (E10-E14&#134; with common fourth and fifth characters .60)");
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, icdTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString
				.indexOf("(&lt;a href=\"#E10\"&gt;E10&lt;/a&gt;-&lt;a href=\"#E14\"&gt;E14&amp;#134;&lt;/a&gt;") != -1);
	}

}