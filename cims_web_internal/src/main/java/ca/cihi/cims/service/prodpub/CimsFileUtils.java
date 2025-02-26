package ca.cihi.cims.service.prodpub;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;

import ca.cihi.cims.CIMSException;
import ca.cihi.cims.util.CimsUtils;

public class CimsFileUtils {

	private static final String LANGUAGE_CODE_ENG = "ENG";

	public static final String LINE_SEPARATOR = "\r\n";

	private static void addToZip(String directoryName, File zipFile, File file, ZipArchiveOutputStream zos)
			throws FileNotFoundException, IOException {
		FileInputStream fis = new FileInputStream(file);
		String zipFilePath = file.getCanonicalPath().substring(directoryName.length(),
				file.getCanonicalPath().length());
		ZipArchiveEntry zipEntry = new ZipArchiveEntry(zipFilePath);
		zos.putArchiveEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}
		zos.closeArchiveEntry();
		fis.close();
	}

	public static CellStyle applyTableHeaderStyle(HSSFSheet sheet) {
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		HSSFFont font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		style.setFont(font);
		return style;
	}

	public static CellStyle applyTitleStyle(HSSFSheet sheet) {
		CellStyle style = sheet.getWorkbook().createCellStyle();
		style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		HSSFFont font = sheet.getWorkbook().createFont();
		font.setBold(true);
		font.setColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
		style.setFont(font);
		return style;
	}

	public static String buildAsciiFileName(String baseClassification, String codeType, String languageCode,
			String versionYear, String releaseNumber, String delimitedType, String codeTypeSuffix) {
		StringBuilder fileName = new StringBuilder();

		Date cutDate = new Date();

		fileName.append(baseClassification);
		fileName.append("_");
		fileName.append(codeType);
		if (languageCode != null) {
			fileName.append("_");
			fileName.append(languageCode);
		}
		if (codeTypeSuffix != null) {
			fileName.append("_");
			fileName.append(codeTypeSuffix);
		}
		fileName.append("_");
		if ("ICD".equalsIgnoreCase(baseClassification)) {
			fileName.append("10CA");
		} else {
			fileName.append("CCI");
		}
		fileName.append(versionYear);
		fileName.append("_");
		fileName.append(CimsUtils.getDateStr(cutDate));
		if (releaseNumber != null) {
			fileName.append("_");
			fileName.append(releaseNumber);
		}
		fileName.append("_");
		fileName.append(delimitedType);
		fileName.append(".txt");
		return fileName.toString();

	}

	public static String buildAuditFileName(String releaseDesc, String classificationDesc, String fiscalYear,
			String auditDesc) {
		StringBuilder auditFileName = new StringBuilder();

		auditFileName.append(fiscalYear).append("_").append(classificationDesc).append(" ");
		auditFileName.append(auditDesc);
		if (releaseDesc != null) {
			auditFileName.append("_").append(releaseDesc);
		}
		auditFileName.append(".xls");

		return auditFileName.toString();
	}

	public static String buildAuditReportCommonTitle(String languageCode, String classificationDesc,
			Long currentOpenYear, String releaseId) {
		StringBuilder titleValue = new StringBuilder();

		if (LANGUAGE_CODE_ENG.equalsIgnoreCase(languageCode)) {

			titleValue.append(classificationDesc);
			titleValue.append(currentOpenYear).append("_");
			// append MonthYear, //yyyy.MMMMM
			SimpleDateFormat smf = new SimpleDateFormat("MMMMMyyyy", Locale.CANADA);
			titleValue.append(smf.format(Calendar.getInstance().getTime()));
			if (releaseId != null) {
				titleValue.append("_").append(releaseId);
			}
		} else {

			titleValue.append(classificationDesc);
			titleValue.append(currentOpenYear).append("_");
			// append MonthYear, //yyyy.MMMMM
			SimpleDateFormat smf = new SimpleDateFormat("MMMMMyyyy", Locale.CANADA_FRENCH);
			titleValue.append(smf.format(Calendar.getInstance().getTime()));
			if (releaseId != null) {
				titleValue.append("_").append(releaseId);
			}
		}
		return titleValue.toString();

	}

	public static int buildAuditReportTableHeaderLine(HSSFSheet sheet, int rownum, String[] columnDescs) {
		Row tblHeader = sheet.createRow(rownum);
		CellStyle tableHeaderstyle = CimsFileUtils.applyTableHeaderStyle(sheet);
		int columnNum = columnDescs.length;
		for (int i = 1; i <= columnNum; i++) {
			Cell tblHeaderColumnCell = tblHeader.createCell(i, Cell.CELL_TYPE_STRING);
			tblHeaderColumnCell.setCellValue(columnDescs[i - 1]);
			tblHeaderColumnCell.setCellStyle(tableHeaderstyle);
			try {
				sheet.addMergedRegion(CellRangeAddress.valueOf("B" + rownum + ":C" + rownum));
			} catch (Exception e) {
				//most likely caused by a duplicated merged region, so ignore it

				//System.out.println("exception in sheet.addMergedRegion : " + e.getMessage());
			}
		}
		return rownum;
	}

	public static int buildAuditReportTitleLine(HSSFSheet sheet, int rownum, String title) {
		Row titleRow = sheet.createRow(rownum);
		Cell titleRowCell = titleRow.createCell(1, Cell.CELL_TYPE_STRING);
		titleRowCell.setCellValue(title);
		CellStyle style = CimsFileUtils.applyTitleStyle(sheet);
		titleRowCell.setCellStyle(style);
		int mergedRow = rownum + 1;
		String mergedRegionValue = "B" + mergedRow + ":" + "C" + mergedRow;
		sheet.addMergedRegion(CellRangeAddress.valueOf(mergedRegionValue));
		return rownum;
	}

	public static void cleanFolder(File folder) {
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					file.delete();
				} else {
					cleanFolder(file);
				}
			}
		}
	}

	public static void copyCell(HSSFCell oldCell, HSSFCell newCell, Map<Integer, HSSFCellStyle> styleMap) {
		if (styleMap != null) {
			if (oldCell.getSheet().getWorkbook() == newCell.getSheet().getWorkbook()) {
				newCell.setCellStyle(oldCell.getCellStyle());
			} else {
				int stHashCode = oldCell.getCellStyle().hashCode();
				HSSFCellStyle newCellStyle = styleMap.get(stHashCode);
				if (newCellStyle == null) {
					newCellStyle = newCell.getSheet().getWorkbook().createCellStyle();
					newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
					styleMap.put(stHashCode, newCellStyle);
				}
				newCell.setCellStyle(newCellStyle);
			}
		}
		switch (oldCell.getCellType()) {
		case HSSFCell.CELL_TYPE_STRING:
			newCell.setCellValue(oldCell.getStringCellValue());
			break;
		case HSSFCell.CELL_TYPE_NUMERIC:
			newCell.setCellValue(oldCell.getNumericCellValue());
			break;
		case HSSFCell.CELL_TYPE_BLANK:
			newCell.setCellType(HSSFCell.CELL_TYPE_BLANK);
			break;
		case HSSFCell.CELL_TYPE_BOOLEAN:
			newCell.setCellValue(oldCell.getBooleanCellValue());
			break;
		case HSSFCell.CELL_TYPE_ERROR:
			newCell.setCellErrorValue(oldCell.getErrorCellValue());
			break;
		case HSSFCell.CELL_TYPE_FORMULA:
			newCell.setCellFormula(oldCell.getCellFormula());
			break;
		default:
			break;
		}

	}

	public static void copyRow(HSSFSheet srcSheet, HSSFSheet destSheet, HSSFRow srcRow, HSSFRow destRow,
			Map<Integer, HSSFCellStyle> styleMap) {
		// manage a list of merged zone in order to not insert two times a merged zone
		Set<CellRangeAddressWrapper> mergedRegions = new TreeSet<CellRangeAddressWrapper>();
		destRow.setHeight(srcRow.getHeight());
		// reckoning delta rows
		int deltaRows = destRow.getRowNum() - srcRow.getRowNum();
		// pour chaque row
		for (int j = srcRow.getFirstCellNum(); j <= srcRow.getLastCellNum(); j++) {
			HSSFCell oldCell = srcRow.getCell(j); // ancienne cell
			HSSFCell newCell = destRow.getCell(j); // new cell
			if (oldCell != null) {
				if (newCell == null) {
					newCell = destRow.createCell(j);
				}
				// copy chaque cell
				copyCell(oldCell, newCell, styleMap);
				// copy les informations de fusion entre les cellules
				// System.out.println("row num: " + srcRow.getRowNum() + " , col: " + (short)oldCell.getColumnIndex());
				CellRangeAddress mergedRegion = getMergedRegion(srcSheet, srcRow.getRowNum(),
						(short) oldCell.getColumnIndex());

				if (mergedRegion != null) {
					// System.out.println("Selected merged region: " + mergedRegion.toString());
					CellRangeAddress newMergedRegion = new CellRangeAddress(mergedRegion.getFirstRow() + deltaRows,
							mergedRegion.getLastRow() + deltaRows, mergedRegion.getFirstColumn(),
							mergedRegion.getLastColumn());
					// System.out.println("New merged region: " + newMergedRegion.toString());
					CellRangeAddressWrapper wrapper = new CellRangeAddressWrapper(newMergedRegion);
					if (isNewMergedRegion(wrapper, mergedRegions)) {
						mergedRegions.add(wrapper);
						destSheet.addMergedRegion(wrapper.range);
					}
				}
			}
		}
	}

	public static void copySheets(HSSFSheet newSheet, HSSFSheet sheet, boolean copyStyle) {
		int maxColumnNum = 0;
		Map<Integer, HSSFCellStyle> styleMap = (copyStyle) ? new HashMap<Integer, HSSFCellStyle>() : null;
		for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
			HSSFRow srcRow = sheet.getRow(i);
			HSSFRow destRow = newSheet.createRow(i);
			if (srcRow != null) {
				copyRow(sheet, newSheet, srcRow, destRow, styleMap);
				if (srcRow.getLastCellNum() > maxColumnNum) {
					maxColumnNum = srcRow.getLastCellNum();
				}
			}
		}
		for (int i = 0; i <= maxColumnNum; i++) {
			newSheet.setColumnWidth(i, sheet.getColumnWidth(i));
		}
	}

	public static File createSubFolder(File parentFolder, String subFolderName) {
		File subFolder;

		parentFolder.isDirectory();
		subFolder = new File(parentFolder, subFolderName);
		subFolder.mkdirs();

		return subFolder;
	}

	public static String findCCIBlockRubricAuditFileNameEnglish(String versionYear) {
		String auditFileNameEng = buildAuditFileName(null, "CCI", versionYear,
				"English Blk Category Rubric Description_Audit_Report");
		return auditFileNameEng;
	}

	public static String findCCIBlockRubricAuditFileNameFrench(String versionYear) {
		String auditFileNameFra = buildAuditFileName(null, "CCI", versionYear,
				"Piste de Vérification Des Blocs Rub Cat");
		return auditFileNameFra;
	}

	public static String findCCICodeAuditFileNameEnglish(String versionYear) {
		String auditFileNameEng = buildAuditFileName(null, "CCI", versionYear, "English Code Description_Audit_Report");
		return auditFileNameEng;
	}

	public static String findCCICodeAuditFileNameFrench(String versionYear) {
		String auditFileNameFra = buildAuditFileName(null, "CCI", versionYear,
				"Piste de Vérification Descriptions de Codes");
		return auditFileNameFra;
	}

	public static String findCCIValidationAuditFileNameEnglish(String versionYear) {
		return buildAuditFileName(null, "CCI", versionYear, "English Validation Audit_Report");
	}

	public static String findCCIValidationAuditFileNameFrench(String versionYear) {
		return buildAuditFileName(null, "CCI", versionYear, "Piste de Vérification des Validations Françaises");
	}

	public static String findICDBlockCategoryAuditFileNameEnglish(String versionYear) {
		String auditFileNameEng = buildAuditFileName(null, "ICD-10-CA", versionYear,
				"English Blk Category Rubric Description_Audit_Report");
		return auditFileNameEng;
	}

	public static String findICDBlockCategoryAuditFileNameFrench(String versionYear) {
		String auditFileNameFra = buildAuditFileName(null, "CIM-10-CA", versionYear,
				"Piste de Vérification Des Blocs Rub Cat");
		return auditFileNameFra;
	}

	public static String findICDCodeAuditFileNameEnglish(String versionYear) {
		String auditFileNameEng = buildAuditFileName(null, "ICD-10-CA", versionYear,
				"English Code Description_Audit_Report");
		return auditFileNameEng;
	}

	public static String findICDCodeAuditFileNameFrench(String versionYear) {
		String auditFileNameFra = buildAuditFileName(null, "CIM-10-CA", versionYear,
				"Piste de Vérification Descriptions de Codes");
		return auditFileNameFra;
	}

	public static String findICDValidationAuditFileNameEnglish(String versionYear) {
		return buildAuditFileName(null, "ICD-10-CA", versionYear, "Validation Audit_Report");
	}

	public static String findICDValidationAuditFileNameFrench(String versionYear) {
		return buildAuditFileName(null, "CIM-10-CA", versionYear, "Piste de Vérification des Validations Françaises");
	}

	// V0_1 ICD10CA CCI 2015 English Block, Rubric, Category Description Audit Trail.xls
	public static String findReleaseBlockRubricCategoryAuditFileNameEnglish(String versionCode, String versionYear) {
		String auditFileNameEng = buildAuditFileName(versionCode, "ICD-10-CA CCI", versionYear,
				"English Blk Category Rubric Description_Audit_Report");
		return auditFileNameEng;
	}

	// V0_1 CIM-10-CA CCI 2015 Piste de Vérification Desc Blocs Rub Cat
	public static String findReleaseBlockRubricCategoryAuditFileNameFrench(String versionCode, String versionYear) {
		String auditFileNameEng = buildAuditFileName(versionCode, "CIM-10-CA et CCI", versionYear,
				"Piste de Vérification Des Blocs Rub Cat");
		return auditFileNameEng;
	}

	// V0_1 ICD10CA CCI 2015 English Code Description Audit Trail.xls
	public static String findReleaseCodeAuditFileNameEnglish(String versionCode, String versionYear) {
		String auditFileNameEng = buildAuditFileName(versionCode, "ICD-10-CA CCI", versionYear,
				"English Code Description_Audit_Report");
		return auditFileNameEng;
	}

	// V0_1 CIM-10-CA CCI 2015 Piste de Vérification Descriptions de Codes.xls
	public static String findReleaseCodeAuditFileNameFrench(String versionCode, String versionYear) {
		String auditFileNameFra = buildAuditFileName(versionCode, "CIM-10-CA et CCI", versionYear,
				"Piste de Vérification Descriptions de Codes");
		return auditFileNameFra;
	}

	public static String findReleaseValidationAuditFileNameEnglish(String versionCode, String versionYear) {
		String auditFileNameEng = buildAuditFileName(versionCode, "ICD-10-CA CCI", versionYear,
				"English Validation_Audit_Report");
		return auditFileNameEng;
	}

	public static String findReleaseValidationAuditFileNameFrench(String versionCode, String versionYear) {
		String auditFileNameFra = buildAuditFileName(versionCode, "CIM-10-CA et CCI", versionYear,
				"Piste de Vérification Validations Françaises");
		return auditFileNameFra;
	}

	private static void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			fileList.add(file);
			if (file.isDirectory()) {
				getAllFiles(file, fileList);
			}
		}
	}

	public static CellRangeAddress getMergedRegion(HSSFSheet sheet, int rowNum, short cellNum) {
		for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
			CellRangeAddress merged = sheet.getMergedRegion(i);
			if (merged.isInRange(rowNum, cellNum)) {
				return merged;
			}
		}
		return null;
	}

	private static boolean isNewMergedRegion(CellRangeAddressWrapper newMergedRegion,
			Set<CellRangeAddressWrapper> mergedRegions) {
		return !mergedRegions.contains(newMergedRegion);
	}

	/**
	 * This process assume CCI file is always sourceFile
	 *
	 * @param sourceFileName
	 * @param targetFileName
	 * @param releaseFileName
	 * @param auditFileTitleICD
	 * @param auditFileTitleCCI
	 * @throws IOException
	 */
	public static void mergeExcelFiles(String sourceFileName, String targetFileName, String releaseFileName,
			StringBuilder auditFileTitleCCI, StringBuilder auditFileTitleICD) throws IOException {
		FileInputStream targetFileInput = new FileInputStream(targetFileName);
		FileInputStream sourceFileInput = new FileInputStream(sourceFileName);
		HSSFWorkbook target = new HSSFWorkbook(targetFileInput);
		for (int i = 0; i < target.getNumberOfSheets(); i++) {
			HSSFSheet targetSheet = target.getSheetAt(i);
			HSSFRow row = targetSheet.getRow(3);
			if (row == null) {
				row = targetSheet.createRow(3);
			}
			HSSFCell cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			cell.setCellValue(auditFileTitleICD.toString());
		}
		HSSFWorkbook source = new HSSFWorkbook(sourceFileInput);
		for (int i = 0; i < source.getNumberOfSheets(); i++) {
			// not entering sheet name, because of duplicated names
			HSSFSheet sourceSheet = source.getSheetAt(i);
			sourceSheet.getSheetName();
			HSSFRow row = sourceSheet.getRow(3);
			if (row == null) {
				row = sourceSheet.createRow(3);
			}
			HSSFCell cell = row.getCell(1);
			if (cell == null) {
				cell = row.createCell(1);
			}
			cell.setCellValue(auditFileTitleCCI.toString());
			copySheets(target.createSheet(sourceSheet.getSheetName()), sourceSheet, true);
		}

		FileOutputStream outputFile = new FileOutputStream(targetFileName);
		target.write(outputFile);
		outputFile.close();
		sourceFileInput.close();
		targetFileInput.close();

		File sourceFile = new File(sourceFileName);
		File targetFile = new File(targetFileName);
		File releaseFile = new File(releaseFileName);
		FileUtils.forceDelete(sourceFile);
		targetFile.renameTo(releaseFile);

	}

	public static void padBlanksToString(String field, int maxLengthOfField, StringBuilder builder) {
		padBlanksToString(field, maxLengthOfField, builder, false);
	}

	public static void padBlanksToString(String field, int maxLengthOfField, StringBuilder builder, boolean truncate) {
		if (field == null) {
			field = "";
		}
		int lengthOfField = field.length();
		int numberOfBlankSpacesToPutAfterString = maxLengthOfField - lengthOfField;

		if (numberOfBlankSpacesToPutAfterString >= 0) {
			builder.append(field);
			for (int i = 1; i <= numberOfBlankSpacesToPutAfterString; i++) {
				// append a blank space
				builder.append(" ");
			}
		} else if (truncate) {
			builder.append(field.substring(0, maxLengthOfField));
		} else {
			throw new CIMSException("field :" + field + "  length is over :" + maxLengthOfField);
		}
	}

	private static String renameSnapShotFileNameToReleaseFileName(String snapShotFileName, String releaseDesc) {
		// the snapshot file is like CCI_Code_Eng_Desc_CCI<year>_<YYYYMMDD>_<format>.txt
		// need rename it to like CCI_Code_Eng_Desc_CCI<year>_<YYYYMMDD>_<release ID>_<format>.txt
		StringBuilder sb_releaseFileName = new StringBuilder();
		if (snapShotFileName != null) {
			String[] snapShotFileNameParts = snapShotFileName.split("_");
			int i = 0;
			for (; i < (snapShotFileNameParts.length - 1); i++) {
				String namePart = snapShotFileNameParts[i];
				if ((namePart.length() == 8) && StringUtils.isNumeric(namePart)) {
					sb_releaseFileName.append(releaseDesc).append("_");
				} else {
					sb_releaseFileName.append(namePart).append("_");
				}
			}
			sb_releaseFileName.append(snapShotFileNameParts[i]);
		}
		return sb_releaseFileName.toString();
	}

	public static void unzipFileAndRenameAsciiFileToFolder(String zipFileName, String releaseDesc, String outputFolder)
			throws IOException {
		byte[] buffer = new byte[1024];
		// create output directory is not exists
		File folder = new File(outputFolder);
		if (!folder.exists()) {
			folder.mkdir();
		}
		// get the zip file content
		ZipArchiveInputStream zis = new ZipArchiveInputStream(new FileInputStream(zipFileName), "CP437");
		// get the zipped file list entry
		ZipArchiveEntry ze = zis.getNextZipEntry();
		while (ze != null) {
			String fileName = ze.getName();
			String newFileName = fileName;
			if (fileName.endsWith(".txt")) {
				// the snapshot file is like CCI_Code_Eng_Desc_CCI<year>_<YYYYMMDD>_<release ID>_<format>
				// need rename it to like CCI_Code_Eng_Desc_CCI<year>_<YYYYMMDD>_<release ID>_<format>.
				newFileName = renameSnapShotFileNameToReleaseFileName(fileName, releaseDesc);
			}
			File newFile = new File(outputFolder + File.separator + newFileName);
			// create all non exists folders
			// else you will hit FileNotFoundException for compressed folder
			new File(newFile.getParent()).mkdirs();
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			ze = zis.getNextZipEntry();
		}
		zis.close();
	}

	private static void writeZipFile(String directoryName, File zipFile, List<File> fileList) throws IOException {
		FileOutputStream fos = new FileOutputStream(zipFile);
		ZipArchiveOutputStream zos = new ZipArchiveOutputStream(fos);
		zos.setEncoding("CP437");
		// zos.setEncoding("UTF-8");
		zos.setUseLanguageEncodingFlag(true);
		for (File file : fileList) {
			if (!file.isDirectory()) { // we only zip files, not directories
				addToZip(directoryName, zipFile, file, zos);
			}
		}
		zos.flush();
		zos.close();
		fos.close();

	}

	public static void zipFilesForFolder(String directoryName, String zipFileFolder, String zipFileName)
			throws IOException {
		File directoryToZip = new File(directoryName);
		File zipFile = new File(zipFileFolder + zipFileName);
		List<File> fileList = new ArrayList<File>();
		getAllFiles(directoryToZip, fileList);
		writeZipFile(directoryName, zipFile, fileList);
	}

}
