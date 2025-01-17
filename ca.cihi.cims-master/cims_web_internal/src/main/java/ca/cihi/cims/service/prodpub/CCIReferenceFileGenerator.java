package ca.cihi.cims.service.prodpub;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.prodpub.AuditData;
import ca.cihi.cims.model.prodpub.AuditTable;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIGenericAttributeAudit;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;

public abstract class CCIReferenceFileGenerator extends FileGenerator {

	private static final int MAX_LENGTH_CCI_ATTRIBUTE_IN_CONTEXT_DESCRIPTION = 255;

	private void buildAuditList(List<CCIReferenceAttribute> currentAttributes,
			List<CCIReferenceAttribute> priorAttributes, List<CCIGenericAttributeAudit> revisedDescriptions,
			List<CCIGenericAttributeAudit> newDescriptions, List<CCIGenericAttributeAudit> disabledDescriptions) {
		Map<String, CCIReferenceAttribute> currentMap = new TreeMap<String, CCIReferenceAttribute>();
		for (CCIReferenceAttribute referenceAttribute : currentAttributes) {
			currentMap.put(referenceAttribute.getCode(), referenceAttribute);
		}
		for (CCIReferenceAttribute referenceAttribute : priorAttributes) {
			if (currentMap.containsKey(referenceAttribute.getCode())) {
				// reference code is active in both years
				List<CCIGenericAttribute> currGenericAttributes = currentMap.get(referenceAttribute.getCode())
						.getGenericAttributes();
				List<CCIGenericAttribute> priorGenericAttributes = referenceAttribute.getGenericAttributes();
				processOneReferenceAttribute(currGenericAttributes, priorGenericAttributes, revisedDescriptions,
						newDescriptions, disabledDescriptions, referenceAttribute);
				currentMap.remove(referenceAttribute.getCode());
			} else {
				// whole reference code disabled
				if ("N".equals(referenceAttribute.getMandatoryIndicator())) {
					CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
					audit.setCode("");
					audit.setOldDescription("");
					audit.setReferenceCode(referenceAttribute.getCode());
					disabledDescriptions.add(audit);
				}
				for (CCIGenericAttribute genericAttribute : referenceAttribute.getGenericAttributes()) {
					CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
					audit.setCode(genericAttribute.getCode());
					audit.setOldDescription(genericAttribute.getDescription());
					audit.setReferenceCode(referenceAttribute.getCode());
					disabledDescriptions.add(audit);
				}
			}
		}
		for (String referenceCode : currentMap.keySet()) {
			// add a new reference code
			CCIReferenceAttribute refAttribute = currentMap.get(referenceCode);
			if ("N".equals(refAttribute.getMandatoryIndicator())) {
				CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
				audit.setCode("");
				audit.setNewDescription("");
				audit.setReferenceCode(referenceCode);
				newDescriptions.add(audit);
			}
			for (CCIGenericAttribute attribute : refAttribute.getGenericAttributes()) {
				CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
				audit.setCode(attribute.getCode());
				audit.setNewDescription(attribute.getDescription());
				audit.setReferenceCode(referenceCode);
				newDescriptions.add(audit);
			}
		}
	}

	@Override
	protected int buildAuditReportTable(HSSFSheet sheet, int rownum, AuditTable auditTable) {
		int result = rownum;
		result = CimsFileUtils.buildAuditReportTitleLine(sheet, result, auditTable.getTableTitle());
		result++;

		result = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, result, auditTable.getHeaderDesc());
		result++;

		if (auditTable.getAuditData().size() > 0) {
			for (AuditData validation : auditTable.getAuditData()) {
				CCIGenericAttributeAudit audit = (CCIGenericAttributeAudit) validation;
				Row rowRevisions = sheet.createRow(result);
				Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell1.setCellValue(audit.getReferenceCode());
				Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell2.setCellValue(audit.getCode());
				if (TABLE_TYPE_REVISED.equals(auditTable.getTableType())) {
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(audit.getNewDescription());
					Cell tblRevisionsColumnCell4 = rowRevisions.createCell(4, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell4.setCellValue(audit.getOldDescription());
				} else if (TABLE_TYPE_REMOVED.equals(auditTable.getTableType())) {
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(audit.getOldDescription());
				} else if (TABLE_TYPE_NEW.equals(auditTable.getTableType())) {
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(audit.getNewDescription());
				}
				result++;
			}
		}

		return result;
	}

