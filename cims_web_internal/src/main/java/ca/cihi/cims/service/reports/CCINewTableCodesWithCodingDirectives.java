package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.CodeValueChangeRequest;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class CCINewTableCodesWithCodingDirectives extends ReportGenerator {

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String year = reportViewBean.getCurrentYear();
		String priorYear = reportViewBean.getPriorYear();
		String classification = reportViewBean.getClassification();

		reportData.put("year", year);
		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();
		ContextIdentifier baseContextIdentifier = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, priorYear);
		ContextIdentifier contextIdentifier = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, year);

		params.put("baseContextId", baseContextIdentifier.getContextId());
		params.put("contextId", contextIdentifier.getContextId());

		params.put("catRubricClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params.put("cciCodeClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params.put("narrowClassId", getConceptService()
				.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));
		params.put("includePresentationClassId",
				getConceptService().getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "IncludePresentation"));
		params.put("excludePresentationClassId",
				getConceptService().getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "ExcludePresentation"));
		params.put("codeAlsoPresentationClassId",
				getConceptService().getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "CodeAlsoPresentation"));
		params.put("notePresentationClassId",
				getConceptService().getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "NotePresentation"));
		params.put("omitCodePresentationClassId",
				getConceptService().getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "OmitCodePresentation"));
		params.put("tablePresentationClassId",
				getConceptService().getCCIClassID(WebConstants.HTML_PROPERTY_VERSION, "TablePresentation"));

		List<CodeValueChangeRequest> codeValues = getReportMapper().findCCINewTableCodesWithCodingDirectives(params);

		for (CodeValueChangeRequest codeValue : codeValues) {
			Map<String, Object> detailData = new HashMap<String, Object>();
			detailData.put("codeValue", codeValue.getCodeValue());
			detailData.put("changeRequestId", codeValue.getChangeRequestId().toString());
			detailData.put("changeRequestName", codeValue.getChangeRequestName());
			detailDataList.add(detailData);
		}

		return reportData;
	}

}
