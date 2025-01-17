package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.MissingValidationHierarchy;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class MissingValidationICD extends ReportGenerator {

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reportViewBean.getClassification();
		String year = reportViewBean.getYear();
		reportData.put("classification", classification);
		reportData.put("codeFrom", reportViewBean.getCodeFrom());
		reportData.put("year", year);
		reportData.put("codeTo", reportViewBean.getCodeTo());
		reportData.put("dataHolding", reportViewBean.getDataHolding());

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();
		ContextIdentifier contextIdentifier = getLookupService()
				.findBaseContextIdentifierByClassificationAndYear(
						classification, year);
		params.put("contextId", contextIdentifier.getContextId());
		params.put("codeFrom", reportViewBean.getCodeFrom());
		params.put("codeTo", reportViewBean.getCodeTo() + "Z");
		params.put("dhCode", reportViewBean.getDataHoldingCode());

		params.put("validationCPVClassId", getConceptService().getICDClassID(
				WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationICDCPV"));
		params.put("validationClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "ValidationICD"));
		params.put("catRubricClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("validationFacilityClassId", getConceptService().getICDClassID(
				WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", getConceptService().getICDClassID(
				"TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId",
				getConceptService().getICDClassID("TextPropertyVersion", "Code"));
		params.put("narrowClassId", getConceptService().getICDClassID(
				WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		List<MissingValidationHierarchy> missingValidationCodes = getReportMapper()
				.getICDMissingValidationCodes(params);

		for (MissingValidationHierarchy codeHierarchy : missingValidationCodes) {
			Map<String, Object> detailData = new HashMap<String, Object>();
			Map<String, String> validationRuleCache = new HashMap<String, String>();
			String elementIdPath = codeHierarchy.getElementIdPath();
			boolean hasActiveValidationRule = false;
			String[] elementIds = elementIdPath.split(",");
			for (int i = 1; i < elementIds.length; i++) {
				String validationRule = validationRuleCache
						.get(elementIds[i]);
				if (validationRule == null) {
					Map<String, Object> params1 = new HashMap<String, Object>();
					params1.put("contextId",
							contextIdentifier.getContextId());
					params1.put("conceptId", elementIds[i]);
					params1.put("dhCode",
							reportViewBean.getDataHoldingCode());
					validationRule = getReportMapper()
							.getHasActiveValidationRuleDH(params1);
					validationRuleCache.put(elementIds[1], validationRule);
				}
				if ("Y".equals(validationRule)) {
					hasActiveValidationRule = true;
					break;
				}
			}
			if (!hasActiveValidationRule) {
				detailData.put("codeValue", codeHierarchy.getCode());
				detailDataList.add(detailData);
			}
		}

		return reportData;
	}

}
