package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.model.changerequest.ConceptModification;
//import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.ReviewGroupOutboundQuestionForReviewer;
import ca.cihi.cims.model.reports.ChangeRequestSendBack;
import ca.cihi.cims.model.reports.CodeValueChangeRequest;
import ca.cihi.cims.model.reports.MissingValidationHierarchy;
import ca.cihi.cims.model.reports.ModifiedValidCodeModel;
import ca.cihi.cims.model.reports.ModifiedValidationsModel;
import ca.cihi.cims.model.reports.QASummaryMetricsModel;

public interface ReportMapper {

	List<ModifiedValidationsModel> findCCIModifiedValidations(Map<String, Object> params);

	List<CodeValueChangeRequest> findCCINewTableCodesWithCodingDirectives(Map<String, Object> params);

	List<ChangeRequestSendBack> findChangeRequestSendBacks(Map<String, Object> params);

	List<ChangeRequestSendBack> findChangeRequestStatusChangeHistories(Map<String, Object> params);

	List<ConceptModification> findClassificationChangeIndexList(java.util.Map<String, Object> map);

	List<ConceptModification> findClassificationChangeTabularList(java.util.Map<String, Object> map);

	List<ModifiedValidationsModel> findICDModifiedValidations(Map<String, Object> params);

	List<ModifiedValidCodeModel> findICDModifiedValidCodes(Map<String, Object> params);

	List<QASummaryMetricsModel> findQASummaryMetrics(Map<String, Object> params);

	//List<QuestionForReviewer> findReviewGroupOutboundQuestionsIndexList(Map<String, Object> params);
	List<ReviewGroupOutboundQuestionForReviewer> findReviewGroupOutboundQuestionsIndexList(Map<String, Object> params);

	//List<QuestionForReviewer> findReviewGroupOutboundQuestionsTabularList(Map<String, Object> params);
	List<ReviewGroupOutboundQuestionForReviewer> findReviewGroupOutboundQuestionsTabularList(Map<String, Object> params);

	//List<QuestionForReviewer> findReviewGroupOutboundQuestionsWithoutCodeValueTabularList(Map<String, Object> params);
	List<ReviewGroupOutboundQuestionForReviewer> findReviewGroupOutboundQuestionsWithoutCodeValueTabularList(Map<String, Object> params);

	//List<QuestionForReviewer> findReviewGroupOutboundQuestionsWithoutLeadTermIndexList(Map<String, Object> params);
	List<ReviewGroupOutboundQuestionForReviewer> findReviewGroupOutboundQuestionsWithoutLeadTermIndexList(Map<String, Object> params);

	int findTotalChangeRequests(Map<String, Object> params);

	List<MissingValidationHierarchy> getCCIMissingValidationCodes(Map<String, Object> params);

	String getDataHoldingByDHCode(Map<String, Object> params);

	String getHasActiveValidationRuleDH(Map<String, Object> params);

	List<MissingValidationHierarchy> getICDMissingValidationCodes(Map<String, Object> params);
}
