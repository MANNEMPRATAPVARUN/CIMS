package ca.cihi.cims.service.prodpub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.CIMSException;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.content.cci.CciValidationXml;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAudit;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.LanguageConstants;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;
import ca.cihi.cims.model.prodpub.WorkSheetData;
import ca.cihi.cims.util.XmlUtils;

public class CCIValidationFileGenerator extends ValidationFileGenerator {

	private static final Logger logger = LogManager.getLogger(CCIValidationFileGenerator.class);
	private static final int MAX_LENGTH_CCI_VALIDATIONRULE = 9;
	private static final int MAX_LENGTH_CCI_VALIDATIONRULE_REFERENCE = 6;

	private static final String STATUS_REF_REGEX = "<STATUS_REF>S([a-zA-Z0-9][a-zA-Z0-9])</STATUS_REF>";
	private static final String EXTENT_REF_REGEX = "<EXTENT_REF>E([a-zA-Z0-9][a-zA-Z0-9])</EXTENT_REF>";
	private static final String LOCATION_REF_REGEX = "<LOCATION_REF>(L|M)([a-zA-Z0-9][a-zA-Z0-9])</LOCATION_REF>";

	private static final String CIMS_MISSING_REFERENCE_VALUE = "cims.notification.releasetable.missingreferencevalue";
	private static final String CIMS_DISABLED_REFERENCE_VALUE = "cims.notification.releasetable.disabledreferencevalue";
	private static final String CANCELLED = "CANCELLED";
	private static final String YA000130 = "YA000130";

	private int addAdditionalLineFixedWith(BufferedWriter bw, String dhCode) throws IOException {
		StringBuilder sb_line = new StringBuilder();
		int count = 0;
		CimsFileUtils.padBlanksToString(CANCELLED, MAX_LENGTH_CCI_CODE, sb_line);

		CimsFileUtils.padBlanksToString(dhCode + YA000130, MAX_LENGTH_CCI_VALIDATIONRULE, sb_line);
		sb_line.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(sb_line.toString());
		return count;
	}

	private int addAdditionalLineReferenceFixedWidth(BufferedWriter bw, String dhCode) throws IOException {
		StringBuilder sb_line = new StringBuilder();
		int count = 0;
		CimsFileUtils.padBlanksToString(CANCELLED, MAX_LENGTH_CCI_CODE, sb_line);

		CimsFileUtils.padBlanksToString(dhCode, MAX_LENGTH_CCI_VALIDATIONRULE_REFERENCE, sb_line);
		sb_line.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(sb_line.toString());
		return count;
	}

	private int addAdditionalLineReferenceTab(BufferedWriter bw, String dhCode) throws IOException {
		StringBuilder lineTab = new StringBuilder();
		int count = 0;
		lineTab.append(CANCELLED);
		lineTab.append(TAB);
		lineTab.append(dhCode);
		lineTab.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(lineTab.toString());
		return count;
	}

	private int addAdditionalLineTab(BufferedWriter bw, String dhCode) throws IOException {
		StringBuilder lineTab = new StringBuilder();
		int count = 0;
		lineTab.append(CANCELLED);
		lineTab.append(TAB);
		lineTab.append(dhCode).append(YA000130);
		lineTab.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(lineTab.toString());
		return count;
	}

	@Override
	protected void buildAuditLists(Long lastVersionICDContextId) {

		List<ValidationRuleSet> cciCodeValidations = findCCIValidationRules(lastVersionICDContextId);

		buildCacheList(priorValidationSetMap, cciCodeValidations, lastVersionICDContextId);

	}

	private int buildAuditReportReferenceRevisionsTable(HSSFSheet sheet, int rownum,
			List<CCIReferenceAudit> referenceAuditList, String titleValue, String[] headerDescs) {
		int result = rownum;
		result = CimsFileUtils.buildAuditReportTitleLine(sheet, result, titleValue);
		result++;

		result = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, result, headerDescs);
		result++;

