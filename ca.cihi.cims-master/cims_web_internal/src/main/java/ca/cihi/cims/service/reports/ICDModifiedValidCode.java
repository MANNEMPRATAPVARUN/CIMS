package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.ModifiedValidCodeModel;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class ICDModifiedValidCode extends ReportGenerator {

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reportViewBean.getClassification();
		String currentYear = reportViewBean.getCurrentYear();
		Long priorYear = Long.parseLong(currentYear) - 1;

		reportData.put("currentYear", currentYear);
		reportData.put("classification", classification);

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentYear", currentYear);
		ContextIdentifier currentContext = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, currentYear);
		params.put("currentContextId", currentContext.getContextId());

		params.put("priorYear", priorYear);
		ContextIdentifier priorContext = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, priorYear.toString());
		params.put("priorContextId", priorContext.getContextId());
		params.put("catRubricClassId", getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("codeClassId", getConceptService().getICDClassID("TextPropertyVersion", "Code"));

		List<ModifiedValidCodeModel> modifiedCodes = getReportMapper().findICDModifiedValidCodes(params);

		reportData.put("codeCount", modifiedCodes.size());

		for (ModifiedValidCodeModel model : modifiedCodes) {
			Map<String, Object> detailData = new HashMap<String, Object>();
			detailData.put("codeValue", model.getCodeValue());
			detailData.put("currentFlag", model.getCurrentFlag());
			detailData.put("priorFlag", model.getPriorFlag());

			detailDataList.add(detailData);
		}

		return reportData;
	}

}
