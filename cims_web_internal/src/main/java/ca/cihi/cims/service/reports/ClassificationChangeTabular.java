package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class ClassificationChangeTabular extends ReportGenerator {

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reportViewBean.getClassification();
		reportData.put("classification", classification);
		reportData.put("valueFrom", reportViewBean.getCodeFrom());
		reportData.put("requestCategory", reportViewBean.getRequestCategory());
		reportData.put("valueTo", reportViewBean.getCodeTo());

		List<ContextIdentifier> openContextIdentifiers = getOpenContextIdentifiers(classification, null);
		int i = 1;
		Map<String, Integer> detailHeader = new HashMap<String, Integer>();
		for (ContextIdentifier contextIdentifier : openContextIdentifiers) {
			detailHeader.put("year" + i++, Integer.parseInt(contextIdentifier.getVersionCode()));
		}
		reportData.put("detailHeader", detailHeader);

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeFrom", reportViewBean.getCodeFrom().replace(".^^", ""));
		params.put("codeTo", reportViewBean.getCodeTo().replace(".^^", "") + "Z");
		params.put("classification", classification);
		List<ConceptModification> conceptModifications = getReportMapper().findClassificationChangeTabularList(params);

		Map<String, Map<String, List<Long>>> tempData = new LinkedHashMap<String, Map<String, List<Long>>>();
		for (ConceptModification conceptModification : conceptModifications) {
			String codeValue = conceptModification.getCode();
			String versionCode = conceptModification.getVersionCode();
			Long changeRequestId = conceptModification.getChangeRequestId();
			String yearLabel = "";
			for (String key : detailHeader.keySet()) {
				Integer versionCodeExpected = detailHeader.get(key);
				if (versionCode.equals(versionCodeExpected.toString())) {
					yearLabel = key;
					break;
				}
			}
			Map<String, List<Long>> changeDataByCode = tempData.get(codeValue);
			if (changeDataByCode == null) {
				changeDataByCode = new TreeMap<String, List<Long>>();
				tempData.put(codeValue, changeDataByCode);
			}
			List<Long> changeRequestIds = changeDataByCode.get(yearLabel);
			if (changeRequestIds == null) {
				changeRequestIds = new ArrayList<Long>();
				changeDataByCode.put(yearLabel, changeRequestIds);
			}
			if (!changeRequestIds.contains(changeRequestId)) {
				changeRequestIds.add(changeRequestId);
			}
		}

		for (String codeValue : tempData.keySet()) {
			Map<String, Object> detailData = new HashMap<String, Object>();
			detailData.put("codeValue", codeValue);
			Map<String, List<Long>> yearDetail = tempData.get(codeValue);
			for (String yearLabel : yearDetail.keySet()) {
				detailData.put(yearLabel, paddingChangeRequestIDs(yearDetail.get(yearLabel)));
			}
			detailDataList.add(detailData);
		}

		return reportData;
	}

}