	@Override
	protected List<AuditTable> buildAuditTables(Long currentVersionYear, Long lastVersionYear, String languageCode,
			Long currentOpenContextId, Long lastVersionContextId) {
		List<CCIGenericAttributeAudit> revisedDescriptions = new ArrayList<CCIGenericAttributeAudit>();
		List<CCIGenericAttributeAudit> newDescriptions = new ArrayList<CCIGenericAttributeAudit>();
		List<CCIGenericAttributeAudit> disabledDescriptions = new ArrayList<CCIGenericAttributeAudit>();

		List<CCIReferenceAttribute> currentAttributes = getCCIReferenceAttributes(languageCode, currentOpenContextId);

		List<CCIReferenceAttribute> priorAttributes = getCCIReferenceAttributes(languageCode, lastVersionContextId);

		buildAuditList(currentAttributes, priorAttributes, revisedDescriptions, newDescriptions, disabledDescriptions);

		List<AuditTable> auditTables = new ArrayList<AuditTable>();
		AuditTable revised = new AuditTable();
		revised.setTableTitle(getRevisionsTitleValue(languageCode, currentVersionYear.toString()));
		revised.setHeaderDesc(getRevisedHeaderDescs(languageCode, currentVersionYear, lastVersionYear));
		List<AuditData> auditData = new ArrayList<AuditData>();
		for (CCIGenericAttributeAudit audit : revisedDescriptions) {
			auditData.add(audit);
		}
		revised.setAuditData(auditData);
		revised.setTableType(TABLE_TYPE_REVISED);
		auditTables.add(revised);

		AuditTable newTable = new AuditTable();
		newTable.setTableTitle(getNewTitleValue(languageCode, currentVersionYear.toString()));
		newTable.setHeaderDesc(getNewHeaderDescs(languageCode, currentVersionYear));
		List<AuditData> auditDataNew = new ArrayList<AuditData>();
		for (CCIGenericAttributeAudit audit : newDescriptions) {
			auditDataNew.add(audit);
		}
		newTable.setAuditData(auditDataNew);
		newTable.setTableType(TABLE_TYPE_NEW);
		auditTables.add(newTable);

		AuditTable removed = new AuditTable();
		removed.setTableTitle(getDisabledTitleValue(languageCode, currentVersionYear.toString()));
		removed.setHeaderDesc(getDisabledHeaderDescs(languageCode, lastVersionYear));
		List<AuditData> auditDataRemoved = new ArrayList<AuditData>();
		for (CCIGenericAttributeAudit audit : disabledDescriptions) {
			auditDataRemoved.add(audit);
		}
		removed.setAuditData(auditDataRemoved);
		removed.setTableType(TABLE_TYPE_REMOVED);
		auditTables.add(removed);
		return auditTables;
	}

	private int buildCCIReferencePublicationLineTab(CCIReferenceAttribute cciReferenceAttribute, BufferedWriter bw)
			throws IOException {
		StringBuilder lineTab = new StringBuilder();
		int count = 0;
		if (CODE_VALUE_N.equals(cciReferenceAttribute.getMandatoryIndicator())) {
			lineTab.append(cciReferenceAttribute.getCode());
			lineTab.append(CimsFileUtils.LINE_SEPARATOR);
			count++;
		}
		for (CCIGenericAttribute genericAttribute : cciReferenceAttribute.getGenericAttributes()) {
			lineTab.append(cciReferenceAttribute.getCode());
			lineTab.append(TAB);
			lineTab.append(genericAttribute.getCode());
			lineTab.append(TAB);
			lineTab.append(genericAttribute.getDescription() == null ? "" : genericAttribute.getDescription()
					.replace("\r", " ").replace("\n", " "));
			lineTab.append(CimsFileUtils.LINE_SEPARATOR);
			count++;
		}
		bw.write(lineTab.toString());
		return count;
	}

