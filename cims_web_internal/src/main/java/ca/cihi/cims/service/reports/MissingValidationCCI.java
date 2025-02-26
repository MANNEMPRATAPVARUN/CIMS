package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.MissingValidationHierarchy;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class MissingValidationCCI extends ReportGenerator {

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
		params.put("validationCPVClassId", getConceptService().getCCIClassID(
				WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationCCICPV"));
		params.put("validationClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "ValidationCCI"));
		params.put("catRubricClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params.put("validationFacilityClassId", getConceptService().getCCIClassID(
				WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", getConceptService().getCCIClassID(
				"TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId",
				getConceptService().getCCIClassID("TextPropertyVersion", "Code"));
		params.put("cciCodeClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params.put("narrowClassId", getConceptService().getCCIClassID(
				WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		List<MissingValidationHierarchy> missingValidationCodes = getReportMapper()
				.getCCIMissingValidationCodes(params);

		for (MissingValidationHierarchy codeHierarchy : missingValidationCodes) {
			Map<String, Object> detailData = new HashMap<String, Object>();
			detailData.put("codeValue", codeHierarchy.getCode());
			detailDataList.add(detailData);
		}

		return reportData;
	}

}
