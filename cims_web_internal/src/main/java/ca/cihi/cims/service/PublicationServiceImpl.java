package ca.cihi.cims.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.LookupMapper;
import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.exception.ConcurrentCloseYearException;
import ca.cihi.cims.model.ComponentAndAttributeElementModel;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.CodeDescriptionAudit;
import ca.cihi.cims.model.prodpub.CodeDescriptionPublication;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateFileStatus;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.service.prodpub.CimsFileUtils;
import ca.cihi.cims.service.prodpub.FileGenerator;
import ca.cihi.cims.service.prodpub.FileGeneratorFactory;

public class PublicationServiceImpl implements PublicationService {
	private static final String CCI_CODE_LASTLINE_2ND_COLUMN_EN = "Intervention abandoned before onset";
	private static final String CCI_CODE_LASTLINE_2ND_COLUMN_FR = "Intervention annulée au préalable ";
	private static final String CCI_CODE_LASTLINE_3RD_COLUMN_EN = "Intervention abandoned before onset";
	private static final String CCI_CODE_LASTLINE_3RD_COLUMN_FR = "Intervention annulée au préalable ";

	// private static final int TWO_HOURS = 2;
	private static final String CCI_CODE_LASTLINE_CANCELLED = "CANCELLED";
	private static final String CCI_STATISTICS_FILE_NAME = "CCI Table Statistics Summary.xls";

	public static final String CIMS_RELEASE_NOTIFICATION_TEMPLATE = "cims.notification.release.template";
	private static final String FILE_EXT_ZIP = ".zip";
	public static final String FISCAL_YEAR = "FISCAL_YEAR";
	private static final String ICD_STATISTICS_FILE_NAME = "ICD Table Statistics Summary.xls";
	private static final String LANGUAGE_CODE_ENG = "ENG";

	private static final String LANGUAGE_CODE_FRA = "FRA";
	private static final Log LOGGER = LogFactory.getLog(PublicationServiceImpl.class);
	private static final int MAX_LENGTH_CCI_BLOCK = 10;
	private static final int MAX_LENGTH_CCI_CODE = 10;

	private static final int MAX_LENGTH_CCI_RUBRIC = 10;
	private static final int MAX_LENGTH_CCI_SHORTTITLE = 60;
	private static final int MAX_LENGTH_ICD_BLOCK = 7;
	private static final int MAX_LENGTH_ICD_CAT1 = 7;
	private static final int MAX_LENGTH_ICD_CODE = 7;
	private static final int MAX_LENGTH_ICD_SHORTTITLE = 40;
	private static final int MAX_LENGTH_LONGTITLE = 255;
	public static final String NEXT_RELEASE_TYPE = "NEXT_RELEASE_TYPE";

	private static final HashMap<String, String> processingFileNameMap = new HashMap<String, String>();
	public static final String RELEASE_DATE = "RELEASE_DATE";
	public static final String RELEASE_TYPE = "RELEASE_TYPE";
	private static final String SUB_FOLDER_BlkDesc = "BlkDesc";
	private static final String SUB_FOLDER_CCI = "CCI";

	private static final String SUB_FOLDER_CodeDesc = "CodeDesc";
	private static final String SUB_FOLDER_ENG = "ENG";

	private static final String SUB_FOLDER_FRA = "FRA";
	private static final String SUB_FOLDER_ICD = "ICD";
	private static final String SUB_FOLDER_ICD_CCI = "ICD_CCI";
	private static final String SUB_FOLDER_RELEASE = "RELEASE";
	private static final String SUB_FOLDER_SNAPSHOT = "SNAPSHOT";
	private static final String SUB_FOLDER_Validation = "Validation";
	private static final int TWO_HOURS = 2 * 60 * 60 * 1000;
	public static final String TYPE_BLOCK = "Blk";

	public static final String TYPE_CATEGORY1 = "Cat1";

	@Autowired
	private ASOTService asotService;

	private ChangeRequestService changeRequestService;

	@Autowired
	private ContextProvider contextProvider;

	private ContextService contextService;

	private String currentProcessingYear;

	private EmailService emailService;

	private FileGeneratorFactory fileGeneratorFactory;

	private LookupMapper lookupMapper;

	private MessageSource messageSource;

	private NotificationService notificationService;

	private String pubDirectory;

	private PublicationMapper publicationMapper;

	private VelocityEngine velocityEngine;

