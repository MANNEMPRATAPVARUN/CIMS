package ca.cihi.cims.transformation;

import static ca.cihi.cims.bll.query.FindCriteria.ref;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.icd.IcdTabular;
import ca.cihi.cims.model.AsteriskBlockInfo;
import ca.cihi.cims.model.AttributeInfo;
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
public class CciXmlGeneratorTest {

	@Autowired
	private TransformationService transformationService;

	private CciXmlGenerator xmlGenerator;
	private MockCciTabular cciTabular;
	private ContextAccess context;
	private TabularConceptInfo tabularConceptInfo;

	private static final String LANGUAGE = "ENG";
	private static final String CLASSIFICATION = "CCI";

	private static final String DTD_FILE = "/dtd/cihi_cims.dtd";
	private static final String SPACE = "\\s+";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		xmlGenerator = new CciXmlGenerator();
		context = transformationService.getContextProvider().findContext(
				ContextDefinition.forVersion(CLASSIFICATION, CIMSTestConstants.TEST_VERSION));
		tabularConceptInfo = new TabularConceptInfo();

		tabularConceptInfo.setBlockList(new ArrayList<AsteriskBlockInfo>());
		tabularConceptInfo.setAsteriskList(new ArrayList<AsteriskBlockInfo>());
		tabularConceptInfo.setValidCode(false);

