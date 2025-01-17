package ca.cihi.cims.service.prodpub;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAudit;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;
import ca.cihi.cims.service.ConceptService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CCIValidationFileGeneratorTest {

	CCIValidationFileGenerator fileGenerator = null;

	private static String FILE_SEPARATOR = System.getProperty("file.separator");

	@Mock
	PublicationMapper publicationMapper;

	@Mock
	BufferedWriter writer;

	@Autowired
	ConceptService conceptService;

	@Value("${cims.publication.classification.tables.dir}")
	String pubDirectory;

	private List<ValidationRuleSet> mockChilds() {
		List<ValidationRuleSet> ruleSets = new ArrayList<ValidationRuleSet>();
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode("1AA13H2");
		ruleSet.setHasChild("N");
		ruleSets.add(ruleSet);
		ValidationRuleSet ruleSet1 = new ValidationRuleSet();
		ruleSet1.setCode("1AA13D3");
		ruleSet1.setHasChild("N");
		ruleSets.add(ruleSet1);
		return ruleSets;
	}

	private List<ValidationRuleSet> mockValidationRules() {
		List<ValidationRuleSet> ruleSets = new ArrayList<ValidationRuleSet>();
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode("1AA13");
		ruleSet.setDhcode("1");
		ruleSet.setHasChild("Y");
		ruleSet.setConceptId(1l);
		ruleSet.setXmlText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"CCI\" language=\"\"><ELEMENT_ID>1141757</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><STATUS_REF></STATUS_REF><LOCATION_REF></LOCATION_REF><EXTENT_REF></EXTENT_REF></validation>");
		ruleSets.add(ruleSet);

		return ruleSets;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		fileGenerator = new CCIValidationFileGenerator();
		fileGenerator.setPubDirectory(pubDirectory);
		fileGenerator.setPublicationMapper(publicationMapper);
		fileGenerator.setConceptService(conceptService);
		fileGenerator.currentReferenceCodeMap = new HashMap<String, CCIReferenceAttribute>();
		fileGenerator.currentValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
		fileGenerator.disabledCodeValidations = new ArrayList<CodeValidationAudit>();
		fileGenerator.extentAuditList = new ArrayList<CCIReferenceAudit>();
		fileGenerator.locationAuditList = new ArrayList<CCIReferenceAudit>();
		fileGenerator.newCodeValidations = new ArrayList<CodeValidationAudit>();
		fileGenerator.priorValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
		fileGenerator.revisedCodeValidations = new ArrayList<CodeValidationAudit>();
		fileGenerator.statusAuditList = new ArrayList<CCIReferenceAudit>();
		CCIReferenceAttribute attribute = new CCIReferenceAttribute();
		attribute.setCode("E10");
		attribute.setCodeType("Extent");
		attribute.setMandatoryIndicator("Y");
		List<CCIGenericAttribute> genericAttributes = new ArrayList<CCIGenericAttribute>();
		CCIGenericAttribute genericAttribute = new CCIGenericAttribute();
		genericAttribute.setCode("L");
		genericAttribute.setDescription("Test");
		genericAttributes.add(genericAttribute);
		attribute.setGenericAttributes(genericAttributes);
		fileGenerator.currentReferenceCodeMap.put("E10", attribute);
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("E13");
		attribute1.setCodeType("Extent");
		attribute1.setMandatoryIndicator("N");
		fileGenerator.currentReferenceCodeMap.put("E13", attribute1);

		CCIReferenceAttribute attribute2 = new CCIReferenceAttribute();
		attribute2.setCode("L05");
		attribute2.setCodeType("Location");
		attribute2.setMandatoryIndicator("Y");
		fileGenerator.currentReferenceCodeMap.put("L05", attribute2);
		CCIReferenceAttribute attribute3 = new CCIReferenceAttribute();
		attribute3.setCode("L15");
		attribute3.setCodeType("Location");
		attribute3.setMandatoryIndicator("N");
		List<CCIGenericAttribute> genericAttributes1 = new ArrayList<CCIGenericAttribute>();
		CCIGenericAttribute genericAttribute1 = new CCIGenericAttribute();
		genericAttribute1.setCode("A");
		genericAttribute1.setDescription("Test");
		genericAttributes1.add(genericAttribute1);
		attribute.setGenericAttributes(genericAttributes1);
		fileGenerator.currentReferenceCodeMap.put("L15", attribute3);

		CCIReferenceAttribute attribute4 = new CCIReferenceAttribute();
		attribute4.setCode("S12");
		attribute4.setCodeType("Status");
		attribute4.setMandatoryIndicator("N");
		fileGenerator.currentReferenceCodeMap.put("S12", attribute4);

		String statusRefRegex = "<STATUS_REF>S([a-zA-Z0-9][a-zA-Z0-9])</STATUS_REF>";
		String extentRefRegex = "<EXTENT_REF>E([a-zA-Z0-9][a-zA-Z0-9])</EXTENT_REF>";
		String locationRefRegex = "<LOCATION_REF>(L|M)([a-zA-Z0-9][a-zA-Z0-9])</LOCATION_REF>";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", 1l);
		params.put("extentRefRegex", extentRefRegex);
		params.put("statusRefRegex", statusRefRegex);
		params.put("locationRefRegex", locationRefRegex);
		params.put("catRubricClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params.put("validationDefinitionClassId",
				conceptService.getCCIClassID("XMLPropertyVersion", "ValidationDefinition"));
		params.put("cciCodeClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params.put("validationCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationCCICPV"));
		params.put("codeClassId", conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));

		when(publicationMapper.findCCIValidationRules(params)).thenReturn(mockValidationRules());

		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("contextId", 2l);
		params2.put("extentRefRegex", extentRefRegex);
		params2.put("statusRefRegex", statusRefRegex);
		params2.put("locationRefRegex", locationRefRegex);
		params2.put("catRubricClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params2.put("validationDefinitionClassId",
				conceptService.getCCIClassID("XMLPropertyVersion", "ValidationDefinition"));
		params2.put("cciCodeClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params2.put("validationCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationCCICPV"));
		params2.put("codeClassId", conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));

		when(publicationMapper.findCCIValidationRules(params2)).thenReturn(mockValidationRules());

		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("contextId", 1l);
		params1.put("conceptId", 1l);
		params1.put("narrowerClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));
		params1.put("codeClassId", conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));
		params1.put("cciCodeClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		when(publicationMapper.findCCIChildCodes(params1)).thenReturn(mockChilds());

		Map<String, Object> params3 = new HashMap<String, Object>();
		params3.put("contextId", 2l);
		params3.put("conceptId", 1l);
		params3.put("narrowerClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));
		params3.put("codeClassId", conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));
		params3.put("cciCodeClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		when(publicationMapper.findCCIChildCodes(params3)).thenReturn(mockChilds());

	}

	@Test
	public void testBuildAuditList() {
		fileGenerator.buildAuditLists(2l);
		assertEquals(1, fileGenerator.priorValidationSetMap.size());
		Map<String, ValidationRuleSet> dhMap = fileGenerator.priorValidationSetMap.get("1");
		assertNotNull(dhMap);
		assertEquals(2, dhMap.size());
	}

	@Test
	public void testBuildLineFixedWidth() throws IOException {
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode("1AA23HC");
		ruleSet.setValidationRuleText("1YA000130");
		ruleSet.setDhcode("1");
		int count = fileGenerator.buildLineFixedWidth(ruleSet, writer);
		assertEquals(1, count);
	}

	@Test
	public void testGenerateAuditFileName() {
		String auditFileName = fileGenerator.generateAuditFileName("ENG", 2016l);
		String auditFileNameExpected = pubDirectory + "CCI" + FILE_SEPARATOR + "Validation" + FILE_SEPARATOR + "ENG"
				+ FILE_SEPARATOR + CimsFileUtils.findCCIValidationAuditFileNameEnglish("2016");
		assertEquals(auditFileNameExpected, auditFileName);

		String auditFileNameFra = fileGenerator.generateAuditFileName("FRA", 2016l);
		String auditFileNameExpectedFra = pubDirectory + "CCI" + FILE_SEPARATOR + "Validation" + FILE_SEPARATOR + "FRA"
				+ FILE_SEPARATOR + CimsFileUtils.findCCIValidationAuditFileNameFrench("2016");
		assertEquals(auditFileNameExpectedFra, auditFileNameFra);
	}

	@Test
	public void testGenerateEnglishFile() throws IOException {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setClassification("CCI");
		generateReleaseTablesCriteria.setCurrentOpenYear(2016l);
		generateReleaseTablesCriteria.setFileFormat(FileFormat.TAB);
		List<PublicationStatistics> summary = new ArrayList<PublicationStatistics>();

		fileGenerator.generateEnglishFile(1l, generateReleaseTablesCriteria, summary);
		assertEquals(1, fileGenerator.currentValidationSetMap.size());
		Map<String, ValidationRuleSet> dhMap = fileGenerator.currentValidationSetMap.get("1");
		assertNotNull(dhMap);
		assertEquals(2, dhMap.size());
		assertEquals(4, summary.size());
	}

	@Test
	public void testGenerateValidationRuleSet() {
		String validationRuleSet = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"CCI\" language=\"\"><ELEMENT_ID>1141757</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><STATUS_REF></STATUS_REF><LOCATION_REF></LOCATION_REF><EXTENT_REF></EXTENT_REF></validation>");
		assertEquals("1YA000130", validationRuleSet);

		String validationRuleSet1 = fileGenerator
				.generateValidationRuleSet(
						"A",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"CCI\" language=\"\"><ELEMENT_ID>1141757</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><STATUS_REF></STATUS_REF><LOCATION_REF></LOCATION_REF><EXTENT_REF></EXTENT_REF></validation>");
		assertEquals("AYA000130", validationRuleSet1);
	}

	@Test
	public void testGetDisabledHeaderDescs() {
		String[] headerDescs = new String[3];
		headerDescs[0] = "CCI";
		headerDescs[1] = "Sector";
		headerDescs[2] = "Old Rule CCI2016";

		String[] headerDescsToTest = fileGenerator.getDisabledHeaderDescs("ENG", 2016l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[3];
		headerDescsFra[0] = "CCI";
		headerDescsFra[1] = "Secteur";
		headerDescsFra[2] = "Règle 2016";

		String[] headerDescsToTestFra = fileGenerator.getDisabledHeaderDescs("FRA", 2016l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetDisabledTitleValue() {
		String titleValue = fileGenerator.getDisabledTitleValue("ENG", "2016");
		assertEquals("2016 CCI Disabled Validation Rule", titleValue);

		String titleValueFra = fileGenerator.getDisabledTitleValue("FRA", "2016");
		assertEquals("2016 CCI Règles de validation désactivées", titleValueFra);
	}

	@Test
	public void testGetNewHeaderDescs() {
		String[] headerDescs = new String[3];
		headerDescs[0] = "CCI";
		headerDescs[1] = "Sector";
		headerDescs[2] = "New Rule CCI2016";

		String[] headerDescsToTest = fileGenerator.getNewHeaderDescs("ENG", 2016l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[3];
		headerDescsFra[0] = "CCI";
		headerDescsFra[1] = "Secteur";
		headerDescsFra[2] = "Règle 2016";

		String[] headerDescsToTestFra = fileGenerator.getNewHeaderDescs("FRA", 2016l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetNewTitleValue() {
		String titleValue = fileGenerator.getNewTitleValue("ENG", "2016");
		assertEquals("2016 CCI New Validation Rule", titleValue);

		String titleValueFra = fileGenerator.getNewTitleValue("FRA", "2016");
		assertEquals("2016 CCI Règles de validation nouvelles", titleValueFra);
	}

	@Test
	public void testGetRevisedHeaderDescs() {
		String[] headerDescs = new String[4];
		headerDescs[0] = "CCI";
		headerDescs[1] = "Sector";
		headerDescs[2] = "New Rule CCI2016";
		headerDescs[3] = "Old Rule CCI2015";

		String[] headerDescsToTest = fileGenerator.getRevisedHeaderDescs("ENG", 2016l, 2015l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[4];
		headerDescsFra[0] = "CCI";
		headerDescsFra[1] = "Secteur";
		headerDescsFra[2] = "Règle 2016";
		headerDescsFra[3] = "Règle 2015";

		String[] headerDescsToTestFra = fileGenerator.getRevisedHeaderDescs("FRA", 2016l, 2015l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetRevisionsTitleValue() {
		String titleValue = fileGenerator.getRevisionsTitleValue("ENG", "2016");
		assertEquals("2016 CCI Validation Rule Revisions", titleValue);

		String titleValueFra = fileGenerator.getRevisionsTitleValue("FRA", "2016");
		assertEquals("2016 CCI Règles de validation révisées", titleValueFra);
	}

	@Test
	public void testGetWorksheetName() {
		String worksheetName = fileGenerator.getWorksheetName(Language.ENGLISH.getCode());
		assertEquals("CCI_Code_Validation", worksheetName);

		String worksheetNameFra = fileGenerator.getWorksheetName(Language.FRENCH.getCode());
		assertEquals("CCI_Validation_Code", worksheetNameFra);
	}

	@Test(expected = CIMSException.class)
	public void testGetWorksheetNameReference() {
		String worksheetNameExtent = fileGenerator.getWorksheetNameReference(Language.ENGLISH.getCode(), "Extent");
		assertEquals("CCI_Extent_Val", worksheetNameExtent);
		String worksheetNameExtentFra = fileGenerator.getWorksheetNameReference(Language.FRENCH.getCode(), "Extent");
		assertEquals("CCI Validation Étendue", worksheetNameExtentFra);

		String worksheetNameStatus = fileGenerator.getWorksheetNameReference(Language.ENGLISH.getCode(), "Status");
		assertEquals("CCI_Status_Val", worksheetNameStatus);
		String worksheetNameStatusFra = fileGenerator.getWorksheetNameReference(Language.FRENCH.getCode(), "Status");
		assertEquals("CCI Validation Situation", worksheetNameStatusFra);

		String worksheetNameLocation = fileGenerator.getWorksheetNameReference(Language.ENGLISH.getCode(), "Location");
		assertEquals("CCI_Location_Val", worksheetNameLocation);
		String worksheetNameLocationFra = fileGenerator
				.getWorksheetNameReference(Language.FRENCH.getCode(), "Location");
		assertEquals("CCI Validation Lieu", worksheetNameLocationFra);

		fileGenerator.getWorksheetNameReference("ENG", "Other");
	}

	@Test
	public void testProcessExistingValidation() {
		String code = "1AA03";
		Map<String, ValidationRuleSet> priorDhMap = new HashMap<String, ValidationRuleSet>();
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode(code);
		priorDhMap.put(code, ruleSet);

		ValidationRuleSet currentRule = new ValidationRuleSet();
		currentRule.setCode(code);
		currentRule.setValidationRuleText("1YA000130");
		currentRule.setExtentRef("E10");
		currentRule.setLocationRef("L05");

		ValidationRuleSet priorRule = new ValidationRuleSet();
		priorRule.setCode(code);
		priorRule.setValidationRuleText("1YN001130");
		priorRule.setExtentRef("E13");
		priorRule.setStatusRef("S12");
		priorRule.setLocationRef("L15");

		fileGenerator.processExistingValidation(code, currentRule, priorRule, priorDhMap);

		assertEquals(1, fileGenerator.revisedCodeValidations.size());
		assertEquals(1, fileGenerator.extentAuditList.size());
		assertEquals(1, fileGenerator.statusAuditList.size());
		assertEquals(1, fileGenerator.locationAuditList.size());
		assertEquals(0, priorDhMap.size());

		fileGenerator.revisedCodeValidations = new ArrayList<CodeValidationAudit>();
		fileGenerator.extentAuditList = new ArrayList<CCIReferenceAudit>();
		fileGenerator.locationAuditList = new ArrayList<CCIReferenceAudit>();
		fileGenerator.statusAuditList = new ArrayList<CCIReferenceAudit>();

		priorDhMap.put(code, ruleSet);

		ValidationRuleSet currentRule1 = new ValidationRuleSet();
		currentRule1.setCode(code);
		currentRule1.setValidationRuleText("1YA000130");

		ValidationRuleSet priorRule1 = new ValidationRuleSet();
		priorRule1.setCode(code);
		priorRule1.setValidationRuleText("1YA000130");
		priorRule1.setExtentRef("E10");
		priorRule1.setLocationRef("L15");

		fileGenerator.processExistingValidation(code, currentRule1, priorRule1, priorDhMap);

		assertEquals(0, fileGenerator.revisedCodeValidations.size());
		assertEquals(1, fileGenerator.extentAuditList.size());
		assertEquals(0, fileGenerator.statusAuditList.size());
		assertEquals(1, fileGenerator.locationAuditList.size());
		assertEquals(0, priorDhMap.size());

	}

}