		for (CCIReferenceAudit audit : referenceAuditList) {

			if (CODE_VALUE_N.equals(audit.getMandatoryIndicator())) {
				Row rowRevisions = sheet.createRow(result);
				Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell1.setCellValue(audit.getCode());
				Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell2.setCellValue(audit.getDhcode());
				Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell3.setCellValue("");
				Cell tblRevisionsColumnCell4 = rowRevisions.createCell(4, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell4.setCellValue(audit.getNewReferenceValue());
				Cell tblRevisionsColumnCell5 = rowRevisions.createCell(5, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell5.setCellValue(audit.getOldReferenceValue());
				result++;
			}
			if (audit.getGenericAttributes() != null) {
				for (CCIGenericAttribute attribute : audit.getGenericAttributes()) {
					Row rowRevisions = sheet.createRow(result);
					Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell1.setCellValue(audit.getCode());
					Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell2.setCellValue(audit.getDhcode());
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(attribute.getCode());
					Cell tblRevisionsColumnCell4 = rowRevisions.createCell(4, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell4.setCellValue(audit.getNewReferenceValue());
					Cell tblRevisionsColumnCell5 = rowRevisions.createCell(5, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell5.setCellValue(audit.getOldReferenceValue());
					result++;
				}
			}
		}

		return result;
	}

	private int buildAuditReportReferenceTable(HSSFSheet sheet, int rownum, List<CodeValidationAudit> auditList,
			String titleValue, String[] headerDescs, String attributeType, Long currentOpenContextId,
			Long previousContextId) {
		int result = rownum;
		result = CimsFileUtils.buildAuditReportTitleLine(sheet, result, titleValue);
		result++;

		result = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, result, headerDescs);
		result++;

		for (CodeValidationAudit audit : auditList) {
			String referenceValue = null;
			if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
				referenceValue = audit.getValidationRuleSet().getExtentRef();
			} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
				referenceValue = audit.getValidationRuleSet().getStatusRef();
			} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
				referenceValue = audit.getValidationRuleSet().getLocationRef();
			} else {
				throw new CIMSException("Wrong attribute type provided!");
			}
			result = buildReferenceAuditRows(sheet, audit, referenceValue, result, currentOpenContextId,
					previousContextId);
		}

