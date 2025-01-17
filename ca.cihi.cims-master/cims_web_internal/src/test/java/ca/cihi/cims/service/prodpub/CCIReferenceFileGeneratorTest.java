package ca.cihi.cims.service.prodpub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.prodpub.AuditData;
import ca.cihi.cims.model.prodpub.AuditTable;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIGenericAttributeAudit;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.service.ConceptService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CCIReferenceFileGeneratorTest {
	CCIReferenceFileGenerator fileGenerator = null;

	@Mock
	BufferedWriter writer;

	@Value("${cims.publication.classification.tables.dir}")
	private String pubDirectory;

	private static String FILE_SEPARATOR = System.getProperty("file.separator");

	@Mock
	PublicationMapper publicationMapper;

	@Autowired
	ConceptService conceptService;

	private List<CCIReferenceAttribute> mockCCIReferenceAttributes() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("E12");
		attribute1.setCodeType("Extent");
		attribute1.setMandatoryIndicator("N");
		List<CCIGenericAttribute> attributes1 = new ArrayList<CCIGenericAttribute>();
		attribute1.setGenericAttributes(attributes1);

		CCIReferenceAttribute attribute2 = new CCIReferenceAttribute();
		attribute2.setCode("E13");
		attribute2.setCodeType("Extent");
		attribute2.setMandatoryIndicator("Y");
		List<CCIGenericAttribute> attributes2 = new ArrayList<CCIGenericAttribute>();
		attribute2.setGenericAttributes(attributes2);
		CCIGenericAttribute genericAttribute1 = new CCIGenericAttribute();
		genericAttribute1.setCode("A");
		genericAttribute1.setDescription("Test1");
		attributes2.add(genericAttribute1);

		CCIGenericAttribute genericAttribute2 = new CCIGenericAttribute();
		genericAttribute2.setCode("B");
		genericAttribute2.setDescription("Test2");
		attributes2.add(genericAttribute2);

		CCIReferenceAttribute attribute3 = new CCIReferenceAttribute();
		attribute3.setCode("E14");
		attribute3.setCodeType("Extent");
		attribute3.setMandatoryIndicator("N");
		List<CCIGenericAttribute> attributes3 = new ArrayList<CCIGenericAttribute>();
		attribute3.setGenericAttributes(attributes3);
		CCIGenericAttribute genericAttribute3 = new CCIGenericAttribute();
		genericAttribute3.setCode("LT");
		genericAttribute3.setDescription("Test3");
		attributes3.add(genericAttribute3);

		attributes.add(attribute3);
		attributes.add(attribute2);
		attributes.add(attribute1);

		return attributes;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		fileGenerator = new CCIReferenceFileGenerator() {

			@Override
			protected void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionContextId,
					GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException {

			}

			@Override
			protected void generateEnglishFile(Long currentOpenContextId,
					GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
					throws IOException {

			}

			@Override
			protected void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionContextId,
					GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException {

			}

			@Override
			protected void generateFrenchFile(Long currentOpenContextId,
					GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
					throws IOException {

			}

			@Override
			protected List<CCIReferenceAttribute> getCCIReferenceAttributes(String languageCode, Long contextId) {
				return getCCIReferenceAttributes("ENG", "E", contextId);
			}

			@Override
			protected String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear) {
				String[] headerDescs = new String[3];
				headerDescs[0] = "ENG".equals(languageCode) ? "Reference" : "Référence";
				headerDescs[1] = "ENG".equals(languageCode) ? "Extent" : "Étendue";
				headerDescs[2] = ("ENG".equals(languageCode) ? "English Disabled Description " : "Description ")
						+ lastVersionYear;
				return headerDescs;
			}

			@Override
			protected String getDisabledTitleValue(String languageCode, String currentVersion) {
				return null;
			}

			@Override
			protected String[] getNewHeaderDescs(String languageCode, Long currentVersionYear) {
				String[] headerDescs = new String[3];
				headerDescs[0] = "ENG".equals(languageCode) ? "Reference" : "Référence";
				headerDescs[1] = "ENG".equals(languageCode) ? "Extent" : "Étendue";
				headerDescs[2] = ("ENG".equals(languageCode) ? "English New Description " : "Description ")
						+ currentVersionYear;
				return headerDescs;
			}

			@Override
			protected String getNewTitleValue(String languageCode, String currentVersion) {
				return null;
			}

			@Override
			protected String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear) {
				String[] headerDescs = new String[4];
				headerDescs[0] = "ENG".equals(languageCode) ? "Reference" : "Référence";
				headerDescs[1] = "ENG".equals(languageCode) ? "Extent" : "Étendue";
				headerDescs[2] = ("ENG".equals(languageCode) ? "English Revised Description " : "Description ")
						+ currentVersionYear;
				headerDescs[3] = ("ENG".equals(languageCode) ? "English Revised Description " : "Description ")
						+ lastVersionYear;

				return headerDescs;
			}

			@Override
			protected String getRevisionsTitleValue(String languageCode, String currentVersion) {
				return null;
			}

			@Override
			protected String getWorksheetName(String languageCode) {
				return "ENG".equals(languageCode) ? "CCI_Extent_Eng" : "CCI_Étendue_Fra";
			}
		};
		fileGenerator.setPublicationMapper(publicationMapper);
		fileGenerator.setPubDirectory(pubDirectory);
		fileGenerator.setConceptService(conceptService);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params.put("genericAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params.put("attributeTypeIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "AttributeTypeIndicator"));
		params.put("attributeCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params.put("attributeDescriptionClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params.put("domainValueCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "DomainValueCode"));
		params.put("attributeMandatoryIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params.put("contextId", 1l);
		params.put("attributeType", "E");
		params.put("languageCode", "ENG");

		when(publicationMapper.getCCIReferenceAttributes(params)).thenReturn(mockCCIReferenceAttributes());

		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("referenceAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params1.put("genericAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params1.put("attributeTypeIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "AttributeTypeIndicator"));
		params1.put("attributeCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params1.put("attributeDescriptionClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params1.put("domainValueCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "DomainValueCode"));
		params1.put("attributeMandatoryIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params1.put("contextId", 2l);
		params1.put("attributeType", "E");
		params1.put("languageCode", "ENG");
		when(publicationMapper.getCCIReferenceAttributes(params1)).thenReturn(mockCCIReferenceAttributes());
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
		CCIGenericAttributeAudit audit1 = new CCIGenericAttributeAudit();
		audit1.setCode("H");
		audit1.setReferenceCode("E12");
		audit1.setNewDescription("Test");
		audit1.setOldDescription("Old");
		auditDataList1.add(audit1);
		auditTable1.setAuditData(auditDataList1);
		int rownum1 = fileGenerator.buildAuditReportTable(sheet1, 1, auditTable1);
		assertEquals(4, rownum1);

		HSSFSheet sheet2 = workbook.createSheet();
		AuditTable auditTable2 = new AuditTable();
		auditTable2.setTableTitle("Test");
		auditTable2.setTableType("Revised");
		String[] headerDescs2 = new String[4];
		headerDescs2[0] = "Code";
		headerDescs2[2] = "Section";
		headerDescs2[2] = "New";
		headerDescs2[3] = "Old";
		auditTable2.setHeaderDesc(headerDescs2);
		List<AuditData> auditDataList2 = new ArrayList<AuditData>();
		CCIGenericAttributeAudit audit2 = new CCIGenericAttributeAudit();
		audit2.setCode("H");
		audit2.setReferenceCode("E22");
		audit2.setNewDescription("Test");
		audit2.setOldDescription("Old");
		auditDataList2.add(audit2);
		auditTable2.setAuditData(auditDataList2);
		int rownum2 = fileGenerator.buildAuditReportTable(sheet2, 2, auditTable2);
		assertEquals(5, rownum2);

		HSSFSheet sheet3 = workbook.createSheet();
		AuditTable auditTable3 = new AuditTable();
		auditTable3.setTableTitle("Test");
		auditTable3.setTableType("Revised");
		String[] headerDescs3 = new String[4];
		headerDescs3[0] = "Code";
		headerDescs3[3] = "Section";
		headerDescs3[2] = "New";
		headerDescs3[3] = "Old";
		auditTable3.setHeaderDesc(headerDescs3);
		List<AuditData> auditDataList3 = new ArrayList<AuditData>();
		CCIGenericAttributeAudit audit3 = new CCIGenericAttributeAudit();
		audit3.setCode("H");
		audit3.setReferenceCode("E32");
		audit3.setNewDescription("Test");
		audit3.setOldDescription("Old");
		auditDataList3.add(audit3);
		auditTable3.setAuditData(auditDataList3);
		int rownum3 = fileGenerator.buildAuditReportTable(sheet3, 3, auditTable3);
		assertEquals(6, rownum3);
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
	public void testBuildLineFixedWidth() throws IOException {
		CCIReferenceAttribute attribute = new CCIReferenceAttribute();
		attribute.setCode("E13");
		attribute.setMandatoryIndicator("Y");
		attribute.setGenericAttributes(new ArrayList<CCIGenericAttribute>());

		int count = fileGenerator.buildLineFixedWidth(attribute, writer);
		assertEquals(0, count);

		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("E13");
		attribute1.setMandatoryIndicator("N");
		CCIGenericAttribute attributeGeneric = new CCIGenericAttribute();
		attributeGeneric.setCode("L");
		attributeGeneric.setDescription("TEST");
		List<CCIGenericAttribute> attributes = new ArrayList<CCIGenericAttribute>();
		attributes.add(attributeGeneric);
		attribute1.setGenericAttributes(attributes);

		int count1 = fileGenerator.buildLineFixedWidth(attribute1, writer);
		assertEquals(2, count1);

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
	public void testGenerateFile() throws IOException {
		List<PublicationStatistics> statisticsSummary = new ArrayList<PublicationStatistics>();
		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification("CCI");
		generateTablesModel.setCurrentOpenYear(2016l);
		generateTablesModel.setFileFormat(FileFormat.TAB);

		fileGenerator.generateFile(1l, generateTablesModel, pubDirectory, "Extent", "ENG", "E", statisticsSummary, 0);

		File folder = fileGenerator.prepareFolder("CCI", "Validation", "ENG");
		String fileName = CimsFileUtils.buildAsciiFileName("CCI", "Extent", "Eng",
				String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", null);

		File cciReferenceDesc = new File(folder.getCanonicalPath() + FILE_SEPARATOR + fileName);

		assertEquals(true, cciReferenceDesc.isFile());

		assertEquals(1, statisticsSummary.size());

		PublicationStatistics statistics = statisticsSummary.get(0);
		assertEquals(5, statistics.getCount());
	}

}
