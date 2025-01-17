package ca.cihi.cims.service.prodpub;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.model.prodpub.AuditTable;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class FileGeneratorTest {

	FileGenerator fileGenerator = null;

	@Mock
	BufferedWriter writer;

	@Value("${cims.publication.classification.tables.dir}")
	String pubDirectory;

	protected final Log LOGGER = LogFactory.getLog(getClass());

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		fileGenerator = new FileGenerator() {

			@Override
			protected int buildAuditReportTable(HSSFSheet sheet, int rownum, AuditTable auditTable) {
				return 0;
			}

			@Override
			protected List<AuditTable> buildAuditTables(Long currentVersionYear, Long lastVersionYear,
					String languageCode, Long currentOpenContextId, Long lastVersionContextId) {
				List<AuditTable> auditTables = new ArrayList<AuditTable>();
				if ("FRA".equals(languageCode)) {
					AuditTable auditTable = new AuditTable();
					auditTable.setHeaderDesc(new String[4]);
					auditTables.add(auditTable);
				}
				return auditTables;
			}

			@Override
			protected int buildLineFixedWidth(Object source, BufferedWriter bw) throws IOException {
				return 0;
			}

			@Override
			public void generateAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
					GenerateReleaseTablesCriteria generateTablesModel) throws IOException {

			}

			@Override
			protected String generateAuditFileName(String languageCode, Long currentVersionYear) {
				return pubDirectory
						+ "CCI"
						+ FILE_SEPARATOR
						+ "Validation"
						+ FILE_SEPARATOR
						+ languageCode
						+ FILE_SEPARATOR
						+ ("ENG".equals(languageCode) ? CimsFileUtils
								.findCCIValidationAuditFileNameEnglish(currentVersionYear.toString()) : CimsFileUtils
								.findCCIValidationAuditFileNameFrench(currentVersionYear.toString()));
			}

			@Override
			protected void generateEnglishFile(Long currentOpenContextId,
					GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
					throws IOException {

			}

			@Override
			protected void generateFrenchFile(Long currentOpenContextId,
					GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
					throws IOException {

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
				return "ENG".equals(languageCode) ? "CCI_Code_Validation" : "CCI_Validation_Code";
			}
		};

		fileGenerator.setPubDirectory(pubDirectory);
	}

	@Test
	public void test() {
		ZipArchiveOutputStream zipOut;
		ZipArchiveInputStream zipIn;
		try {
			zipOut = new ZipArchiveOutputStream(new FileOutputStream("utf8.zip"));
			zipOut.setEncoding("CP437");
			zipOut.setUseLanguageEncodingFlag(true);
			zipOut.putArchiveEntry(new ZipArchiveEntry("Piste de Vérification des Validations Français.txt"));
			zipOut.closeArchiveEntry();
			zipOut.putArchiveEntry(new ZipArchiveEntry("Piste de Vérification Des Blocs Rub Cat.txt"));
			zipOut.closeArchiveEntry();
			zipOut.flush();
			zipOut.close();
			byte[] buffer = new byte[1024];
			zipIn = new ZipArchiveInputStream(new FileInputStream("utf8.zip"), "CP437");
			ZipArchiveEntry ze = zipIn.getNextZipEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(fileName);
				// create all non exists folders
				// else you will hit FileNotFoundException for compressed folder
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zipIn.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();
				ze = zipIn.getNextZipEntry();
			}
			zipIn.close();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

	}

	@Test
	public void testBuildLineTab() throws IOException {
		ValidationRuleSet rule = new ValidationRuleSet();
		rule.setCode("A00");
		rule.setValidationRuleText("1SNN4A000130");
		int count = fileGenerator.buildLineTab(rule, writer);

		assertEquals(1, count);
	}

	@Test
	public void testBuildWorksheet() throws IOException {
		fileGenerator.prepareFolder("CCI", "Validation", "ENG");
		fileGenerator.prepareFolder("CCI", "Validation", "FRA");
		String fileName = pubDirectory + "CCI" + FileGenerator.FILE_SEPARATOR + "Validation"
				+ FileGenerator.FILE_SEPARATOR + "ENG" + FileGenerator.FILE_SEPARATOR
				+ CimsFileUtils.findCCIValidationAuditFileNameEnglish("2016");
		File file = new File(fileName);
		if (file.exists()) {
			file.delete();
		}
		fileGenerator.buildWorksheet("CCI", "ENG", null, 2l, 1l, 2016l, 0);

		FileInputStream inputStream = new FileInputStream(fileName);
		HSSFWorkbook workbook = new HSSFWorkbook(inputStream);

		assertNotNull(workbook);
		assertNotNull(workbook.getSheet("CCI_Code_Validation"));

		String fileName1 = pubDirectory + "CCI" + FileGenerator.FILE_SEPARATOR + "Validation"
				+ FileGenerator.FILE_SEPARATOR + "FRA" + FileGenerator.FILE_SEPARATOR
				+ CimsFileUtils.findCCIValidationAuditFileNameFrench("2016");
		File file1 = new File(fileName1);
		if (!file1.exists()) {
			HSSFWorkbook workbook2 = new HSSFWorkbook();
			HSSFSheet sheet = workbook2.createSheet("Test");
			Row row = sheet.createRow(1);
			Cell cell = row.createCell(1);
			cell.setCellValue("Test");
			FileOutputStream out = new FileOutputStream(fileName1);
			workbook2.write(out);
			out.close();
		} else {
			FileInputStream inputStream1 = new FileInputStream(fileName1);
			HSSFWorkbook workbook3 = new HSSFWorkbook(inputStream1);
			HSSFSheet sheet = workbook3.getSheet("CCI_Validation_Code");
			if (sheet != null) {
				workbook3.removeSheetAt(workbook3.getSheetIndex("CCI_Validation_Code"));
			}
			FileOutputStream out = new FileOutputStream(fileName1);
			workbook3.write(out);
			out.close();
		}
		fileGenerator.buildWorksheet("CCI", "FRA", null, 2l, 1l, 2016l, 1);

		FileInputStream inputStream1 = new FileInputStream(fileName1);
		HSSFWorkbook workbook1 = new HSSFWorkbook(inputStream1);

		assertNotNull(workbook1);
		assertNotNull(workbook1.getSheet("CCI_Validation_Code"));
	}

	@Test
	public void testGetDelimitedType() {
		FileFormat fileFormatTab = FileFormat.TAB;

		String delimetedTypeTab = fileGenerator.getDelimitedType(fileFormatTab);
		assertEquals("tab", delimetedTypeTab);

		FileFormat fileFormatFixedWidth = FileFormat.FIX;

		String delimetedTypeFixed = fileGenerator.getDelimitedType(fileFormatFixedWidth);
		assertEquals("fixed", delimetedTypeFixed);
	}

	@Test
	public void testPrepareFolder() {
		File cciValidationFolderEng = fileGenerator.prepareFolder("CCI", "Validation", "ENG");
		assertNotNull(cciValidationFolderEng);

		assertEquals(true, cciValidationFolderEng.isDirectory());

		File cciValidationFolderFra = fileGenerator.prepareFolder("CCI", "Validation", "FRA");

		assertNotNull(cciValidationFolderFra);

		assertEquals(true, cciValidationFolderFra.isDirectory());
	}
}
