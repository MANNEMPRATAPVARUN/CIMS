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

import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.data.mapper.PublicationMapper;
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
public class ICDValidationFileGeneratorTest {

	ICDValidationFileGenerator fileGenerator = null;

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
		ruleSet.setCode("A201");
		ruleSet.setHasChild("N");
		ruleSets.add(ruleSet);
		ValidationRuleSet ruleSet1 = new ValidationRuleSet();
		ruleSet1.setCode("A202");
		ruleSet1.setHasChild("N");
		ruleSets.add(ruleSet1);
		return ruleSets;
	}

	private List<ValidationRuleSet> mockValidationRules() {
		List<ValidationRuleSet> ruleSets = new ArrayList<ValidationRuleSet>();
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode("A20");
		ruleSet.setDhcode("1");
		ruleSet.setHasChild("Y");
		ruleSet.setConceptId(1l);
		ruleSet.setXmlText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>N</DX_TYPE_3><DX_TYPE_4>Y</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		ruleSets.add(ruleSet);

		return ruleSets;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		fileGenerator = new ICDValidationFileGenerator();
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

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", 1l);
		params.put("validationCPVClassId",
				conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationICDCPV"));
		params.put("validationClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "ValidationICD"));
		params.put("catRubricClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("validationFacilityClassId",
				conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", conceptService.getICDClassID("TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId", conceptService.getICDClassID("TextPropertyVersion", "Code"));

		when(publicationMapper.findICDValidationRules(params)).thenReturn(mockValidationRules());

		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("contextId", 2l);
		params1.put("validationCPVClassId",
				conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationICDCPV"));
		params1.put("validationClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "ValidationICD"));
		params1.put("catRubricClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params1.put("validationFacilityClassId",
				conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params1.put("facilityTypeClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params1.put("domainValueCodeClassId", conceptService.getICDClassID("TextPropertyVersion", "DomainValueCode"));
		params1.put("codeClassId", conceptService.getICDClassID("TextPropertyVersion", "Code"));
		when(publicationMapper.findICDValidationRules(params1)).thenReturn(mockValidationRules());

		Map<String, Object> params2 = new HashMap<String, Object>();
		params2.put("contextId", 1l);
		params2.put("conceptId", 1l);
		params2.put("narrowerClassId", conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		params2.put("codeClassId", conceptService.getICDClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));
		params2.put("catRubricClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		when(publicationMapper.findICDChildCodes(params2)).thenReturn(mockChilds());

		Map<String, Object> params3 = new HashMap<String, Object>();
		params3.put("contextId", 2l);
		params3.put("conceptId", 1l);
		params3.put("narrowerClassId", conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		params3.put("codeClassId", conceptService.getICDClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));
		params3.put("catRubricClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		when(publicationMapper.findICDChildCodes(params3)).thenReturn(mockChilds());
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
		ruleSet.setCode("A003");
		ruleSet.setValidationRuleText("1NNA000130");
		ruleSet.setDhcode("1");
		int count = fileGenerator.buildLineFixedWidth(ruleSet, writer);
		assertEquals(1, count);
	}

	@Test
	public void testFindChildCodes() {
		List<ValidationRuleSet> results = fileGenerator.findChildCodes(1l, 1l);
		assertEquals(2, results.size());
	}

	@Test
	public void testGenerateAuditFileName() {
		String auditFileName = fileGenerator.generateAuditFileName("ENG", 2016l);
		String auditFileNameExpected = pubDirectory + "ICD" + FILE_SEPARATOR + "Validation" + FILE_SEPARATOR + "ENG"
				+ FILE_SEPARATOR + CimsFileUtils.findICDValidationAuditFileNameEnglish("2016");
		assertEquals(auditFileNameExpected, auditFileName);

		String auditFileNameFra = fileGenerator.generateAuditFileName("FRA", 2016l);
		String auditFileNameExpectedFra = pubDirectory + "ICD" + FILE_SEPARATOR + "Validation" + FILE_SEPARATOR + "FRA"
				+ FILE_SEPARATOR + CimsFileUtils.findICDValidationAuditFileNameFrench("2016");
		assertEquals(auditFileNameExpectedFra, auditFileNameFra);
	}

	@Test
	public void testGenerateEnglishFile() throws IOException {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setClassification("ICD-10-CA");
		generateReleaseTablesCriteria.setCurrentOpenYear(2016l);
		generateReleaseTablesCriteria.setFileFormat(FileFormat.TAB);
		List<PublicationStatistics> summary = new ArrayList<PublicationStatistics>();

		fileGenerator.generateEnglishFile(1l, generateReleaseTablesCriteria, summary);
		assertEquals(1, fileGenerator.currentValidationSetMap.size());
		Map<String, ValidationRuleSet> dhMap = fileGenerator.currentValidationSetMap.get("1");
		assertNotNull(dhMap);
		assertEquals(2, dhMap.size());
		assertEquals(2, summary.size());
	}

	@Test
	public void testGenerateValidationRuleSet() {
		String validationRuleSet = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>N</DX_TYPE_3><DX_TYPE_4>Y</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		assertEquals("1SNN4A000130", validationRuleSet);

		String validationRuleSet1 = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>N</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		assertEquals("1PNN6A000130", validationRuleSet1);

		String validationRuleSet2 = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>Y</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		assertEquals("1PNN6A000130", validationRuleSet2);

		String validationRuleSet3 = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		assertEquals("1PNN3A000130", validationRuleSet3);

		String validationRuleSet4 = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>N</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>Y</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		assertEquals("1PNN6A000130", validationRuleSet4);

		String validationRuleSet5 = fileGenerator
				.generateValidationRuleSet(
						"1",
						"<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>352779</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>N</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>Y</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");
		assertEquals("1SNN9A000130", validationRuleSet5);
	}

	@Test
	public void testGetDisabledHeaderDescs() {
		String[] headerDescs = new String[3];
		headerDescs[0] = "ICD-10-CA";
		headerDescs[1] = "Sector";
		headerDescs[2] = "Old Rule 10CA2016";

		String[] headerDescsToTest = fileGenerator.getDisabledHeaderDescs("ENG", 2016l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[3];
		headerDescsFra[0] = "CIM-10-CA";
		headerDescsFra[1] = "Secteur";
		headerDescsFra[2] = "Règle 2016";

		String[] headerDescsToTestFra = fileGenerator.getDisabledHeaderDescs("FRA", 2016l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetDisabledTitleValue() {
		String titleValue = fileGenerator.getDisabledTitleValue("ENG", "2016");
		assertEquals("10CA2016 ICD-10-CA Disabled Validation Rule", titleValue);

		String titleValueFra = fileGenerator.getDisabledTitleValue("FRA", "2016");
		assertEquals("CIM-10-CA 2016 Règles de validation désactivées", titleValueFra);
	}

	@Test
	public void testGetNewHeaderDescs() {
		String[] headerDescs = new String[3];
		headerDescs[0] = "ICD-10-CA";
		headerDescs[1] = "Sector";
		headerDescs[2] = "New Rule 10CA2016";

		String[] headerDescsToTest = fileGenerator.getNewHeaderDescs("ENG", 2016l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[3];
		headerDescsFra[0] = "CIM-10-CA";
		headerDescsFra[1] = "Secteur";
		headerDescsFra[2] = "Règle 2016";

		String[] headerDescsToTestFra = fileGenerator.getNewHeaderDescs("FRA", 2016l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetNewTitleValue() {
		String titleValue = fileGenerator.getNewTitleValue("ENG", "2016");
		assertEquals("10CA2016 ICD-10-CA New Validation Rule", titleValue);

		String titleValueFra = fileGenerator.getNewTitleValue("FRA", "2016");
		assertEquals("CIM-10-CA 2016 Règles de validation nouvelles", titleValueFra);
	}

	@Test
	public void testGetRevisedHeaderDescs() {
		String[] headerDescs = new String[4];
		headerDescs[0] = "ICD-10-CA";
		headerDescs[1] = "Sector";
		headerDescs[2] = "New Rule 10CA2016";
		headerDescs[3] = "Old Rule 10CA2015";

		String[] headerDescsToTest = fileGenerator.getRevisedHeaderDescs("ENG", 2016l, 2015l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[4];
		headerDescsFra[0] = "CIM-10-CA";
		headerDescsFra[1] = "Secteur";
		headerDescsFra[2] = "Règle 2016";
		headerDescsFra[3] = "Règle 2015";

		String[] headerDescsToTestFra = fileGenerator.getRevisedHeaderDescs("FRA", 2016l, 2015l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetRevisionsTitleValue() {
		String titleValue = fileGenerator.getRevisionsTitleValue("ENG", "2016");
		assertEquals("10CA2016 ICD-10-CA Validation Rule Revisions", titleValue);

		String titleValueFra = fileGenerator.getRevisionsTitleValue("FRA", "2016");
		assertEquals("CIM-10-CA 2016 Règles de validation révisées", titleValueFra);
	}

	@Test
	public void testGetWorksheetName() {
		String worksheetName = fileGenerator.getWorksheetName(Language.ENGLISH.getCode());
		assertEquals("ICD10CA_Code_Validation", worksheetName);

		String worksheetNameFra = fileGenerator.getWorksheetName(Language.FRENCH.getCode());
		assertEquals("Validation CIM-10-CA", worksheetNameFra);
	}

	@Test
	public void testProcessExistingValidation() {
		String code = "A031";
		Map<String, ValidationRuleSet> priorDhMap = new HashMap<String, ValidationRuleSet>();
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode(code);
		priorDhMap.put(code, ruleSet);

		ValidationRuleSet currentRule = new ValidationRuleSet();
		currentRule.setCode(code);
		currentRule.setValidationRuleText("1YA000130");

		ValidationRuleSet priorRule = new ValidationRuleSet();
		priorRule.setCode(code);
		priorRule.setValidationRuleText("1YN001130");

		fileGenerator.processExistingValidation(code, currentRule, priorRule, priorDhMap);

		assertEquals(1, fileGenerator.revisedCodeValidations.size());
		assertEquals(0, priorDhMap.size());

		priorDhMap.put(code, ruleSet);
		ValidationRuleSet currentRule1 = new ValidationRuleSet();
		currentRule1.setCode(code);
		currentRule1.setValidationRuleText("1YNA000130");

		ValidationRuleSet priorRule1 = new ValidationRuleSet();
		priorRule1.setCode(code);
		priorRule1.setValidationRuleText("1YNA000130");
		fileGenerator.revisedCodeValidations = new ArrayList<CodeValidationAudit>();

		fileGenerator.processExistingValidation(code, currentRule1, priorRule1, priorDhMap);

		assertEquals(0, fileGenerator.revisedCodeValidations.size());
		assertEquals(0, priorDhMap.size());

	}
}
