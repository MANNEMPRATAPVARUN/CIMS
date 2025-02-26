package ca.cihi.cims.service.prodpub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.prodpub.AuditData;
import ca.cihi.cims.model.prodpub.AuditTable;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ValidationFileGeneratorTest {

	ValidationFileGenerator fileGenerator = null;

	@Mock
	PublicationMapper publicationMapper;

	@Mock
	BufferedWriter writer;

	@Value("${cims.publication.classification.tables.dir}")
	private String pubDirectory;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		fileGenerator = new ValidationFileGenerator() {

			@Override
			protected void buildAuditLists(Long lastVersionICDContextId) {

			}

			@Override
			protected int buildLineFixedWidth(Object source, BufferedWriter bw) throws IOException {
				return 0;
			}

			@Override
			protected List<ValidationRuleSet> findChildCodes(Long contextId, Long conceptId) {
				List<ValidationRuleSet> result = new ArrayList<ValidationRuleSet>();
				if (contextId == 1 && conceptId == 1) {
					ValidationRuleSet ruleSet = new ValidationRuleSet();
					ruleSet.setCode("A03.2");
					ruleSet.setConceptId(3l);
					ruleSet.setDhcode("1");
					ruleSet.setHasChild("N");
					ruleSet.setXmlText("<test></test>");
					result.add(ruleSet);
					ValidationRuleSet ruleSet1 = new ValidationRuleSet();
					ruleSet1.setCode("A03.4");
					ruleSet1.setConceptId(4l);
					ruleSet1.setDhcode("1");
					ruleSet1.setHasChild("N");
					ruleSet1.setXmlText("<test></test>");
					result.add(ruleSet1);
				}

				if (contextId == 1 && conceptId == 2) {
					ValidationRuleSet ruleSet = new ValidationRuleSet();
					ruleSet.setCode("A02.2");
					ruleSet.setConceptId(5l);
					ruleSet.setDhcode("1");
					ruleSet.setHasChild("N");
					ruleSet.setXmlText("<test></test>");
					result.add(ruleSet);
				}

				return result;
			}

			@Override
			protected String generateAuditFileName(String languageCode, Long currentVersionYear) {
				return null;
			}

			@Override
			protected void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
					GenerateReleaseTablesCriteria generateTablesModel) throws IOException {

			}

			@Override
			protected void generateEnglishFile(Long currentOpenContextId,
					GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
					throws IOException {

			}

			@Override
			protected void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
					GenerateReleaseTablesCriteria generateTablesModel) throws IOException {

			}

			@Override
			protected String generateValidationRuleSet(String dhCode, String xmlString) {
				return dhCode + "YA000130";
			}

			@Override
			protected String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear) {
				return null;
			}

			@Override
			protected String getDisabledTitleValue(String languageCode, String currentVersion) {
				return null;
			}

			@Override
			protected String[] getNewHeaderDescs(String languageCode, Long currentVersionYear) {
				return null;
			}

			@Override
			protected String getNewTitleValue(String languageCode, String currentVersion) {
				return null;
			}

			@Override
			protected String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear) {
				return null;
			}

			@Override
			protected String getRevisionsTitleValue(String languageCode, String currentVersion) {
				return null;
			}

			@Override
			protected String getWorksheetName(String languageCode) {
				return null;
			}

			@Override
			protected void processExistingValidation(String code, ValidationRuleSet currentRule,
					ValidationRuleSet priorRule, Map<String, ValidationRuleSet> priorDhMap) {

			}
		};

		fileGenerator.setPublicationMapper(publicationMapper);
		fileGenerator.setPubDirectory(pubDirectory);
		// fileGenerator.currentValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();

	}

	@Test
	public void testBuildAuditReportTable() {
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet();
		AuditTable auditTable = new AuditTable();
		auditTable.setTableTitle("Test");
		auditTable.setTableType("Revised");
		String[] headerDescs = new String[4];
		headerDescs[0] = "Code";
		headerDescs[1] = "Section";
		headerDescs[2] = "New";
		headerDescs[3] = "Old";
		auditTable.setHeaderDesc(headerDescs);
		auditTable.setAuditData(new ArrayList<AuditData>());
		int rownum = fileGenerator.buildAuditReportTable(sheet, 1, auditTable);
		assertEquals(3, rownum);

		HSSFSheet sheet1 = workbook.createSheet();
		AuditTable auditTable1 = new AuditTable();
		auditTable1.setTableTitle("Test");
		auditTable1.setTableType("Revised");
		String[] headerDescs1 = new String[4];
		headerDescs1[0] = "Code";
		headerDescs1[1] = "Section";
		headerDescs1[2] = "New";
		headerDescs1[3] = "Old";
		auditTable1.setHeaderDesc(headerDescs1);
		List<AuditData> auditDataList1 = new ArrayList<AuditData>();
		CodeValidationAudit audit = new CodeValidationAudit();
		audit.setCode("A001");
		audit.setDhcode("1");
		audit.setNewDescription("Test");
		audit.setOldDescription("Old");
		auditDataList1.add(audit);
		auditTable1.setAuditData(auditDataList1);
		int rownum1 = fileGenerator.buildAuditReportTable(sheet1, 1, auditTable1);
		assertEquals(4, rownum1);

		HSSFSheet sheet2 = workbook.createSheet();
		AuditTable auditTable2 = new AuditTable();
		auditTable2.setTableTitle("Test");
		auditTable2.setTableType("New");
		String[] headerDescs2 = new String[3];
		headerDescs2[0] = "Code";
		headerDescs2[1] = "Section";
		headerDescs2[2] = "New";
		auditTable2.setHeaderDesc(headerDescs2);
		List<AuditData> auditDataList2 = new ArrayList<AuditData>();
		CodeValidationAudit audit2 = new CodeValidationAudit();
		audit2.setCode("A001");
		audit2.setDhcode("1");
		audit2.setNewDescription("Test");
		auditDataList2.add(audit2);
		auditTable2.setAuditData(auditDataList2);
		int rownum2 = fileGenerator.buildAuditReportTable(sheet2, 1, auditTable2);
		assertEquals(4, rownum2);

		HSSFSheet sheet3 = workbook.createSheet();
		AuditTable auditTable3 = new AuditTable();
		auditTable3.setTableTitle("Test");
		auditTable3.setTableType("Removed");
		String[] headerDescs3 = new String[3];
		headerDescs3[0] = "Code";
		headerDescs3[1] = "Section";
		headerDescs3[2] = "Old";
		auditTable3.setHeaderDesc(headerDescs3);
		List<AuditData> auditDataList3 = new ArrayList<AuditData>();
		CodeValidationAudit audit3 = new CodeValidationAudit();
		audit3.setCode("A001");
		audit3.setDhcode("1");
		audit3.setNewDescription("Test");
		auditDataList3.add(audit3);
		auditTable3.setAuditData(auditDataList3);
		int rownum3 = fileGenerator.buildAuditReportTable(sheet3, 1, auditTable3);
		assertEquals(4, rownum3);
	}

	@Test
	public void testBuildAuditTables() {
		List<AuditTable> auditTables = fileGenerator.buildAuditTables(2016l, 2015l, "ENG", 2l, 1l);
		assertNotNull(auditTables);
		assertEquals(3, auditTables.size());

		List<AuditTable> auditTablesFra = fileGenerator.buildAuditTables(2016l, 2015l, "FRA", 2l, 1l);
		assertNotNull(auditTablesFra);
		assertEquals(3, auditTablesFra.size());

	}

	@Test
	public void testBuildCacheList() {
		Map<String, Map<String, ValidationRuleSet>> validationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
		List<ValidationRuleSet> codeValidations = new ArrayList<ValidationRuleSet>();
		ValidationRuleSet ruleSet = new ValidationRuleSet();
		ruleSet.setCode("A03");
		ruleSet.setConceptId(1l);
		ruleSet.setDhcode("1");
		ruleSet.setHasChild("Y");
		ruleSet.setXmlText("<test></test>");
		codeValidations.add(ruleSet);
		ValidationRuleSet ruleSet1 = new ValidationRuleSet();
		ruleSet1.setCode("A02");
		ruleSet1.setConceptId(2l);
		ruleSet1.setDhcode("1");
		ruleSet1.setHasChild("Y");
		ruleSet1.setXmlText("<test></test>");
		codeValidations.add(ruleSet1);
		ValidationRuleSet ruleSet2 = new ValidationRuleSet();
		ruleSet2.setCode("A05");
		ruleSet2.setConceptId(6l);
		ruleSet2.setDhcode("A");
		ruleSet2.setHasChild("N");
		ruleSet2.setXmlText("<test></test>");
		codeValidations.add(ruleSet2);
		fileGenerator.buildCacheList(validationSetMap, codeValidations, 1l);

		assertEquals(2, validationSetMap.size());
		Map<String, ValidationRuleSet> dh1 = validationSetMap.get("1");
		assertNotNull(dh1);
		assertEquals(3, dh1.size());

		Map<String, ValidationRuleSet> dha = validationSetMap.get("A");
		assertNotNull(dha);
		assertEquals(1, dha.size());
	}

	@Test
	public void testGenerateAuditFile() throws IOException {
		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification("CCI");
		generateTablesModel.setCurrentOpenYear(2016l);
		generateTablesModel.setFileFormat(FileFormat.TAB);

		Map<String, Map<String, ValidationRuleSet>> currentValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
		Map<String, Map<String, ValidationRuleSet>> priorValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
		List<CodeValidationAudit> revisedCodeValidations = new ArrayList<CodeValidationAudit>();
		List<CodeValidationAudit> newCodeValidations = new ArrayList<CodeValidationAudit>();
		List<CodeValidationAudit> disabledCodeValidations = new ArrayList<CodeValidationAudit>();

		fileGenerator.currentValidationSetMap = currentValidationSetMap;
		fileGenerator.priorValidationSetMap = priorValidationSetMap;

		fileGenerator.revisedCodeValidations = revisedCodeValidations;
		fileGenerator.newCodeValidations = newCodeValidations;
		fileGenerator.disabledCodeValidations = disabledCodeValidations;

		Map<String, ValidationRuleSet> currDhMap = new HashMap<String, ValidationRuleSet>();
		Map<String, ValidationRuleSet> priorDhMap = new HashMap<String, ValidationRuleSet>();

		ValidationRuleSet currRule1 = new ValidationRuleSet();
		currRule1.setCode("A001");
		currRule1.setDhcode("1");
		currRule1.setValidationRuleText("<test></test>");
		currDhMap.put("A001", currRule1);

		ValidationRuleSet currRule2 = new ValidationRuleSet();
		currRule2.setCode("A002");
		currRule2.setDhcode("1");
		currRule2.setValidationRuleText("<test></test>");
		currDhMap.put("A002", currRule2);

		ValidationRuleSet priorRule1 = new ValidationRuleSet();
		priorRule1.setCode("A001");
		priorRule1.setDhcode("1");
		priorRule1.setValidationRuleText("<test>TT</test>");
		priorDhMap.put("A001", priorRule1);

		ValidationRuleSet priorRule2 = new ValidationRuleSet();
		priorRule2.setCode("A003");
		priorRule2.setDhcode("1");
		priorRule2.setValidationRuleText("<test></test>");
		priorDhMap.put("A003", priorRule2);

		currentValidationSetMap.put("1", currDhMap);
		priorValidationSetMap.put("1", priorDhMap);

		fileGenerator.generateAuditFile(2l, 1l, generateTablesModel);

		assertEquals(1, newCodeValidations.size());
		assertEquals(2, disabledCodeValidations.size());
		assertEquals(0, revisedCodeValidations.size());
	}
}
