package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.cihi.cims.dal.ContextIdentifier;
//import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.ReviewGroupOutboundQuestionForReviewer;
import ca.cihi.cims.web.bean.report.ReportViewBean;
import ca.cihi.cims.web.bean.report.ReviewGroupReportViewBean;

public class ReviewGroupOutboundQuestionsTabular extends ReportGenerator {

	private static final Log LOGGER = LogFactory.getLog(ReviewGroupOutboundQuestionsTabular.class);

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {

		ReviewGroupReportViewBean reviewGroupReportViewBean = (ReviewGroupReportViewBean)reportViewBean;

		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification=reviewGroupReportViewBean.getClassification();
		reportData.put("classification", classification);
		reportData.put("requestCategory", reviewGroupReportViewBean.getRequestCategory());
		reportData.put("valueFrom", reviewGroupReportViewBean.getCodeFrom());
		reportData.put("valueTo", reviewGroupReportViewBean.getCodeTo());
        reportData.put("year", reviewGroupReportViewBean.getYear());
        reportData.put("language", reviewGroupReportViewBean.getLanguageDesc());
        reportData.put("reviewGroup", reviewGroupReportViewBean.getReviewGroupName());
        reportData.put("patternTopic", reviewGroupReportViewBean.getPatternTopic());

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("classification", classification);
		params.put("year", reviewGroupReportViewBean.getYear());
		params.put("language", reviewGroupReportViewBean.getLanguage());
		params.put("reviewGroup", reviewGroupReportViewBean.getReviewGroup());
		params.put("patternTopic", reviewGroupReportViewBean.getPatternTopic());
		LOGGER.info("ReviewGroupOutboundQuestionsTabular.generatReportData()> params.get('patternTopic')=" + params.get("patternTopic"));

		//List<QuestionForReviewer> questionForReviewers = null;
		List<ReviewGroupOutboundQuestionForReviewer> questionForReviewers = null;

		if ( reviewGroupReportViewBean.getCodeFrom()!=null && !reviewGroupReportViewBean.getCodeFrom().equals("") &&
		     reviewGroupReportViewBean.getCodeTo()!=null && !reviewGroupReportViewBean.getCodeTo().equals("") ) {
		    params.put("codeFrom", reviewGroupReportViewBean.getCodeFrom());
		    params.put("codeTo", reviewGroupReportViewBean.getCodeTo()+"Z");
		    questionForReviewers = getReportMapper().findReviewGroupOutboundQuestionsTabularList(params);
	    } else {
		    questionForReviewers = getReportMapper().findReviewGroupOutboundQuestionsWithoutCodeValueTabularList(params);
		}

		LOGGER.info("ReviewGroupOutboundQuestionsTabular.generatReportData()> questionForReviewers.size()=" + questionForReviewers.size());

		//for(QuestionForReviewer questionForReviewer : questionForReviewers) {
		for(ReviewGroupOutboundQuestionForReviewer questionForReviewer : questionForReviewers) {

			Map<String, Object> detailData = new HashMap<String, Object>();
			Long changeRequestId = questionForReviewer.getChangeRequestId();
			String changeRequestName = questionForReviewer.getChangeRequestName();
			Long questionForReviewerId = questionForReviewer.getQuestionForReviewerId();
            String questionForReviewerTxt = questionForReviewer.getQuestionForReviewerTxt();

			detailData.put("changeRequestId", changeRequestId.toString());
			detailData.put("changeRequestName", changeRequestName);
			//detailData.put("questionForReviewerId", questionForReviewerId.toString());
			detailData.put("questionForReviewerId", questionForReviewerId+"");
			detailData.put("questionForReviewerTxt", questionForReviewerTxt);

			detailDataList.add(detailData);
        }

		return reportData;
	}



}