	@Override
	protected int buildLineFixedWidth(Object source, BufferedWriter bw) throws IOException {
		StringBuilder sb_line = new StringBuilder();
		int count = 0;
		CCIReferenceAttribute attribute = (CCIReferenceAttribute) source;
		if (CODE_VALUE_N.equals(attribute.getMandatoryIndicator())) {
			CimsFileUtils.padBlanksToString(attribute.getCode(), MAX_LENGTH_CCI_ATTRIBUTE_REFERENCE_CODE
					+ MAX_LENGTH_CCI_ATTRIBUTE_GENERIC_CODE + MAX_LENGTH_CCI_ATTRIBUTE_IN_CONTEXT_DESCRIPTION, sb_line);
			sb_line.append(CimsFileUtils.LINE_SEPARATOR);
			count++;
		}
		for (CCIGenericAttribute genericAttribute : attribute.getGenericAttributes()) {
			CimsFileUtils.padBlanksToString(attribute.getCode(), MAX_LENGTH_CCI_ATTRIBUTE_REFERENCE_CODE, sb_line);

			CimsFileUtils.padBlanksToString(genericAttribute.getCode(), MAX_LENGTH_CCI_ATTRIBUTE_GENERIC_CODE, sb_line);

			CimsFileUtils.padBlanksToString(genericAttribute.getDescription() == null ? "" : genericAttribute
					.getDescription().replace("\r", " ").replace("\n", " "),
					MAX_LENGTH_CCI_ATTRIBUTE_IN_CONTEXT_DESCRIPTION, sb_line);
			sb_line.append(CimsFileUtils.LINE_SEPARATOR);
			count++;
		}
		bw.write(sb_line.toString());
		return count;
	}

