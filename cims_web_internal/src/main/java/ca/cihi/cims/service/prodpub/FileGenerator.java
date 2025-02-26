package ca.cihi.cims.service.prodpub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.AuditTable;
import ca.cihi.cims.model.prodpub.AuditWorkSheet;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAudit;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.EmailService;

/**
 * This is an abstract class for product publication file generation process, it covers all validation related files
 * 
 * @author tyang
 * 
 */
public abstract class FileGenerator {

	protected static final String SUB_FOLDER_ENG = "ENG";
	protected static final String SUB_FOLDER_FRA = "FRA";
	protected static final String SUB_FOLDER_RELEASE = "RELEASE";
	protected static final String SUB_FOLDER_CCI = "CCI";
	protected static final String SUB_FOLDER_ICD = "ICD";
	protected static final String SUB_FOLDER_ICD_CCI = "ICD_CCI";

	protected static final String SUB_FOLDER_BLKDESC = "BlkDesc";
	protected static final String SUB_FOLDER_CODEDESC = "CodeDesc";
	protected static final String SUB_FOLDER_VALIDATION = "Validation";

	protected static final String LANGUAGE_CODE_ENG = "ENG";
	protected static final String LANGUAGE_CODE_FRA = "FRA";

	protected static final int MAX_LENGTH_ICD_CODE = 7;
	protected static final int MAX_LENGTH_CCI_CODE = 10;

	protected static final int MAX_LENGTH_CCI_ATTRIBUTE_REFERENCE_CODE = 3;
	protected static final int MAX_LENGTH_CCI_ATTRIBUTE_GENERIC_CODE = 2;

	protected static final String CODE_VALUE_Y = "Y";
	protected static final String CODE_VALUE_N = "N";

	protected static final String FILE_SEPARATOR = System.getProperty("file.separator");

	protected static final String TAB = "\t";
	protected static final String BLANK = " ";

	protected static final String TABLE_TYPE_REVISED = "Revised";
	protected static final String TABLE_TYPE_NEW = "New";
	protected static final String TABLE_TYPE_REMOVED = "Removed";

	protected List<CodeValidationAudit> revisedCodeValidations = new ArrayList<CodeValidationAudit>();
	protected List<CodeValidationAudit> newCodeValidations = new ArrayList<CodeValidationAudit>();
	protected List<CodeValidationAudit> disabledCodeValidations = new ArrayList<CodeValidationAudit>();

	protected Map<String, Map<String, ValidationRuleSet>> currentValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();
	protected Map<String, Map<String, ValidationRuleSet>> priorValidationSetMap = new TreeMap<String, Map<String, ValidationRuleSet>>();

	protected Map<String, CCIReferenceAttribute> currentReferenceCodeMap = new HashMap<String, CCIReferenceAttribute>();

	protected List<CCIReferenceAudit> extentAuditList = new ArrayList<CCIReferenceAudit>();
	protected List<CCIReferenceAudit> statusAuditList = new ArrayList<CCIReferenceAudit>();
	protected List<CCIReferenceAudit> locationAuditList = new ArrayList<CCIReferenceAudit>();

	private PublicationMapper publicationMapper;
	private ConceptService conceptService;
	protected String pubDirectory;
	private EmailService emailService;

	private GenerateReleaseTablesCriteria generateReleaseTablesCriteria;
	private User currentUser;

	/**
	 * This method populates one validation table information based on the {@link AuditTable} provided
	 * 
	 * @param sheet
	 *            The excel sheet to populate information on
	 * @param rownum
	 *            the starting row number of the validation table
	 * @param auditTable
	 *            the detail data of the validation table
	 * @return the row number for next method
	 */
	protected abstract int buildAuditReportTable(HSSFSheet sheet, int rownum, AuditTable auditTable);

	/**
	 * 
	 * @param currentVersionYear
	 * @param lastVersionYear
	 * @param languageCode
	 * @param currentOpenContextId
	 * @param lastVersionContextId
	 * @return
	 */
	protected abstract List<AuditTable> buildAuditTables(Long currentVersionYear, Long lastVersionYear,
			String languageCode, Long currentOpenContextId, Long lastVersionContextId);

	protected abstract int buildLineFixedWidth(Object source, BufferedWriter bw) throws IOException;

	protected int buildLineTab(ValidationRuleSet codeValidation, BufferedWriter bw) throws IOException {
		StringBuilder lineTab = new StringBuilder();
		int count = 1;
		lineTab.append(codeValidation.getCode());
		lineTab.append(TAB);
		lineTab.append(codeValidation.getValidationRuleText());
		lineTab.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(lineTab.toString());
		return count;
	}