		cciTabular = new MockCciTabular();
	}

	@Test
	public void testGenerateBlockXml() {

		// Test block 1
		tabularConceptInfo.setCode("2AA-2ZZ");
		tabularConceptInfo.setTypeCode(IcdTabular.BLOCK);
		tabularConceptInfo.setNestingLevel(1);
		cciTabular.setUserDescription(LANGUAGE, "Tabular List of Diagnostic Interventions");

		String partOfExpectedXml = "<CODE>2AA-2ZZ</CODE><PRESENTATION_CODE>2AA-2ZZ</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CCIBLOCK1</PRESENTATION_TYPE_CODE><USER_DESC>Tabular List of Diagnostic Interventions</USER_DESC><CONCEPT_DETAIL><CLOB /></CONCEPT_DETAIL></concept>";
		List<TransformationError> errors = new ArrayList<TransformationError>();
		String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());

		// Test block 2
		tabularConceptInfo.setCode("2AA-2BX");
		tabularConceptInfo.setTypeCode(IcdTabular.BLOCK);
		tabularConceptInfo.setNestingLevel(2);
		cciTabular.setUserDescription(LANGUAGE, "Diagnostic Interventions on the Nervous System (2AA - 2BX)");

		partOfExpectedXml = "<CODE>2AA-2BX</CODE><PRESENTATION_CODE>2AA-2BX</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CCIBLOCK2</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions on the Nervous System (2AA - 2BX)</USER_DESC><CONCEPT_DETAIL><CLOB /></CONCEPT_DETAIL></concept>";
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());

		// Test block 3
		tabularConceptInfo.setCode("2AA - 2AZ");
		tabularConceptInfo.setTypeCode(IcdTabular.BLOCK);
		tabularConceptInfo.setNestingLevel(3);
		cciTabular.setUserDescription(LANGUAGE, "Diagnostic Interventions on the Brain and Spinal Cord (2AA - 2AZ)");

		partOfExpectedXml = "<CODE>2AA - 2AZ</CODE><PRESENTATION_CODE>2AA - 2AZ</PRESENTATION_CODE><TYPE_CODE>BLOCK</TYPE_CODE><PRESENTATION_TYPE_CODE>CCIBLOCK3</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions on the Brain and Spinal Cord (2AA - 2AZ)</USER_DESC><CONCEPT_DETAIL><CLOB /></CONCEPT_DETAIL></concept>";
		errors.clear();
		xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGenerateGrpXml() {

		tabularConceptInfo.setCode("2AA");
		tabularConceptInfo.setTypeCode("Group");
		tabularConceptInfo.setNestingLevel(0);
		cciTabular.setUserDescription(LANGUAGE, "Diagnostic Interventions on the Meninges and Dura Mater of Brain");
		cciTabular
				.setIncludeXml(
						LANGUAGE,
						"<qualifierlist type=\"includes\"><include><label>Bursa, capsule, cartilage, ligament and synovial lining of coracohumeral and glenohumeral joints</label></include><include><label>Shoulder ligaments [coracohumeral, glenohumeral, glenoid labrum, humeral and rotator interval]</label></include><include><label>Glenoid cavity</label></include><include><label>Labrum</label></include><include><label>Humeral head and surgical neck</label></include><include><label>Greater tuberosity [tubercle] of humerus</label></include><include><label>Shoulder joint NOS</label></include></qualifierlist>");

		cciTabular
				.setExcludeXml(
						LANGUAGE,
						"<qualifierlist type=\"excludes\"><exclude><label>Acromioclavicular and sternoclavicular joints </label></exclude></qualifierlist>");

		final String partOfExpectedXml = "<CODE>2AA</CODE><PRESENTATION_CODE>2AA</PRESENTATION_CODE><TYPE_CODE>GROUP</TYPE_CODE><PRESENTATION_TYPE_CODE>GROUP</PRESENTATION_TYPE_CODE><USER_DESC>Diagnostic Interventions on the Meninges and Dura Mater of Brain</USER_DESC><CONCEPT_DETAIL><CLOB><qualifierlist type=\"includes\"><include><label>Bursa, capsule, cartilage, ligament and synovial lining of coracohumeral and glenohumeral joints</label></include><include><label>Shoulder ligaments [coracohumeral, glenohumeral, glenoid labrum, humeral and rotator interval]</label></include><include><label>Glenoid cavity</label></include><include><label>Labrum</label></include><include><label>Humeral head and surgical neck</label></include><include><label>Greater tuberosity [tubercle] of humerus</label></include><include><label>Shoulder joint NOS</label></include></qualifierlist><qualifierlist type=\"excludes\"><exclude><label>Acromioclavicular and sternoclavicular joints </label></exclude></qualifierlist></CLOB></CONCEPT_DETAIL></concept>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGenerateRubTableXml() {

		tabularConceptInfo.setCode("1.AB.86.^^");
		tabularConceptInfo.setTypeCode(CciTabular.RUBRIC);
		tabularConceptInfo.setNestingLevel(0);
		tabularConceptInfo.setHasValidation(true);
		tabularConceptInfo.setAttributeInfo(new AttributeInfo());
		cciTabular.setCode("1.AB.86.^^");
		cciTabular.setTypeCode(CciTabular.RUBRIC);
		cciTabular.setUserDescription(LANGUAGE, "Closure of fistula, subarachnoid");
		cciTabular
				.setIncludeXml(LANGUAGE,
						"<qualifierlist type=\"includes\"><include><label>Repair, fistula, CSF of brain</label></include></qualifierlist>");
		cciTabular
				.setCodeAlsoXml(
						LANGUAGE,
						"<qualifierlist type=\"also\"><also><label>Any concomitant insertion of shunt system (see <xref refid=\"1AC52\">1.AC.52.^^</xref>)</label></also></qualifierlist>");
		cciTabular
				.setTableOutput(
						LANGUAGE,
						"<table cols=\"4\" colwidth=\"216pt 72pt 88pt 84pt\"><thead><tr><td><xref refid=\"1AB86\">1.AB.86.^^</xref> Closure of fistula, subarachnoid</td><td>using apposition technique [e.g. suture]</td><td>using autograft [e.g. fascia lata, pericranium, fat, muscle or bone]</td><td>using fibrin [glue]</td></tr></thead><tbody><tr><td>for fistula terminating at skin</td><td><xref refid=\"1AB86MB\">1.AB.86.MB</xref></td><td><xref refid=\"1AB86MBXXA\">1.AB.86.MB-XX-A</xref></td><td><xref refid=\"1AB86MBW3\">1.AB.86.MB-W3</xref></td></tr><tr><td>for fistula terminating in ear</td><td><xref refid=\"1AB86MS\">1.AB.86.MS</xref></td><td><xref refid=\"1AB86MSXXA\">1.AB.86.MS-XX-A</xref></td><td><xref refid=\"1AB86MSW3\">1.AB.86.MS-W3</xref></td></tr><tr><td>for fistula terminating in head and neck [e.g. subdural space]</td><td><xref refid=\"1AB86MJ\">1.AB.86.MJ</xref></td><td><xref refid=\"1AB86MJXXA\">1.AB.86.MJ-XX-A</xref></td><td><xref refid=\"1AB86MJW3\">1.AB.86.MJ-W3</xref></td></tr><tr><td>for fistula terminating in nasal (oral) cavity</td><td><xref refid=\"1AB86ML\">1.AB.86.ML</xref></td><td><xref refid=\"1AB86MLXXA\">1.AB.86.ML-XX-A</xref></td><td><xref refid=\"1AB86MLW3\">1.AB.86.ML-W3</xref></td></tr></tbody></table>");

		final String partOfExpectedXml = "<CODE>1.AB.86.^^</CODE><PRESENTATION_CODE>1.AB.86.^^</PRESENTATION_CODE><TYPE_CODE>RUBRIC</TYPE_CODE><PRESENTATION_TYPE_CODE>RUBRIC</PRESENTATION_TYPE_CODE><HAS_VALIDATION>true</HAS_VALIDATION><ATTRIBUTES><ATTRIBUTE><TYPE>S</TYPE><HAS_REF>false</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE /></ATTRIBUTE><ATTRIBUTE><TYPE>L</TYPE><HAS_REF>false</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE /></ATTRIBUTE><ATTRIBUTE><TYPE>E</TYPE><HAS_REF>false</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE /></ATTRIBUTE></ATTRIBUTES><USER_DESC>Closure of fistula, subarachnoid</USER_DESC><CONCEPT_DETAIL><CLOB><table cols=\"4\" colwidth=\"216pt 72pt 88pt 84pt\" type=\"portrait\"><thead><tr><td><xref refid=\"1AB86\">1.AB.86.^^</xref> Closure of fistula, subarachnoid</td><td>using apposition technique [e.g. suture]</td><td>using autograft [e.g. fascia lata, pericranium, fat, muscle or bone]</td><td>using fibrin [glue]</td></tr></thead><tbody><tr><td>for fistula terminating at skin</td><td><xref refid=\"1AB86MB\">1.AB.86.MB</xref></td><td><xref refid=\"1AB86MBXXA\">1.AB.86.MB-XX-A</xref></td><td><xref refid=\"1AB86MBW3\">1.AB.86.MB-W3</xref></td></tr><tr><td>for fistula terminating in ear</td><td><xref refid=\"1AB86MS\">1.AB.86.MS</xref></td><td><xref refid=\"1AB86MSXXA\">1.AB.86.MS-XX-A</xref></td><td><xref refid=\"1AB86MSW3\">1.AB.86.MS-W3</xref></td></tr><tr><td>for fistula terminating in head and neck [e.g. subdural space]</td><td><xref refid=\"1AB86MJ\">1.AB.86.MJ</xref></td><td><xref refid=\"1AB86MJXXA\">1.AB.86.MJ-XX-A</xref></td><td><xref refid=\"1AB86MJW3\">1.AB.86.MJ-W3</xref></td></tr><tr><td>for fistula terminating in nasal (oral) cavity</td><td><xref refid=\"1AB86ML\">1.AB.86.ML</xref></td><td><xref refid=\"1AB86MLXXA\">1.AB.86.ML-XX-A</xref></td><td><xref refid=\"1AB86MLW3\">1.AB.86.ML-W3</xref></td></tr></tbody></table><qualifierlist type=\"includes\"><include><label>Repair, fistula, CSF of brain</label></include></qualifierlist><qualifierlist type=\"also\"><also><label>Any concomitant insertion of shunt system (see <xref refid=\"1AC52\">1.AC.52.^^</xref>)</label></also></qualifierlist></CLOB></CONCEPT_DETAIL></concept>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGenerateRubXml() {
		tabularConceptInfo.setCode("2.AF.71.^^");
		tabularConceptInfo.setTypeCode("Rubric");
		tabularConceptInfo.setNestingLevel(0);
		tabularConceptInfo.setHasValidation(true);
		tabularConceptInfo.setAttributeInfo(new AttributeInfo());
		cciTabular.setCode("2.AF.71.^^");
		cciTabular.setTypeCode(CciTabular.RUBRIC);
		cciTabular.setUserDescription(LANGUAGE, "Biopsy, pituitary region");
		cciTabular.setTableOutput(LANGUAGE, "");
		cciTabular.setIncludeXml(LANGUAGE, "");
		cciTabular
				.setCodeAlsoXml(
						LANGUAGE,
						"<qualifierlist type=\"also\"><also><label>Any intraoperative stereotactic or computer guidance (see <xref refid=\"3AN94\">3.AN.94.^^</xref>)</label></also></qualifierlist>");

		final String partOfExpectedXml = "<CODE>2.AF.71.^^</CODE><PRESENTATION_CODE>2.AF.71.^^</PRESENTATION_CODE><TYPE_CODE>RUBRIC</TYPE_CODE><PRESENTATION_TYPE_CODE>RUBRIC</PRESENTATION_TYPE_CODE><HAS_VALIDATION>true</HAS_VALIDATION><ATTRIBUTES><ATTRIBUTE><TYPE>S</TYPE><HAS_REF>false</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE/></ATTRIBUTE><ATTRIBUTE><TYPE>L</TYPE><HAS_REF>false</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE/></ATTRIBUTE><ATTRIBUTE><TYPE>E</TYPE><HAS_REF>false</HAS_REF><MANDATORY>false</MANDATORY><REF_CODE/></ATTRIBUTE></ATTRIBUTES><USER_DESC>Biopsy, pituitary region</USER_DESC><CONCEPT_DETAIL><CLOB><qualifierlist type=\"also\"><also><label>Any intraoperative stereotactic or computer guidance (see <xref refid=\"3AN94\">3.AN.94.^^</xref>)</label></also></qualifierlist></CLOB></CONCEPT_DETAIL><CODE_LIST hasCode=\"true\"><codeConcept><CODE_CONCEPT_CODE>2.AF.71.GR</CODE_CONCEPT_CODE><CODE_CONCEPT_TYPE_CODE>CCICODE</CODE_CONCEPT_TYPE_CODE><CODE_CONCEPT_USER_DESC>using percutaneous transluminal approach</CODE_CONCEPT_USER_DESC><CODE_DETAIL><CODE_CLOB><qualifierlist type=\"includes\"><include><label>Petrosal sinus sampling (for elevated ACTH secretions) </label></include></qualifierlist></CODE_CLOB></CODE_DETAIL></codeConcept><codeConcept><CODE_CONCEPT_CODE>2.AF.71.QS</CODE_CONCEPT_CODE><CODE_CONCEPT_TYPE_CODE>CCICODE</CODE_CONCEPT_TYPE_CODE><CODE_CONCEPT_USER_DESC>using open trans sphenoidal [trans ethmoidal] approach</CODE_CONCEPT_USER_DESC><CODE_DETAIL><CODE_CLOB /></CODE_DETAIL></codeConcept><codeConcept><CODE_CONCEPT_CODE>2.AF.71.SZ</CODE_CONCEPT_CODE><CODE_CONCEPT_TYPE_CODE>CCICODE</CODE_CONCEPT_TYPE_CODE><CODE_CONCEPT_USER_DESC>using open transfrontal [craniotomy flap] approach</CODE_CONCEPT_USER_DESC><CODE_DETAIL><CODE_CLOB /></CODE_DETAIL></codeConcept></CODE_LIST></concept>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());
	}

	@Test
	public void testGenerateSecXml() {
		Ref<CciTabular> meRef = ref(CciTabular.class);
		Iterator<CciTabular> iterator = context.find(meRef, meRef.eq("code", "2"));

		assertNotNull(iterator);
		CciTabular aTabular = iterator.next();
		Long elementId = aTabular.getElementId();
		cciTabular.setElementId(elementId);

		tabularConceptInfo.setCode("02");
		tabularConceptInfo.setTypeCode("Section");
		cciTabular.setUserDescription(LANGUAGE, "Diagnostic Interventions");
		cciTabular.setCodeAlsoXml(LANGUAGE, "");
		cciTabular
				.setExcludeXml(
						LANGUAGE,
						"<qualifierlist type=\"excludes\"><exclude><label>Diagnostic imaging interventions (see Section 3)</label></exclude>"
								+ "<exclude><label>Diagnostic interventions unique to the state of pregnancy or to the fetus (see Section 5)</label></exclude>"
								+ "<exclude><label>Routine, preventative or screening dental, health or eye examinations (see Section 7)</label></exclude></qualifierlist>");

		final String partOfExpectedXml = "<CODE>02</CODE><PRESENTATION_CODE>02</PRESENTATION_CODE><TYPE_CODE>SECTION</TYPE_CODE><PRESENTATION_TYPE_CODE>SECTION</PRESENTATION_TYPE_CODE>";
		final List<TransformationError> errors = new ArrayList<TransformationError>();
		final String xmlString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular,
				errors, DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);

		Assert.assertTrue(xmlString.replaceAll(SPACE, "").contains(partOfExpectedXml.replaceAll(SPACE, "")));
		Assert.assertTrue(errors.isEmpty());

	}

	@Test
	public void testGenerateXmlFail() {
		List<TransformationError> errors = new ArrayList<TransformationError>();

		tabularConceptInfo.setCode("02");
		tabularConceptInfo.setTypeCode("Section");
		// The xml generation should fail and record an error if there are
		// invalid xml characters in the xml string
		cciTabular.setIncludeXml(LANGUAGE, "<qualifierlist type=\"includes\"><include>A & B</include></qualifierlist>");
		String docString = xmlGenerator.generateXml(CLASSIFICATION, CIMSTestConstants.TEST_VERSION, cciTabular, errors,
				DTD_FILE, LANGUAGE, context, XmlGeneratorHelper.LONG_PRESENTATION, tabularConceptInfo);
		Assert.assertTrue(docString.isEmpty());
		Assert.assertTrue(errors.size() == 1);
	}

}