	@Override
	public boolean areBothClassificationFixedWidthFilesGenerated(GenerateReleaseTablesCriteria releaseTablesModel) {
		boolean generated = false;
		Long currentOpenYear = releaseTablesModel.getCurrentOpenYear();

		ContextIdentifier currentCCIOpenContext = lookupMapper.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(currentOpenYear));
		Long currentCCIOpenContextId = currentCCIOpenContext.getContextId();
		ContextIdentifier currentICDOpenContext = lookupMapper
				.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(currentOpenYear));
		Long currentICDOpenContextId = currentICDOpenContext.getContextId();

		PublicationSnapShot latestCCIFixedWidthSnapShot = publicationMapper
				.findLatestSuccessFixedWidthSnapShotByContextId(currentCCIOpenContextId);

		PublicationSnapShot latestICDFixedWidthSnapShot = publicationMapper
				.findLatestSuccessFixedWidthSnapShotByContextId(currentICDOpenContextId);
		if ((latestCCIFixedWidthSnapShot != null) && (latestICDFixedWidthSnapShot != null)) {
			generated = true;
		}
		return generated;
	}

	@Override
	public boolean areBothClassificationTabFilesGenerated(GenerateReleaseTablesCriteria releaseTablesModel) {
		boolean generated = false;
		Long currentOpenYear = releaseTablesModel.getCurrentOpenYear();

		ContextIdentifier currentCCIOpenContext = lookupMapper.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(currentOpenYear));
		Long currentCCIOpenContextId = currentCCIOpenContext.getContextId();
		ContextIdentifier currentICDOpenContext = lookupMapper
				.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(currentOpenYear));
		Long currentICDOpenContextId = currentICDOpenContext.getContextId();

		PublicationSnapShot latestCCITabSnapShot = publicationMapper
				.findLatestSuccessTabSnapShotByContextId(currentCCIOpenContextId);

		PublicationSnapShot latestICDTabSnapShot = publicationMapper
				.findLatestSuccessTabSnapShotByContextId(currentICDOpenContextId);
		if ((latestCCITabSnapShot != null) && (latestICDTabSnapShot != null)) {
			generated = true;
		}
		return generated;
	}

	// private final ThreadLocalGenerateFileName generateFileName;

	// private String currentProcessingClassification;

	/*
	 * filename will be like ICD_Code_Eng_Desc_10CA<year>_<MMDDYYYY>_<releaseNumber>_<format>
	 *
	 * baseClassification -- CCI or ICD codeType -- Blk or Rubic or Cat1 versionYear -- 2016
	 *
	 * delimited type -- tab or fixed
	 */

	/*
	 * // V0_1 ICD10CA CCI 2015 English Block, Rubric, Category Description Audit Trail.xls private String
	 * buildAuditFileName(String releaseDesc, String classificationDesc, String fiscalYear, String languageDesc, String
	 * auditDesc) { StringBuilder sb_auditFileName = new StringBuilder(); if (releaseDesc != null) {
	 * sb_auditFileName.append(releaseDesc).append(" "); } sb_auditFileName.append(classificationDesc).append(" "
	 * ).append(fiscalYear).append(" "); sb_auditFileName.append(languageDesc).append(" ");
	 * sb_auditFileName.append(auditDesc).append(".xls");
	 *
	 * return sb_auditFileName.toString(); }
	 */

	// for snapshot audit
	private void buildAuditReportCCIBlockRubricDesc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		String auditFileNameEng = CimsFileUtils
				.findCCIBlockRubricAuditFileNameEnglish(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		String auditFileNameFra = CimsFileUtils
				.findCCIBlockRubricAuditFileNameFrench(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		File pubFolder = new File(pubDirectory);
		File subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
		File cciBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_BlkDesc);
		File cciBlkDescEngFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_ENG);
		File cciBlkDescFraFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_FRA);

		File cciAuditEng = new File(
				cciBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameEng);

		File cciAuditFra = new File(
				cciBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameFra);

		HSSFWorkbook workbookEng = new HSSFWorkbook();
		// build icd blk
		buildAuditReportSheetCCIBlkDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				cciAuditEng, workbookEng, LANGUAGE_CODE_ENG, null);
		// build icd cat1
		buildAuditReportSheetCCIRubricDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				cciAuditEng, workbookEng, LANGUAGE_CODE_ENG, null);
		// write out and close english file

		FileOutputStream outEng = new FileOutputStream(cciAuditEng);
		workbookEng.write(outEng);
		outEng.close();
		HSSFWorkbook workbookFra = new HSSFWorkbook();
		buildAuditReportSheetCCIBlkDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				cciAuditFra, workbookFra, LANGUAGE_CODE_FRA, null);
		buildAuditReportSheetCCIRubricDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				cciAuditEng, workbookFra, LANGUAGE_CODE_FRA, null);
		// write out and close french file

		FileOutputStream outFra = new FileOutputStream(cciAuditFra);
		workbookFra.write(outFra);
		outFra.close();

	}

	private void buildAuditReportCCICodeDesc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		// CCI 2015 English Code Description Audit Trail.xls
		String auditFileNameEng = CimsFileUtils
				.findCCICodeAuditFileNameEnglish(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		String auditFileNameFra = CimsFileUtils
				.findCCICodeAuditFileNameFrench(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		File pubFolder = new File(pubDirectory);
		File subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
		File cciBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_CodeDesc);
		File cciBlkDescEngFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_ENG);
		File cciBlkDescFraFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_FRA);

		File cciAuditEng = new File(
				cciBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameEng);

		File cciAuditFra = new File(
				cciBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameFra);

		HSSFWorkbook workbookEng = new HSSFWorkbook();
		// build cci code
		buildAuditReportSheetCCICodeDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				cciAuditEng, workbookEng, LANGUAGE_CODE_ENG, null);

		// write out and close english file

		FileOutputStream outEng = new FileOutputStream(cciAuditEng);
		workbookEng.write(outEng);
		outEng.close();
		LOGGER.debug("Excel audit english file written successfully");

		HSSFWorkbook workbookFra = new HSSFWorkbook();
		buildAuditReportSheetCCICodeDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				cciAuditFra, workbookFra, LANGUAGE_CODE_FRA, null);

		// write out and close french file
		FileOutputStream outFra = new FileOutputStream(cciAuditFra);
		workbookFra.write(outFra);
		outFra.close();
		LOGGER.debug("Excel audit french file  written successfully");

	}

	private int buildAuditReportDescLongNewOrDisabled(HSSFSheet sheet, int rownum,
			List<CodeDescriptionPublication> cdps, String titleValue, String[] tableHeaderDescs, boolean isBlock) {
		// Block Long English Description Disabled
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheet, rownum, titleValue);
		// sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));

		rownum++;

		rownum = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, rownum, tableHeaderDescs);
		rownum++;
		if (cdps.size() > 0) {
			for (CodeDescriptionPublication cdp : cdps) {
				Row rowNewOrDisabled = sheet.createRow(rownum);
				Cell tblNewOrDisabledColumnCell1 = rowNewOrDisabled.createCell(1, Cell.CELL_TYPE_STRING);
				String codeInFile = null;
				if (isBlock) {
					codeInFile = removeCharatersFromCodeKeepDash(cdp.getCode());
				} else {
					codeInFile = removeCharatersFromCode(cdp.getCode());
				}

				tblNewOrDisabledColumnCell1.setCellValue(codeInFile);

				Cell tblNewOrDisabledColumnCell2 = rowNewOrDisabled.createCell(2, Cell.CELL_TYPE_STRING);
				tblNewOrDisabledColumnCell2.setCellValue(cdp.getLongTitle());
				rownum++;
			}
		}
		return rownum;
	}

	private int buildAuditReportDescLongOrShortRevisions(HSSFSheet sheet, int rownum,
			List<CodeDescriptionAudit> revisedDescs, String titleValue, String[] tableHeaderDescs, boolean isBlock) {

		rownum = CimsFileUtils.buildAuditReportTitleLine(sheet, rownum, titleValue);
		// sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
		rownum++;

		rownum = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, rownum, tableHeaderDescs);
		rownum++;
		if (revisedDescs.size() > 0) {
			for (CodeDescriptionAudit revisedDesc : revisedDescs) {
				Row rowRevisions = sheet.createRow(rownum);
				Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
				String codeInFile = null;
				if (isBlock) {
					codeInFile = removeCharatersFromCodeKeepDash(revisedDesc.getCode());
				} else {
					codeInFile = removeCharatersFromCode(revisedDesc.getCode());
				}

				tblRevisionsColumnCell1.setCellValue(codeInFile);

				Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell2.setCellValue(revisedDesc.getCurrentDesc());
				Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell3.setCellValue(revisedDesc.getPreviousDesc());
				rownum++;
			}
		}
		return rownum;
	}

	private int buildAuditReportDescShortNewOrDisabled(HSSFSheet sheet, int rownum,
			List<CodeDescriptionPublication> cdps, String titleValue, String[] tableHeaderDescs, boolean isBlock) {
		// Short Description Disabled
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheet, rownum, titleValue);
		// sheet.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
		rownum++;

		rownum = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, rownum, tableHeaderDescs);
		rownum++;
		if (cdps.size() > 0) {
			for (CodeDescriptionPublication cdp : cdps) {
				Row rowNewOrDisabled = sheet.createRow(rownum);
				Cell tblNewOrDisabledColumnCell1 = rowNewOrDisabled.createCell(1, Cell.CELL_TYPE_STRING);
				String codeInFile = null;
				if (isBlock) {
					codeInFile = removeCharatersFromCodeKeepDash(cdp.getCode());
				} else {
					codeInFile = removeCharatersFromCode(cdp.getCode());
				}

				tblNewOrDisabledColumnCell1.setCellValue(codeInFile);
				Cell tblNewOrDisabledColumnCell2 = rowNewOrDisabled.createCell(2, Cell.CELL_TYPE_STRING);
				tblNewOrDisabledColumnCell2.setCellValue(cdp.getShortTitle());
				rownum++;
			}
		}
		return rownum;
	}

	// ICD10CA 2015 English Block, Rubric, Category Description Audit Trail.xls
	// V0_1 CIM-10-CA CCI 2015 Piste de Vérification Desc Blocs Rub Cat.
	private void buildAuditReportICDBlockCategoryDesc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		String auditFileNameEng = CimsFileUtils
				.findICDBlockCategoryAuditFileNameEnglish(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		String auditFileNameFra = CimsFileUtils
				.findICDBlockCategoryAuditFileNameFrench(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		File pubFolder = new File(pubDirectory);
		File subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
		File icdBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_BlkDesc);
		File icdBlkDescEngFolder = CimsFileUtils.createSubFolder(icdBlkDescFolder, SUB_FOLDER_ENG);
		File icdBlkDescFraFolder = CimsFileUtils.createSubFolder(icdBlkDescFolder, SUB_FOLDER_FRA);

		File icdAuditEng = new File(
				icdBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameEng);

		File icdAuditFra = new File(
				icdBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameFra);

		HSSFWorkbook workbookEng = new HSSFWorkbook();
		// build icd blk
		buildAuditReportSheetICDBlkDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				icdAuditEng, workbookEng, LANGUAGE_CODE_ENG, null);
		// build icd cat1
		buildAuditReportSheetICDCat1Desc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				icdAuditEng, workbookEng, LANGUAGE_CODE_ENG, null);

		// write out and close english file

		FileOutputStream outEng = new FileOutputStream(icdAuditEng);
		workbookEng.write(outEng);
		outEng.close();
		LOGGER.debug("Excel audit english file written successfully");

		HSSFWorkbook workbookFra = new HSSFWorkbook();
		buildAuditReportSheetICDBlkDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				icdAuditFra, workbookFra, LANGUAGE_CODE_FRA, null);
		buildAuditReportSheetICDCat1Desc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				icdAuditEng, workbookFra, LANGUAGE_CODE_FRA, null);
		// write out and close french file

		FileOutputStream outFra = new FileOutputStream(icdAuditFra);
		workbookFra.write(outFra);
		outFra.close();
		LOGGER.debug("Excel audit french file  written successfully");

	}

	private void buildAuditReportICDCodeDesc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		// CCI 2015 English Code Description Audit Trail.xls
		String auditFileNameEng = CimsFileUtils
				.findICDCodeAuditFileNameEnglish(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		String auditFileNameFra = CimsFileUtils
				.findICDCodeAuditFileNameFrench(String.valueOf(generateTablesModel.getCurrentOpenYear()));

		File pubFolder = new File(pubDirectory);
		File subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
		File icdCodeDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_CodeDesc);
		File icdCodeDescEngFolder = CimsFileUtils.createSubFolder(icdCodeDescFolder, SUB_FOLDER_ENG);
		File icdCodeDescFraFolder = CimsFileUtils.createSubFolder(icdCodeDescFolder, SUB_FOLDER_FRA);

		File icdAuditEng = new File(
				icdCodeDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameEng);

		File icdAuditFra = new File(
				icdCodeDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + auditFileNameFra);

		HSSFWorkbook workbookEng = new HSSFWorkbook();
		// build cci code
		buildAuditReportSheetICDCodeDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				icdAuditEng, workbookEng, LANGUAGE_CODE_ENG, null);

		// write out and close english file

		FileOutputStream outEng = new FileOutputStream(icdAuditEng);
		workbookEng.write(outEng);
		outEng.close();
		LOGGER.debug("Excel audit english file written successfully");

		HSSFWorkbook workbookFra = new HSSFWorkbook();
		buildAuditReportSheetICDCodeDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel,
				icdAuditFra, workbookFra, LANGUAGE_CODE_FRA, null);

		// write out and close french file

		FileOutputStream outFra = new FileOutputStream(icdAuditFra);
		workbookFra.write(outFra);
		outFra.close();
		LOGGER.debug("Excel audit french file  written successfully");

	}

	private void buildAuditReportSheetCCIBlkDesc(Long currentCCIOpenContextId, Long lastVersionCCIContextId,
			GenerateReleaseTablesCriteria generateTablesModel, File auditFile, HSSFWorkbook workbook,
			String languageCode, String releaseId) {
		// put two list into two maps, build revised list, removed list and new list
		// keep current and last
		List<CodeDescriptionAudit> revisedBlkLongDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionAudit> revisedBlkShortDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionPublication> newBlks = new ArrayList<CodeDescriptionPublication>();
		List<CodeDescriptionPublication> removedBlks = new ArrayList<CodeDescriptionPublication>();

		Long cciClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Code");
		Long cciClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "ShortTitle");
		Long cciClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "LongTitle");
		Long cciClassIdSection = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Section");
		Long cciClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Block");
		Long cciClassIdGroup = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Group");

		List<CodeDescriptionPublication> currentBlkDescs = publicationMapper.findCCIBlkDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdSection, cciClassIdBlock,
				cciClassIdGroup, languageCode);

		List<CodeDescriptionPublication> lastBlkDescs = publicationMapper.findCCIBlkDesc(lastVersionCCIContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdSection, cciClassIdBlock,
				cciClassIdGroup, languageCode);

		generateRevisedNewRemovedDescsFromCurrentAndLastVersion(revisedBlkLongDescs, revisedBlkShortDescs, newBlks,
				removedBlks, currentBlkDescs, lastBlkDescs);

		// build excel file
		HSSFSheet sheetBlk = null;
		String titleValue = null;
		Long currentVersionYear = generateTablesModel.getCurrentOpenYear();
		Long lastVersionYear = currentVersionYear - 1;

		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {
			sheetBlk = workbook.createSheet("CCI_Blk_Eng_Desc");
		} else {
			sheetBlk = workbook.createSheet("CCI_Blk_Fra_Desc");
		}

		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, "CCI", currentVersionYear, releaseId);
		Integer rownum = 3;
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheetBlk, rownum, titleValue);
		sheetBlk.setColumnWidth(1, 255 * 25);
		sheetBlk.setColumnWidth(2, 255 * 50);
		sheetBlk.setColumnWidth(3, 255 * 50);
		rownum = rownum + 3;
		// BlockLongEnglishDescriptionRevisions

		String cciCurrentVersion = "CCI" + currentVersionYear;
		String cciLastVersion = "CCI" + lastVersionYear;

		String revisionsLongTitleValue = cciCurrentVersion + " Block Long English Description Revisions";
		String revisionsShortTitleValue = cciCurrentVersion + " Block Short English Description Revisions";
		String newLongTitleValue = cciCurrentVersion + " Block Long English Description New";
		String disabledLongTitleValue = cciCurrentVersion + " Block Long English Description Disabled";
		String newShortTitleValue = cciCurrentVersion + " Block Short English Description New";
		String disabledShortTitleValue = cciCurrentVersion + " Block Short English Description Disabled";
		String[] revisionsTableHeaderDescs = new String[3];
		revisionsTableHeaderDescs[0] = "Block";
		revisionsTableHeaderDescs[1] = "English Long Description New " + cciCurrentVersion;
		revisionsTableHeaderDescs[2] = "English Long Description Old " + cciLastVersion;
		String[] newTableHeaderDescs = new String[2];
		newTableHeaderDescs[0] = "Block";
		newTableHeaderDescs[1] = "English Long Description New " + cciCurrentVersion;
		String[] disabledTableHeaderDescs = new String[2];
		disabledTableHeaderDescs[0] = "Block";
		disabledTableHeaderDescs[1] = "English Long Description Disabled";
		String[] revisionsShortTableHeaderDescs = new String[3];
		revisionsShortTableHeaderDescs[0] = "Block";
		revisionsShortTableHeaderDescs[1] = "English Short Description New " + cciCurrentVersion;
		revisionsShortTableHeaderDescs[2] = "English Short Description Old " + cciLastVersion;
		String[] newShortTableHeaderDescs = new String[2];
		newShortTableHeaderDescs[0] = "Block";
		newShortTableHeaderDescs[1] = "English Short Description New " + cciCurrentVersion;
		String[] disabledShortTableHeaderDescs = new String[2];
		disabledShortTableHeaderDescs[0] = "Block";
		disabledShortTableHeaderDescs[1] = "English Short Description Disabled";
		if (LANGUAGE_CODE_FRA.equalsIgnoreCase(languageCode)) {
			revisionsLongTitleValue = "Descriptions détaillées révisées des groupes de la " + cciCurrentVersion;
			revisionsShortTitleValue = "Descriptions courtes révisées des groupes de la " + cciCurrentVersion;
			newLongTitleValue = "Descriptions détaillées des nouveaux groupes de la " + cciCurrentVersion;
			newShortTitleValue = "Descriptions courtes des nouveaux groupes de la " + cciCurrentVersion;
			disabledLongTitleValue = "Descriptions détaillées des groupes désactivées de la " + cciCurrentVersion;
			disabledShortTitleValue = "Descriptions courtes des groupes désactivées de la " + cciCurrentVersion;
			revisionsTableHeaderDescs[0] = "Bloc";
			revisionsTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			revisionsTableHeaderDescs[2] = "Desc détaillée " + lastVersionYear;
			newTableHeaderDescs[0] = "Bloc";
			newTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			disabledTableHeaderDescs[0] = "Bloc";
			disabledTableHeaderDescs[1] = "Desc détaillée " + lastVersionYear;
			revisionsShortTableHeaderDescs[0] = "Bloc";
			revisionsShortTableHeaderDescs[1] = "Desc courte " + cciCurrentVersion;
			revisionsShortTableHeaderDescs[2] = "Desc courte " + cciLastVersion;
			newShortTableHeaderDescs[0] = "Bloc";
			newShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			disabledShortTableHeaderDescs[0] = "Bloc";
			disabledShortTableHeaderDescs[1] = "Desc courte " + lastVersionYear;

		}
		boolean isBlock = true;
		// long revise
		rownum = buildAuditReportDescLongOrShortRevisions(sheetBlk, rownum, revisedBlkLongDescs,
				revisionsLongTitleValue, revisionsTableHeaderDescs, isBlock);
		rownum++;
		// CCI2015 Block Long English Description New
		rownum = buildAuditReportDescLongNewOrDisabled(sheetBlk, rownum, newBlks, newLongTitleValue,
				newTableHeaderDescs, isBlock);
		rownum++;
		// CCI2015 Block Long English Description Disabled
		rownum = buildAuditReportDescLongNewOrDisabled(sheetBlk, rownum, removedBlks, disabledLongTitleValue,
				disabledTableHeaderDescs, isBlock);
		rownum++;
		// BlockShortEnglishDescriptionRevisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetBlk, rownum, revisedBlkShortDescs,
				revisionsShortTitleValue, revisionsShortTableHeaderDescs, isBlock);
		rownum++;
		// BlockShortEnglishDescription new
		rownum = buildAuditReportDescShortNewOrDisabled(sheetBlk, rownum, newBlks, newShortTitleValue,
				newShortTableHeaderDescs, isBlock);

		rownum++;
		// rownum = buildAuditReportCCIBlkDescShortDisabled(sheetBlk, rownum, removedBlks, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetBlk, rownum, removedBlks, disabledShortTitleValue,
				disabledShortTableHeaderDescs, isBlock);

	}

	private void buildAuditReportSheetCCICodeDesc(Long currentCCIOpenContextId, Long lastVersionCCIContextId,
			GenerateReleaseTablesCriteria generateTablesModel, File auditFile, HSSFWorkbook workbook,
			String languageCode, String releaseId) {
		// put two list into two maps, build revised list, removed list and new list
		// keep current and last
		List<CodeDescriptionAudit> revisedCodeLongDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionAudit> revisedCodeShortDescs = new ArrayList<CodeDescriptionAudit>();

		List<CodeDescriptionPublication> newCodes = new ArrayList<CodeDescriptionPublication>();
		List<CodeDescriptionPublication> removedCodes = new ArrayList<CodeDescriptionPublication>();

		Long cciClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Code");
		Long cciClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "ShortTitle");
		Long cciClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "LongTitle");
		Long cciClassIdCCICode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "CCICODE");
		List<CodeDescriptionPublication> currentCodeDescs = publicationMapper.findCCICodeDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdCCICode, languageCode);
		List<CodeDescriptionPublication> lastCodeDescs = publicationMapper.findCCICodeDesc(lastVersionCCIContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdCCICode, languageCode);
		generateRevisedNewRemovedDescsFromCurrentAndLastVersion(revisedCodeLongDescs, revisedCodeShortDescs, newCodes,
				removedCodes, currentCodeDescs, lastCodeDescs);

		// build excel file
		HSSFSheet sheetCode = null;
		String titleValue = null;
		Long currentVersionYear = generateTablesModel.getCurrentOpenYear();
		Long lastVersionYear = currentVersionYear - 1;
		String cciCurrentVersion = "CCI" + currentVersionYear;
		String cciLastVersion = "CCI" + lastVersionYear;
		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {
			sheetCode = workbook.createSheet("CCI_Code_Eng_Desc");
		} else {
			sheetCode = workbook.createSheet("CCI_Code_Fra_Desc");
		}
		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, "CCI", currentVersionYear, releaseId);
		Integer rownum = 3;

		rownum = CimsFileUtils.buildAuditReportTitleLine(sheetCode, rownum, titleValue);
		// sheetCode.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
		sheetCode.setColumnWidth(1, 255 * 25);
		sheetCode.setColumnWidth(2, 255 * 50);
		sheetCode.setColumnWidth(3, 255 * 50);

		rownum = rownum + 3;
		// Code LongEnglishDescriptionRevisions
		String revisionsLongTitleValue = cciCurrentVersion + " Code Long English Description Revisions";
		String revisionsShortTitleValue = cciCurrentVersion + " Code Short English Description Revisions";
		String newLongTitleValue = cciCurrentVersion + " Code Long English Description New";
		String disabledLongTitleValue = cciCurrentVersion + " Code Long English Description Disabled";
		String newShortTitleValue = cciCurrentVersion + " Code Short English Description New";
		String disabledShortTitleValue = cciCurrentVersion + " Code Short English Description Disabled";
		String[] revisionsTableHeaderDescs = new String[3];
		revisionsTableHeaderDescs[0] = "Code";
		revisionsTableHeaderDescs[1] = "English Long Description New " + cciCurrentVersion;
		revisionsTableHeaderDescs[2] = "English Long Description Old " + cciLastVersion;
		String[] newTableHeaderDescs = new String[2];
		newTableHeaderDescs[0] = "Code";
		newTableHeaderDescs[1] = "English Long Description New " + cciCurrentVersion;
		String[] disabledTableHeaderDescs = new String[2];
		disabledTableHeaderDescs[0] = "Code";
		disabledTableHeaderDescs[1] = "English Long Description Disabled";
		String[] revisionsShortTableHeaderDescs = new String[3];
		revisionsShortTableHeaderDescs[0] = "Code";
		revisionsShortTableHeaderDescs[1] = "English Short Description New " + cciCurrentVersion;
		revisionsShortTableHeaderDescs[2] = "English Short Description Old " + cciLastVersion;
		String[] newShortTableHeaderDescs = new String[2];
		newShortTableHeaderDescs[0] = "Code";
		newShortTableHeaderDescs[1] = "English Short Description New " + cciCurrentVersion;
		String[] disabledShortTableHeaderDescs = new String[2];
		disabledShortTableHeaderDescs[0] = "Code";
		disabledShortTableHeaderDescs[1] = "English Short Description Disabled";

		if (LANGUAGE_CODE_FRA.equalsIgnoreCase(languageCode)) {
			revisionsLongTitleValue = "Descriptions détaillées révisées des codes de la " + cciCurrentVersion;
			revisionsShortTitleValue = "Descriptions courtes révisées des codes de la " + cciCurrentVersion;
			newLongTitleValue = "Descriptions détaillées des nouveaux codes de la " + cciCurrentVersion;
			newShortTitleValue = "Descriptions courtes des nouveaux codes de la " + cciCurrentVersion;
			disabledLongTitleValue = "Descriptions détaillées des codes désactivées de la " + cciCurrentVersion;
			disabledShortTitleValue = "Descriptions courtes des codes désactivées de la " + cciCurrentVersion;
			revisionsTableHeaderDescs[0] = "Code";
			revisionsTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			revisionsTableHeaderDescs[2] = "Desc détaillée " + lastVersionYear;
			newTableHeaderDescs[0] = "Code";
			newTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			disabledTableHeaderDescs[0] = "Code";
			disabledTableHeaderDescs[1] = "Desc détaillée " + lastVersionYear;
			revisionsShortTableHeaderDescs[0] = "Code";
			revisionsShortTableHeaderDescs[1] = "Desc courte " + cciCurrentVersion;
			revisionsShortTableHeaderDescs[2] = "Desc courte " + cciLastVersion;
			newShortTableHeaderDescs[0] = "Code";
			newShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			disabledShortTableHeaderDescs[0] = "Code";
			disabledShortTableHeaderDescs[1] = "Desc courte " + lastVersionYear;
		}
		boolean isBlock = false;
		// long revisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetCode, rownum, revisedCodeLongDescs,
				revisionsLongTitleValue, revisionsTableHeaderDescs, isBlock);

		rownum++;
		// CCI2015 Code Long English Description New
		rownum = buildAuditReportDescLongNewOrDisabled(sheetCode, rownum, newCodes, newLongTitleValue,
				newTableHeaderDescs, isBlock);
		rownum++;
		// CCI2015 Code Long English Description Disabled
		rownum = buildAuditReportDescLongNewOrDisabled(sheetCode, rownum, removedCodes, disabledLongTitleValue,
				disabledTableHeaderDescs, isBlock);
		rownum++;

		// Code ShortEnglishDescriptionRevisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetCode, rownum, revisedCodeShortDescs,
				revisionsShortTitleValue, revisionsShortTableHeaderDescs, isBlock);

		rownum++;
		// code ShortEnglishDescription new
		// rownum = buildAuditReportCCIRubricDescShortNew(sheetRubric, rownum, newRubrics, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCode, rownum, newCodes, newShortTitleValue,
				newShortTableHeaderDescs, isBlock);

		rownum++;
		// rownum = buildAuditReportCCIRubricDescShortDisabled(sheetRubric, rownum, removedRubrics, languageCode);
		// short disabled
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCode, rownum, removedCodes, disabledShortTitleValue,
				disabledShortTableHeaderDescs, isBlock);

	}

	private void buildAuditReportSheetCCIRubricDesc(Long currentCCIOpenContextId, Long lastVersionCCIContextId,
			GenerateReleaseTablesCriteria generateTablesModel, File auditFile, HSSFWorkbook workbook,
			String languageCode, String releaseId) {
		// put two list into two maps, build revised list, removed list and new list
		// keep current and last
		List<CodeDescriptionAudit> revisedRubricLongDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionAudit> revisedRubricShortDescs = new ArrayList<CodeDescriptionAudit>();

		List<CodeDescriptionPublication> newRubrics = new ArrayList<CodeDescriptionPublication>();
		List<CodeDescriptionPublication> removedRubrics = new ArrayList<CodeDescriptionPublication>();

		Long cciClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Code");
		Long cciClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "ShortTitle");
		Long cciClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "LongTitle");
		Long cciClassIdRubric = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Rubric");
		// Long cciClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Block");
		List<CodeDescriptionPublication> currentRubricDescs = publicationMapper.findCCIRubricDesc(
				currentCCIOpenContextId, cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdRubric,
				languageCode);

		List<CodeDescriptionPublication> lastRubricDescs = publicationMapper.findCCIRubricDesc(lastVersionCCIContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdRubric, languageCode);

		generateRevisedNewRemovedDescsFromCurrentAndLastVersion(revisedRubricLongDescs, revisedRubricShortDescs,
				newRubrics, removedRubrics, currentRubricDescs, lastRubricDescs);
		// build excel file
		HSSFSheet sheetRubric = null;
		String titleValue = null;
		Long currentVersionYear = generateTablesModel.getCurrentOpenYear();
		Long lastVersionYear = currentVersionYear - 1;
		String cciCurrentVersion = "CCI" + currentVersionYear;
		String cciLastVersion = "CCI" + lastVersionYear;

		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {
			sheetRubric = workbook.createSheet("CCI_Rubric_Eng_Desc");
		} else {
			sheetRubric = workbook.createSheet("CCI_Rubric_Fra_Desc");
		}
		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, "CCI", currentVersionYear, releaseId);

		Integer rownum = 3;
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheetRubric, rownum, titleValue);
		sheetRubric.setColumnWidth(1, 255 * 25);
		sheetRubric.setColumnWidth(2, 255 * 50);
		sheetRubric.setColumnWidth(3, 255 * 50);
		rownum = rownum + 3;
		// RubricLongEnglishDescriptionRevisions
		String revisionsLongTitleValue = cciCurrentVersion + " Rubric Long English Description Revisions";
		String revisionsShortTitleValue = cciCurrentVersion + " Rubric Short English Description Revisions";
		String newLongTitleValue = cciCurrentVersion + " Rubric Long English Description New";
		String disabledLongTitleValue = cciCurrentVersion + " Rubric Long English Description Disabled";
		String newShortTitleValue = cciCurrentVersion + " Rubric Short English Description New";
		String disabledShortTitleValue = cciCurrentVersion + " Rubric Short English Description Disabled";
		String[] revisionsTableHeaderDescs = new String[3];
		revisionsTableHeaderDescs[0] = "Rubric";
		revisionsTableHeaderDescs[1] = "English Long Description New " + cciCurrentVersion;
		revisionsTableHeaderDescs[2] = "English Long Description Old " + cciLastVersion;
		String[] newTableHeaderDescs = new String[2];
		newTableHeaderDescs[0] = "Rubric";
		newTableHeaderDescs[1] = "English Long Description New " + cciCurrentVersion;
		String[] disabledTableHeaderDescs = new String[2];
		disabledTableHeaderDescs[0] = "Rubric";
		disabledTableHeaderDescs[1] = "English Long Description Disabled";
		String[] revisionsShortTableHeaderDescs = new String[3];
		revisionsShortTableHeaderDescs[0] = "Rubric";
		revisionsShortTableHeaderDescs[1] = "English Short Description New " + cciCurrentVersion;
		revisionsShortTableHeaderDescs[2] = "English Short Description Old " + cciLastVersion;
		String[] newShortTableHeaderDescs = new String[2];
		newShortTableHeaderDescs[0] = "Rubric";
		newShortTableHeaderDescs[1] = "English Short Description New " + cciCurrentVersion;
		String[] disabledShortTableHeaderDescs = new String[2];
		disabledShortTableHeaderDescs[0] = "Rubric";
		disabledShortTableHeaderDescs[1] = "English Short Description Disabled";

		if (LANGUAGE_CODE_FRA.equalsIgnoreCase(languageCode)) {
			revisionsLongTitleValue = "Descriptions détaillées révisées des rubriques de la " + cciCurrentVersion;
			revisionsShortTitleValue = "Descriptions courtes révisées des rubriques de la " + cciCurrentVersion;
			newLongTitleValue = "Descriptions détaillées des nouvelles rubriques de la " + cciCurrentVersion;
			newShortTitleValue = "Descriptions courtes des nouvelles rubriques de la " + cciCurrentVersion;
			disabledLongTitleValue = "Descriptions détaillées des rubriques désactivées de la " + cciCurrentVersion;
			disabledShortTitleValue = "Descriptions courtes des rubriques désactivées de la " + cciCurrentVersion;
			revisionsTableHeaderDescs[0] = "Rubrique";
			revisionsTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			revisionsTableHeaderDescs[2] = "Desc détaillée " + lastVersionYear;
			newTableHeaderDescs[0] = "Rubrique";
			newTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			disabledTableHeaderDescs[0] = "Rubrique";
			disabledTableHeaderDescs[1] = "Desc détaillée " + lastVersionYear;
			revisionsShortTableHeaderDescs[0] = "Rubrique";
			revisionsShortTableHeaderDescs[1] = "Desc courte " + cciCurrentVersion;
			revisionsShortTableHeaderDescs[2] = "Desc courte " + cciLastVersion;
			newShortTableHeaderDescs[0] = "Rubrique";
			newShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			disabledShortTableHeaderDescs[0] = "Rubrique";
			disabledShortTableHeaderDescs[1] = "Desc courte " + lastVersionYear;
		}
		boolean isBlock = false;
		// long revisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetRubric, rownum, revisedRubricLongDescs,
				revisionsLongTitleValue, revisionsTableHeaderDescs, isBlock);

		rownum++;
		// CCI2015 Rubric Long English Description New
		rownum = buildAuditReportDescLongNewOrDisabled(sheetRubric, rownum, newRubrics, newLongTitleValue,
				newTableHeaderDescs, isBlock);
		rownum++;
		// CCI2015 Rubric Long English Description Disabled
		rownum = buildAuditReportDescLongNewOrDisabled(sheetRubric, rownum, removedRubrics, disabledLongTitleValue,
				disabledTableHeaderDescs, isBlock);
		rownum++;

		// Rubric ShortEnglishDescriptionRevisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetRubric, rownum, revisedRubricShortDescs,
				revisionsShortTitleValue, revisionsShortTableHeaderDescs, isBlock);

		rownum++;
		// Rubric ShortEnglishDescription new
		rownum = buildAuditReportDescShortNewOrDisabled(sheetRubric, rownum, newRubrics, newShortTitleValue,
				newShortTableHeaderDescs, isBlock);

		rownum++;
		// short disabled
		rownum = buildAuditReportDescShortNewOrDisabled(sheetRubric, rownum, removedRubrics, disabledShortTitleValue,
				disabledShortTableHeaderDescs, isBlock);

	}

	private void buildAuditReportSheetICDBlkDesc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel, File auditFile, HSSFWorkbook workbook,
			String languageCode, String releaseId) {
		// put two list into two maps, build revised list, removed list and new list
		// keep current and last
		List<CodeDescriptionAudit> revisedBlkLongDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionAudit> revisedBlkShortDescs = new ArrayList<CodeDescriptionAudit>();

		List<CodeDescriptionPublication> newBlks = new ArrayList<CodeDescriptionPublication>();
		List<CodeDescriptionPublication> removedBlks = new ArrayList<CodeDescriptionPublication>();

		Long icdClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Code");
		Long icdClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "ShortTitle");
		Long icdClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "LongTitle");
		Long icdClassIdChapter = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Chapter");
		Long icdClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Block");

		Long chapter22ConceptId = publicationMapper.findConceptIdForChapter22(currentICDOpenContextId, icdClassIdCode,
				icdClassIdChapter, languageCode);
		List<CodeDescriptionPublication> currentBlkDescs = publicationMapper.findICDBlkDescWithoutChapter22(
				currentICDOpenContextId, icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdChapter,
				icdClassIdBlock, chapter22ConceptId, languageCode);

		List<CodeDescriptionPublication> lastBlkDescs = publicationMapper.findICDBlkDescWithoutChapter22(
				lastVersionICDContextId, icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdChapter,
				icdClassIdBlock, chapter22ConceptId, languageCode);

		generateRevisedNewRemovedDescsFromCurrentAndLastVersion(revisedBlkLongDescs, revisedBlkShortDescs, newBlks,
				removedBlks, currentBlkDescs, lastBlkDescs);

		// build excel file
		HSSFSheet sheetBlk = null;
		String titleValue = null;
		Long currentVersionYear = generateTablesModel.getCurrentOpenYear();
		Long lastVersionYear = currentVersionYear - 1;
		String icdCurrentVersion = "10CA" + currentVersionYear;
		String icdLastVersion = "10CA" + lastVersionYear;
		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {
			sheetBlk = workbook.createSheet("Blk_Eng_Desc");
		} else {
			sheetBlk = workbook.createSheet("Blk_Fra_Desc");
		}
		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, "10CA", currentVersionYear, releaseId);

		Integer rownum = 3;
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheetBlk, rownum, titleValue);
		sheetBlk.setColumnWidth(1, 255 * 25);
		sheetBlk.setColumnWidth(2, 255 * 50);
		sheetBlk.setColumnWidth(3, 255 * 50);
		rownum = rownum + 3;

		String revisionsLongTitleValue = icdCurrentVersion + " Block Long English Description Revisions";
		String revisionsShortTitleValue = icdCurrentVersion + " Block Short English Description Revisions";
		String newLongTitleValue = icdCurrentVersion + " Block Long English Description New";
		String newShortTitleValue = icdCurrentVersion + " Block Short English Description New";
		String disabledLongTitleValue = icdCurrentVersion + " Block Long English Description Disabled";
		String disabledShortTitleValue = icdCurrentVersion + " Block Short English Description Disabled";
		String[] revisionsLongTableHeaderDescs = new String[3];
		revisionsLongTableHeaderDescs[0] = "Block";
		revisionsLongTableHeaderDescs[1] = "English Long Description New " + icdCurrentVersion;
		revisionsLongTableHeaderDescs[2] = "English Long Description Old " + icdLastVersion;
		String[] revisionsShortTableHeaderDescs = new String[3];
		revisionsShortTableHeaderDescs[0] = "Block";
		revisionsShortTableHeaderDescs[1] = "English Short Description New " + icdCurrentVersion;
		revisionsShortTableHeaderDescs[2] = "English Short Description Old " + icdLastVersion;

		String[] newTableHeaderDescs = new String[2];
		newTableHeaderDescs[0] = "Block";
		newTableHeaderDescs[1] = "English Long Description New " + icdCurrentVersion;

		String[] disabledTableHeaderDescs = new String[2];
		disabledTableHeaderDescs[0] = "Block";
		disabledTableHeaderDescs[1] = "English Long Description Disabled";
		String[] newShortTableHeaderDescs = new String[2];
		newShortTableHeaderDescs[0] = "Block";
		newShortTableHeaderDescs[1] = "English Short Description New " + icdCurrentVersion;

		String[] disabledShortTableHeaderDescs = new String[2];
		disabledShortTableHeaderDescs[0] = "Block";
		disabledShortTableHeaderDescs[1] = "English Short Description Disabled";

		if (LANGUAGE_CODE_FRA.equalsIgnoreCase(languageCode)) {
			revisionsLongTitleValue = "Descriptions détaillées révisées des groupes de la CIM-10-CA "
					+ currentVersionYear;
			revisionsShortTitleValue = "Descriptions courtes révisées des groupes de la CIM-10-CA "
					+ currentVersionYear;
			newLongTitleValue = "Descriptions détaillées des nouveaux groupes de la CIM-10-CA " + currentVersionYear;
			newShortTitleValue = "Descriptions courtes des nouveaux groupes de la CIM-10-CA " + currentVersionYear;
			disabledLongTitleValue = "Descriptions détaillées des groupes désactivées de la CIM-10-CA "
					+ currentVersionYear;
			disabledShortTitleValue = "Descriptions courtes des groupes désactivées de la CIM-10-CA "
					+ currentVersionYear;
			revisionsLongTableHeaderDescs[0] = "Bloc";
			revisionsLongTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			revisionsLongTableHeaderDescs[2] = "Desc détaillée " + lastVersionYear;
			revisionsShortTableHeaderDescs[0] = "Bloc";
			revisionsShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			revisionsShortTableHeaderDescs[2] = "Desc courte " + lastVersionYear;

			newTableHeaderDescs[0] = "Bloc";
			newTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			disabledTableHeaderDescs[0] = "Bloc";
			disabledTableHeaderDescs[1] = "Desc détaillée " + lastVersionYear;
			newShortTableHeaderDescs[0] = "Bloc";
			newShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			disabledShortTableHeaderDescs[0] = "Bloc";
			disabledShortTableHeaderDescs[1] = "Desc courte " + lastVersionYear;
			;
		}
		boolean isBlock = true;
		// BlockLongEnglishDescriptionRevisions
		// rownum = buildAuditReportICDBlkDescLongRevisions(sheetBlk, rownum, revisedBlkLongDescs, languageCode);
		rownum = buildAuditReportDescLongOrShortRevisions(sheetBlk, rownum, revisedBlkLongDescs,
				revisionsLongTitleValue, revisionsLongTableHeaderDescs, isBlock);
		rownum++;
		// 10CA2015 Block Long English Description New
		// rownum = buildAuditReportICDBlkDescLongNew(sheetBlk, rownum, newBlks, languageCode);
		rownum = buildAuditReportDescLongNewOrDisabled(sheetBlk, rownum, newBlks, newLongTitleValue,
				newTableHeaderDescs, isBlock);
		rownum++;
		// 10CA2015 Block Long English Description Disabled
		// rownum = buildAuditReportICDBlkDescLongDisabled(sheetBlk, rownum, removedBlks, languageCode);
		rownum = buildAuditReportDescLongNewOrDisabled(sheetBlk, rownum, removedBlks, disabledLongTitleValue,
				disabledTableHeaderDescs, isBlock);
		rownum++;
		// BlockShortEnglishDescriptionRevisions
		// rownum = buildAuditReportICDBlkDescShortRevisions(sheetBlk, rownum, revisedBlkShortDescs, languageCode);
		rownum = buildAuditReportDescLongOrShortRevisions(sheetBlk, rownum, revisedBlkShortDescs,
				revisionsShortTitleValue, revisionsShortTableHeaderDescs, isBlock);
		rownum++;
		// BlockShortEnglishDescription new
		// rownum = buildAuditReportICDBlkDescShortNew(sheetBlk, rownum, newBlks, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetBlk, rownum, newBlks, newShortTitleValue,
				newShortTableHeaderDescs, isBlock);
		rownum++;
		// rownum = buildAuditReportICDBlkDescShortDisabled(sheetBlk, rownum, removedBlks, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetBlk, rownum, removedBlks, disabledShortTitleValue,
				disabledShortTableHeaderDescs, isBlock);

	}

	private void buildAuditReportSheetICDCat1Desc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel, File auditFile, HSSFWorkbook workbook,
			String languageCode, String releaseId) {
		// put two list into two maps, build revised list, removed list and new list
		// keep current and last
		List<CodeDescriptionAudit> revisedCat1LongDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionAudit> revisedCat1ShortDescs = new ArrayList<CodeDescriptionAudit>();

		List<CodeDescriptionPublication> newCat1s = new ArrayList<CodeDescriptionPublication>();
		List<CodeDescriptionPublication> removedCat1s = new ArrayList<CodeDescriptionPublication>();

		Long icdClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Code");
		Long icdClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "ShortTitle");
		Long icdClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "LongTitle");

		Long icdClassIdCategory = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Category");

		List<CodeDescriptionPublication> currentCat1Descs = publicationMapper.findICDCat1Desc(currentICDOpenContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, languageCode);
		List<CodeDescriptionPublication> lastCat1Descs = publicationMapper.findICDCat1Desc(lastVersionICDContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, languageCode);
		generateRevisedNewRemovedDescsFromCurrentAndLastVersion(revisedCat1LongDescs, revisedCat1ShortDescs, newCat1s,
				removedCat1s, currentCat1Descs, lastCat1Descs);

		// build cat1 sheet in excel file
		HSSFSheet sheetCat1 = null;
		String titleValue = null;
		Long currentVersionYear = generateTablesModel.getCurrentOpenYear();
		Long lastVersionYear = currentVersionYear - 1;
		String icdCurrentVersion = "10CA" + currentVersionYear;
		String icdLastVersion = "10CA" + lastVersionYear;
		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {
			sheetCat1 = workbook.createSheet("Cat1_Eng_Desc");
		} else {
			sheetCat1 = workbook.createSheet("Cat1_Fra_Desc");
		}
		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, "10CA", currentVersionYear, releaseId);
		Integer rownum = 3;

		rownum = CimsFileUtils.buildAuditReportTitleLine(sheetCat1, rownum, titleValue);

		sheetCat1.setColumnWidth(1, 255 * 25);
		sheetCat1.setColumnWidth(2, 255 * 50);
		sheetCat1.setColumnWidth(3, 255 * 50);
		rownum = rownum + 3;
		String revisionsLongTitleValue = icdCurrentVersion + " Category Long English Description Revisions";
		String revisionsShortTitleValue = icdCurrentVersion + " Category Short English Description Revisions";
		String newLongTitleValue = icdCurrentVersion + " Category Long English Description New";
		String newShortTitleValue = icdCurrentVersion + " Category Short English Description New";
		String disabledLongTitleValue = icdCurrentVersion + " Category Long English Description Disabled";
		String disabledShortTitleValue = icdCurrentVersion + " Category Short English Description Disabled";
		String[] revisionsLongTableHeaderDescs = new String[3];
		revisionsLongTableHeaderDescs[0] = "CAT1";
		revisionsLongTableHeaderDescs[1] = "English Long Description New " + icdCurrentVersion;
		revisionsLongTableHeaderDescs[2] = "English Long Description Old " + icdLastVersion;
		String[] revisionsShortTableHeaderDescs = new String[3];
		revisionsShortTableHeaderDescs[0] = "CAT1";
		revisionsShortTableHeaderDescs[1] = "English Short Description New " + icdCurrentVersion;
		revisionsShortTableHeaderDescs[2] = "English Short Description Old " + icdLastVersion;

		String[] newTableHeaderDescs = new String[2];
		newTableHeaderDescs[0] = "CAT1";
		newTableHeaderDescs[1] = "English Long Description New " + icdCurrentVersion;
		String[] disabledTableHeaderDescs = new String[2];
		disabledTableHeaderDescs[0] = "CAT1";
		disabledTableHeaderDescs[1] = "English Long Description Disabled";
		String[] newShortTableHeaderDescs = new String[2];
		newShortTableHeaderDescs[0] = "CAT1";
		newShortTableHeaderDescs[1] = "English Short Description New " + icdCurrentVersion;
		String[] disabledShortTableHeaderDescs = new String[2];
		disabledShortTableHeaderDescs[0] = "CAT1";
		disabledShortTableHeaderDescs[1] = "English Short Description Disabled";

		if (LANGUAGE_CODE_FRA.equalsIgnoreCase(languageCode)) {
			revisionsLongTitleValue = "Descriptions détaillées révisées des catégories de la CIM-10-CA "
					+ currentVersionYear;
			revisionsShortTitleValue = "Descriptions courtes révisées des catégories de la CIM-10-CA "
					+ currentVersionYear;
			newLongTitleValue = "Descriptions détaillées des nouvelles catégories de la CIM-10-CA "
					+ currentVersionYear;
			newShortTitleValue = "Descriptions courtes des nouvelles catégories de la CIM-10-CA " + currentVersionYear;
			disabledLongTitleValue = "Descriptions détaillées des catégories désactivées de la CIM-10-CA "
					+ currentVersionYear;
			disabledShortTitleValue = "Descriptions courtes des catégories désactivées de la CIM-10-CA "
					+ currentVersionYear;
			revisionsLongTableHeaderDescs[0] = "CAT1";
			revisionsLongTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			revisionsLongTableHeaderDescs[2] = "Desc détaillée " + lastVersionYear;
			revisionsShortTableHeaderDescs[0] = "CAT1";
			revisionsShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			revisionsShortTableHeaderDescs[2] = "Desc courte " + lastVersionYear;

			newTableHeaderDescs[0] = "CAT1";
			newTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			disabledTableHeaderDescs[0] = "CAT1";
			disabledTableHeaderDescs[1] = "Desc détaillée " + lastVersionYear;
			newShortTableHeaderDescs[0] = "CAT1";
			newShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			disabledShortTableHeaderDescs[0] = "CAT1";
			disabledShortTableHeaderDescs[1] = "Desc courte " + lastVersionYear;
		}
		boolean isBlock = false;
		// rownum = buildAuditReportICDCat1DescLongRevisions(sheetCat1, rownum, revisedCat1LongDescs, languageCode);
		rownum = buildAuditReportDescLongOrShortRevisions(sheetCat1, rownum, revisedCat1LongDescs,
				revisionsLongTitleValue, revisionsLongTableHeaderDescs, isBlock);
		rownum++;
		// 10CA2015 Cat1 Long English Description New
		// rownum = buildAuditReportICDCat1DescLongNew(sheetCat1, rownum, newCat1s, languageCode);
		rownum = buildAuditReportDescLongNewOrDisabled(sheetCat1, rownum, newCat1s, newLongTitleValue,
				newTableHeaderDescs, isBlock);
		rownum++;
		// 10CA2015 Block Long English Description Disabled
		// rownum = buildAuditReportICDCat1DescLongDisabled(sheetCat1, rownum, removedCat1s, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCat1, rownum, removedCat1s, disabledLongTitleValue,
				disabledTableHeaderDescs, isBlock);
		rownum++;
		// Cat1ShortEnglishDescriptionRevisions
		// rownum = buildAuditReportICDCat1DescShortRevisions(sheetCat1, rownum, revisedCat1ShortDescs, languageCode);
		rownum = buildAuditReportDescLongOrShortRevisions(sheetCat1, rownum, revisedCat1ShortDescs,
				revisionsShortTitleValue, revisionsShortTableHeaderDescs, isBlock);

		rownum++;
		// Cat1ShortEnglishDescription new
		// rownum = buildAuditReportICDCat1DescShortNew(sheetCat1, rownum, newCat1s, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCat1, rownum, newCat1s, newShortTitleValue,
				newShortTableHeaderDescs, isBlock);
		rownum++;
		// rownum = buildAuditReportICDCat1DescShortDisabled(sheetCat1, rownum, newCat1s, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCat1, rownum, removedCat1s, disabledShortTitleValue,
				disabledShortTableHeaderDescs, isBlock);

	}

	private void buildAuditReportSheetICDCodeDesc(Long currentICDOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel, File auditFile, HSSFWorkbook workbook,
			String languageCode, String releaseId) {
		// put two list into two maps, build revised list, removed list and new list
		// keep current and last
		List<CodeDescriptionAudit> revisedCodeLongDescs = new ArrayList<CodeDescriptionAudit>();
		List<CodeDescriptionAudit> revisedCodeShortDescs = new ArrayList<CodeDescriptionAudit>();

		List<CodeDescriptionPublication> newCodes = new ArrayList<CodeDescriptionPublication>();
		List<CodeDescriptionPublication> removedCodes = new ArrayList<CodeDescriptionPublication>();

		Long icdClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Code");
		Long icdClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "ShortTitle");
		Long icdClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "LongTitle");
		Long icdClassIdCategory = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Category");

		List<CodeDescriptionPublication> currentCodeDescs = publicationMapper.findICDCodeDesc(currentICDOpenContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, languageCode);

		List<CodeDescriptionPublication> lastCodeDescs = publicationMapper.findICDCodeDesc(lastVersionICDContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, languageCode);

		generateRevisedNewRemovedDescsFromCurrentAndLastVersion(revisedCodeLongDescs, revisedCodeShortDescs, newCodes,
				removedCodes, currentCodeDescs, lastCodeDescs);

		// build excel file
		HSSFSheet sheetCode = null;
		String titleValue = null;
		Long currentVersionYear = generateTablesModel.getCurrentOpenYear();
		Long lastVersionYear = currentVersionYear - 1;
		String icdCurrentVersion = "10CA" + currentVersionYear;
		String icdLastVersion = "10CA" + lastVersionYear;
		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {
			sheetCode = workbook.createSheet("ICD_Code_Eng_Desc");
		} else {
			sheetCode = workbook.createSheet("ICD_Code_Fra_Desc");
		}
		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, "10CA", currentVersionYear, releaseId);
		Integer rownum = 3;

		rownum = CimsFileUtils.buildAuditReportTitleLine(sheetCode, rownum, titleValue);
		// sheetCode.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 2));
		sheetCode.setColumnWidth(1, 255 * 25);
		sheetCode.setColumnWidth(2, 255 * 50);
		sheetCode.setColumnWidth(3, 255 * 50);

		rownum = rownum + 3;
		// Code LongEnglishDescriptionRevisions

		String revisionsLongTitleValue = icdCurrentVersion + " Code Long English Description Revisions";
		String revisionsShortTitleValue = icdCurrentVersion + " Code Short English Description Revisions";
		String newLongTitleValue = icdCurrentVersion + " Code Long English Description New";
		String disabledLongTitleValue = icdCurrentVersion + " Code Long English Description Disabled";
		String newShortTitleValue = icdCurrentVersion + " Code Short English Description New";
		String disabledShortTitleValue = icdCurrentVersion + " Code Short English Description Disabled";
		String[] revisionsTableHeaderDescs = new String[3];
		revisionsTableHeaderDescs[0] = "Code";
		revisionsTableHeaderDescs[1] = "English Long Description New " + icdCurrentVersion;
		revisionsTableHeaderDescs[2] = "English Long Description Old " + icdLastVersion;
		String[] newTableHeaderDescs = new String[2];
		newTableHeaderDescs[0] = "Code";
		newTableHeaderDescs[1] = "English Long Description New " + icdCurrentVersion;
		String[] disabledTableHeaderDescs = new String[2];
		disabledTableHeaderDescs[0] = "Code";
		disabledTableHeaderDescs[1] = "English Long Description Disabled";
		String[] revisionsShortTableHeaderDescs = new String[3];
		revisionsShortTableHeaderDescs[0] = "Code";
		revisionsShortTableHeaderDescs[1] = "English Short Description New " + icdCurrentVersion;
		revisionsShortTableHeaderDescs[2] = "English Short Description Old " + icdLastVersion;
		String[] newShortTableHeaderDescs = new String[2];
		newShortTableHeaderDescs[0] = "Code";
		newShortTableHeaderDescs[1] = "English Short Description New " + icdCurrentVersion;
		String[] disabledShortTableHeaderDescs = new String[2];
		disabledShortTableHeaderDescs[0] = "Code";
		disabledShortTableHeaderDescs[1] = "English Short Description Disabled";

		if (LANGUAGE_CODE_FRA.equalsIgnoreCase(languageCode)) {
			revisionsLongTitleValue = "Descriptions détaillées révisées des codes de la CIM-10-CA "
					+ currentVersionYear;
			revisionsShortTitleValue = "Descriptions courtes révisées des codes de la CIM-10-CA " + currentVersionYear;
			newLongTitleValue = "Descriptions détaillées des nouveaux codes de la CIM-10-CA " + currentVersionYear;
			newShortTitleValue = "Descriptions courtes des nouveaux codes de la CIM-10-CA " + currentVersionYear;
			disabledLongTitleValue = "Descriptions détaillées des codes désactivées de la CIM-10-CA "
					+ currentVersionYear;
			disabledShortTitleValue = "Descriptions courtes des codes désactivées de la CIM-10-CA "
					+ currentVersionYear;
			revisionsTableHeaderDescs[0] = "Code";
			revisionsTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			revisionsTableHeaderDescs[2] = "Desc détaillée " + lastVersionYear;
			newTableHeaderDescs[0] = "Code";
			newTableHeaderDescs[1] = "Desc détaillée " + currentVersionYear;
			disabledTableHeaderDescs[0] = "Code";
			disabledTableHeaderDescs[1] = "Desc détaillée " + lastVersionYear;
			revisionsShortTableHeaderDescs[0] = "Code";
			revisionsShortTableHeaderDescs[1] = "Desc courte " + icdCurrentVersion;
			revisionsShortTableHeaderDescs[2] = "Desc courte " + icdLastVersion;
			newShortTableHeaderDescs[0] = "Code";
			newShortTableHeaderDescs[1] = "Desc courte " + currentVersionYear;
			disabledShortTableHeaderDescs[0] = "Code";
			disabledShortTableHeaderDescs[1] = "Desc courte " + lastVersionYear;
		}
		boolean isBlock = false;
		// long revisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetCode, rownum, revisedCodeLongDescs,
				revisionsLongTitleValue, revisionsTableHeaderDescs, isBlock);

		rownum++;
		// CCI2015 Code Long English Description New
		rownum = buildAuditReportDescLongNewOrDisabled(sheetCode, rownum, newCodes, newLongTitleValue,
				newTableHeaderDescs, isBlock);
		rownum++;
		// CCI2015 Code Long English Description Disabled
		rownum = buildAuditReportDescLongNewOrDisabled(sheetCode, rownum, removedCodes, disabledLongTitleValue,
				disabledTableHeaderDescs, isBlock);
		rownum++;

		// Code ShortEnglishDescriptionRevisions
		rownum = buildAuditReportDescLongOrShortRevisions(sheetCode, rownum, revisedCodeShortDescs,
				revisionsShortTitleValue, revisionsShortTableHeaderDescs, isBlock);

		rownum++;
		// code ShortEnglishDescription new
		// rownum = buildAuditReportCCIRubricDescShortNew(sheetRubric, rownum, newRubrics, languageCode);
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCode, rownum, newCodes, newShortTitleValue,
				newShortTableHeaderDescs, isBlock);

		rownum++;
		// rownum = buildAuditReportCCIRubricDescShortDisabled(sheetRubric, rownum, removedRubrics, languageCode);
		// short disabled
		rownum = buildAuditReportDescShortNewOrDisabled(sheetCode, rownum, removedCodes, disabledShortTitleValue,
				disabledShortTableHeaderDescs, isBlock);

	}

	private String buildBlkLineFixedWidth(CodeDescriptionPublication cdp, boolean isBlock, int blockLength,
			int shortTitleLength) {
		return buildBlkLineFixedWidth(cdp, isBlock, blockLength, shortTitleLength, false);
	}

	private String buildBlkLineFixedWidth(CodeDescriptionPublication cdp, boolean isBlock, int blockLength,
			int shortTitleLength, boolean truncate) {
		StringBuilder sb_line = new StringBuilder();
		String codeInFile = null;
		if (isBlock) {
			codeInFile = removeCharatersFromCodeKeepDash(cdp.getCode());
		} else {
			codeInFile = removeCharatersFromCode(cdp.getCode());
		}

		CimsFileUtils.padBlanksToString(codeInFile, blockLength, sb_line);
		// System should not add one additional space between the field colulmn.
		// sb_line.append(" ");
		if (truncate) {
			CimsFileUtils.padBlanksToString(cdp.getShortTitle(), shortTitleLength, sb_line, true);
		} else {
			CimsFileUtils.padBlanksToString(cdp.getShortTitle(), shortTitleLength, sb_line);
		}
		// sb_line.append(" ");
		CimsFileUtils.padBlanksToString(cdp.getLongTitle().replace("\r", " ").replace("\n", " "), MAX_LENGTH_LONGTITLE,
				sb_line);

		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private void buildCCIBlkDescFiles(Long currentCCIOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String releaseId, boolean isRelease, List<PublicationStatistics> statisticsSummary) throws IOException {

		String fileNameEng = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameEng = CimsFileUtils.buildAsciiFileName("CCI", "Blk", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "tab", "Desc");
		} else {
			fileNameEng = CimsFileUtils.buildAsciiFileName("CCI", "Blk", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "fixed", "Desc");
		}

		File pubFolder = new File(pubDirectory);

		File subFolder = null;
		if (isRelease) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
		}

		File cciBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_BlkDesc);
		File cciBlkDescEngFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_ENG);
		File cciBlkDescFraFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_FRA);

		File cciBlkDescEng = new File(
				cciBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameEng);

		BufferedWriter bwEng = new BufferedWriter(new FileWriter(cciBlkDescEng));
		Long cciClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Code");
		Long cciClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "ShortTitle");
		Long cciClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "LongTitle");
		Long cciClassIdSection = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Section");
		Long cciClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Block");
		Long cciClassIdGroup = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Group");

		List<CodeDescriptionPublication> blkDescsEng = publicationMapper.findCCIBlkDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdSection, cciClassIdBlock,
				cciClassIdGroup, "ENG");
		PublicationStatistics cciBlkEngStatistics = new PublicationStatistics("CCI_BLK_ENG_DESC", "English",
				blkDescsEng.size());
		statisticsSummary.add(cciBlkEngStatistics);

		boolean isBlock = true;
		for (CodeDescriptionPublication blkDesc : blkDescsEng) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(blkDesc, isBlock);
			} else {
				blkLine = buildBlkLineFixedWidth(blkDesc, isBlock, MAX_LENGTH_CCI_BLOCK, MAX_LENGTH_CCI_SHORTTITLE);
			}
			bwEng.write(blkLine);
		}
		bwEng.close();

		String fileNameFra = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameFra = CimsFileUtils.buildAsciiFileName("CCI", "Blk", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", "Desc");
		} else {
			fileNameFra = CimsFileUtils.buildAsciiFileName("CCI", "Blk", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "fixed", "Desc");
		}

		File cciBlkDescFra = new File(
				cciBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameFra);

		BufferedWriter bwFra = new BufferedWriter(new FileWriter(cciBlkDescFra));
		List<CodeDescriptionPublication> blkDescsFra = publicationMapper.findCCIBlkDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdSection, cciClassIdBlock,
				cciClassIdGroup, "FRA");
		PublicationStatistics cciBlkFraStatistics = new PublicationStatistics("CCI_BLK_FRA_DESC", "French",
				blkDescsFra.size());
		statisticsSummary.add(cciBlkFraStatistics);

		for (CodeDescriptionPublication blkDesc : blkDescsFra) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(blkDesc, isBlock);
			} else {
				blkLine = buildBlkLineFixedWidth(blkDesc, isBlock, MAX_LENGTH_CCI_BLOCK, MAX_LENGTH_CCI_SHORTTITLE);
			}
			bwFra.write(blkLine);
		}
		bwFra.close();

	}

	private void buildCCICodeDescFiles(Long currentCCIOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String releaseId, boolean isRelease, List<PublicationStatistics> statisticsSummary) throws IOException {

		String fileNameEng = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameEng = CimsFileUtils.buildAsciiFileName("CCI", "Code", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "tab", "Desc");
		} else {
			fileNameEng = CimsFileUtils.buildAsciiFileName("CCI", "Code", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "fixed", "Desc");
		}

		File pubFolder = new File(pubDirectory);

		File subFolder = null;
		if (isRelease) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
		}

		// File cciFolder = createSubFolder(pubFolder, SUB_FOLDER_CCI);
		File cciCodeDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_CodeDesc);
		File cciCodeDescEngFolder = CimsFileUtils.createSubFolder(cciCodeDescFolder, SUB_FOLDER_ENG);
		File cciCodeDescFraFolder = CimsFileUtils.createSubFolder(cciCodeDescFolder, SUB_FOLDER_FRA);

		File cciRubricDescEng = new File(
				cciCodeDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameEng);

		BufferedWriter bwEng = new BufferedWriter(new FileWriter(cciRubricDescEng));
		Long cciClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Code");
		Long cciClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "ShortTitle");
		Long cciClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "LongTitle");
		Long cciClassIdCCICode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "CCICODE");
		// Long cciClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Block");
		List<CodeDescriptionPublication> codeDescsEng = publicationMapper.findCCICodeDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdCCICode, "ENG");
		PublicationStatistics cciCodeEngStatistics = new PublicationStatistics("CCI_CODE_ENG_DESC", "English",
				codeDescsEng.size());
		statisticsSummary.add(cciCodeEngStatistics);
		boolean isBlock = false;
		for (CodeDescriptionPublication codeDesc : codeDescsEng) {
			String codeLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				codeLine = buildCodeDescriptionPublicationLineTab(codeDesc, isBlock);
			} else {
				codeLine = buildCCICodeLineFixedWidth(codeDesc);
			}
			bwEng.write(codeLine);
		}

		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			bwEng.write(buildCCICodeLastLineTab("ENG"));
		} else {
			bwEng.write(buildCCICodeLastLineFixedWidth("ENG"));
		}
		bwEng.close();

		String fileNameFra = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameFra = CimsFileUtils.buildAsciiFileName("CCI", "Code", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", "Desc");
		} else {
			fileNameFra = CimsFileUtils.buildAsciiFileName("CCI", "Code", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "fixed", "Desc");
		}

		File cciRubricDescFra = new File(
				cciCodeDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameFra);
		BufferedWriter bwFra = new BufferedWriter(new FileWriter(cciRubricDescFra));
		List<CodeDescriptionPublication> codeDescsFra = publicationMapper.findCCIRubricDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdCCICode, "FRA");
		PublicationStatistics cciCodeFraStatistics = new PublicationStatistics("CCI_CODE_FRA_DESC", "French",
				codeDescsFra.size());
		statisticsSummary.add(cciCodeFraStatistics);
		for (CodeDescriptionPublication codeDesc : codeDescsFra) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(codeDesc, isBlock);
			} else {
				blkLine = buildCCICodeLineFixedWidth(codeDesc);
			}
			bwFra.write(blkLine);
		}
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			bwFra.write(buildCCICodeLastLineTab("FRA"));
		} else {
			bwFra.write(buildCCICodeLastLineFixedWidth("FRA"));
		}

		bwFra.close();

	}

	private String buildCCICodeLastLineFixedWidth(String languageCode) {
		StringBuilder sb_line = new StringBuilder();

		CimsFileUtils.padBlanksToString(CCI_CODE_LASTLINE_CANCELLED, MAX_LENGTH_CCI_CODE, sb_line);
		if ("ENG".equalsIgnoreCase(languageCode)) {
			CimsFileUtils.padBlanksToString(CCI_CODE_LASTLINE_2ND_COLUMN_EN, MAX_LENGTH_CCI_SHORTTITLE, sb_line);
			CimsFileUtils.padBlanksToString(CCI_CODE_LASTLINE_3RD_COLUMN_EN, MAX_LENGTH_LONGTITLE, sb_line);
		} else {
			CimsFileUtils.padBlanksToString(CCI_CODE_LASTLINE_2ND_COLUMN_FR, MAX_LENGTH_CCI_SHORTTITLE, sb_line);
			CimsFileUtils.padBlanksToString(CCI_CODE_LASTLINE_3RD_COLUMN_FR, MAX_LENGTH_LONGTITLE, sb_line);
		}
		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private String buildCCICodeLastLineTab(String languageCode) {
		StringBuilder sb_line = new StringBuilder();
		sb_line.append(CCI_CODE_LASTLINE_CANCELLED);
		sb_line.append("\t");
		if ("ENG".equalsIgnoreCase(languageCode)) {
			sb_line.append(CCI_CODE_LASTLINE_2ND_COLUMN_EN);
			sb_line.append("\t");
			sb_line.append(CCI_CODE_LASTLINE_3RD_COLUMN_EN);
		} else {
			sb_line.append(CCI_CODE_LASTLINE_2ND_COLUMN_FR);
			sb_line.append("\t");
			sb_line.append(CCI_CODE_LASTLINE_3RD_COLUMN_FR);
		}
		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private String buildCCICodeLineFixedWidth(CodeDescriptionPublication cdp) {
		StringBuilder sb_line = new StringBuilder();
		String codeInFile = removeCharatersFromCode(cdp.getCode());
		CimsFileUtils.padBlanksToString(codeInFile, MAX_LENGTH_CCI_CODE, sb_line);
		CimsFileUtils.padBlanksToString(cdp.getShortTitle(), MAX_LENGTH_CCI_SHORTTITLE, sb_line);
		CimsFileUtils.padBlanksToString(cdp.getLongTitle().replace("\r", " ").replace("\n", " "), MAX_LENGTH_LONGTITLE,
				sb_line);

		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	/*
	 * releaseId is V0_? or V1_? or null
	 */
	private void buildCCIRubricDescFiles(Long currentCCIOpenContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String releaseId, boolean isRelease,
			List<PublicationStatistics> statisticsSummary) throws IOException {

		String fileNameEng = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameEng = CimsFileUtils.buildAsciiFileName("CCI", "Rubric", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "tab", "Desc");
		} else {
			fileNameEng = CimsFileUtils.buildAsciiFileName("CCI", "Rubric", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "fixed", "Desc");
		}

		File pubFolder = new File(pubDirectory);

		File subFolder = null;
		if (isRelease) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
		}

		File cciBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_BlkDesc);
		File cciBlkDescEngFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_ENG);
		File cciBlkDescFraFolder = CimsFileUtils.createSubFolder(cciBlkDescFolder, SUB_FOLDER_FRA);

		File cciRubricDescEng = new File(
				cciBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameEng);
		BufferedWriter bwEng = new BufferedWriter(new FileWriter(cciRubricDescEng));
		Long cciClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Code");
		Long cciClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "ShortTitle");
		Long cciClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("CCI", "LongTitle");
		Long cciClassIdRubric = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Rubric");
		// Long cciClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("CCI", "Block");
		List<CodeDescriptionPublication> rubricDescsEng = publicationMapper.findCCIRubricDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdRubric, "ENG");
		PublicationStatistics cciRubricEngStatistics = new PublicationStatistics("CCI_RUBRIC_ENG_DESC", "English",
				rubricDescsEng.size());
		statisticsSummary.add(cciRubricEngStatistics);
		boolean isBlock = false;
		for (CodeDescriptionPublication rubricDesc : rubricDescsEng) {
			String rubricLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				rubricLine = buildCodeDescriptionPublicationLineTab(rubricDesc, isBlock);
			} else {
				rubricLine = buildCCIRubricLineFixedWidth(rubricDesc);
			}
			bwEng.write(rubricLine);
		}
		bwEng.close();

		String fileNameFra = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameFra = CimsFileUtils.buildAsciiFileName("CCI", "Rubric", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", "Desc");
		} else {
			fileNameFra = CimsFileUtils.buildAsciiFileName("CCI", "Rubric", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "fixed", "Desc");
		}

		File cciRubricDescFra = new File(
				cciBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameFra);

		BufferedWriter bwFra = new BufferedWriter(new FileWriter(cciRubricDescFra));
		List<CodeDescriptionPublication> rubricDescsFra = publicationMapper.findCCIRubricDesc(currentCCIOpenContextId,
				cciClassIdCode, cciClassIdShortTitle, cciClassIdLongTitle, cciClassIdRubric, "FRA");
		PublicationStatistics cciRubricFraStatistics = new PublicationStatistics("CCI_RUBRIC_FRA_DESC", "French",
				rubricDescsFra.size());
		statisticsSummary.add(cciRubricFraStatistics);

		for (CodeDescriptionPublication rubricDesc : rubricDescsFra) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(rubricDesc, isBlock);
			} else {
				blkLine = buildCCIRubricLineFixedWidth(rubricDesc);
			}
			bwFra.write(blkLine);
		}
		bwFra.close();

	}

	private String buildCCIRubricLineFixedWidth(CodeDescriptionPublication cdp) {
		StringBuilder sb_line = new StringBuilder();
		String codeInFile = removeCharatersFromCode(cdp.getCode());
		CimsFileUtils.padBlanksToString(codeInFile, MAX_LENGTH_CCI_RUBRIC, sb_line);
		CimsFileUtils.padBlanksToString(cdp.getShortTitle(), MAX_LENGTH_CCI_SHORTTITLE, sb_line);
		CimsFileUtils.padBlanksToString(cdp.getLongTitle().replace("\r", " ").replace("\n", " "), MAX_LENGTH_LONGTITLE,
				sb_line);

		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private String buildCodeDescriptionPublicationLineTab(CodeDescriptionPublication cdp, boolean isBlock) {
		StringBuilder sb_line = new StringBuilder();
		String codeInFile = null;
		if (isBlock) {
			codeInFile = removeCharatersFromCodeKeepDash(cdp.getCode());
		} else {
			codeInFile = removeCharatersFromCode(cdp.getCode());
		}
		sb_line.append(codeInFile);
		sb_line.append("\t");
		sb_line.append(cdp.getShortTitle());
		sb_line.append("\t");
		sb_line.append(cdp.getLongTitle().replace("\r", " ").replace("\n", " "));

		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private void buildICDBlkDescFiles(Long currentICDOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String releaseId, boolean isRelease, List<PublicationStatistics> statisticsSummary) throws IOException {
		String fileNameEng = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameEng = CimsFileUtils.buildAsciiFileName("ICD", "Blk", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "tab", "Desc");
		} else {
			fileNameEng = CimsFileUtils.buildAsciiFileName("ICD", "Blk", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "fixed", "Desc");
		}

		File pubFolder = new File(pubDirectory);

		File subFolder = null;
		if (isRelease) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
		}
		// File icdFolder = createSubFolder(pubFolder, SUB_FOLDER_ICD);
		File icdBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_BlkDesc);
		File icdBlkDescEngFolder = CimsFileUtils.createSubFolder(icdBlkDescFolder, SUB_FOLDER_ENG);
		File icdBlkDescFraFolder = CimsFileUtils.createSubFolder(icdBlkDescFolder, SUB_FOLDER_FRA);

		File icdBlkDescEng = new File(
				icdBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameEng);
		BufferedWriter bwEng = new BufferedWriter(new FileWriter(icdBlkDescEng));
		Long icdClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Code");
		Long icdClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "ShortTitle");
		Long icdClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "LongTitle");
		Long icdClassIdChapter = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Chapter");
		Long icdClassIdBlock = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Block");

		Long chapter22ConceptId = publicationMapper.findConceptIdForChapter22(currentICDOpenContextId, icdClassIdCode,
				icdClassIdChapter, "ENG");
		List<CodeDescriptionPublication> blkDescsEng = publicationMapper.findICDBlkDescWithoutChapter22(
				currentICDOpenContextId, icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdChapter,
				icdClassIdBlock, chapter22ConceptId, "ENG");
		PublicationStatistics icdBlkEngStatistics = new PublicationStatistics("ICD_BLK_ENG_DESC", "English",
				blkDescsEng.size());
		statisticsSummary.add(icdBlkEngStatistics);
		boolean isBlock = true;
		for (CodeDescriptionPublication blkDesc : blkDescsEng) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(blkDesc, isBlock);
			} else {
				blkLine = buildBlkLineFixedWidth(blkDesc, isBlock, MAX_LENGTH_ICD_BLOCK, MAX_LENGTH_ICD_SHORTTITLE,
						true);
			}
			bwEng.write(blkLine);
		}
		bwEng.close();

		String fileNameFra = null;

		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameFra = CimsFileUtils.buildAsciiFileName("ICD", "Blk", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", "Desc");
		} else {
			fileNameFra = CimsFileUtils.buildAsciiFileName("ICD", "Blk", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "fixed", "Desc");
		}
		File icdBlkDescFra = new File(
				icdBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameFra);
		BufferedWriter bwFra = new BufferedWriter(new FileWriter(icdBlkDescFra));

		List<CodeDescriptionPublication> blkDescsFra = publicationMapper.findICDBlkDescWithoutChapter22(
				currentICDOpenContextId, icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdChapter,
				icdClassIdBlock, chapter22ConceptId, "FRA");
		PublicationStatistics icdBlkFraStatistics = new PublicationStatistics("ICD_BLK_FRA_DESC", "French",
				blkDescsFra.size());
		statisticsSummary.add(icdBlkFraStatistics);

		for (CodeDescriptionPublication blkDesc : blkDescsFra) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(blkDesc, isBlock);
			} else {
				blkLine = buildBlkLineFixedWidth(blkDesc, isBlock, MAX_LENGTH_ICD_BLOCK, MAX_LENGTH_ICD_SHORTTITLE,
						true);
			}
			bwFra.write(blkLine);
		}
		bwFra.close();
	}

	private void buildICDCat1DescFiles(Long currentICDOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String releaseId, boolean isRelease, List<PublicationStatistics> statisticsSummary) throws IOException {

		String fileNameEng = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameEng = CimsFileUtils.buildAsciiFileName("ICD", "Cat1", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "tab", "Desc");
		} else {
			fileNameEng = CimsFileUtils.buildAsciiFileName("ICD", "Cat1", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "fixed", "Desc");
		}
		File pubFolder = new File(pubDirectory);
		File subFolder = null;
		if (isRelease) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
		}
		// File icdFolder = createSubFolder(pubFolder, SUB_FOLDER_ICD);
		File icdBlkDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_BlkDesc);
		File icdBlkDescEngFolder = CimsFileUtils.createSubFolder(icdBlkDescFolder, SUB_FOLDER_ENG);
		File icdBlkDescFraFolder = CimsFileUtils.createSubFolder(icdBlkDescFolder, SUB_FOLDER_FRA);

		File icdBlkDescEng = new File(
				icdBlkDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameEng);
		BufferedWriter bwEng = new BufferedWriter(new FileWriter(icdBlkDescEng));
		Long icdClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Code");
		Long icdClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "ShortTitle");
		Long icdClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "LongTitle");
		Long icdClassIdCategory = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Category");

		List<CodeDescriptionPublication> cat1DescsEng = publicationMapper.findICDCat1Desc(currentICDOpenContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, "ENG");
		PublicationStatistics icdCat1EngStatistics = new PublicationStatistics("ICD_CAT1_ENG_DESC", "English",
				cat1DescsEng.size());
		statisticsSummary.add(icdCat1EngStatistics);
		boolean isBlock = false;
		for (CodeDescriptionPublication cat1Desc : cat1DescsEng) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(cat1Desc, isBlock);
			} else {
				blkLine = buildICDCat1LineFixedWidth(cat1Desc);
			}
			bwEng.write(blkLine);
		}
		bwEng.close();

		String fileNameFra = null;

		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameFra = CimsFileUtils.buildAsciiFileName("ICD", "Cat1", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", "Desc");
		} else {
			fileNameFra = CimsFileUtils.buildAsciiFileName("ICD", "Cat1", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "fixed", "Desc");
		}
		File icdBlkDescFra = new File(
				icdBlkDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameFra);
		BufferedWriter bwFra = new BufferedWriter(new FileWriter(icdBlkDescFra));
		List<CodeDescriptionPublication> cat1DescsFra = publicationMapper.findICDCat1Desc(currentICDOpenContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, "FRA");
		PublicationStatistics icdCat1FraStatistics = new PublicationStatistics("ICD_CAT1_FRA_DESC", "French",
				cat1DescsFra.size());
		statisticsSummary.add(icdCat1FraStatistics);

		for (CodeDescriptionPublication cat1Desc : cat1DescsFra) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(cat1Desc, isBlock);
			} else {
				blkLine = buildICDCat1LineFixedWidth(cat1Desc);
			}
			bwFra.write(blkLine);
		}
		bwFra.close();
	}

	private String buildICDCat1LineFixedWidth(CodeDescriptionPublication cdp) {
		StringBuilder sb_line = new StringBuilder();
		String codeInFile = removeCharatersFromCode(cdp.getCode());
		CimsFileUtils.padBlanksToString(codeInFile, MAX_LENGTH_ICD_CAT1, sb_line);
		CimsFileUtils.padBlanksToString(cdp.getShortTitle(), MAX_LENGTH_ICD_SHORTTITLE, sb_line, true);
		CimsFileUtils.padBlanksToString(cdp.getLongTitle().replace("\r", " ").replace("\n", " "), MAX_LENGTH_LONGTITLE,
				sb_line);

		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private void buildICDCodeDescFiles(Long currentICDOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String releaseId, boolean isRelease, List<PublicationStatistics> statisticsSummary) throws IOException {

		String fileNameEng = null;
		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameEng = CimsFileUtils.buildAsciiFileName("ICD", "Code", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "tab", "Desc");
		} else {
			fileNameEng = CimsFileUtils.buildAsciiFileName("ICD", "Code", "Eng",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), releaseId, "fixed", "Desc");
		}
		File pubFolder = new File(pubDirectory);
		File subFolder = null;
		if (isRelease) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
		}
		// File icdFolder = createSubFolder(pubFolder, SUB_FOLDER_ICD);
		File icdCodeDescFolder = CimsFileUtils.createSubFolder(subFolder, SUB_FOLDER_CodeDesc);
		File icdCodeDescEngFolder = CimsFileUtils.createSubFolder(icdCodeDescFolder, SUB_FOLDER_ENG);
		File icdCodeDescFraFolder = CimsFileUtils.createSubFolder(icdCodeDescFolder, SUB_FOLDER_FRA);

		File icdCodeDescEng = new File(
				icdCodeDescEngFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameEng);
		BufferedWriter bwEng = new BufferedWriter(new FileWriter(icdCodeDescEng));
		Long icdClassIdCode = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Code");
		Long icdClassIdShortTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "ShortTitle");
		Long icdClassIdLongTitle = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "LongTitle");
		Long icdClassIdCategory = lookupMapper.findClassIdByClassificationAndClassName("ICD-10-CA", "Category");
		List<CodeDescriptionPublication> codeDescsEng = publicationMapper.findICDCodeDesc(currentICDOpenContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, "ENG");
		PublicationStatistics icdCodeEngStatistics = new PublicationStatistics("ICD_CODE_ENG_DESC", "English",
				codeDescsEng.size());
		statisticsSummary.add(icdCodeEngStatistics);
		boolean isBlock = false;
		for (CodeDescriptionPublication codeDesc : codeDescsEng) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(codeDesc, isBlock);
			} else {
				blkLine = buildICDCodeLineFixedWidth(codeDesc);
			}
			bwEng.write(blkLine);
		}
		bwEng.close();

		String fileNameFra = null;

		if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
			fileNameFra = CimsFileUtils.buildAsciiFileName("ICD", "Code", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", "Desc");
		} else {
			fileNameFra = CimsFileUtils.buildAsciiFileName("ICD", "Code", "Fra",
					String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "fixed", "Desc");
		}
		File icdCodeDescFra = new File(
				icdCodeDescFraFolder.getCanonicalPath() + System.getProperty("file.separator") + fileNameFra);
		BufferedWriter bwFra = new BufferedWriter(new FileWriter(icdCodeDescFra));
		List<CodeDescriptionPublication> codeDescsFra = publicationMapper.findICDCodeDesc(currentICDOpenContextId,
				icdClassIdCode, icdClassIdShortTitle, icdClassIdLongTitle, icdClassIdCategory, "FRA");
		PublicationStatistics icdCodeFraStatistics = new PublicationStatistics("ICD_CODE_FRA_DESC", "French",
				codeDescsFra.size());
		statisticsSummary.add(icdCodeFraStatistics);
		for (CodeDescriptionPublication codeDesc : codeDescsFra) {
			String blkLine = null;
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				blkLine = buildCodeDescriptionPublicationLineTab(codeDesc, isBlock);
			} else {
				blkLine = buildICDCodeLineFixedWidth(codeDesc);
			}
			bwFra.write(blkLine);
		}
		bwFra.close();
	}

	private String buildICDCodeLineFixedWidth(CodeDescriptionPublication cdp) {
		StringBuilder sb_line = new StringBuilder();
		String codeInFile = removeCharatersFromCode(cdp.getCode());
		CimsFileUtils.padBlanksToString(codeInFile, MAX_LENGTH_ICD_CODE, sb_line);
		CimsFileUtils.padBlanksToString(cdp.getShortTitle(), MAX_LENGTH_ICD_SHORTTITLE, sb_line, true);
		CimsFileUtils.padBlanksToString(cdp.getLongTitle().replace("\r", " ").replace("\n", " "), MAX_LENGTH_LONGTITLE,
				sb_line);

		sb_line.append(CimsFileUtils.LINE_SEPARATOR);

		return sb_line.toString();
	}

	private void buildStaticsSummaryFile(List<PublicationStatistics> statisticsSummary,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		File pubFolder = new File(pubDirectory);
		File subFolder = null;
		String statisticsFileName = null;
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI
				.equalsIgnoreCase(generateTablesModel.getClassification())) {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
			statisticsFileName = CCI_STATISTICS_FILE_NAME;
		} else {
			subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
			statisticsFileName = ICD_STATISTICS_FILE_NAME;
		}

		File statisticsSummaryFile = new File(
				subFolder.getCanonicalPath() + System.getProperty("file.separator") + statisticsFileName);
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheetStatistics = workbook.createSheet("Table Statistics Summary");
		String titleValue = "Table Statistics Summary for " + generateTablesModel.getCurrentOpenYear();
		Integer rownum = 2;
		Row titleRow = sheetStatistics.createRow(rownum);
		Cell titleRowCell = titleRow.createCell(1, Cell.CELL_TYPE_STRING);
		titleRowCell.setCellValue(titleValue);
		CellStyle style = CimsFileUtils.applyTitleStyle(sheetStatistics);
		titleRowCell.setCellStyle(style);
		sheetStatistics.addMergedRegion(new CellRangeAddress(rownum, rownum, 1, 4));
		sheetStatistics.setColumnWidth(1, 255 * 10);
		sheetStatistics.setColumnWidth(2, 255 * 25);
		sheetStatistics.setColumnWidth(3, 255 * 15);
		sheetStatistics.setColumnWidth(4, 255 * 15);

		rownum = rownum + 2;
		// classification row
		Row classificationRow = sheetStatistics.createRow(rownum);
		Cell classificationRowCell = classificationRow.createCell(1, Cell.CELL_TYPE_STRING);
		classificationRowCell.setCellValue(generateTablesModel.getClassification());

		rownum++;
		// table header row
		String[] columnDescs = new String[3];
		columnDescs[0] = "TABLE";
		columnDescs[1] = "LANGUAGE";
		columnDescs[2] = "RECORD COUNT";
		buildStatisticsTableHeaderLine(sheetStatistics, rownum, columnDescs);
		rownum++;
		if (statisticsSummary.size() > 0) {
			for (PublicationStatistics statistics : statisticsSummary) {
				Row rowStatistics = sheetStatistics.createRow(rownum);
				Cell tblStatisticsColumnCell1 = rowStatistics.createCell(2, Cell.CELL_TYPE_STRING);
				tblStatisticsColumnCell1.setCellValue(statistics.getTableName());
				Cell tblStatisticsColumnCell2 = rowStatistics.createCell(3, Cell.CELL_TYPE_STRING);
				tblStatisticsColumnCell2.setCellValue(statistics.getLanguage());
				Cell tblStatisticsColumnCell3 = rowStatistics.createCell(4, Cell.CELL_TYPE_STRING);
				tblStatisticsColumnCell3.setCellValue(statistics.getCount());
				rownum++;
			}
		}
		FileOutputStream outputFile = new FileOutputStream(statisticsSummaryFile);
		workbook.write(outputFile);
		outputFile.close();
	}

	private int buildStatisticsTableHeaderLine(HSSFSheet sheet, int rownum, String[] columnDescs) {
		Row tblHeader = sheet.createRow(rownum);
		CellStyle tableHeaderstyle = CimsFileUtils.applyTableHeaderStyle(sheet);
		int columnNum = columnDescs.length;
		for (int i = 0; i < columnNum; i++) {
			Cell tblHeaderColumnCell = tblHeader.createCell(i + 2, Cell.CELL_TYPE_STRING);
			tblHeaderColumnCell.setCellValue(columnDescs[i]);
			tblHeaderColumnCell.setCellStyle(tableHeaderstyle);
		}
		return rownum;
	}

	@Override
	public List<Long> closeYear(Long currentOpenYear, User currentUser) {
		publicationMapper.synchronizeCloseYear();
		// block unfreeze
		List<Long> newOpenedYears = null;

		String versionCode = String.valueOf(currentOpenYear);

		ContextDefinition icdCd = ContextDefinition.forVersion(CIMSConstants.ICD_10_CA, versionCode);
		ContextAccess icdContext = contextProvider.findContext(icdCd);
		ContextDefinition cciCd = ContextDefinition.forVersion(CIMSConstants.CCI, versionCode);
		ContextAccess cciContext = contextProvider.findContext(cciCd);
		// check if it is block
		FreezingStatus icdFreezingStatus = icdContext.getContextId().getFreezingStatus();
		if (FreezingStatus.BLK == icdFreezingStatus) { // closed already
			throw new ConcurrentCloseYearException("concurrent close");
		}

		// block unfreeze
		contextService.blockUnfreeze(icdContext.getContextId().getContextId());
		contextService.blockUnfreeze(cciContext.getContextId().getContextId());

		// 1. Update the request status of all change requests in the closing year from ""Closed-Approved"" to
		// ""Published"".

		changeRequestService.publishAllChangeRequestsForYear(versionCode, currentUser);
		// 2.Clean up (remove) all notification messages with the closing year.
		notificationService.removeAllNotificationsForYear(versionCode);
		// 3. Open up more years to the system to allow for change request submission and classification management.
		// See ADMIN-RU04 for details." , ICD and CCI

		icdContext.closeContext();

		cciContext.closeContext();

		// 4. if current year is two year before the version year(2018) , like 2016, we need create
		// 2019(non-version),2020(non-version), 2021 (version)
		ContextIdentifier currentOpenVersionICDContextIdentifier = lookupMapper
				.findLargestOpenVersionContextIdentifier(CIMSConstants.ICD_10_CA);
		ContextIdentifier currentOpenVersionCCIContextIdentifier = lookupMapper
				.findLargestOpenVersionContextIdentifier(CIMSConstants.CCI);
		Long currentOpenVersionYear = Long.valueOf(currentOpenVersionICDContextIdentifier.getVersionCode());
		if ((currentOpenVersionYear - currentOpenYear) == 2L) { // create three years ,

			newOpenedYears = new ArrayList<Long>();
			newOpenedYears.add(currentOpenVersionYear + 1);
			newOpenedYears.add(currentOpenVersionYear + 2);
			newOpenedYears.add(currentOpenVersionYear + 3);

			// ICD
			// ContextIdentifier currentICDContextId = icdContext.getContextId();
			ContextAccess firstCreatedICDContext = contextProvider.createContext(currentOpenVersionICDContextIdentifier,
					false);
			ContextIdentifier firstCreatedICDContextId = firstCreatedICDContext.getContextId();
			ContextAccess secondCreatedICDContext = contextProvider.createContext(firstCreatedICDContextId, false);
			ContextIdentifier secondCreatedICDContextId = secondCreatedICDContext.getContextId();
			ContextAccess thirdCreatedICDContext = contextProvider.createContext(secondCreatedICDContextId, true);

			// CCI
			// ContextIdentifier currentCCIContextId = cciContext.getContextId();
			ContextAccess firstCreatedCCIContext = contextProvider.createContext(currentOpenVersionCCIContextIdentifier,
					false);
			ContextIdentifier firstCreatedCCIContextId = firstCreatedCCIContext.getContextId();
			ContextAccess secondCreatedCCIContext = contextProvider.createContext(firstCreatedCCIContextId, false);
			ContextIdentifier secondCreatedCCIContextId = secondCreatedCCIContext.getContextId();
			ContextAccess thirdCreatedCCIContext = contextProvider.createContext(secondCreatedCCIContextId, true);

		}
		return newOpenedYears;
	}
	
	/* One time use function to adjust version years for the COVID-19 changes that caused the version year release to be delayed by one year: */
	public List<Long> addSingleYearContext(String icd, String cci) {
				List<Long> newOpenedYears = null;
				
				ContextIdentifier currentOpenVersionICDContextIdentifier = lookupMapper
						.findLargestOpenVersionContextIdentifier(CIMSConstants.ICD_10_CA);
				ContextIdentifier currentOpenVersionCCIContextIdentifier = lookupMapper
						.findLargestOpenVersionContextIdentifier(CIMSConstants.CCI);

				newOpenedYears = new ArrayList<Long>();

				// ICD
				// ContextIdentifier currentICDContextId = icdContext.getContextId();
				if(icd.equalsIgnoreCase("true")){
					ContextAccess firstCreatedICDContext = contextProvider.createContext(currentOpenVersionICDContextIdentifier,
						false);
				}
				// CCI
				// ContextIdentifier currentCCIContextId = cciContext.getContextId();
				if(cci.equalsIgnoreCase("true")){
					ContextAccess firstCreatedCCIContext = contextProvider.createContext(currentOpenVersionCCIContextIdentifier,
						false);
				}
					

				
				return newOpenedYears;
	}

	@Override
	public void createPublicationRelease(PublicationRelease publicationRelease) {
		publicationMapper.insertPublicationRelease(publicationRelease);
		publicationMapper.insertPublicationReleaseSnapShot(publicationRelease);
	}

	@Override
	public void createPublicationSnapShot(PublicationSnapShot publicationSnapShot) {
		publicationMapper.insertPublicationSnapShot(publicationSnapShot);
	}

	@Override
	public List<PublicationSnapShot> findAllLatestSnapShots() {
		return publicationMapper.findAllLatestSnapShots();
	}

	@Override
	public List<PublicationRelease> findAllReleases() {
		return publicationMapper.findAllReleases();
	}

	@Override
	public List<PublicationSnapShot> findAllSnapShotsByContextId(Long contextId) {
		return publicationMapper.findAllSnapShotsByContextId(contextId);
	}

	@Override
	public List<PublicationSnapShot> findAllSuccessLatestSnapShots() {
		return publicationMapper.findAllSuccessLatestSnapShots();
	}

	@Override
	public PublicationRelease findLatestHighestSuccessPublicationReleaseByFiscalYear(String fiscalYear) {
		PublicationRelease highestLevelSuccessPublicationRelease = null;
		List<PublicationRelease> allSuccessPublicationReleases = publicationMapper
				.findAllSuccessDescentOrderPublicationReleasesByFiscalYear(fiscalYear);
		if ((allSuccessPublicationReleases != null) && (allSuccessPublicationReleases.size() > 0)) {
			highestLevelSuccessPublicationRelease = allSuccessPublicationReleases.get(0);
		}
		return highestLevelSuccessPublicationRelease;
	}

	@Override
	public PublicationRelease findLatestPublicationReleaseByFiscalYear(String fiscalYear) {
		return publicationMapper.findLatestPublicationReleaseByFiscalYear(fiscalYear);
	}

	@Override
	public PublicationSnapShot findLatestSnapShotByContextId(Long contextId) {
		return publicationMapper.findLatestSnapShotByContextId(contextId);
	}

	@Override
	public PublicationRelease findLatestSuccessPublicationReleaseByFiscalYear(String fiscalYear) {
		return publicationMapper.findLatestSuccessPublicationReleaseByFiscalYear(fiscalYear);
	}

	@Override
	/*
	 * if the two snap shot dates are same, use same version number (non-Javadoc)
	 *
	 * @see ca.cihi.cims.service.PublicationService#findNextVersionNumber(java.lang.String,
	 * ca.cihi.cims.model.prodpub.ReleaseType, ca.cihi.cims.model.prodpub.PublicationSnapShot,
	 * ca.cihi.cims.model.prodpub.PublicationSnapShot)
	 */
	public Integer findNextVersionNumber(String fiscalYear, ReleaseType releaseType, PublicationSnapShot icdSnapShot,
			PublicationSnapShot cciSnapShot) {
		Integer nextVersionNumber;
		// Integer currentVersion = publicationMapper.findVersionCodeNumber(fiscalYear, releaseType);
		PublicationRelease latestSameTypePublicationRelease = publicationMapper
				.findLatestPublicationReleaseByFiscalYearAndReleaseType(fiscalYear, releaseType);

		if (latestSameTypePublicationRelease == null) {
			if ((ReleaseType.PRELIMINARY_INTERNAL_QA == releaseType) || (ReleaseType.PRELIMINARY == releaseType)) {
				nextVersionNumber = 1;
			} else {
				nextVersionNumber = 0;
			}
		} else {
			PublicationSnapShot latestICDSnapshot = null;
			PublicationSnapShot latestCCISnapshot = null;
			List<PublicationSnapShot> latestSnapShots = latestSameTypePublicationRelease.getPublicationSnapShots();
			for (PublicationSnapShot publicationSnapShot : latestSnapShots) {
				if (PublicationSnapShot.CLASSIFICATION_ICD.equalsIgnoreCase(publicationSnapShot.getClassification())) {
					latestICDSnapshot = publicationSnapShot;
				} else {
					latestCCISnapshot = publicationSnapShot;
				}
			}
			if (DateUtils.isSameDay(icdSnapShot.getCreatedDate(), latestICDSnapshot.getCreatedDate())
					&& DateUtils.isSameDay(cciSnapShot.getCreatedDate(), latestCCISnapshot.getCreatedDate())) {
				nextVersionNumber = latestSameTypePublicationRelease.getVersionCodeNumber();
			} else {
				nextVersionNumber = latestSameTypePublicationRelease.getVersionCodeNumber() + 1;
			}

		}
		return nextVersionNumber;
	}

	@Override
	public PublicationRelease findPublicationReleaseAndReleaseMsgTmpById(Long releaseId) {
		PublicationRelease publicationRelease = publicationMapper.findPublicationReleaseById(releaseId);
		// set msg temp
		String tmpTxt = findTemplateForReleaseEmailMsg(publicationRelease);
		publicationRelease.setReleaseNote(tmpTxt);
		return publicationRelease;
	}

	@Override
	public PublicationRelease findPublicationReleaseById(Long releaseId) {
		return publicationMapper.findPublicationReleaseById(releaseId);
	}

	@Override
	public String findReleaseZipFileName(Long releaseId) {
		String zipFileName = null;
		PublicationRelease release = findPublicationReleaseById(releaseId);
		zipFileName = release.getReleaseFileName();

		return zipFileName;
	}

	@Override
	public PublicationSnapShot findSnapShotById(Long snapShotId) {
		return publicationMapper.findSnapShotById(snapShotId);
	}

	/*
	 * this method return the folder and zip file name based on the snapShotI (non-Javadoc)
	 *
	 * @see ca.cihi.cims.service.PublicationService#findSnapShotZipFileWholeName(java.lang.Long)
	 */
	@Override
	public String findSnapShotZipFileName(Long snapShotId) {

		PublicationSnapShot snapShot = findSnapShotById(snapShotId);
		Date snapShotDate = snapShot.getCreatedDate();
		DateFormat sf = new SimpleDateFormat("yyyyMMdd");
		StringBuilder sb_zipFileName = new StringBuilder();
		sb_zipFileName.append(snapShot.getClassification()).append("_").append(sf.format(snapShotDate)).append("_");
		if (FileFormat.TAB == snapShot.getFileFormat()) {
			sb_zipFileName.append("TAB");
		} else {
			sb_zipFileName.append("FIX");
		}
		sb_zipFileName.append(FILE_EXT_ZIP);
		return sb_zipFileName.toString();
	}

	private String findTemplateForReleaseEmailMsg(PublicationRelease publicationRelease) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Map<String, Object> model = new HashMap<String, Object>();
		model.put(FISCAL_YEAR, publicationRelease.getFiscalYear());
		model.put(RELEASE_TYPE, publicationRelease.getReleaseType().getReleaseTypeCode());
		model.put(NEXT_RELEASE_TYPE, publicationRelease.getNextReleaseType().getReleaseTypeCode());
		model.put(RELEASE_DATE, sdf.format(publicationRelease.getCreatedDate()));

		String template = getMessageSource().getMessage(CIMS_RELEASE_NOTIFICATION_TEMPLATE, null, Locale.getDefault());
		String tempTxt = null;
		try {
			VelocityContext velocityContext = new VelocityContext();
			for (String name : model.keySet()) {
				velocityContext.put(name, model.get(name)); 
			}
			StringWriter stringWriter = new StringWriter();
			velocityEngine.mergeTemplate(template, "UTF-8", velocityContext, stringWriter);
			tempTxt = stringWriter.toString();
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Failed to send email notification."), e);
		}

		return tempTxt;
	}

	@Override
	public List<ComponentAndAttributeElementModel> findUnusedComponentElements(Long openBaseContextId,
			Long closedBaseContextId) {

		List<ComponentAndAttributeElementModel> unusedComponents = publicationMapper
				.findUnusedComponentElements(openBaseContextId, closedBaseContextId);
		// parsing the uuid to get the section and code , eg. CCI:ConceptVersion:GroupComp:Z2__1
		for (ComponentAndAttributeElementModel unusedComponent : unusedComponents) {
			String[] unusedComponentFields = StringUtils.split(unusedComponent.getElementUUID(), ":");
			String codeAndSection = unusedComponentFields[3];
			String[] codeOrSection = StringUtils.split(codeAndSection, "__");
			unusedComponent.setCode(codeOrSection[0]);
			unusedComponent.setSection(codeOrSection[1]);
		}
		return unusedComponents;

	}

	@Override
	public List<ComponentAndAttributeElementModel> findUnusedGenericAttributes(Long openBaseContextId,
			Long closedBaseContextId) {

		List<ComponentAndAttributeElementModel> unusedGenericAttributes = publicationMapper
				.findUnusedGenericAttributes(openBaseContextId, closedBaseContextId);
		// parsing the uuid to get the and type CCI:ConceptVersion:GenericAttribute:ZZ__S

		for (ComponentAndAttributeElementModel unusedGenericAttribute : unusedGenericAttributes) {
			String[] unusedGenericAttributeFields = StringUtils.split(unusedGenericAttribute.getElementUUID(), ":");
			String codeAndType = unusedGenericAttributeFields[3]; // ZZ__S
			String[] codeOrType = StringUtils.split(codeAndType, "__");
			String type = codeOrType[1]; // S , L, M or E
			String typeDesc = null;
			if ("S".equalsIgnoreCase(type)) {
				typeDesc = "Status";
			}
			if ("L".equalsIgnoreCase(type)) {
				typeDesc = "Location";
			}
			if ("M".equalsIgnoreCase(type)) {
				typeDesc = "Mode of Delivery";
			}
			if ("E".equalsIgnoreCase(type)) {
				typeDesc = "Extent";
			}
			unusedGenericAttribute.setType(typeDesc);

		}

		return unusedGenericAttributes;

	}

	@Override
	public List<ComponentAndAttributeElementModel> findUnusedReferenceValues(Long openBaseContextId,
			Long closedBaseContextId) {

		List<ComponentAndAttributeElementModel> unusedReferenceValues = publicationMapper
				.findUnusedReferenceValues(openBaseContextId, closedBaseContextId);
		return unusedReferenceValues;

	}

	/*
	 * generate files
	 *
	 * @see ca.cihi.cims.service.PublicationService#generateClassificationTables(ca.cihi.cims.model.prodpub.
	 * GenerateTablesCriteria)
	 */
	@Override
	public void generateClassificationTables(GenerateReleaseTablesCriteria generateTablesModel, User currentUser,
			String sessionId) throws Exception {
		// remove wrap up notifications
		notificationService.removeWrapupWorkNotifcation(generateTablesModel);

		String baseClassification = generateTablesModel.getClassification();
		Long currentOpenYear = generateTablesModel.getCurrentOpenYear();
		currentProcessingYear = String.valueOf(currentOpenYear);
		Date snapShotDate = Calendar.getInstance().getTime();

		Long lastVersionYear = currentOpenYear - 1;
		Long currentCCIOpenContextId = null;
		Long lastVersionCCIContextId = null;
		Long currentICDOpenContextId = null;
		Long lastVersionICDContextId = null;
		PublicationSnapShot cciPublicationSnapShot = null;
		PublicationSnapShot icdPublicationSnapShot = null;
		List<PublicationStatistics> statisticsSummary = new ArrayList<PublicationStatistics>();

		File pubFolder = new File(pubDirectory);
		File snapShotFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_SNAPSHOT);
		boolean isRelease = false;
		String releaseId = null;
		// create snapshot first , then generate files
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equals(baseClassification)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(baseClassification)) { // CCI or
			// Both

			ContextIdentifier currentCCIOpenContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("CCI", String.valueOf(currentOpenYear));
			ContextIdentifier lastVersionCCIContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("CCI", String.valueOf(lastVersionYear));

			currentCCIOpenContextId = currentCCIOpenContext.getContextId();
			lastVersionCCIContextId = lastVersionCCIContext.getContextId();
			// freeze tabular changes , comment it out for now
			contextService.freezeTabularChanges(currentCCIOpenContextId);
			Integer lstSnapShotSeqNumber = publicationMapper.findSnapShotSeqNumber(currentCCIOpenContextId);
			Integer currentSnapShotSeqNumber = null;
			if (lstSnapShotSeqNumber == null) {
				currentSnapShotSeqNumber = 1;
			} else {
				currentSnapShotSeqNumber = lstSnapShotSeqNumber + 1;
			}
			cciPublicationSnapShot = new PublicationSnapShot();
			cciPublicationSnapShot.setStructureId(currentCCIOpenContextId);
			cciPublicationSnapShot.setSnapShotSeqNumber(currentSnapShotSeqNumber);
			cciPublicationSnapShot.setSnapShotNote(generateTablesModel.getNote());
			cciPublicationSnapShot.setCreatedByUserId(currentUser.getUserId());
			cciPublicationSnapShot.setCreatedDate(Calendar.getInstance().getTime());
			cciPublicationSnapShot.setStatus(GenerateFileStatus.I);
			cciPublicationSnapShot.setFileFormat(generateTablesModel.getFileFormat());

			createPublicationSnapShot(cciPublicationSnapShot);
		}
		// clear CCI folder
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equals(baseClassification)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(baseClassification)) { // ICD only
			// or Both

			ContextIdentifier currentICDOpenContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(currentOpenYear));
			ContextIdentifier lastVersionICDContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(lastVersionYear));

			currentICDOpenContextId = currentICDOpenContext.getContextId();
			lastVersionICDContextId = lastVersionICDContext.getContextId();
			// freeze tabular changes

			contextService.freezeTabularChanges(currentICDOpenContextId);

			Integer lstSnapShotSeqNumber = publicationMapper.findSnapShotSeqNumber(currentICDOpenContextId);
			Integer currentSnapShotSeqNumber = null;
			if (lstSnapShotSeqNumber == null) {
				currentSnapShotSeqNumber = 1;
			} else {
				currentSnapShotSeqNumber = lstSnapShotSeqNumber + 1;
			}
			icdPublicationSnapShot = new PublicationSnapShot();
			icdPublicationSnapShot.setStructureId(currentICDOpenContextId);
			icdPublicationSnapShot.setSnapShotSeqNumber(currentSnapShotSeqNumber);
			icdPublicationSnapShot.setSnapShotNote(generateTablesModel.getNote());
			icdPublicationSnapShot.setCreatedByUserId(currentUser.getUserId());
			icdPublicationSnapShot.setCreatedDate(Calendar.getInstance().getTime());
			icdPublicationSnapShot.setStatus(GenerateFileStatus.I);
			icdPublicationSnapShot.setFileFormat(generateTablesModel.getFileFormat());
			createPublicationSnapShot(icdPublicationSnapShot);

		}

		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equals(baseClassification)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(baseClassification)) {
			String sessionKey = sessionId + "CCI";
			processingFileNameMap.put(sessionKey, "CCI");
			// ThreadLocalGenerateFileName.set("CCI");
			File cciFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_CCI);
			CimsFileUtils.cleanFolder(cciFolder);
			try {
				// 1. build CCI BLK
				processingFileNameMap.put(sessionKey, "CCI ASCII files are being generated.");
				buildCCIBlkDescFiles(currentCCIOpenContextId, generateTablesModel, releaseId, isRelease,
						statisticsSummary);
				// 2. build CCI Rubric
				buildCCIRubricDescFiles(currentCCIOpenContextId, generateTablesModel, releaseId, isRelease,
						statisticsSummary);
				// 3. build CCI Code
				buildCCICodeDescFiles(currentCCIOpenContextId, generateTablesModel, releaseId, isRelease,
						statisticsSummary);
				// 4. build CCI Validation
				FileGenerator cciValidationFileGenerator = fileGeneratorFactory
						.createFileGenerator("CCIValidationFile");
				cciValidationFileGenerator.generateAsciiFile(currentCCIOpenContextId, generateTablesModel, releaseId,
						isRelease, statisticsSummary, currentUser);
				// 5. build CCI Status Desc
				FileGenerator cciStatusFileGenerator = fileGeneratorFactory.createFileGenerator("CCIStatusFile");
				cciStatusFileGenerator.generateAsciiFile(currentCCIOpenContextId, generateTablesModel, releaseId,
						isRelease, statisticsSummary, currentUser);
				// 6. build CCI Location Desc
				FileGenerator cciLocationFileGenerator = fileGeneratorFactory.createFileGenerator("CCILocationFile");
				cciLocationFileGenerator.generateAsciiFile(currentCCIOpenContextId, generateTablesModel, releaseId,
						isRelease, statisticsSummary, currentUser);
				// 7. build CCI Extent Desc
				FileGenerator cciExtentFileGenerator = fileGeneratorFactory.createFileGenerator("CCIExtentFile");
				cciExtentFileGenerator.generateAsciiFile(currentCCIOpenContextId, generateTablesModel, releaseId,
						isRelease, statisticsSummary, currentUser);

				// build CCI statistics summary file
				processingFileNameMap.put(sessionKey, "CCI statistic summary  is being generated.");
				buildStaticsSummaryFile(statisticsSummary, generateTablesModel);
				// build CCI Block Rubric audit file
				processingFileNameMap.put(sessionKey, "CCI audit reports are being generated.");
				buildAuditReportCCIBlockRubricDesc(currentCCIOpenContextId, lastVersionCCIContextId,
						generateTablesModel);
				// build CCI Code desc audit file

				buildAuditReportCCICodeDesc(currentCCIOpenContextId, lastVersionCCIContextId, generateTablesModel);

				cciValidationFileGenerator.generateAuditFile(currentCCIOpenContextId, lastVersionCCIContextId,
						generateTablesModel);
				cciStatusFileGenerator.generateAuditFile(currentCCIOpenContextId, lastVersionCCIContextId,
						generateTablesModel);
				cciLocationFileGenerator.generateAuditFile(currentCCIOpenContextId, lastVersionCCIContextId,
						generateTablesModel);
				cciExtentFileGenerator.generateAuditFile(currentCCIOpenContextId, lastVersionCCIContextId,
						generateTablesModel);

				// cci zip file name
				DateFormat sf = new SimpleDateFormat("yyyyMMdd");
				StringBuilder sb_cciZipFileName = new StringBuilder();
				sb_cciZipFileName.append("CCI_").append(sf.format(snapShotDate)).append("_");
				if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
					sb_cciZipFileName.append("TAB");
				} else {
					sb_cciZipFileName.append("FIX");
				}
				sb_cciZipFileName.append(FILE_EXT_ZIP);
				String cciZipFileName = sb_cciZipFileName.toString();

				CimsFileUtils.zipFilesForFolder(pubDirectory + "CCI" + System.getProperty("file.separator"),
						pubDirectory + "SNAPSHOT" + System.getProperty("file.separator"), cciZipFileName);

				cciPublicationSnapShot.setStatus(GenerateFileStatus.E);
				updatePublicationSnapShot(cciPublicationSnapShot);

			} catch (Exception e) {
				cciPublicationSnapShot.setStatus(GenerateFileStatus.F);
				cciPublicationSnapShot.setFailedReason(e.getMessage());
				updatePublicationSnapShot(cciPublicationSnapShot);
				// unfreeze tabular changes
				contextService.unfreezeTabularChanges(currentCCIOpenContextId);

				throw e;
			} finally {
				processingFileNameMap.remove(sessionKey);
			}
		}

		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equals(baseClassification)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(baseClassification)) { // ICD only
			// or Both
			String sessionKey = sessionId + GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD;
			processingFileNameMap.put(sessionKey, "ICD");
			ContextIdentifier currentICDOpenContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(currentOpenYear));
			ContextIdentifier lastVersionICDContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(lastVersionYear));

			currentICDOpenContextId = currentICDOpenContext.getContextId();
			lastVersionICDContextId = lastVersionICDContext.getContextId();
			// freeze tabular changes
			contextService.freezeTabularChanges(currentICDOpenContextId);
			// clean ICD folder
			// File pubFolder = new File(pubDirectory);
			File icdFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD);
			CimsFileUtils.cleanFolder(icdFolder);
			try {
				// 1 .build ICD BLK
				processingFileNameMap.put(sessionKey, "ICD ASCII files are being generated.");
				buildICDBlkDescFiles(currentICDOpenContextId, generateTablesModel, releaseId, isRelease,
						statisticsSummary);

				buildICDCat1DescFiles(currentICDOpenContextId, generateTablesModel, releaseId, isRelease,
						statisticsSummary);
				// 3. build ICD Code

				buildICDCodeDescFiles(currentICDOpenContextId, generateTablesModel, releaseId, isRelease,
						statisticsSummary);
				// 4. build ICD Validation

				FileGenerator icdValidationFileGenerator = fileGeneratorFactory
						.createFileGenerator("ICDValidationFile");
				icdValidationFileGenerator.generateAsciiFile(currentICDOpenContextId, generateTablesModel, releaseId,
						isRelease, statisticsSummary, currentUser);

				processingFileNameMap.put(sessionKey, "ICD statistic summery  is being generated.");
				buildStaticsSummaryFile(statisticsSummary, generateTablesModel);

				// build audit file
				processingFileNameMap.put(sessionKey, "ICD audit reports are being generated.");
				buildAuditReportICDBlockCategoryDesc(currentICDOpenContextId, lastVersionICDContextId,
						generateTablesModel);

				// build ICD Code desc audit file
				buildAuditReportICDCodeDesc(currentICDOpenContextId, lastVersionICDContextId, generateTablesModel);

				icdValidationFileGenerator.generateAuditFile(currentICDOpenContextId, lastVersionICDContextId,
						generateTablesModel);

				// ICD zip File Name
				DateFormat sf = new SimpleDateFormat("yyyyMMdd");
				StringBuilder sb_icdZipFileName = new StringBuilder();
				sb_icdZipFileName.append("ICD_").append(sf.format(snapShotDate)).append("_");
				if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
					sb_icdZipFileName.append("TAB");
				} else {
					sb_icdZipFileName.append("FIX");
				}
				sb_icdZipFileName.append(FILE_EXT_ZIP);
				String icdZipFileName = sb_icdZipFileName.toString();

				CimsFileUtils.zipFilesForFolder(pubDirectory + "ICD" + System.getProperty("file.separator"),
						pubDirectory + "SNAPSHOT" + System.getProperty("file.separator"), icdZipFileName);
				icdPublicationSnapShot.setStatus(GenerateFileStatus.E);
				updatePublicationSnapShot(icdPublicationSnapShot);
			} catch (Exception e) {
				icdPublicationSnapShot.setStatus(GenerateFileStatus.F);
				icdPublicationSnapShot.setFailedReason(e.getMessage());
				updatePublicationSnapShot(icdPublicationSnapShot);
				contextService.unfreezeTabularChanges(currentICDOpenContextId);
				throw new RuntimeException(e);
			} finally {
				processingFileNameMap.remove(sessionKey);
			}

		}

	}

	private void generateRevisedNewRemovedDescsFromCurrentAndLastVersion(List<CodeDescriptionAudit> revisedLongDescs,
			List<CodeDescriptionAudit> revisedShortDescs, List<CodeDescriptionPublication> newDescs,
			List<CodeDescriptionPublication> removedDescs, List<CodeDescriptionPublication> currentDescs,
			List<CodeDescriptionPublication> lastDescs) {
		Map<String, CodeDescriptionPublication> currentDescMap = new HashMap<String, CodeDescriptionPublication>();
		for (CodeDescriptionPublication currentDesc : currentDescs) {
			currentDescMap.put(currentDesc.getCode(), currentDesc);
		}
		Map<String, CodeDescriptionPublication> lastDescMap = new HashMap<String, CodeDescriptionPublication>();
		for (CodeDescriptionPublication lastDesc : lastDescs) {
			lastDescMap.put(lastDesc.getCode(), lastDesc);
		}

		Iterator<Entry<String, CodeDescriptionPublication>> itEntrys = currentDescMap.entrySet().iterator();
		while (itEntrys.hasNext()) {
			String code = itEntrys.next().getKey();
			CodeDescriptionPublication currentCdp = currentDescMap.get(code);
			CodeDescriptionPublication lastCdp = lastDescMap.get(code);
			if (lastCdp != null) {

				if (currentCdp.getLongTitle().equals(lastCdp.getLongTitle())
						&& currentCdp.getShortTitle().equals(lastCdp.getShortTitle())) {
					// no changes
				} else { // there is changes
					if (!currentCdp.getLongTitle().equals(lastCdp.getLongTitle())) { // long title got changes
						CodeDescriptionAudit cda = new CodeDescriptionAudit(code, currentCdp.getLongTitle(),
								lastCdp.getLongTitle());
						revisedLongDescs.add(cda);
					}
					if (!currentCdp.getShortTitle().equals(lastCdp.getShortTitle())) { // short title got changed
						CodeDescriptionAudit cda = new CodeDescriptionAudit(code, currentCdp.getShortTitle(),
								lastCdp.getShortTitle());
						revisedShortDescs.add(cda);
					}
				}
				itEntrys.remove();

				lastDescMap.remove(code);

			} else { // it is new added
				newDescs.add(currentCdp);
			}
		}
		//
		if (!lastDescMap.isEmpty()) { // those are removed cat1s , as there are not in currentCat1DescEngMap
			removedDescs.addAll(lastDescMap.values());
		}

		Collections.sort(revisedLongDescs);
		Collections.sort(revisedShortDescs);
		Collections.sort(removedDescs);
		Collections.sort(newDescs);
	}

	public ASOTService getAsotService() {
		return asotService;
	}

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public ContextService getContextService() {
		return contextService;
	}

	@Override
	public String getCurrentProcessingFile(String sessionId) {
		return processingFileNameMap.get(sessionId);
	}

	@Override
	public String getCurrentProcessingYear() {
		return currentProcessingYear;
	}

	public EmailService getEmailService() {
		return emailService;
	}

	public FileGeneratorFactory getFileGeneratorFactory() {
		return fileGeneratorFactory;
	}

	public LookupMapper getLookupMapper() {
		return lookupMapper;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public String getPubDirectory() {
		return pubDirectory;
	}

	public PublicationMapper getPublicationMapper() {
		return publicationMapper;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	@Override
	public boolean isGenerateFileProcessRunning(GenerateReleaseTablesCriteria generateTablesModel) {
		boolean alreadyRunning = false;
		String baseClassification = generateTablesModel.getClassification();
		Long currentOpenYear = generateTablesModel.getCurrentOpenYear();
		currentProcessingYear = String.valueOf(currentOpenYear);
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equals(baseClassification)
				|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(baseClassification)) { // check cci
			ContextIdentifier currentCCIOpenContext = lookupMapper
					.findBaseContextIdentifierByClassificationAndYear("CCI", String.valueOf(currentOpenYear));
			PublicationSnapShot latestCCISnapShot = publicationMapper
					.findLatestSnapShotByContextId(currentCCIOpenContext.getContextId());
			if ((latestCCISnapShot != null) && (GenerateFileStatus.I == latestCCISnapShot.getStatus())) {
				if ((Calendar.getInstance().getTimeInMillis()
						- latestCCISnapShot.getCreatedDate().getTime()) < TWO_HOURS) {
					alreadyRunning = true;
				}

			}
		}
		if (!alreadyRunning) {
			if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equals(baseClassification)
					|| GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(baseClassification)) { // check icd
				ContextIdentifier currentICDOpenContext = lookupMapper
						.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(currentOpenYear));
				PublicationSnapShot latestICDSnapShot = publicationMapper
						.findLatestSnapShotByContextId(currentICDOpenContext.getContextId());
				if ((latestICDSnapShot != null) && (latestICDSnapShot.getStatus() == GenerateFileStatus.I)) {
					if ((Calendar.getInstance().getTimeInMillis()
							- latestICDSnapShot.getCreatedDate().getTime()) < TWO_HOURS) {
						alreadyRunning = true;
					}

				}
			}
		}

		return alreadyRunning;

	}

	private void mergerAndRenameAuditFile(String releaseDesc, String versionYear, String releaseId) throws IOException {

		StringBuilder auditFileTitleICDEng = new StringBuilder();
		auditFileTitleICDEng.append(CimsFileUtils.buildAuditReportCommonTitle(LANGUAGE_CODE_ENG, "10CA",
				Long.parseLong(versionYear), releaseId));
		StringBuilder auditFileTitleICDFra = new StringBuilder();
		auditFileTitleICDFra.append(CimsFileUtils.buildAuditReportCommonTitle(LANGUAGE_CODE_FRA, "10CA",
				Long.parseLong(versionYear), releaseId));
		StringBuilder auditFileTitleCCIEng = new StringBuilder();
		auditFileTitleCCIEng.append(CimsFileUtils.buildAuditReportCommonTitle(LANGUAGE_CODE_ENG, "CCI",
				Long.parseLong(versionYear), releaseId));
		StringBuilder auditFileTitleCCIFra = new StringBuilder();
		auditFileTitleCCIFra.append(CimsFileUtils.buildAuditReportCommonTitle(LANGUAGE_CODE_FRA, "CCI",
				Long.parseLong(versionYear), releaseId));

		String blockRubricAuditFileFolderEng = pubDirectory + SUB_FOLDER_ICD_CCI + File.separator + SUB_FOLDER_BlkDesc
				+ File.separator + SUB_FOLDER_ENG;
		String cciBlockRubricAuditFileNameEnglish = blockRubricAuditFileFolderEng + File.separator
				+ CimsFileUtils.findCCIBlockRubricAuditFileNameEnglish(versionYear);
		String icdBlockCategoryAuditFileNameEnglish = blockRubricAuditFileFolderEng + File.separator
				+ CimsFileUtils.findICDBlockCategoryAuditFileNameEnglish(versionYear);
		String releaseBlockRubricCategoryAuditFileNameEnglish = blockRubricAuditFileFolderEng + File.separator
				+ CimsFileUtils.findReleaseBlockRubricCategoryAuditFileNameEnglish(releaseDesc, versionYear);

		String codeAuditFileFolderEng = pubDirectory + SUB_FOLDER_ICD_CCI + File.separator + SUB_FOLDER_CodeDesc
				+ File.separator + SUB_FOLDER_ENG;
		String cciCodeAuditFileNameEnglish = codeAuditFileFolderEng + File.separator
				+ CimsFileUtils.findCCICodeAuditFileNameEnglish(versionYear);
		String icdCodeAuditFileNameEnglish = codeAuditFileFolderEng + File.separator
				+ CimsFileUtils.findICDCodeAuditFileNameEnglish(versionYear);
		String releaseCodeAuditFileNameEnglish = codeAuditFileFolderEng + File.separator
				+ CimsFileUtils.findReleaseCodeAuditFileNameEnglish(releaseDesc, versionYear);

		String blockRubricAuditFileFolderFra = pubDirectory + SUB_FOLDER_ICD_CCI + File.separator + SUB_FOLDER_BlkDesc
				+ File.separator + SUB_FOLDER_FRA;
		String cciBlockRubricAuditFileNameFrench = blockRubricAuditFileFolderFra + File.separator
				+ CimsFileUtils.findCCIBlockRubricAuditFileNameFrench(versionYear);
		String icdBlockCategoryAuditFileNameFrench = blockRubricAuditFileFolderFra + File.separator
				+ CimsFileUtils.findICDBlockCategoryAuditFileNameFrench(versionYear);
		String releaseBlockRubricCategoryAuditFileNameFrench = blockRubricAuditFileFolderFra + File.separator
				+ CimsFileUtils.findReleaseBlockRubricCategoryAuditFileNameFrench(releaseDesc, versionYear);

		String codeAuditFileFolderFra = pubDirectory + SUB_FOLDER_ICD_CCI + File.separator + SUB_FOLDER_CodeDesc
				+ File.separator + SUB_FOLDER_FRA;
		String cciCodeAuditFileNameFrench = codeAuditFileFolderFra + File.separator
				+ CimsFileUtils.findCCICodeAuditFileNameFrench(versionYear);
		String icdCodeAuditFileNameFrench = codeAuditFileFolderFra + File.separator
				+ CimsFileUtils.findICDCodeAuditFileNameFrench(versionYear);
		String releaseCodeAuditFileNameFrench = codeAuditFileFolderFra + File.separator
				+ CimsFileUtils.findReleaseCodeAuditFileNameFrench(releaseDesc, versionYear);

		String validationAuditFileFolderEng = pubDirectory + SUB_FOLDER_ICD_CCI + File.separator + SUB_FOLDER_Validation
				+ File.separator + SUB_FOLDER_ENG;
		String cciValidationFileNameEnglish = validationAuditFileFolderEng + File.separator
				+ CimsFileUtils.findCCIValidationAuditFileNameEnglish(versionYear);
		String icdValidationFileNameEnglish = validationAuditFileFolderEng + File.separator
				+ CimsFileUtils.findICDValidationAuditFileNameEnglish(versionYear);
		String releaseValidationFileNameEnglish = validationAuditFileFolderEng + File.separator
				+ CimsFileUtils.findReleaseValidationAuditFileNameEnglish(releaseDesc, versionYear);

		String validationAuditFileFolderFra = pubDirectory + SUB_FOLDER_ICD_CCI + File.separator + SUB_FOLDER_Validation
				+ File.separator + SUB_FOLDER_FRA;
		String cciValidationFileNameFrench = validationAuditFileFolderFra + File.separator
				+ CimsFileUtils.findCCIValidationAuditFileNameFrench(versionYear);
		String icdValidationFileNameFrench = validationAuditFileFolderFra + File.separator
				+ CimsFileUtils.findICDValidationAuditFileNameFrench(versionYear);
		String releaseValidationFileNameFrench = validationAuditFileFolderFra + File.separator
				+ CimsFileUtils.findReleaseValidationAuditFileNameFrench(releaseDesc, versionYear);

		// mergeExcelFiles(String sourceFileName, String targetFileName);
		CimsFileUtils.mergeExcelFiles(cciBlockRubricAuditFileNameEnglish, icdBlockCategoryAuditFileNameEnglish,
				releaseBlockRubricCategoryAuditFileNameEnglish, auditFileTitleCCIEng, auditFileTitleICDEng);

		CimsFileUtils.mergeExcelFiles(cciBlockRubricAuditFileNameFrench, icdBlockCategoryAuditFileNameFrench,
				releaseBlockRubricCategoryAuditFileNameFrench, auditFileTitleCCIFra, auditFileTitleICDFra);

		CimsFileUtils.mergeExcelFiles(cciCodeAuditFileNameEnglish, icdCodeAuditFileNameEnglish,
				releaseCodeAuditFileNameEnglish, auditFileTitleCCIEng, auditFileTitleICDEng);

		CimsFileUtils.mergeExcelFiles(cciCodeAuditFileNameFrench, icdCodeAuditFileNameFrench,
				releaseCodeAuditFileNameFrench, auditFileTitleCCIFra, auditFileTitleICDFra);

		CimsFileUtils.mergeExcelFiles(cciValidationFileNameEnglish, icdValidationFileNameEnglish,
				releaseValidationFileNameEnglish, auditFileTitleCCIEng, auditFileTitleICDEng);

		CimsFileUtils.mergeExcelFiles(cciValidationFileNameFrench, icdValidationFileNameFrench,
				releaseValidationFileNameFrench, auditFileTitleCCIFra, auditFileTitleICDFra);

	}

	@Override
	public void notifyUsersToWrapupWork(GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser) {
		// 1. notify content developers reviewers to wrap up
		notificationService.postWrapupWorkNotifcationToContentDeveloperAndReviewer(generateTablesCriteria, currentUser);

		// 2. notify administrator to wrap up
		notificationService.postWrapupWorkNotifcationToAdministrator(generateTablesCriteria, currentUser);

	}

	@Override
	public void releaseClassificationTables(GenerateReleaseTablesCriteria releaseTablesModel, User currentUser,
			String sessionId) throws Exception {
		publicationMapper.synchronizeRelease();
		Long currentOpenYear = releaseTablesModel.getCurrentOpenYear();
		currentProcessingYear = String.valueOf(currentOpenYear);

		ContextIdentifier currentCCIOpenContext = lookupMapper.findBaseContextIdentifierByClassificationAndYear("CCI",
				String.valueOf(currentOpenYear));

		Long currentCCIOpenContextId = currentCCIOpenContext.getContextId();

		ContextIdentifier currentICDOpenContext = lookupMapper
				.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", String.valueOf(currentOpenYear));

		Long currentICDOpenContextId = currentICDOpenContext.getContextId();

		PublicationSnapShot icdSnapShot = publicationMapper
				.findLatestSuccessFixedWidthSnapShotByContextId(currentICDOpenContextId);
		PublicationSnapShot cciSnapShot = publicationMapper
				.findLatestSuccessFixedWidthSnapShotByContextId(currentCCIOpenContextId);

		List<PublicationSnapShot> publicationSnapShots = new ArrayList<PublicationSnapShot>();
		publicationSnapShots.add(icdSnapShot);
		publicationSnapShots.add(cciSnapShot);

		Integer nextVersionNumber = findNextVersionNumber(String.valueOf(releaseTablesModel.getCurrentOpenYear()),
				ReleaseType.fromString(releaseTablesModel.getReleaseType()), icdSnapShot, cciSnapShot);

		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setFiscalYear(String.valueOf(releaseTablesModel.getCurrentOpenYear()));
		publicationRelease.setVersionCodeNumber(nextVersionNumber);

		publicationRelease.setCreatedByUserId(currentUser.getUserId());

		publicationRelease.setCreatedDate(Calendar.getInstance().getTime());
		publicationRelease.setReleaseType(ReleaseType.fromString(releaseTablesModel.getReleaseType()));
		publicationRelease.setStatus(GenerateFileStatus.I);
		publicationRelease.setPublicationSnapShots(publicationSnapShots);
		createPublicationRelease(publicationRelease);

		try {
			// asotService.generateASOT(String.valueOf(releaseTablesModel.getCurrentOpenYear()),
			// publicationRelease.getReleaseId(), currentUser.getEmail());
			Date releaseDate = Calendar.getInstance().getTime();
			DateFormat sf = new SimpleDateFormat("yyyyMMdd");
			StringBuilder sb_releaseZipFileName = new StringBuilder();
			sb_releaseZipFileName.append(releaseTablesModel.getReleaseType());
			sb_releaseZipFileName.append("_");
			String releaseId = null;
			sb_releaseZipFileName.append(sf.format(releaseDate));

			if (releaseTablesModel.getReleaseType().equalsIgnoreCase(ReleaseType.PRELIMINARY.getReleaseTypeCode())
					|| releaseTablesModel.getReleaseType()
							.equalsIgnoreCase(ReleaseType.OFFICIAL.getReleaseTypeCode())) {
				if (releaseTablesModel.getReleaseType()
						.equalsIgnoreCase(ReleaseType.PRELIMINARY.getReleaseTypeCode())) {
					releaseId = "V0." + nextVersionNumber;
				} else {
					releaseId = "V1." + nextVersionNumber;
				}
				sb_releaseZipFileName.append("_");
				sb_releaseZipFileName.append(releaseId);
			}
			String releaseDesc = sf.format(releaseDate) + ((releaseId != null) ? releaseId : "");
			sb_releaseZipFileName.append(FILE_EXT_ZIP);
			File pubFolder = new File(pubDirectory);
			File releaseFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_RELEASE);
			String icdcciFolder = pubDirectory + SUB_FOLDER_ICD_CCI;
			File subFolder = CimsFileUtils.createSubFolder(pubFolder, SUB_FOLDER_ICD_CCI);
			CimsFileUtils.cleanFolder(subFolder);
			String cciFixedWidthZipFileName = findSnapShotZipFileName(cciSnapShot.getSnapShotId());
			String icdFixedWidthZipFileName = findSnapShotZipFileName(icdSnapShot.getSnapShotId());
			String cciFixedWidthZipFileWholeName = pubDirectory + SUB_FOLDER_SNAPSHOT + File.separator
					+ cciFixedWidthZipFileName;
			String icdFixedWidthZipFileWholeName = pubDirectory + SUB_FOLDER_SNAPSHOT + File.separator
					+ icdFixedWidthZipFileName;
			CimsFileUtils.unzipFileAndRenameAsciiFileToFolder(cciFixedWidthZipFileWholeName, releaseDesc, icdcciFolder);
			CimsFileUtils.unzipFileAndRenameAsciiFileToFolder(icdFixedWidthZipFileWholeName, releaseDesc, icdcciFolder);
			Thread.sleep(10000); // stop for 10 secs
			// remove two statistics file
			String cciStatisticsFileName = CCI_STATISTICS_FILE_NAME;
			String icdStatisticsFileName = ICD_STATISTICS_FILE_NAME;
			File cciStatisticsFile = new File(subFolder, cciStatisticsFileName);
			File icdStatisticsFile = new File(subFolder, icdStatisticsFileName);
			FileUtils.forceDelete(cciStatisticsFile);
			FileUtils.forceDelete(icdStatisticsFile);

			// merger the audit files

			// rename all the ascii file name, CCI_Code_Eng_Desc_CCI<year>_<YYYYMMDD>_<release ID>_<format>

			mergerAndRenameAuditFile(releaseDesc, currentProcessingYear, releaseId);

			CimsFileUtils.zipFilesForFolder(pubDirectory + SUB_FOLDER_ICD_CCI + System.getProperty("file.separator"),
					pubDirectory + SUB_FOLDER_RELEASE + System.getProperty("file.separator"),
					sb_releaseZipFileName.toString());

			publicationRelease.setStatus(GenerateFileStatus.E);
			notificationService.postPackageReleaseNotifcation(releaseTablesModel, currentUser);
			// The notification sent to DL-Internal Release, DL-Preliminary Release and DL-Official Release are via
			// email
			emailService.sendReleaseTableNotificationEmail(releaseTablesModel, currentUser);

		} catch (Exception e) {
			publicationRelease.setStatus(GenerateFileStatus.F);
			publicationRelease.setFailedReason(e.getMessage());
			throw e;
		} finally {
			processingFileNameMap.remove(sessionId);
			updatePublicationRelease(publicationRelease);
		}
	}

	private String removeCharatersFromCode(String code) {
		String codeInFile = code.replace(".", "");
		codeInFile = codeInFile.replace("-", "");
		codeInFile = codeInFile.replace("^", "");
		codeInFile = codeInFile.replace("/", "");
		return codeInFile;
	}

	private String removeCharatersFromCodeKeepDash(String code) {
		String codeInFile = code.replace(".", "");
		codeInFile = codeInFile.replace("^", "");
		codeInFile = codeInFile.replace("/", "");
		return codeInFile;
	}

	@Override
	@Transactional
	public void sendReleaseEmailNotification(PublicationRelease publicationRelease) {
		publicationRelease.setNotificationSent(true);
		publicationMapper.updatePublicationRelease(publicationRelease);
		emailService.emailReleaseNotification(publicationRelease);

	}

	public void setAsotService(ASOTService asotService) {
		this.asotService = asotService;
	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setCurrentProcessingYear(String currentProcessingYear) {
		this.currentProcessingYear = currentProcessingYear;
	}

	public void setEmailService(EmailService emailService) {
		this.emailService = emailService;
	}

	@Autowired
	public void setFileGeneratorFactory(FileGeneratorFactory fileGeneratorFactory) {
		this.fileGeneratorFactory = fileGeneratorFactory;
	}

	public void setLookupMapper(LookupMapper lookupMapper) {
		this.lookupMapper = lookupMapper;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public void setPubDirectory(String pubDirectory) {
		this.pubDirectory = pubDirectory;
	}

	public void setPublicationMapper(PublicationMapper publicationMapper) {
		this.publicationMapper = publicationMapper;
	}

	public void setVelocityEngine(VelocityEngine velocityEngine) {
		this.velocityEngine = velocityEngine;
	}

	@Override
	public void unfreezeTabularChanges(Long baseContextId) {
		contextService.unfreezeTabularChanges(baseContextId);
	}

	@Override
	public void updatePublicationRelease(PublicationRelease publicationRelease) {
		publicationMapper.updatePublicationRelease(publicationRelease);
	}

	@Override
	public void updatePublicationSnapShot(PublicationSnapShot publicationSnapShot) {
		publicationMapper.updatePublicationSnapShot(publicationSnapShot);
	}

	@Override
	public void updatePublicationSnapShotQANote(PublicationSnapShot publicationSnapShot) {
		publicationMapper.updatePublicationSnapShotQANote(publicationSnapShot);
	}

}
