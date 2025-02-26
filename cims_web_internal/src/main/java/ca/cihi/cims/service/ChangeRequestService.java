package ca.cihi.cims.service;

import java.util.Collection;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.OptimisticLock;

public interface ChangeRequestService {

	@Transactional
	void acceptChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void addCommentForAdvice(ChangeRequestDTO changeRequestDTO, Long adviceId, User currentUser);

	@Transactional
	void addCommentForQuestion(ChangeRequestDTO changeRequestDTO, Long questionId, User currentUser);

	@Transactional
	void approveChangeRequest(ChangeRequest changeRequest, User currentUser);

	/*
	 * @Transactional void insertChangeRequestReviewGroups(ChangeRequestDTO changeRequestDTO);
	 */

	@Transactional
	void assignAndTransferChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void assignChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	void checkChangeRequestIsLocked(long changeRequestId, long changeRequestlastUpdatedTime);

	@Transactional
	void createChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	ChangeRequestRealization createChangeRequestRealization(ChangeRequest changeRequest);

	@Transactional
	void createChangeRequestRealization(ChangeRequestRealization changeRequestRealization);

	@Transactional
	void deferChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void deleteChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	List<ChangeRequest> findAllCCIChangeRequests();

	List<ChangeRequest> findAllChangeRequests();

	List<ChangeRequest> findAllICDChangeRequests();

	ChangeRequestRealization findChangeRequestRealizationById(Long changeRequestRealizationId);

	// ContextIdentifier findContextIdentificationById(Long baseContextId);

	// ContextIdentifier findOpenContextByChangeRquestId(Long changeRequestId);

	List<ChangeRequest> findChangeRequestsBySearchCriteria(UserSearchCriteria userSearchCriteria);
	
	List<ChangeRequest> findChangeRequestsByCode(String code);
	
	List<ChangeRequest> findChangeRequestsByLeadTerm(String leadTerm);

	ChangeRequestDTO findCourseGrainedChangeRequestDTOById(Long changeRequestId);

	ChangeRequestRealization findCurrentRunningRealizationByChangeRequestId(Long changeRequestId);

	// List<Long> findDistinctQFRReviewerId(Long changeRequestId);

	ChangeRequest findLightWeightChangeRequestById(Long changeRequestId);

	int findNumOfChangeRequests(Long conceptId);

	int findNumOfMyChangeRequests(Long userId);

	List<ChangeRequest> findOpenChangeRequestsByClassificationAndVersionYear(String baseClassification, Long versionYear);

	// List<ChangeRequest> listChangeRequest ();

	List<ChangeRequest> findOpenTabularChangeRequestsByClassificationAndVersionYear(String baseClassification,
			Long versionYear);

	ChangeRequestRealization findRunningRealization();

	@Transactional
	void getAdviceForChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	boolean isChangeRequestNameExist(String name); // for creating new change request

	boolean isChangeRequestNameExistInContext(String name, long baseContextId);

	boolean isIncomplete(long changeRequestId);

	boolean isSameChangeRequestNameExist(String name, Long changeRequestId); // for existing change request

	@Transactional
	void publishAllChangeRequestsForYear(String versionCode, User currentUse);

	@Transactional
	void qaDoneChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void readyForAccept(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void readyForRealizeChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void readyForTranslationChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void readyForValidationChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void realizeChangeRequest(ChangeRequest changeRequest, ChangeRequestRealization runningRealization, User currentUser);

	@Transactional
	void rejectChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	List<String> searchPatternTopic(String searchString, Integer maxResults);

	List<String> searchPatternTopicByContext(String searchString, Collection<Long> searchContext, Integer maxResults);

	List<String> searchPatternTopicByContext(String searchString, Long searchContext, Integer maxResults);

	@Transactional
	void sendBackChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void sendQuestionForReviewer(ChangeRequestDTO changeRequestDTO, int questionIndex, User currentUser);

	void setChangeRequestMapper(ChangeRequestMapper changeRequestMapper);

	@Transactional
	void submitChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void takeOverChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void transferChangeRequestOwnerShip(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void updateChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void updateChangeRequestLastUpdateTime(long changeRequestId, User currentUser, OptimisticLock changeRequestLock);

	@Transactional
	void updateChangeRequestRealization(ChangeRequestRealization changeRequestRealization);

	@Transactional
	void updateLightWeightChangeRequest(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void validateChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser);

}