	@Override
	public void generateAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		generateEnglishAuditFile(currentOpenContextId, lastVersionContextId, generateTablesModel, pubDirectory);
		generateFrenchAuditFile(currentOpenContextId, lastVersionContextId, generateTablesModel, pubDirectory);
	}

	@Override
	protected String generateAuditFileName(String languageCode, Long currentVersionYear) {
		StringBuilder fileName = new StringBuilder();
		fileName.append(pubDirectory)
				.append(SUB_FOLDER_CCI)
				.append(FILE_SEPARATOR)
				.append(SUB_FOLDER_VALIDATION)
				.append(File.separator)
				.append(languageCode)
				.append(FILE_SEPARATOR)
				.append(LANGUAGE_CODE_ENG.equals(languageCode) ? CimsFileUtils
						.findCCIValidationAuditFileNameEnglish(currentVersionYear.toString()) : CimsFileUtils
						.findCCIValidationAuditFileNameFrench(currentVersionYear.toString()));
		return fileName.toString();
	}

	protected abstract void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException;

	protected void generateFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			String pubDirectory, String fileType, String languageCode, String attributeTypeCode,
			List<PublicationStatistics> statisticsSummary, int index) throws IOException {
		File folder = prepareFolder(SUB_FOLDER_CCI, SUB_FOLDER_VALIDATION, languageCode);
		String fileName = CimsFileUtils.buildAsciiFileName(CIMSConstants.CCI, fileType,
				LANGUAGE_CODE_ENG.equals(languageCode) ? "Eng" : "Fra",
				String.valueOf(generateTablesModel.getCurrentOpenYear()), null,
				getDelimitedType(generateTablesModel.getFileFormat()), null);

		File cciReferenceDesc = new File(folder.getCanonicalPath() + FILE_SEPARATOR + fileName);
		BufferedWriter bw = new BufferedWriter(new FileWriter(cciReferenceDesc));

		List<CCIReferenceAttribute> attributes = getCCIReferenceAttributes(languageCode, attributeTypeCode,
				currentOpenContextId);

		if ("L".equals(attributeTypeCode)) {

			attributes.addAll(getCCIReferenceAttributes(languageCode, "M", currentOpenContextId));
		}
		int count = 0;
		for (CCIReferenceAttribute cciReferenceAttribute : attributes) {
			if (FileFormat.TAB == generateTablesModel.getFileFormat()) {
				count += buildCCIReferencePublicationLineTab(cciReferenceAttribute, bw);
			} else {
				count += buildLineFixedWidth(cciReferenceAttribute, bw);
			}
		}
		bw.close();
		PublicationStatistics cciValidationStatistics = new PublicationStatistics("CCI_" + fileType.toUpperCase() + "_"
				+ languageCode + "_DESC", LANGUAGE_CODE_ENG.equals(languageCode) ? "English" : "French", count);
		if (statisticsSummary.size() >= index) {
			statisticsSummary.add(index, cciValidationStatistics);
		} else {
			statisticsSummary.add(cciValidationStatistics);
		}
	}

	protected abstract void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionContextId,
			GenerateReleaseTablesCriteria generateTablesModel, String pubDirectory) throws IOException;

	protected abstract List<CCIReferenceAttribute> getCCIReferenceAttributes(String languageCode, Long contextId);

	protected List<CCIReferenceAttribute> getCCIReferenceAttributes(String languageCode, String referenceType,
			Long contextId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params.put("genericAttributeCPVClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params.put("attributeTypeIndicatorClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "AttributeTypeIndicator"));
		params.put("attributeCodeClassId",
				getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params.put("attributeDescriptionClassId",
				getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params.put("domainValueCodeClassId",
				getConceptService().getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "DomainValueCode"));
		params.put("attributeMandatoryIndicatorClassId",
				getConceptService().getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params.put("contextId", contextId);
		params.put("attributeType", referenceType);
		params.put("languageCode", languageCode);
		return getPublicationMapper().getCCIReferenceAttributes(params);
	}

	private void processOneReferenceAttribute(List<CCIGenericAttribute> currGenericAttributes,
			List<CCIGenericAttribute> priorGenericAttributes, List<CCIGenericAttributeAudit> revisedDescriptions,
			List<CCIGenericAttributeAudit> newDescriptions, List<CCIGenericAttributeAudit> disabledDescriptions,
			CCIReferenceAttribute refAttribute) {

		Map<String, String> descMap = new TreeMap<String, String>();
		if (currGenericAttributes != null) {
			for (CCIGenericAttribute genericAttribute : currGenericAttributes) {
				descMap.put(genericAttribute.getCode(), genericAttribute.getDescription() == null ? ""
						: genericAttribute.getDescription());
			}
		}
		if (priorGenericAttributes != null) {
			for (CCIGenericAttribute genericAttribute : priorGenericAttributes) {

				if (descMap.containsKey(genericAttribute.getCode())) {
					// description change for same generic attribute
					String newDescription = descMap.get(genericAttribute.getCode());
					String oldDescription = genericAttribute.getDescription() == null ? "" : genericAttribute
							.getDescription();
					if (!newDescription.equals(oldDescription)) {
						CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
						audit.setCode(genericAttribute.getCode());
						audit.setNewDescription(newDescription);
						audit.setOldDescription(genericAttribute.getDescription());
						audit.setReferenceCode(refAttribute.getCode());
						revisedDescriptions.add(audit);
					}
					descMap.remove(genericAttribute.getCode());
				} else {
					// generic attribute removed
					CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
					audit.setCode(genericAttribute.getCode());
					audit.setOldDescription(genericAttribute.getDescription());
					audit.setReferenceCode(refAttribute.getCode());
					disabledDescriptions.add(audit);
				}

			}
		}
		for (String code : descMap.keySet()) {
			// add new generic attribute
			CCIGenericAttributeAudit audit = new CCIGenericAttributeAudit();
			audit.setCode(code);
			audit.setNewDescription(descMap.get(code));
			audit.setReferenceCode(refAttribute.getCode());
			newDescriptions.add(audit);
		}

	}
}
