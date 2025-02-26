package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import ca.cihi.cims.model.reports.ChangeRequestSendBack;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class SendBackDetail extends ReportGenerator {

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reportViewBean.getClassification();
		String year = reportViewBean.getYear();
		String owner = StringUtils.isEmpty(reportViewBean.getOwner()) ? null : reportViewBean.getOwner();
		String fromStatus = StringUtils.isEmpty(reportViewBean.getFromStatus()) ? null : reportViewBean.getFromStatus();
		String language = StringUtils.isEmpty(reportViewBean.getLanguage()) ? null : reportViewBean.getLanguage();
		Date fromDate = reportViewBean.getFromDate();
		Date toDate = reportViewBean.getToDate();

		reportData.put("classification", classification == null ? "" : classification);
		reportData.put("year", year);
		reportData.put("language", language == null ? "" : language);
		reportData.put("owner", owner == null ? "" : owner);
		reportData.put("fromStatus", fromStatus == null ? "" : fromStatus);
		String fromDateStr = getDateStr(fromDate);
		String toDateStr = getDateStr(toDate);
		reportData.put("fromDate", fromDateStr == null ? "" : fromDateStr);
		reportData.put("toDate", fromDateStr == null ? "" : toDateStr);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", classification);
		params.put("year", year);
		params.put("languageCode", language);
		params.put("fromStatus", fromStatus);
		params.put("owner", owner);
		params.put("fromDate", fromDate);
		params.put("toDate", toDate);

		List<ChangeRequestSendBack> histories = getReportMapper().findChangeRequestStatusChangeHistories(params);

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);
		for (ChangeRequestSendBack sendBack : histories) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("changeRequestId", sendBack.getChangeRequestId());
			data.put("classification", sendBack.getClassification());
			data.put("date", getDateStr(sendBack.getSendBackDate()));
			data.put("language", sendBack.getLanguage());
			data.put("fromStatus", sendBack.getFromStatus());
			data.put("sendBackReason", sendBack.getSendBackReason());
			data.put("owner", sendBack.getOwner());
			detailDataList.add(data);
		}
		reportData.put("qaErrorCount", String.valueOf(detailDataList.size()));
		return reportData;
	}

}
