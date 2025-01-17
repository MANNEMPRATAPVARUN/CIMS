package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class ClassificationChangeIndex extends ReportGenerator {

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reportViewBean.getClassification();
		reportData.put("classification", classification);
		reportData.put("requestCategory", reportViewBean.getRequestCategory());
		reportData.put("indexBook", reportViewBean.getIndexBook());
		reportData.put("leadIndexTerm", reportViewBean.getLeadTerm());

		List<ContextIdentifier> openContextIdentifiers = getOpenContextIdentifiers(classification, "Y");
		int i = 1;
		Map<String, Integer> detailHeader = new HashMap<String, Integer>();
		for (ContextIdentifier contextIdentifier : openContextIdentifiers) {
			detailHeader.put("year" + i++, Integer.parseInt(contextIdentifier.getVersionCode()));
		}
		reportData.put("detailHeader", detailHeader);

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("leadTermElementId", reportViewBean.getLeadTermElementId());
		params.put("classification", classification);
		ContextIdentifier contextIdentifier = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, getLookupService().findCurrentOpenYear(classification) + "");
		params.put("contextId", contextIdentifier.getContextId());
		List<ConceptModification> conceptModifications = getReportMapper().findClassificationChangeIndexList(params);

		Map<String, Map<String, Map<String, List<Long>>>> tempData = new TreeMap<String, Map<String, Map<String, List<Long>>>>();
		for (ConceptModification conceptModification : conceptModifications) {

			String indexTerm = conceptModification.getIndexTerm();
			String versionCode = conceptModification.getVersionCode();
			String indexPath = conceptModification.getIndexPath();
			indexPath = indexPath.substring(indexPath.indexOf(">") + 1);
			Long changeRequestId = conceptModification.getChangeRequestId();

			String yearLabel = "";
			for (String key : detailHeader.keySet()) {
				Integer versionCodeExpected = detailHeader.get(key);
				if (versionCode.equals(versionCodeExpected.toString())) {
					yearLabel = key;
					break;
				}
			}

			Map<String, Map<String, List<Long>>> changeDataByIndexPath = tempData.get(indexPath);
			if (changeDataByIndexPath == null) {
				changeDataByIndexPath = new TreeMap<String, Map<String, List<Long>>>();
				tempData.put(indexPath, changeDataByIndexPath);
			}
			Map<String, List<Long>> changeDataByIndexTerm = changeDataByIndexPath.get(indexTerm);
			if (changeDataByIndexTerm == null) {
				changeDataByIndexTerm = new TreeMap<String, List<Long>>();
				changeDataByIndexPath.put(indexTerm, changeDataByIndexTerm);
			}
			List<Long> changeRequestIds = changeDataByIndexTerm.get(yearLabel);
			if (changeRequestIds == null) {
				changeRequestIds = new ArrayList<Long>();
				changeDataByIndexTerm.put(yearLabel, changeRequestIds);
			}
			if (!changeRequestIds.contains(changeRequestId)) {
				changeRequestIds.add(changeRequestId);
			}
		}

		for (String indexPath : tempData.keySet()) {
			Map<String, Object> detailData = new HashMap<String, Object>();
			detailData.put("hierarchicalPath", indexPath);

			Map<String, Map<String, List<Long>>> indexTermDetail = tempData.get(indexPath);
			for (String indexTerm : indexTermDetail.keySet()) {
				detailData.put("indexTerm", indexTerm);
				Map<String, List<Long>> yearDetail = indexTermDetail.get(indexTerm);
				for (String yearLabel : yearDetail.keySet()) {
					detailData.put(yearLabel, paddingChangeRequestIDs(yearDetail.get(yearLabel)));
				}
			}
			detailDataList.add(detailData);
		}

		return reportData;
	}

}
