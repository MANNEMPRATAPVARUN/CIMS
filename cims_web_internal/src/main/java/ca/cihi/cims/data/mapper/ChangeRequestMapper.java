package ca.cihi.cims.data.mapper;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestEvolution;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;

public interface ChangeRequestMapper {

	// List<ChangeRequest> getAllChangeRequests();

	void deleteChangeRequestCodingQuestions(Long changeRequestId);

	void deleteChangeRequestEvolution(Long changeRequestId);

	void deleteChangeRequestOtherLinks(Long changeRequestId);

	void deleteChangeRequestReviewerQuestion(Long questionForReviewerId);

	// void updateChangeRequestStatusOnly (ChangeRequest changeRequest);
	// ContextIdentifier findContextIdentificationById(Long baseContextId);

	void deleteChangeRequestReviewGroups(Long changeRequestId);

	void deleteChangeRequestUrcLinks(Long changeRequestId);

	// ContextIdentifier findOpenContextByChangeRquestId(Long changeRequestId);

	void deleteDocumentReferenceById(Long documentReferenceId);

	List<Advice> findAdvicesForChangeRequest(Long changeRequestId);

	List<ChangeRequest> findAllCCIChangeRequests();

	List<ChangeRequest> findAllChangeRequests();

	List<ChangeRequest> findAllICDChangeRequests();

	ChangeRequestDTO findChangeRequestById(Long changeRequestId);

	ChangeRequestRealization findChangeRequestRealizationById(Long changeRequestRealizationId);

	List<ChangeRequest> findChangeRequestsBySearchCriteria(UserSearchCriteria userSearchCriteria);
	
	List<ChangeRequest> findChangeRequestsByCode(String code);
	
	List<ChangeRequest> findChangeRequestsByLeadTerm(String leadTerm);

	ChangeRequestDTO findCourseGrainedChangeRequestById(Long changeRequestId);

	ChangeRequestRealization findCurrentRunningRealizationByChangeRequestId(Long changeRequestId);

	ChangeRequestDTO findDeferedChangeRequestByOriginalId(Long changeRequestId);

	int findNumOfChangeRequests(Long conceptId);

	int findNumOfMyChangeRequests(Long userId);

	List<ChangeRequest> findOpenChangeRequestsByClassificationAndVersionYear(
			@Param("baseClassification") String baseClassification, @Param("versionCode") String versionCode);

	List<ChangeRequest> findOpenTabularChangeRequestsByClassificationAndVersionYear(
			@Param("baseClassification") String baseClassification, @Param("versionCode") String versionCode);

	List<QuestionForReviewer> findReviewQuestionsForChangeRequest(Long changeRequestId);

	ChangeRequestRealization findRunningChangeRequestRealization();

	void insertAdvice(Advice advice);

	void insertChangeRequest(ChangeRequestDTO changeRequestDTO); // return Id

	void insertChangeRequestCodingQuestions(ChangeRequestDTO changeRequestDTO);

	void insertChangeRequestCommentDiscussion(UserComment userComment);

	void insertChangeRequestEvolution(ChangeRequestEvolution changeRequestEvolution);

	void insertChangeRequestOtherAttachments(ChangeRequestDTO changeRequestDTO);

	void insertChangeRequestOtherLinks(ChangeRequestDTO changeRequestDTO);

	void insertChangeRequestRealization(ChangeRequestRealization changeRequestRealization);

	void insertChangeRequestReviewerQuestion(QuestionForReviewer questionForReviewer);

	void insertChangeRequestReviewGroups(ChangeRequestDTO changeRequestDTO);

	void insertChangeRequestUrcAttachments(ChangeRequestDTO changeRequestDTO);

	void insertChangeRequestUrcLinks(ChangeRequestDTO changeRequestDTO);

	void insertCommentForAdvice(UserComment userComment);

	void insertCommentForReviewerQuestion(UserComment userComment);
	
	void updateChangeRequestCommentDiscussion(UserComment userComment);
	
	int findNumOfUserComments(UserComment userComment);

	String isChangeRequestNameExist(String name);

	String isChangeRequestNameExistInContext(@Param("name") String name, @Param("baseContextId") long baseContextId);

	String isSameChangeRequestNameExist(@Param("name") String name, @Param("changeRequestId") Long changeRequestId);

	Object listChangeRequest(Map para);

	int publishAllChangeRequestsForBaseContext(@Param("baseContextId") long baseContextId,
			@Param("lastUpdatedByUserId") long lastUpdatedByUserId);

	List<String> searchPatternTopic(java.util.Map<String, Object> map);

	List<String> searchPatternTopicByContext(@Param("searchString") String searchString,
			@Param("searchContext") Collection<Long> searchContext, @Param("maxResults") Integer maxResults);

	List<String> searchPatternTopicByContext(@Param("searchString") String searchString,
			@Param("searchContext") Long searchContext, @Param("maxResults") Integer maxResults);

	Long synchronizeRealization();

	int updateChangeRequest(ChangeRequest changeRequest);

	int updateChangeRequestLastUpdateTime(@Param("changeRequestId") long changeRequestId,
			@Param("lastUpdateDate") Date lastUpdateDate, @Param("lastUpdatedByUserId") long lastUpdatedByUserId,
			@Param("lockTimestamp") Date lockTimestamp);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	void updateChangeRequestRealization(ChangeRequestRealization changeRequestRealization);

	void updateChangeRequestReviewerQuestion(QuestionForReviewer questionForReviewer);

}
