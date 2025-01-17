package ca.cihi.cims.service.reports;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.text.SimpleDateFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.web.bean.report.ReportViewBean;
import ca.cihi.cims.web.bean.report.QAErrorReportViewBean;
import ca.cihi.cims.model.reports.ChangeRequestSendBack;


public class QAErrorDescriptions extends ReportGenerator {

	private static final Log LOGGER = LogFactory.getLog(QAErrorDescriptions.class);

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {

		LOGGER.info("QAErrorDescriptions.generatReportData()> begin...");

		QAErrorReportViewBean qaErrorReportViewBean = (QAErrorReportViewBean)reportViewBean;

		Map<String, Object> reportData = new HashMap<String, Object>();
		reportData.put("classification", qaErrorReportViewBean.getClassification());
        reportData.put("year", qaErrorReportViewBean.getYear());
        reportData.put("language", qaErrorReportViewBean.getLanguageDesc());
		reportData.put("owner", qaErrorReportViewBean.getOwnerUserName());
		//reportData.put("dateFrom", qaErrorReportViewBean.getDateFrom()+"");
		//reportData.put("dateTo", qaErrorReportViewBean.getDateTo()+"");
		reportData.put("dateFrom", formatDate(qaErrorReportViewBean.getDateFrom()));
		reportData.put("dateTo", formatDate(qaErrorReportViewBean.getDateTo()));
		reportData.put("statusFrom", qaErrorReportViewBean.getStatusFrom());

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", qaErrorReportViewBean.getClassification());
		params.put("year", qaErrorReportViewBean.getYear());
		params.put("language", qaErrorReportViewBean.getLanguage());
		//params.put("owner", qaErrorReportViewBean.getOwner());
		if (qaErrorReportViewBean.getOwnerUserName() != null) {
			params.put("owner", qaErrorReportViewBean.getOwnerUserName().trim());
		} else
			params.put("owner", qaErrorReportViewBean.getOwnerUserName());
		LOGGER.info("QAErrorDescriptions.generatReportData()> params.get('owner')=<" + params.get("owner") + ">");

		params.put("dateFrom", qaErrorReportViewBean.getDateFrom());
		params.put("dateTo", qaErrorReportViewBean.getDateTo());
		params.put("statusFrom", qaErrorReportViewBean.getStatusFrom());

		List<ChangeRequestSendBack> changeRequestSendBacks = getReportMapper().findChangeRequestSendBacks(params);
        long qaErrorCount = 0;
        if (changeRequestSendBacks != null) {
            qaErrorCount = changeRequestSendBacks.size();
		}
		LOGGER.info("QAErrorDescriptions.generatReportData()> qaErrorCount = " + qaErrorCount);
		reportData.put("errorCount", qaErrorCount+"");

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		for(ChangeRequestSendBack changeRequestSendBack : changeRequestSendBacks) {

			Map<String, Object> detailData = new HashMap<String, Object>();
			String changeRequestId = changeRequestSendBack.getChangeRequestId();
			Date sendBackDate = changeRequestSendBack.getSendBackDate();
			String classification = changeRequestSendBack.getClassification();
			String language = changeRequestSendBack.getLanguage();
			String owner = changeRequestSendBack.getOwner();
			String fromStatus = changeRequestSendBack.getFromStatus();
			String sendBackReason = changeRequestSendBack.getSendBackReason();

			detailData.put("changeRequestId", changeRequestId);
			//detailData.put("sendBackDate", sendBackDate+"");
			detailData.put("sendBackDate", formatDate(sendBackDate));
			detailData.put("classification", classification);
			detailData.put("language", language);
			detailData.put("owner", owner);
			detailData.put("fromStatus", fromStatus);
			detailData.put("sendBackReason", sendBackReason);

			detailDataList.add(detailData);
        }

		return reportData;
	}

	public static String formatDate(Date date) {
		if (date == null) {
			return "";
		}
    	//SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
    	//String fDate = sdf.format(date);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	String fDate = sdf.format(date);
		return fDate;
	}

}
