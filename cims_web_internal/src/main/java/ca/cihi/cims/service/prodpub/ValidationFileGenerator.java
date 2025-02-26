package ca.cihi.cims.service.prodpub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ca.cihi.cims.model.prodpub.AuditData;
import ca.cihi.cims.model.prodpub.AuditTable;
import ca.cihi.cims.model.prodpub.CodeValidationAudit;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;

/**
 * This is a sub class of {@link FileGenerator}, covers validation ICD and CCI validation file generation.
 * 
 * @author tyang
 * 
 */
public abstract class ValidationFileGenerator extends FileGenerator {

	protected abstract void buildAuditLists(Long lastVersionICDContextId);

	@Override
	protected int buildAuditReportTable(HSSFSheet sheet, int rownum, AuditTable auditTable) {
		int result = rownum;
		result = CimsFileUtils.buildAuditReportTitleLine(sheet, result, auditTable.getTableTitle());
		result++;

		result = CimsFileUtils.buildAuditReportTableHeaderLine(sheet, result, auditTable.getHeaderDesc());
		result++;

		if (auditTable.getAuditData().size() > 0) {
			for (AuditData validation : auditTable.getAuditData()) {
				CodeValidationAudit audit = (CodeValidationAudit) validation;
				Row rowRevisions = sheet.createRow(result);
				Cell tblRevisionsColumnCell1 = rowRevisions.createCell(1, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell1.setCellValue(audit.getCode());
				Cell tblRevisionsColumnCell2 = rowRevisions.createCell(2, Cell.CELL_TYPE_STRING);
				tblRevisionsColumnCell2.setCellValue(audit.getDhcode());
				if (TABLE_TYPE_REVISED.equals(auditTable.getTableType())) {
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(audit.getNewDescriptionAuditReport());
					Cell tblRevisionsColumnCell4 = rowRevisions.createCell(4, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell4.setCellValue(audit.getOldDescriptionAuditReport());
				} else if (TABLE_TYPE_REMOVED.equals(auditTable.getTableType())) {
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(audit.getOldDescriptionAuditReport());
				} else if (TABLE_TYPE_NEW.equals(auditTable.getTableType())) {
					Cell tblRevisionsColumnCell3 = rowRevisions.createCell(3, Cell.CELL_TYPE_STRING);
					tblRevisionsColumnCell3.setCellValue(audit.getNewDescriptionAuditReport());
				}
				result++;
			}
		}

		return result;
	}

	@Override
	protected List<AuditTable> buildAuditTables(Long currentVersionYear, Long lastVersionYear, String languageCode,
			Long currentOpenContextId, Long lastVersionContextId) {
		List<AuditTable> auditTables = new ArrayList<AuditTable>();
		AuditTable revised = new AuditTable();
		revised.setTableTitle(getRevisionsTitleValue(languageCode, currentVersionYear.toString()));
		revised.setHeaderDesc(getRevisedHeaderDescs(languageCode, currentVersionYear, lastVersionYear));
		List<AuditData> auditData = new ArrayList<AuditData>();
		for (CodeValidationAudit audit : revisedCodeValidations) {
			audit.setNewDescription(audit.getNewDescription() != null ? audit.getNewDescription().replace(" ", "") : "");
			audit.setOldDescription(audit.getOldDescription() != null ? audit.getOldDescription().replace(" ", "") : "");
			auditData.add(audit);
		}
		revised.setAuditData(auditData);
		revised.setTableType(TABLE_TYPE_REVISED);
		auditTables.add(revised);

		AuditTable newTable = new AuditTable();
		newTable.setTableTitle(getNewTitleValue(languageCode, currentVersionYear.toString()));
		newTable.setHeaderDesc(getNewHeaderDescs(languageCode, currentVersionYear));
		List<AuditData> auditDataNew = new ArrayList<AuditData>();
		for (CodeValidationAudit audit : newCodeValidations) {
			audit.setNewDescription(audit.getNewDescription() != null ? audit.getNewDescription().replace(" ", "") : "");
			auditDataNew.add(audit);
		}
		newTable.setAuditData(auditDataNew);
		newTable.setTableType(TABLE_TYPE_NEW);
		auditTables.add(newTable);

		AuditTable removed = new AuditTable();
		removed.setTableTitle(getDisabledTitleValue(languageCode, currentVersionYear.toString()));
		removed.setHeaderDesc(getDisabledHeaderDescs(languageCode, lastVersionYear));
		List<AuditData> auditDataRemoved = new ArrayList<AuditData>();
		for (CodeValidationAudit audit : disabledCodeValidations) {
			audit.setOldDescription(audit.getOldDescription() != null ? audit.getOldDescription().replace(" ", "") : "");
			auditDataRemoved.add(audit);
		}
		removed.setAuditData(auditDataRemoved);
		removed.setTableType(TABLE_TYPE_REMOVED);
		auditTables.add(removed);
		return auditTables;
	}

	protected void buildCacheList(Map<String, Map<String, ValidationRuleSet>> validationSetMap,
			List<ValidationRuleSet> codeValidations, Long contextId) {
		for (ValidationRuleSet codeValidation : codeValidations) {
			String dhCode = codeValidation.getDhcode();
			Map<String, ValidationRuleSet> dhCacheMap = validationSetMap.get(dhCode);
			if (dhCacheMap == null) {
				dhCacheMap = new TreeMap<String, ValidationRuleSet>();
				validationSetMap.put(dhCode, dhCacheMap);
			}
			codeValidation.setValidationRuleText(generateValidationRuleSet(dhCode, codeValidation.getXmlText()));
			processValidationRule(codeValidation, dhCacheMap, contextId);
		}
	}

	protected abstract List<ValidationRuleSet> findChildCodes(Long contextId, Long conceptId);

	@Override
	public void generateAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException {
		buildAuditLists(lastVersionICDContextId);

		for (String dhCode : currentValidationSetMap.keySet()) {
			Map<String, ValidationRuleSet> dhMap = currentValidationSetMap.get(dhCode);
			Map<String, ValidationRuleSet> priorDhMap = priorValidationSetMap.get(dhCode);
			processOneDhCode(dhMap, priorDhMap);
		}

		generateEnglishAuditFile(currentOpenContextId, lastVersionICDContextId, generateTablesModel);
		generateFrenchAuditFile(currentOpenContextId, lastVersionICDContextId, generateTablesModel);

	}

	protected abstract void generateEnglishAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException;

	protected abstract void generateFrenchAuditFile(Long currentOpenContextId, Long lastVersionICDContextId,
			GenerateReleaseTablesCriteria generateTablesModel) throws IOException;

	@Override
	/**
	 * French validation file is the same as english file
	 */
	protected void generateFrenchFile(Long currentOpenContextId, GenerateReleaseTablesCriteria generateTablesModel,
			List<PublicationStatistics> statisticsSummary) throws IOException {

	}

	protected abstract String generateValidationRuleSet(String dhCode, String xmlString);

	private void processDisabledValidations(Map<String, ValidationRuleSet> priorDhMap) {
		for (String code : priorDhMap.keySet()) {
			ValidationRuleSet rule = priorDhMap.get(code);
			CodeValidationAudit audit = new CodeValidationAudit();
			audit.setCode(code);
			audit.setDhcode(rule.getDhcode());
			audit.setOldDescription(rule.getValidationRuleText());
			audit.setValidationRuleSet(rule);
			disabledCodeValidations.add(audit);
		}
	}

	protected abstract void processExistingValidation(String code, ValidationRuleSet currentRule,
			ValidationRuleSet priorRule, Map<String, ValidationRuleSet> priorDhMap);

	private void processOneCode(ValidationRuleSet currentRule, ValidationRuleSet priorRule, String code,
			Map<String, ValidationRuleSet> priorDhMap) {
		if (priorRule != null) {
			processExistingValidation(code, currentRule, priorRule, priorDhMap);
		} else {
			CodeValidationAudit audit = new CodeValidationAudit();
			audit.setCode(code);
			audit.setDhcode(currentRule.getDhcode());
			audit.setNewDescription(currentRule.getValidationRuleText());
			audit.setValidationRuleSet(currentRule);
			newCodeValidations.add(audit);
		}

	}

	private void processOneDhCode(Map<String, ValidationRuleSet> dhMap, Map<String, ValidationRuleSet> priorDhMap) {
		for (String code : dhMap.keySet()) {
			ValidationRuleSet currentRule = dhMap.get(code);
			ValidationRuleSet priorRule = priorDhMap.get(code);

			processOneCode(currentRule, priorRule, code, priorDhMap);
		}

		if (!priorDhMap.isEmpty()) {
			processDisabledValidations(priorDhMap);

		}

	}

	private void processParent(ValidationRuleSet codeValidation, Map<String, ValidationRuleSet> dhCacheMap,
			Long contextId) {
		List<ValidationRuleSet> childs = findChildCodes(contextId, codeValidation.getConceptId());
		for (ValidationRuleSet childCode : childs) {
			ValidationRuleSet child = new ValidationRuleSet();
			child.setValidationRuleText(codeValidation.getValidationRuleText());
			child.setExtentRef(codeValidation.getExtentRef());
			child.setStatusRef(codeValidation.getStatusRef());
			child.setLocationRef(codeValidation.getLocationRef());
			child.setDhcode(codeValidation.getDhcode());
			child.setHasChild(childCode.getHasChild());
			child.setConceptId(childCode.getConceptId());
			child.setCode(childCode.getCode());
			processValidationRule(child, dhCacheMap, contextId);
		}

	}

	private void processValidationRule(ValidationRuleSet codeValidation, Map<String, ValidationRuleSet> dhCacheMap,
			Long contextId) {
		if (CODE_VALUE_N.equals(codeValidation.getHasChild())) {
		    dhCacheMap.put(codeValidation.getCode(), codeValidation);
		} else {
			processParent(codeValidation, dhCacheMap, contextId);
		}
	}

}