	protected void buildWorksheet(String classificationDesc, String languageCode, String releaseId,
			Long currentOpenContextId, Long lastVersionContextId, Long currentVersionYear, int index)
			throws IOException {
		AuditWorkSheet workSheetData = generateWorkSheetData(languageCode, currentVersionYear, currentOpenContextId,
				lastVersionContextId, classificationDesc, releaseId);
		HSSFWorkbook workbook = null;
		HSSFSheet sheet = null;
		if (index > 0) {
			FileInputStream inputStream = new FileInputStream(workSheetData.getFileName());
			workbook = new HSSFWorkbook(inputStream);
			sheet = workbook.createSheet(workSheetData.getWorksheetName());

			workbook.setSheetOrder(workSheetData.getWorksheetName(), index);
		} else {
			workbook = new HSSFWorkbook();
			sheet = workbook.createSheet(workSheetData.getWorksheetName());
		}
		String titleValue = workSheetData.getTitleValue();

		Integer rownum = 3;
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheet, rownum, titleValue);

		List<AuditTable> auditTables = workSheetData.getAuditTables();
		if (auditTables != null && auditTables.size() > 0) {
			AuditTable firstTable = auditTables.get(0);
			for (int i = 1; i <= firstTable.getHeaderDesc().length; i++) {
				sheet.setColumnWidth(i, 255 * (i == 1 ? 25 : 50));
			}
			rownum = rownum + 3;

			for (AuditTable auditTable : auditTables) {
				rownum = buildAuditReportTable(sheet, rownum, auditTable);
				rownum++;
			}
		}

		FileOutputStream out = new FileOutputStream(workSheetData.getFileName());
		workbook.write(out);
		out.close();
	}

	public void generateAsciiFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String releaseId, boolean isRelease, List<PublicationStatistics> statisticsSummary, User currentUser)
			throws IOException {
		this.setGenerateReleaseTablesCriteria(generateTablesModel);
		this.setCurrentUser(currentUser);
		generateEnglishFile(currentOpenContextId, generateTablesModel, statisticsSummary);
		generateFrenchFile(currentOpenContextId, generateTablesModel, statisticsSummary);
	}

	public abstract void generateAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException;

	protected abstract String generateAuditFileName(String languageCode, Long currentVersionYear);

	protected abstract void generateEnglishFile(Long currentOpenContextId,
			GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
			throws IOException;

	protected abstract void generateFrenchFile(Long currentOpenContextId,
			GenerateReleaseTablesCriteria generateTablesModel, List<PublicationStatistics> statisticsSummary)
			throws IOException;

	private AuditWorkSheet generateWorkSheetData(String languageCode, Long currentVersionYear,
			Long currentOpenContextId, Long lastVersionContextId, String classificationDesc, String releaseId) {
		AuditWorkSheet workSheetData = new AuditWorkSheet();

		workSheetData.setFileName(generateAuditFileName(languageCode, currentVersionYear));
		workSheetData.setWorksheetName(getWorksheetName(languageCode));

		Long lastVersionYear = currentVersionYear - 1;

		workSheetData.setTitleValue(CimsFileUtils.buildAuditReportCommonTitle(languageCode, classificationDesc,
				currentVersionYear, releaseId));

		List<AuditTable> auditTables = buildAuditTables(currentVersionYear, lastVersionYear, languageCode,
				currentOpenContextId, lastVersionContextId);

		workSheetData.setAuditTables(auditTables);

		return workSheetData;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	protected String getDelimitedType(FileFormat fileFormat) {
		return fileFormat == FileFormat.TAB ? "tab" : "fixed";
	}

	protected abstract String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear);

	protected abstract String getDisabledTitleValue(String languageCode, String currentVersion);

	public EmailService getEmailService() {
		return emailService;
	}

	public GenerateReleaseTablesCriteria getGenerateReleaseTablesCriteria() {
		return generateReleaseTablesCriteria;
	}

	protected abstract String[] getNewHeaderDescs(String languageCode, Long currentVersionYear);

	protected abstract String getNewTitleValue(String languageCode, String currentVersion);

	public String getPubDirectory() {
		return pubDirectory;
	}

	public PublicationMapper getPublicationMapper() {
		return publicationMapper;
	}

	protected abstract String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear);

	protected abstract String getRevisionsTitleValue(String languageCode, String currentVersion);

	protected abstract String getWorksheetName(String languageCode);

	protected String padAge(int age) {
		return StringUtils.leftPad(age + "", 3, "0");
	}

	protected File prepareFolder(String subFolderName, String folderName, String languageCode) {
		File pubFolder = new File(pubDirectory);
		File subFolder = CimsFileUtils.createSubFolder(pubFolder, subFolderName);
		File folder = CimsFileUtils.createSubFolder(subFolder, folderName);
		return CimsFileUtils.createSubFolder(folder, languageCode.equals(SUB_FOLDER_ENG) ? SUB_FOLDER_ENG
				: SUB_FOLDER_FRA);
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	public void setGenerateReleaseTablesCriteria(GenerateReleaseTablesCriteria generateReleaseTablesCriteria) {
		this.generateReleaseTablesCriteria = generateReleaseTablesCriteria;
	}

	public void setPubDirectory(String pubDirectory) {
		this.pubDirectory = pubDirectory;
	}

	public void setPublicationMapper(PublicationMapper publicationMapper) {
		this.publicationMapper = publicationMapper;
	}

}
