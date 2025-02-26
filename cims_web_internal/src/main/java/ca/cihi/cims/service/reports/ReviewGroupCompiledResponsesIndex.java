package ca.cihi.cims.service.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.cihi.cims.dal.ContextIdentifier;
//import ca.cihi.cims.model.changerequest.ConceptModification;
//import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.ReviewGroupOutboundQuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.web.bean.report.ReportViewBean;
import ca.cihi.cims.web.bean.report.ReviewGroupReportViewBean;

public class ReviewGroupCompiledResponsesIndex extends ReportGenerator {

	private static final Log LOGGER = LogFactory.getLog(ReviewGroupCompiledResponsesIndex.class);

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {

		ReviewGroupReportViewBean reviewGroupReportViewBean = (ReviewGroupReportViewBean)reportViewBean;

		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reviewGroupReportViewBean.getClassification();
		reportData.put("classification", classification);
		reportData.put("requestCategory", reviewGroupReportViewBean.getRequestCategory());
		reportData.put("indexBook", reviewGroupReportViewBean.getIndexBook());
		reportData.put("leadIndexTerm", reviewGroupReportViewBean.getLeadTerm());
		LOGGER.info("ReviewGroupCompiledResponsesIndex.generatReportData()> indexBook=" + reviewGroupReportViewBean.getIndexBook());
		LOGGER.info("ReviewGroupCompiledResponsesIndex.generatReportData()> leadIndexTerm=" + reviewGroupReportViewBean.getLeadTerm());

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

		ContextIdentifier contextIdentifier=getLookupService().findBaseContextIdentifierByClassificationAndYear(classification, getLookupService().findCurrentOpenYear(classification)+"");
		params.put("contextId", contextIdentifier.getContextId());
		LOGGER.info("ReviewGroupCompiledResponsesIndex.generatReportData()> contextId=" + contextIdentifier.getContextId());

		//List<QuestionForReviewer> questionForReviewers = null;
		List<ReviewGroupOutboundQuestionForReviewer> questionForReviewers = null;
        if (reviewGroupReportViewBean.getLeadTermElementId() != null && !reviewGroupReportViewBean.getLeadTermElementId().equals("")) {
		    params.put("leadTermElementId", reviewGroupReportViewBean.getLeadTermElementId());
		    LOGGER.info("ReviewGroupOutboundQuestionsIndex.generatReportData()> params.get('leadTermElementId')=" + params.get("leadTermElementId"));
		    questionForReviewers = getReportMapper().findReviewGroupOutboundQuestionsIndexList(params);
		} else {
		    questionForReviewers = getReportMapper().findReviewGroupOutboundQuestionsWithoutLeadTermIndexList(params);
		}

		LOGGER.info("ReviewGroupCompiledResponsesIndex.generatReportData()> questionForReviewers.size()=" + questionForReviewers.size());

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

	        LOGGER.info("ReviewGroupCompiledResponsesIndex.generatReportData()> changeRequestId=<" + changeRequestId + ">");

            List<UserComment> userComments = questionForReviewer.getQuestionComments();
    		LOGGER.info("ReviewGroupCompiledResponsesTabular.generatReportData()> userComments.size()=" + userComments.size());

            int i = 0;
		    for(UserComment userComment : userComments) {
				i++;
                if (i == 1) {
                    //detailData.put("responseId", userComment.getUserCommentId().toString());
                    detailData.put("responseId", userComment.getUserCommentId()+"");
                    detailData.put("response", userComment.getUserCommentTxt());
		            detailDataList.add(detailData);
			    } else {
			        Map<String, Object> detailData2 = new HashMap<String, Object>();
			        detailData2.put("changeRequestId", changeRequestId.toString());
			        detailData2.put("changeRequestName", changeRequestName);
			        //detailData2.put("questionForReviewerId", questionForReviewerId.toString());
			        detailData2.put("questionForReviewerId", questionForReviewerId+"");
			        detailData2.put("questionForReviewerTxt", questionForReviewerTxt);

                    //detailData2.put("responseId", userComment.getUserCommentId().toString());
                    detailData2.put("responseId", userComment.getUserCommentId()+"");
                    detailData2.put("response", userComment.getUserCommentTxt());
		            detailDataList.add(detailData2);
				}
		    }

            if (userComments.size() == 0) {
			    detailDataList.add(detailData);
		    }

        }

		return reportData;
	}

}