		return result;
	}

	@Override
	protected int buildLineFixedWidth(Object source, BufferedWriter bw) throws IOException {
		StringBuilder sb_line = new StringBuilder();
		int count = 1;
		CimsFileUtils.padBlanksToString(((ValidationRuleSet) source).getCode(), MAX_LENGTH_CCI_CODE, sb_line);

		CimsFileUtils.padBlanksToString(((ValidationRuleSet) source).getValidationRuleText(),
				MAX_LENGTH_CCI_VALIDATIONRULE, sb_line);
		sb_line.append(CimsFileUtils.LINE_SEPARATOR);
		bw.write(sb_line.toString());
		return count;
	}

	private String buildLineTab(List<String> values) {
		StringBuilder lineTab = new StringBuilder();
		int i = 0;
		for (String value : values) {
			if (i++ > 0) {
				lineTab.append(TAB);
			}
			lineTab.append(value);
		}
		lineTab.append(CimsFileUtils.LINE_SEPARATOR);
		return lineTab.toString();
	}

	private int buildReferenceAuditRows(HSSFSheet sheet, CodeValidationAudit audit, String referenceValue, int rownum,
			Long currentOpenContextId, Long previousContextId) {
		int result = rownum;
		if (StringUtils.isEmpty(referenceValue)) {
			Row rowRevisions = sheet.createRow(result);
			Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
			tblRevisionsColumnCell1.setCellValue(audit.getCode());
			Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
			tblRevisionsColumnCell2.setCellValue(audit.getDhcode());
			result++;
		} else {

			CCIReferenceAttribute referenceAttribure = currentReferenceCodeMap.get(referenceValue);
			if (referenceAttribure == null) {
				// caused by the last validation reference to the refereceValue was disabled in currentYear, did not go
				// into
				// cache
				referenceAttribure = getCCIReferenceAttribute(currentOpenContextId, referenceValue, LANGUAGE_CODE_ENG);
				if (referenceAttribure == null) {// referenceValue was disabled or removed in currentYear
					referenceAttribure = getCCIReferenceAttribute(previousContextId, referenceValue, LANGUAGE_CODE_ENG);

				}
				if (referenceAttribure == null) {
					logger.error("Reference Value: " + referenceValue + " not found in current and previous year.");
					getEmailService().sendMissingReferenceValueEmail(getGenerateReleaseTablesCriteria(),
							getCurrentUser(), CIMS_MISSING_REFERENCE_VALUE, referenceValue, audit.getCode(),
							audit.getDhcode());
					return result;
				}
				currentReferenceCodeMap.put(referenceValue, referenceAttribure);
			}

			if (CODE_VALUE_N.equals(referenceAttribure.getMandatoryIndicator())) {
				Row rowRevisions = sheet.createRow(result);
				Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell1.setCellValue(audit.getCode());
				Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell2.setCellValue(audit.getDhcode());
				Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell3.setCellValue("");
				Cell tblRevisionsColumnCell4 = rowRevisions.createCell(4, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell4.setCellValue(referenceValue);
				result++;
			}
			if (referenceAttribure.getGenericAttributes() != null) {
				for (CCIGenericAttribute attribute : referenceAttribure.getGenericAttributes()) {
					Row rowRevisions = sheet.createRow(result);
					Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell1.setCellValue(audit.getCode());
					Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell2.setCellValue(audit.getDhcode());
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(attribute.getCode());
					Cell tblRevisionsColumnCell4 = rowRevisions.createCell(4, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell4.setCellValue(referenceValue);
					result++;
				}
			}
		}
		return result;
	}

	private void buildReferenceWorksheet(String classificationDesc, List<CCIReferenceAudit> referenceAuditList,
			String languageCode, String attributeType, Long currentVersionYear, Long currentOpenContextId,
			Long lastVersionContextId) throws IOException {
		WorkSheetData workSheetData = getWorkSheetDataReference(languageCode, currentVersionYear, attributeType);
		HSSFSheet sheet = null;
		String titleValue = null;
		FileInputStream inputStream = new FileInputStream(workSheetData.getFileName());
		HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
		sheet = workbook.createSheet(workSheetData.getWorksheetName());
		titleValue = CimsFileUtils.buildAuditReportCommonTitle(languageCode, classificationDesc, currentVersionYear,
				null);

		Integer rownum = 3;
		rownum = CimsFileUtils.buildAuditReportTitleLine(sheet, rownum, titleValue);
		sheet.setColumnWidth(1, 255 * 25);
		sheet.setColumnWidth(2, 255 * 50);
		sheet.setColumnWidth(3, 255 * 50);
		sheet.setColumnWidth(4, 255 * 50);
		sheet.setColumnWidth(5, 255 * 50);
		rownum = rownum + 3;

		rownum = buildAuditReportReferenceRevisionsTable(sheet, rownum, referenceAuditList,
				workSheetData.getRevisionsTitleValue(), workSheetData.getHeaderDescs());

		rownum++;

		rownum = buildAuditReportReferenceTable(sheet, rownum, newCodeValidations, workSheetData.getNewTitleValue(),
				workSheetData.getHeaderNewDescs(), attributeType, currentOpenContextId, lastVersionContextId);

		rownum++;

		rownum = buildAuditReportReferenceTable(sheet, rownum, disabledCodeValidations,
				workSheetData.getDisabledTitleValue(), workSheetData.getHeaderDisabledDescs(), attributeType,
				currentOpenContextId, lastVersionContextId);
		FileOutputStream outputFile = new FileOutputStream(workSheetData.getFileName());
		workbook.write(outputFile);
		outputFile.close();
		inputStream.close();

	}

	protected List<ValidationRuleSet> findCCIValidationRules(Long contextId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", contextId);
		params.put("extentRefRegex", EXTENT_REF_REGEX);
		params.put("statusRefRegex", STATUS_REF_REGEX);
		params.put("locationRefRegex", LOCATION_REF_REGEX);
		params.put("catRubricClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params.put("validationDefinitionClassId",
				getConceptService().getCCIClassID("XMLPropertyVersion", "ValidationDefinition"));
		params.put("cciCodeClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params.put("validationCPVClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationCCICPV"));
		params.put("codeClassId", getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));

		return getPublicationMapper().findCCIValidationRules(params);
	}

	@Override
	protected List<ValidationRuleSet> findChildCodes(Long contextId, Long conceptId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", contextId);
		params.put("conceptId", conceptId);
		params.put("narrowerClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));
		params.put("codeClassId", getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "Code"));
		params.put("cciCodeClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		List<ValidationRuleSet> children = getPublicationMapper().findCCIChildCodes(params);
		return children;
	}

	@Override
	protected String generateAuditFileName(String languageCode, Long currentVersionYear) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(pubDirectory).append(SUB_FOLDER_CCI).append(File.separator).append(SUB_FOLDER_VALIDATION)
				.append(File.separator).append(languageCode).append(File.separator)
				.append(LANGUAGE_CODE_ENG.equals(languageCode)
						? CimsFileUtils.findCCIValidationAuditFileNameEnglish(currentVersionYear.toString())
						: CimsFileUtils.findCCIValidationAuditFileNameFrench(currentVersionYear.toString()));
		return fileName.toString();
	}

	@Override
	public void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Start generating CCI " + generateTablesModel.getCurrentOpenYear() + " English Audit File.");
		}
		buildWorksheet(CIMSConstants.CCI, LANGUAGE_CODE_ENG, null, currentOpenContextId, lastVersionContextId,
				generateTablesModel.getCurrentOpenYear(), 0);

		buildReferenceWorksheet(CIMSConstants.CCI, statusAuditList, LANGUAGE_CODE_ENG,
				LanguageConstants.ENG.getStatus(), generateTablesModel.getCurrentOpenYear(), currentOpenContextId,
				lastVersionContextId);

		buildReferenceWorksheet(CIMSConstants.CCI, locationAuditList, LANGUAGE_CODE_ENG,
				LanguageConstants.ENG.getLocation(), generateTablesModel.getCurrentOpenYear(), currentOpenContextId,
				lastVersionContextId);

		buildReferenceWorksheet(CIMSConstants.CCI, extentAuditList, LANGUAGE_CODE_ENG,
				LanguageConstants.ENG.getExtent(), generateTablesModel.getCurrentOpenYear(), currentOpenContextId,
				lastVersionContextId);
		if (logger.isDebugEnabled()) {
			logger.debug("Finish generating CCI " + generateTablesModel.getCurrentOpenYear() + " English Audit File.");
		}
	}

	@Override
	protected void generateEnglishFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("Start generating CCI " + generateTablesModel.getCurrentOpenYear()
					+ " English Validation ASCII File.");
		}
		File folderEng = prepareFolder(SUB_FOLDER_CCI, SUB_FOLDER_VALIDATION, LANGUAGE_CODE_ENG);
		String fileName = CimsFileUtils.buildAsciiFileName(CIMSConstants.CCI, "Code_Validation", null,
				String.valueOf(generateTablesModel.getCurrentOpenYear()), null,
				getDelimitedType(generateTablesModel.getFileFormat()), null);

		File cciValidationEng = new File(folderEng.getCanonicalPath() + FILE_SEPARATOR + fileName);

		String fileNameExtent = CimsFileUtils.buildAsciiFileName(CIMSConstants.CCI, LanguageConstants.ENG.getExtent(),
				null, String.valueOf(generateTablesModel.getCurrentOpenYear()), null,
				getDelimitedType(generateTablesModel.getFileFormat()), "Val");
		String fileNameStatus = CimsFileUtils.buildAsciiFileName(CIMSConstants.CCI, LanguageConstants.ENG.getStatus(),
				null, String.valueOf(generateTablesModel.getCurrentOpenYear()), null,
				getDelimitedType(generateTablesModel.getFileFormat()), "Val");
		String fileNameLocation = CimsFileUtils.buildAsciiFileName(CIMSConstants.CCI,
				LanguageConstants.ENG.getLocation(), null, String.valueOf(generateTablesModel.getCurrentOpenYear()),
				null, getDelimitedType(generateTablesModel.getFileFormat()), "Val");

		File cciExtentValidationEng = new File(folderEng.getCanonicalPath() + FILE_SEPARATOR + fileNameExtent);
		File cciLocationValidationEng = new File(folderEng.getCanonicalPath() + FILE_SEPARATOR + fileNameLocation);
		File cciStatusValidationEng = new File(folderEng.getCanonicalPath() + FILE_SEPARATOR + fileNameStatus);

		File folderFra = prepareFolder(SUB_FOLDER_CCI, SUB_FOLDER_VALIDATION, LANGUAGE_CODE_FRA);

		File cciValidationFra = new File(folderFra.getCanonicalPath() + FILE_SEPARATOR + fileName);
		File cciExtentValidationFra = new File(folderFra.getCanonicalPath() + FILE_SEPARATOR + fileNameExtent);
		File cciLocationValidationFra = new File(folderFra.getCanonicalPath() + FILE_SEPARATOR + fileNameLocation);
		File cciStatusValidationFra = new File(folderFra.getCanonicalPath() + FILE_SEPARATOR + fileNameStatus);

		BufferedWriter bwEng = new BufferedWriter(new FileWriter(cciValidationEng));
		BufferedWriter bwExtentEng = new BufferedWriter(new FileWriter(cciExtentValidationEng));
		BufferedWriter bwStatusEng = new BufferedWriter(new FileWriter(cciStatusValidationEng));
		BufferedWriter bwLocationEng = new BufferedWriter(new FileWriter(cciLocationValidationEng));

		List<ValidationRuleSet> cciCodeValidations = findCCIValidationRules(currentOpenContextId);

		buildCacheList(currentValidationSetMap, cciCodeValidations, currentOpenContextId);

		int[] valCounts = writeValidationReferenceFile(currentValidationSetMap, generateTablesModel.getFileFormat(),
				bwEng, bwExtentEng, bwStatusEng, bwLocationEng, currentOpenContextId);

		PublicationStatistics cciValidationEngStatistics = new PublicationStatistics("CCI_CODE_VALIDATION", "Both",
				valCounts[0]);
		statisticsSummary.add(cciValidationEngStatistics);

		FileUtils.copyFile(cciValidationEng, cciValidationFra);

		PublicationStatistics cciStatusValidationEngStatistics = new PublicationStatistics("CCI_STATUS_VALIDATION",
				"Both", valCounts[2]);
		statisticsSummary.add(cciStatusValidationEngStatistics);

		FileUtils.copyFile(cciStatusValidationEng, cciStatusValidationFra);

		PublicationStatistics cciLocationValidationEngStatistics = new PublicationStatistics("CCI_LOCATION_VALIDATION",
				"Both", valCounts[3]);
		statisticsSummary.add(cciLocationValidationEngStatistics);
		FileUtils.copyFile(cciLocationValidationEng, cciLocationValidationFra);

		PublicationStatistics cciExtentValidationEngStatistics = new PublicationStatistics("CCI_EXTENT_VALIDATION",
				"Both", valCounts[1]);
		statisticsSummary.add(cciExtentValidationEngStatistics);

		FileUtils.copyFile(cciExtentValidationEng, cciExtentValidationFra);

		if (logger.isDebugEnabled()) {
			logger.debug("Finish generating CCI " + generateTablesModel.getCurrentOpenYear()
					+ " English Validation ASCII File.");
		}

	}

	@Override
	protected void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {

		buildWorksheet(CIMSConstants.CCI, LANGUAGE_CODE_FRA, null, currentOpenContextId, lastVersionContextId,
				generateTablesModel.getCurrentOpenYear(), 0);

		buildReferenceWorksheet(CIMSConstants.CCI, statusAuditList, LANGUAGE_CODE_FRA,
				LanguageConstants.ENG.getStatus(), generateTablesModel.getCurrentOpenYear(), currentOpenContextId,
				lastVersionContextId);

		buildReferenceWorksheet(CIMSConstants.CCI, locationAuditList, LANGUAGE_CODE_FRA,
				LanguageConstants.ENG.getLocation(), generateTablesModel.getCurrentOpenYear(), currentOpenContextId,
				lastVersionContextId);

		buildReferenceWorksheet(CIMSConstants.CCI, extentAuditList, LANGUAGE_CODE_FRA,
				LanguageConstants.ENG.getExtent(), generateTablesModel.getCurrentOpenYear(), currentOpenContextId,
				lastVersionContextId);

	}

	private String generateValidationRuleReference(String dhCode, String refCode, String genericCode) {
		StringBuilder sb = new StringBuilder();
		sb.append(dhCode);
		if (!StringUtils.isEmpty(refCode)) {
			CimsFileUtils.padBlanksToString(genericCode, MAX_LENGTH_CCI_ATTRIBUTE_GENERIC_CODE, sb);
			sb.append(refCode);
		}
		return sb.toString();
	}

	@Override
	protected String generateValidationRuleSet(String dhCode, String xmlText) {
		StringBuilder builder = new StringBuilder();
		builder.append(dhCode);
		CciValidationXml validationXml = XmlUtils.deserialize(CciValidationXml.class, xmlText);
		builder.append(CODE_VALUE_Y);
		builder.append(validationXml.getGenderCode());
		builder.append(StringUtils.leftPad(validationXml.getAgeMin() + "", 3, "0"));
		builder.append(StringUtils.leftPad(validationXml.getAgeMax() + "", 3, "0"));
		return builder.toString();
	}

	private CCIReferenceAttribute getCCIReferenceAttribute(Long contextId, String referenceCode, String languageCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params.put("genericAttributeCPVClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params.put("attributeCodeClassId",
				getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params.put("attributeDescriptionClassId",
				getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params.put("referenceAttributeClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "ReferenceAttribute"));
		params.put("attributeMandatoryIndicatorClassId", getConceptService()
				.getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params.put("contextId", contextId);
		params.put("referenceCode", referenceCode);
		params.put("languageCode", LANGUAGE_CODE_ENG);
		return getPublicationMapper().getCCIReferenceAttribute(params);
	}

	@Override
	protected String[] getDisabledHeaderDescs(String languageCode, Long lastVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = CIMSConstants.CCI;
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		headerDescs[2] = (LanguageConstants.getConstant(languageCode).getOldRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI" : "") + lastVersionYear;

		return headerDescs;
	}

	private String[] getDisabledHeaderDescsReference(String languageCode, String attributeType, Long lastVersionYear) {
		String[] headerDescs = new String[4];
		headerDescs[0] = CIMSConstants.CCI;
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getExtent());
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getStatus());
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getLocation());
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}
		headerDescs[3] = (LanguageConstants.getConstant(languageCode).getReference()) + BLANK + lastVersionYear;

		return headerDescs;
	}

	@Override
	protected String getDisabledTitleValue(String languageCode, String currentVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentVersion).append(" ").append(LANGUAGE_CODE_ENG.equals(languageCode)
				? "CCI Disabled Validation Rule" : "CCI Règles de validation désactivées");
		return sb.toString();
	}

	private String getDisabledTitleValueReference(String languageCode, String currentVersionYear,
			String attributeType) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentVersionYear).append(" ");
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI Disabled Extent Validation Rule"
					: "CCI Étendue Règles de validation désactivées");
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI Disabled Status Validation Rule"
					: "CCI Situation Règles de validation désactivées");
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI Disabled Location Validation Rule"
					: "CCI Lieu Règles de validation désactivées");
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}
		return sb.toString();
	}

	@Override
	protected String[] getNewHeaderDescs(String languageCode, Long currentVersionYear) {
		String[] headerDescs = new String[3];
		headerDescs[0] = CIMSConstants.CCI;
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		headerDescs[2] = (LanguageConstants.getConstant(languageCode).getNewRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI" : "") + currentVersionYear;

		return headerDescs;
	}

	private String[] getNewHeaderDescsReference(String languageCode, String attributeType, Long currentVersionYear) {
		String[] headerDescs = new String[4];
		headerDescs[0] = CIMSConstants.CCI;
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getExtent());
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getStatus());
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getLocation());
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}
		headerDescs[3] = LanguageConstants.getConstant(languageCode).getReference() + BLANK + currentVersionYear;

		return headerDescs;
	}

	@Override
	protected String getNewTitleValue(String languageCode, String currentVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentVersion).append(" ").append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI New Validation Rule"
				: "CCI Règles de validation nouvelles");
		return sb.toString();
	}

	private String getNewTitleValueReference(String languageCode, String currentVersionYear, String attributeType) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentVersionYear).append(" ");
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI New Extent Validation Rule"
					: "CCI Étendue Règles de validation nouvelles");
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI New Status Validation Rule"
					: "CCI Situation Règles de validation nouvelles");
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI New Location Validation Rule"
					: "CCI Lieu Règles de validation nouvelles");
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}
		return sb.toString();
	}

	@Override
	protected String[] getRevisedHeaderDescs(String languageCode, Long currentVersionYear, Long lastVersionYear) {
		String[] headerDescs = new String[4];
		headerDescs[0] = CIMSConstants.CCI;
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		headerDescs[2] = (LanguageConstants.getConstant(languageCode).getNewRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI" : "") + currentVersionYear;
		headerDescs[3] = (LanguageConstants.getConstant(languageCode).getOldRule()) + BLANK
				+ (LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI" : "") + lastVersionYear;

		return headerDescs;
	}

	private String[] getRevisedHeaderDescsReference(String languageCode, Long currentVersionYear, Long lastVersionYear,
			String attributeType) {
		String[] headerDescs = new String[5];
		headerDescs[0] = CIMSConstants.CCI;
		headerDescs[1] = LanguageConstants.getConstant(languageCode).getSector();
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getExtent());
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getStatus());
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			headerDescs[2] = (LanguageConstants.getConstant(languageCode).getLocation());
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}
		headerDescs[3] = (LanguageConstants.getConstant(languageCode).getReference()) + BLANK + currentVersionYear;
		headerDescs[4] = (LanguageConstants.getConstant(languageCode).getReference()) + BLANK + lastVersionYear;

		return headerDescs;
	}

	@Override
	protected String getRevisionsTitleValue(String languageCode, String currentVersion) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentVersion).append(" ").append(LANGUAGE_CODE_ENG.equals(languageCode)
				? "CCI Validation Rule Revisions" : "CCI Règles de validation révisées");
		return sb.toString();
	}

	private String getRevisionsTitleValueReference(String languageCode, String currentVersionYear,
			String attributeType) {
		StringBuilder sb = new StringBuilder();
		sb.append(currentVersionYear).append(" ");
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI Extent Validation Rule Revisions"
					: "CCI Étendue Règles de validation révisées");
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI Status Validation Rule Revisions"
					: "CCI Situation Règles de validation révisées");
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			sb.append(LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI Location Validation Rule Revisions"
					: "CCI Lieu Règles de validation révisées");
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}
		return sb.toString();
	}

	private WorkSheetData getWorkSheetDataReference(String languageCode, Long currentVersionYear,
			String attributeType) {
		WorkSheetData workSheetData = new WorkSheetData();
		workSheetData.setFileName(generateAuditFileName(languageCode, currentVersionYear));
		workSheetData.setWorksheetName(getWorksheetNameReference(languageCode, attributeType));

		Long lastVersionYear = currentVersionYear - 1;

		workSheetData.setHeaderDescs(
				getRevisedHeaderDescsReference(languageCode, currentVersionYear, lastVersionYear, attributeType));
		workSheetData.setHeaderNewDescs(getNewHeaderDescsReference(languageCode, attributeType, currentVersionYear));
		workSheetData
				.setHeaderDisabledDescs(getDisabledHeaderDescsReference(languageCode, attributeType, lastVersionYear));

		workSheetData.setRevisionsTitleValue(
				getRevisionsTitleValueReference(languageCode, currentVersionYear.toString(), attributeType));
		workSheetData.setNewTitleValue(
				getNewTitleValueReference(languageCode, currentVersionYear.toString(), attributeType));
		workSheetData.setDisabledTitleValue(
				getDisabledTitleValueReference(languageCode, currentVersionYear.toString(), attributeType));

		return workSheetData;
	}

	@Override
	protected String getWorksheetName(String languageCode) {
		return LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI_Code_Validation" : "CCI_Validation_Code";
	}

	public String getWorksheetNameReference(String languageCode, String attributeType) {
		if (LanguageConstants.ENG.getExtent().equals(attributeType)) {
			return LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI_Extent_Val" : "CCI Validation Étendue";
		} else if (LanguageConstants.ENG.getStatus().equals(attributeType)) {
			return LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI_Status_Val" : "CCI Validation Situation";
		} else if (LanguageConstants.ENG.getLocation().equals(attributeType)) {
			return LANGUAGE_CODE_ENG.equals(languageCode) ? "CCI_Location_Val" : "CCI Validation Lieu";
		} else {
			throw new CIMSException("Attribute type " + attributeType + " not exists.");
		}

	}

	@Override
	protected void processExistingValidation(String code, ValidationRuleSet currentRule, ValidationRuleSet priorRule,
			Map<String, ValidationRuleSet> priorDhMap) {
		if (!currentRule.getValidationRuleText().equals(priorRule.getValidationRuleText())) {
			CodeValidationAudit audit = new CodeValidationAudit();
			audit.setCode(code);
			audit.setDhcode(currentRule.getDhcode());
			audit.setNewDescription(currentRule.getValidationRuleText());
			audit.setOldDescription(priorRule.getValidationRuleText());
			revisedCodeValidations.add(audit);
		}
		if (currentRule.getExtentRef() == null) {
			currentRule.setExtentRef("");
			if (priorRule.getExtentRef() == null) {
				priorRule.setExtentRef("");
			}
		}
		if (!currentRule.getExtentRef().equals(priorRule.getExtentRef())) {
			processReferenceAudit(code, currentRule.getExtentRef(), priorRule.getExtentRef(), extentAuditList,
					currentRule.getDhcode());
		}
		if (currentRule.getStatusRef() == null) {
			currentRule.setStatusRef("");
			if (priorRule.getStatusRef() == null) {
				priorRule.setStatusRef("");
			}
		}
		if (!currentRule.getStatusRef().equals(priorRule.getStatusRef())) {
			processReferenceAudit(code, currentRule.getStatusRef(), priorRule.getStatusRef(), statusAuditList,
					currentRule.getDhcode());
		}
		if (currentRule.getLocationRef() == null) {
			currentRule.setLocationRef("");
			if (priorRule.getLocationRef() == null) {
				priorRule.setLocationRef("");
			}
		}
		if (!currentRule.getLocationRef().equals(priorRule.getLocationRef())) {
			processReferenceAudit(code, currentRule.getLocationRef(), priorRule.getLocationRef(), locationAuditList,
					currentRule.getDhcode());
		}
		priorDhMap.remove(code);
	}

	private void processReferenceAudit(String code, String currentReference, String priorReference,
			List<CCIReferenceAudit> auditList, String dhCode) {

		CCIReferenceAudit audit = new CCIReferenceAudit();
		audit.setCode(code);
		audit.setDhcode(dhCode);
		audit.setNewReferenceValue(currentReference);
		audit.setOldReferenceValue(priorReference);
		CCIReferenceAttribute referenceAttribure = currentReferenceCodeMap.get(currentReference);
		if (referenceAttribure != null) {
			audit.setGenericAttributes(referenceAttribure.getGenericAttributes());
			audit.setMandatoryIndicator(referenceAttribure.getMandatoryIndicator());
		} else {
			audit.setGenericAttributes(new ArrayList<CCIGenericAttribute>());
		}
		auditList.add(audit);
	}

	private int writeLineFixedWithReference(Long contextId, String code, String dhcode, String refCode,
			BufferedWriter bw) throws IOException {
		StringBuilder sb = new StringBuilder();
		int recordCount = 0;
		if (!StringUtils.isEmpty(refCode)) {
			CCIReferenceAttribute referenceAttribure = currentReferenceCodeMap.get(refCode);
			if (referenceAttribure == null) {

				referenceAttribure = getCCIReferenceAttribute(contextId, refCode, LANGUAGE_CODE_ENG);
				if (referenceAttribure == null) {
					logger.error("Reference Value: " + refCode + " not found in current year.");
					getEmailService().sendMissingReferenceValueEmail(getGenerateReleaseTablesCriteria(),
							getCurrentUser(), CIMS_MISSING_REFERENCE_VALUE, refCode, code, dhcode);
					return recordCount;
				}
				if (!"ACTIVE".equals(referenceAttribure.getStatus())) {
					logger.error("Reference Value: " + refCode + " was disabled or removed in current year.");
					getEmailService().sendMissingReferenceValueEmail(getGenerateReleaseTablesCriteria(),
							getCurrentUser(), CIMS_DISABLED_REFERENCE_VALUE, refCode, code, dhcode);

				}
				currentReferenceCodeMap.put(refCode, referenceAttribure);
			}

			if (referenceAttribure != null) {
				if (CODE_VALUE_N.equals(referenceAttribure.getMandatoryIndicator())) {
					CimsFileUtils.padBlanksToString(code, MAX_LENGTH_CCI_CODE, sb);

					CimsFileUtils.padBlanksToString(generateValidationRuleReference(dhcode, refCode, ""),
							MAX_LENGTH_CCI_VALIDATIONRULE_REFERENCE, sb);
					sb.append(CimsFileUtils.LINE_SEPARATOR);
					recordCount++;
				}
				for (CCIGenericAttribute genericCode : referenceAttribure.getGenericAttributes()) {
					CimsFileUtils.padBlanksToString(code, MAX_LENGTH_CCI_CODE, sb);

					CimsFileUtils.padBlanksToString(
							generateValidationRuleReference(dhcode, refCode, genericCode.getCode()),
							MAX_LENGTH_CCI_VALIDATIONRULE_REFERENCE, sb);
					sb.append(CimsFileUtils.LINE_SEPARATOR);
					recordCount++;
				}
			}
		} else {
			CimsFileUtils.padBlanksToString(code, MAX_LENGTH_CCI_CODE, sb);

			CimsFileUtils.padBlanksToString(generateValidationRuleReference(dhcode, "", ""),
					MAX_LENGTH_CCI_VALIDATIONRULE_REFERENCE, sb);
			sb.append(CimsFileUtils.LINE_SEPARATOR);
			recordCount++;
		}
		bw.write(sb.toString());
		return recordCount;

	}

	private int writeLineTabReference(Long contextId, String code, String dhcode, String refCode, BufferedWriter bw)
			throws IOException {
		StringBuilder sb = new StringBuilder();
		int recordCount = 0;
		if (!StringUtils.isEmpty(refCode)) {
			CCIReferenceAttribute referenceAttribure = currentReferenceCodeMap.get(refCode);
			if (referenceAttribure == null) {

				referenceAttribure = getCCIReferenceAttribute(contextId, refCode, LANGUAGE_CODE_ENG);
				if (referenceAttribure == null) {
					logger.error("Reference Value: " + refCode + " not found in current year.");
					getEmailService().sendMissingReferenceValueEmail(getGenerateReleaseTablesCriteria(),
							getCurrentUser(), CIMS_MISSING_REFERENCE_VALUE, refCode, code, dhcode);
					return recordCount;
				}
				if (!"ACTIVE".equals(referenceAttribure.getStatus())) {
					logger.error("Reference Value: " + refCode + " was disabled or removed in current year.");
					getEmailService().sendMissingReferenceValueEmail(getGenerateReleaseTablesCriteria(),
							getCurrentUser(), CIMS_DISABLED_REFERENCE_VALUE, refCode, code, dhcode);

				}
				currentReferenceCodeMap.put(refCode, referenceAttribure);
			}
			if (referenceAttribure != null) {
				if (CODE_VALUE_N.equals(referenceAttribure.getMandatoryIndicator())) {
					List<String> values = new ArrayList<String>();
					values.add(code);
					values.add(generateValidationRuleReference(dhcode, refCode, ""));
					sb.append(buildLineTab(values));
					recordCount++;
				}
				for (CCIGenericAttribute genericCode : referenceAttribure.getGenericAttributes()) {
					List<String> values = new ArrayList<String>();
					values.add(code);
					values.add(generateValidationRuleReference(dhcode, refCode, genericCode.getCode()));
					sb.append(buildLineTab(values));
					recordCount++;
				}
			}
		} else {
			List<String> values = new ArrayList<String>();
			values.add(code);
			values.add(generateValidationRuleReference(dhcode, "", ""));
			sb.append(buildLineTab(values));
			recordCount++;
		}
		bw.write(sb.toString());
		return recordCount;
	}

	private int[] writeValidationReferenceFile(Map<String, Map<String, ValidationRuleSet>> validationRuleSetMap,
			FileFormat fileFormat, BufferedWriter bwEng, BufferedWriter bwExtent, BufferedWriter bwStatus,
			BufferedWriter bwLocation, Long contextId) throws IOException {
		int[] counts = new int[4];
		counts[0] = 0;
		counts[1] = 0;
		counts[2] = 0;
		counts[3] = 0;
		for (String dhCode : validationRuleSetMap.keySet()) {
			Map<String, ValidationRuleSet> dhMap = validationRuleSetMap.get(dhCode);
			for (String code : dhMap.keySet()) {
				ValidationRuleSet validationRuleSet = dhMap.get(code);
				if (FileFormat.TAB.equals(fileFormat)) {
					counts[0] += buildLineTab(validationRuleSet, bwEng);
					counts[1] += writeLineTabReference(contextId, code, dhCode, validationRuleSet.getExtentRef(),
							bwExtent);
					counts[2] += writeLineTabReference(contextId, code, dhCode, validationRuleSet.getStatusRef(),
							bwStatus);
					counts[3] += writeLineTabReference(contextId, code, dhCode, validationRuleSet.getLocationRef(),
							bwLocation);

				} else {
					counts[0] += buildLineFixedWidth(validationRuleSet, bwEng);
					counts[1] += writeLineFixedWithReference(contextId, code, dhCode, validationRuleSet.getExtentRef(),
							bwExtent);
					counts[2] += writeLineFixedWithReference(contextId, code, dhCode, validationRuleSet.getStatusRef(),
							bwStatus);
					counts[3] += writeLineFixedWithReference(contextId, code, dhCode,
							validationRuleSet.getLocationRef(), bwLocation);
				}
			}
			if (FileFormat.TAB.equals(fileFormat)) {
				counts[0] += addAdditionalLineTab(bwEng, dhCode);
				counts[1] += addAdditionalLineReferenceTab(bwExtent, dhCode);
				counts[2] += addAdditionalLineReferenceTab(bwStatus, dhCode);
				counts[3] += addAdditionalLineReferenceTab(bwLocation, dhCode);
			} else {
				counts[0] += addAdditionalLineFixedWith(bwEng, dhCode);
				counts[1] += addAdditionalLineReferenceFixedWidth(bwExtent, dhCode);
				counts[2] += addAdditionalLineReferenceFixedWidth(bwStatus, dhCode);
				counts[3] += addAdditionalLineReferenceFixedWidth(bwLocation, dhCode);
			}
		}

		bwEng.close();
		bwExtent.close();
		bwStatus.close();
		bwLocation.close();
		return counts;
	}

}
