package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.model.reports.QASummaryMetricsModel;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class QASummaryMetrics extends ReportGenerator {

	private static final String VALID = "Valid";
	private static final String ACCEPTED = "Accepted";
	private static final String TRANSLATION_DONE = "Translation Done";
	private static final String REALIZED = "Realized";
	private static final String QA_DONE = "QA Done";
	private static final String VALIDATION_DONE = "Validation Done";

	private String findPreviousStatus(QASummaryMetricsModel model) {
		String result = null;
		if (model.getValid() == 1) {
			result = VALID;
		} else if (model.getAccepted() == 1) {
			result = ACCEPTED;
		} else if (model.getTranslationDone() == 1) {
			result = TRANSLATION_DONE;
		} else if (model.getValidationDone() == 1) {
			result = VALIDATION_DONE;
		} else if (model.getRealized() == 1) {
			result = REALIZED;
		} else if (model.getQaDone() == 1) {
			result = QA_DONE;
		}
		return result;
	}

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = StringUtils.isEmpty(reportViewBean.getClassification()) ? null : reportViewBean
				.getClassification();
		String year = reportViewBean.getYear();
		String language = StringUtils.isEmpty(reportViewBean.getLanguage()) ? null : reportViewBean.getLanguage();
		Date fromDate = reportViewBean.getFromDate();
		Date toDate = reportViewBean.getToDate();

		reportData.put("classification", classification == null ? "" : classification);
		reportData.put("year", year);
		reportData.put("language", language == null ? "" : language);
		String fromDateStr = getDateStr(fromDate);
		String toDateStr = getDateStr(toDate);
		reportData.put("fromDate", fromDateStr == null ? "" : fromDateStr);
		reportData.put("toDate", toDateStr == null ? "" : toDateStr);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", classification);
		params.put("year", year);
		params.put("languageCode", language);
		params.put("fromStatus", null);
		params.put("owner", null);
		params.put("fromDate", fromDate);
		params.put("toDate", toDate);

		List<QASummaryMetricsModel> metrics = getReportMapper().findQASummaryMetrics(params);

		int totalChangeRequests = getReportMapper().findTotalChangeRequests(params);

		int uniqueChangeRequests = 0;
		int qaErrors = 0;

		int uniqueValid = 0;
		int uniqueAccepted = 0;
		int uniqueTranslationDone = 0;
		int uniqueValidationDone = 0;
		int uniqueRealized = 0;
		int uniqueQADone = 0;

		int valid = 0;
		int accepted = 0;
		int translationDone = 0;
		int validationDone = 0;
		int realized = 0;
		int qaDone = 0;

		long tempChangeRequestId = 0;
		String previousStatus = null;

		for (QASummaryMetricsModel model : metrics) {
			qaErrors++;
			valid += model.getValid();
			accepted += model.getAccepted();
			translationDone += model.getTranslationDone();
			validationDone += model.getValidationDone();
			realized += model.getRealized();
			qaDone += model.getQaDone();
			long changeRequestId = model.getChangeRequestId();
			if (changeRequestId != tempChangeRequestId) {
				// new change request
				previousStatus = findPreviousStatus(model);
				tempChangeRequestId = changeRequestId;
				uniqueChangeRequests++;
				uniqueValid += model.getValid();
				uniqueAccepted += model.getAccepted();
				uniqueQADone += model.getQaDone();
				uniqueRealized += model.getRealized();
				uniqueTranslationDone += model.getTranslationDone();
				uniqueValidationDone += model.getValidationDone();
			} else {
				if (model.getAccepted() == 1 && !ACCEPTED.equals(previousStatus)) {
					uniqueAccepted++;
					previousStatus = ACCEPTED;
				} else if (model.getQaDone() == 1 && !QA_DONE.equals(previousStatus)) {
					uniqueQADone++;
					previousStatus = QA_DONE;
				} else if (model.getRealized() == 1 && !REALIZED.equals(previousStatus)) {
					uniqueRealized++;
					previousStatus = REALIZED;
				} else if (model.getTranslationDone() == 1 && !TRANSLATION_DONE.equals(previousStatus)) {
					uniqueTranslationDone++;
					previousStatus = TRANSLATION_DONE;
				} else if (model.getValidationDone() == 1 && !VALIDATION_DONE.equals(previousStatus)) {
					uniqueValidationDone++;
					previousStatus = VALIDATION_DONE;
				} else if (model.getValid() == 1 && !VALID.equals(previousStatus)) {
					uniqueValid++;
					previousStatus = VALID;
				}
			}
		}

		List<Map<String, Object>> detailDataList1 = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList1);
		Map<String, Object> detail1Data = new HashMap<String, Object>();
		detail1Data.put("valid", uniqueValid);
		detail1Data.put("accepted", uniqueAccepted);
		detail1Data.put("translationDone", uniqueTranslationDone);
		detail1Data.put("validationDone", uniqueValidationDone);
		detail1Data.put("realized", uniqueRealized);
		detail1Data.put("qaDone", uniqueQADone);
		detail1Data.put("total", uniqueChangeRequests);

		detailDataList1.add(detail1Data);

		List<Map<String, Object>> detailDataList2 = new ArrayList<Map<String, Object>>();
		reportData.put("detail2", detailDataList2);

		Map<String, Object> detail2Data = new HashMap<String, Object>();
		detail2Data.put("valid", valid);
		detail2Data.put("accepted", accepted);
		detail2Data.put("translationDone", translationDone);
		detail2Data.put("validationDone", validationDone);
		detail2Data.put("realized", realized);
		detail2Data.put("qaDone", qaDone);
		detail2Data.put("total", qaErrors);

		detailDataList2.add(detail2Data);

		reportData.put("totalCount", totalChangeRequests);
		return reportData;
	}

}
