package ca.cihi.cims.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.exception.ConcurrentUpdateException;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.ChangeRequestRealizationStatus;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.model.changerequest.DocumentReferenceType;
import ca.cihi.cims.model.changerequest.IncompleteProperty;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.model.changerequest.UserCommentType;
import ca.cihi.cims.service.synchronization.SynchronizationService;
import ca.cihi.cims.util.CimsUtils;

public class ChangeRequestServiceImpl implements ChangeRequestService {

	private ChangeRequestMapper changeRequestMapper;
	private NotificationService notificationService;
	private ChangeRequestHistoryService changeRequestHistoryService;
	private AdminService adminService;
	private ChangeRequestSummaryService changeRequestSummaryService;
	private LookupService lookupService;
	private ContextService contextService;
	private FileService fileService;
	private SynchronizationService synchronizationService;
	private IncompleteReportService incompleteReportService;

	@Autowired
	private ContextProvider contextProvider;

	@Autowired
	private ContextOperations contextOperations;

	@Override
	@Transactional
	public void acceptChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to Owner
		changeRequest.setAssigneeDLId(null);
		changeRequest.setAssigneeUserId(changeRequest.getOwnerId());
		changeRequest.setStatus(ChangeRequestStatus.ACCEPTED_DRAFT);
		updateLightWeightChangeRequest(changeRequest, currentUser);

		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
		// post notification to Owner
		notificationService.postAcceptChangeRequestNotifcation(changeRequest, currentUser);
	}

	@Override
	public void addCommentForAdvice(ChangeRequestDTO changeRequestDTO, Long adviceId, User currentUser) {
		List<Advice> advices = changeRequestDTO.getAdvices();
		UserComment newAdviceComment = null;
		for (Advice advice : advices) {
			if (advice.getAdviceId().longValue() == adviceId.longValue()) {
				for (UserComment adviceComment : advice.getAdviceComments()) {
					if (adviceComment.getUserCommentId() == null) { // new added
						newAdviceComment = adviceComment;
						newAdviceComment.setChangeRequestId(changeRequestDTO.getChangeRequestId());
						newAdviceComment.setUserProfileId(currentUser.getUserId());
						newAdviceComment.setAdviceId(adviceId);
						newAdviceComment.setCommentType(UserCommentType.A); // comment for advice
						// post notification to Owner
						// fix for call #47538
						if(currentUser.getUserId() == advice.getSenderId() && advice.getDistributionListId() != null){
							notificationService.postNewCommentNotifcationToGroup(changeRequestDTO.getChangeRequestId(), adviceComment.getUserCommentTxt(), currentUser.getUserId(), advice.getDistributionListId());
						}
						else{
						    Long targetUser = currentUser.getUserId() == advice.getSenderId() ? advice.getUserProfileId() : advice.getSenderId();
						    notificationService.postNewCommentNotifcation(changeRequestDTO.getChangeRequestId(), adviceComment.getUserCommentTxt(), currentUser.getUserId(), targetUser);
						}
						break;
					}

				}
				break;
				
			}
		}
		changeRequestMapper.insertCommentForAdvice(newAdviceComment);
		
		
		
	}

	// ----------------------------------------------------------------------------

	@Override
	public void addCommentForQuestion(ChangeRequestDTO changeRequestDTO, Long questionId, User currentUser) {
		List<QuestionForReviewer> questionForReviewers = changeRequestDTO.getQuestionForReviewers();
		UserComment newComment = null;
		for (QuestionForReviewer questionForReviewer : questionForReviewers) {
			if (questionForReviewer.getQuestionForReviewerId().longValue() == questionId.longValue()) {
				for (UserComment questionComment : questionForReviewer.getQuestionComments()) {
					if (questionComment.getUserCommentId() == null) { // new added
						newComment = questionComment;
						newComment.setChangeRequestId(changeRequestDTO.getChangeRequestId());
						newComment.setUserProfileId(currentUser.getUserId());
						newComment.setReviewerQuestionId(questionForReviewer.getQuestionForReviewerId());
						newComment.setCommentType(UserCommentType.Q); // comment for question
						// post notification to Owner
						// fix for call #47538
						if(questionForReviewer.getReviewerId() != null && currentUser.getUserId() == changeRequestDTO.getAssigneeUserId()){
							notificationService.postNewCommentNotifcationToGroup(changeRequestDTO.getChangeRequestId(), questionComment.getUserCommentTxt(), currentUser.getUserId(), questionForReviewer.getReviewerId());
						}
						else{
							if(changeRequestDTO.getAssigneeUserId() != null){
								notificationService.postNewCommentNotifcation(changeRequestDTO.getChangeRequestId(), questionComment.getUserCommentTxt(), currentUser.getUserId(), changeRequestDTO.getAssigneeUserId());
							}
						}
						break;
					}

				}
				break;
			}
		}
		changeRequestMapper.insertCommentForReviewerQuestion(newComment);
	}

	@Override
	@Transactional
	public void approveChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssigneeUserId(User.USER_ID_SYSTEM);
		changeRequest.setAssigneeDLId(null);
		changeRequest.setStatus(ChangeRequestStatus.CLOSED_APPROVED);
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
	}

	@Override
	public void assignAndTransferChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		Long transferedTo = changeRequestDTO.getTransferedTo();
		changeRequestDTO.setAssignorId(currentUser.getUserId());
		changeRequestDTO.setAssigneeDLId(null);
		changeRequestDTO.setAssigneeUserId(transferedTo);
		changeRequestDTO.setOwnerId(transferedTo);
		changeRequestDTO.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
		updateChangeRequest(changeRequestDTO, currentUser);
		// remove pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequestDTO.getChangeRequestId());
		// post Change Request Ownership notification
		notificationService.postChangeRequestOwnershipNotifcation(changeRequestDTO, currentUser);
	}

	@Override
	public void assignChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		String assignTo = changeRequestDTO.getAssignedTo();
		changeRequestDTO.setAssignorId(currentUser.getUserId());
		if (assignTo.startsWith("DL_")) { // assign to DL DL_1
			String[] assignToInfo = assignTo.split("_");
			long dl_id = Long.valueOf(assignToInfo[1]);
			changeRequestDTO.setAssigneeDLId(dl_id);
			changeRequestDTO.setAssigneeUserId(null);

			updateChangeRequest(changeRequestDTO, currentUser);
			// remove pickup notifications
			notificationService.removeChangeRequestPickupNotification(changeRequestDTO.getChangeRequestId());
			// post Change Request Pick-Up notification to DLs
			notificationService.postChangeRequestPickUpNotifcation(changeRequestDTO, currentUser);

		} else { // assigned to User
			String[] assignToInfo = assignTo.split("_");
			long userId = Long.valueOf(assignToInfo[1]);
			changeRequestDTO.setAssigneeDLId(null);
			changeRequestDTO.setAssigneeUserId(userId);
			updateChangeRequest(changeRequestDTO, currentUser);
			// remove pcikup notifications
			notificationService.removeChangeRequestPickupNotification(changeRequestDTO.getChangeRequestId());
			// post Change Request Assignment notification to User
			notificationService.postChangeRequestAssignmentNotifcation(changeRequestDTO, currentUser);
		}
	}

	private void changeRequestHistoryAudit(ChangeRequestDTO oldChangeRequest, User currentUser) {
		changeRequestHistoryService.createChangeRequestHistoryForUpdating(oldChangeRequest, currentUser);
	}

	@Override
	public void checkChangeRequestIsLocked(long changeRequestId, long changeRequestlastUpdatedTime) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequestId);
		if (changeRequestInTable.getLastUpdatedTime().getTime() > changeRequestlastUpdatedTime) {
			throw new ConcurrentUpdateException("The change request was updated by others");
		}
	}

	private boolean compareTwoReviewGroups(List<Distribution> newReviewGroups, List<Distribution> oldReviewGroups) {
		boolean areTheySame = true;
		if ((newReviewGroups == null || newReviewGroups.size() == 0)
				&& (oldReviewGroups == null || oldReviewGroups.size() == 0)) {
			areTheySame = true;
		} else {
			if (newReviewGroups.size() == oldReviewGroups.size()) {
				Collections.sort(newReviewGroups);
				Collections.sort(oldReviewGroups);
				for (int i = 0; i < newReviewGroups.size(); i++) {
					if (newReviewGroups.get(i).getDistributionlistid().longValue() != oldReviewGroups.get(i)
							.getDistributionlistid().longValue()) {
						areTheySame = false;
						break;
					}
				}
			} else {
				areTheySame = false;
			}
		}
		return areTheySame;
	}

	private void copyOtherFiles(ChangeRequestDTO newChangeRequestDTO) throws IOException {
		List<DocumentReference> otherAttachments = newChangeRequestDTO.getOtherAttachments();
		if (otherAttachments != null && otherAttachments.size() > 0) {
			for (DocumentReference otherAttachment : otherAttachments) {
				otherAttachment.setReferenceType(DocumentReferenceType.OTHER_FILE);
				String originalFileName = newChangeRequestDTO.getDeferredChangeRequestId() + "_other_"
						+ otherAttachment.getFileName();
				String newFileName = newChangeRequestDTO.getChangeRequestId() + "_other_"
						+ otherAttachment.getFileName();
				fileService.copyFile(originalFileName, newFileName);
			}
			changeRequestMapper.insertChangeRequestOtherAttachments(newChangeRequestDTO);
		}
	}

	/*
	 * private void checkLockedChangeRequest(long tableLastUpdatedTime, long lockedLastUpdatedTime) { if
	 * (tableLastUpdatedTime > lockedLastUpdatedTime) { throw new
	 * ConcurrentUpdateException("The change request was updated by others"); } }
	 */

	private void copyUrcFiles(ChangeRequestDTO newChangeRequestDTO) throws IOException {
		List<DocumentReference> urcAttachments = newChangeRequestDTO.getUrcAttachments();
		if (urcAttachments != null && urcAttachments.size() > 0) {
			for (DocumentReference urcAttachment : urcAttachments) {
				urcAttachment.setReferenceType(DocumentReferenceType.URC_FILE);
				String originalFileName = newChangeRequestDTO.getDeferredChangeRequestId() + "_urc_"
						+ urcAttachment.getFileName();
				String newFileName = newChangeRequestDTO.getChangeRequestId() + "_urc_" + urcAttachment.getFileName();
				fileService.copyFile(originalFileName, newFileName);
			}
			changeRequestMapper.insertChangeRequestUrcAttachments(newChangeRequestDTO);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ca.cihi.cims.service.ChangeRequestService#createChangeRequest(ca.cihi.cims.model.changerequest.ChangeRequestDTO)
	 */
	@Override
	@Transactional
	public void createChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		if (currentUser.isInitiator()) {
			changeRequestDTO.setStatus(ChangeRequestStatus.NEW_INIT_NO_OWNER);
			changeRequestDTO.setOwnerId(User.USER_ID_SYSTEM);
		} else {
			changeRequestDTO.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
			changeRequestDTO.setOwnerId(currentUser.getUserId());
		}
		changeRequestDTO.setAssigneeUserId(currentUser.getUserId()); // assign it to himself
		changeRequestDTO.setCreatedByUserId(currentUser.getUserId());
		changeRequestDTO.setLastUpdatedByUserId(currentUser.getUserId());

		changeRequestMapper.insertChangeRequest(changeRequestDTO);
		Long changeRequestId = changeRequestDTO.getChangeRequestId();
		if (changeRequestDTO.isEvolutionRequired()) {
			changeRequestDTO.getEvolutionInfo().setChangeRequestId(changeRequestId);
			changeRequestMapper.insertChangeRequestEvolution(changeRequestDTO.getEvolutionInfo());
		}

		changeRequestMapper.insertChangeRequestReviewGroups(changeRequestDTO);
		handleDiscussionAndComments(changeRequestDTO, currentUser);

		if (changeRequestDTO.getCodingQuestions() != null) {
			filterCodingQuestions(changeRequestDTO);
			if (changeRequestDTO.getCodingQuestions().size() > 0) {
				changeRequestMapper.insertChangeRequestCodingQuestions(changeRequestDTO);
			}
		}

		if (changeRequestDTO.getUrcLinks() != null) {
			List<DocumentReference> validUrcLinks = new ArrayList<DocumentReference>();
			for (DocumentReference urcLink : changeRequestDTO.getUrcLinks()) {
				if (StringUtils.isNotEmpty(urcLink.getUrl())) {
					urcLink.setReferenceType(DocumentReferenceType.URC_LINK);
					validUrcLinks.add(urcLink);
				}
			}
			if (validUrcLinks.size() > 0) {
				changeRequestDTO.setUrcLinks(validUrcLinks);
				changeRequestMapper.insertChangeRequestUrcLinks(changeRequestDTO);
			}
		}

		if (changeRequestDTO.getOtherLinks() != null) {
			List<DocumentReference> validOtherLinks = new ArrayList<DocumentReference>();
			for (DocumentReference otherLink : changeRequestDTO.getOtherLinks()) {
				if (StringUtils.isNotEmpty(otherLink.getUrl())) {
					otherLink.setReferenceType(DocumentReferenceType.OTHER_LINK);
					validOtherLinks.add(otherLink);
				}
			}
			if (validOtherLinks.size() > 0) {
				changeRequestDTO.setOtherLinks(validOtherLinks);
				changeRequestMapper.insertChangeRequestOtherLinks(changeRequestDTO);
			}
		}

		// upload urc files and create URC_FILE type Document_Reference
		handleNewUrcFiles(changeRequestDTO);

		// upload other documents and create OTHER_FILE type Document_Reference
		handleNewOtherFiles(changeRequestDTO);

		// audit , async
		if (changeRequestDTO.getChangeRequestId() != null) {
			changeRequestHistoryService.createChangeRequestHistoryForCreating(changeRequestDTO, currentUser);
		}
	}

	@Override
	@Transactional
	public ChangeRequestRealization createChangeRequestRealization(ChangeRequest changeRequest) {
		ChangeRequestRealization realization = new ChangeRequestRealization();
		realization.setChangeRequestId(changeRequest.getChangeRequestId());
		realization.setRealizationStatus(ChangeRequestRealizationStatus.PRE_PROCESS);
		createChangeRequestRealization(realization);
		realization.setNewCreated(true);
		return realization;
	}

	@Override
	@Transactional
	public void createChangeRequestRealization(ChangeRequestRealization changeRequestRealization) {
		changeRequestMapper.insertChangeRequestRealization(changeRequestRealization);
	}

	@Override
	@Transactional
	public void deferChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		// set assignee to system
		changeRequestDTO.setAssigneeUserId(User.USER_ID_SYSTEM);
		changeRequestDTO.setAssigneeDLId(null);
		// changeRequestDTO.setOwnerId(User.USER_ID_SYSTEM);
		changeRequestDTO.setStatus(ChangeRequestStatus.DEFERRED);
		updateChangeRequest(changeRequestDTO, currentUser);
		// close context if it has
		ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(changeRequestDTO
				.getChangeRequestId());
		if (contextIdentifier != null) {
			contextService.deleteContext(contextIdentifier.getContextId());
		}
		// create a new change request for deferred change request
		// User currentUser =changeRequestDTO.getCurrentUser();
		changeRequestDTO.setDeferredChangeRequestId(changeRequestDTO.getChangeRequestId()); // deferred from
		changeRequestDTO.setAssigneeUserId(currentUser.getUserId());
		changeRequestDTO.setOwnerId(currentUser.getUserId());
		changeRequestDTO.setBaseContextId(changeRequestDTO.getDeferredToBaseContextId());
		changeRequestDTO.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);

		createChangeRequest(changeRequestDTO, currentUser);
		try {
			copyUrcFiles(changeRequestDTO);
			copyOtherFiles(changeRequestDTO);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	@Override
	@Transactional
	public void deleteChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		// set assignee , owner to null
		changeRequestDTO.setAssigneeUserId(null);
		changeRequestDTO.setAssigneeDLId(null);
		changeRequestDTO.setOwnerId(null);
		changeRequestDTO.setStatus(ChangeRequestStatus.DELETED);
		updateChangeRequest(changeRequestDTO, currentUser);
		// close context if it has
		ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(changeRequestDTO
				.getChangeRequestId());
		if (contextIdentifier != null) {
			contextService.deleteContext(contextIdentifier.getContextId());
		}

	}

	private void filterCodingQuestions(ChangeRequestDTO changeRequestDTO) {
		List<DocumentReference> validCodingQuestions = new ArrayList<DocumentReference>();
		for (DocumentReference codingQuestion : changeRequestDTO.getCodingQuestions()) {
			if (StringUtils.isNotEmpty(codingQuestion.geteQueryId())) { // eQuery Id can't be blank, url can
				codingQuestion.setReferenceType(DocumentReferenceType.CODING_QUESTION);
				validCodingQuestions.add(codingQuestion);
			}
		}
		changeRequestDTO.setCodingQuestions(validCodingQuestions);
	}

	@Override
	public List<ChangeRequest> findAllCCIChangeRequests() {
		return changeRequestMapper.findAllCCIChangeRequests();
	}

	@Override
	public List<ChangeRequest> findAllChangeRequests() {
		return changeRequestMapper.findAllChangeRequests();
	}

	@Override
	public List<ChangeRequest> findAllICDChangeRequests() {
		return changeRequestMapper.findAllICDChangeRequests();
	}

	@Override
	public ChangeRequestRealization findChangeRequestRealizationById(Long changeRequestRealizationId) {
		return changeRequestMapper.findChangeRequestRealizationById(changeRequestRealizationId);
	}

	@Override
	public List<ChangeRequest> findChangeRequestsBySearchCriteria(UserSearchCriteria userSearchCriteria) {
		return changeRequestMapper.findChangeRequestsBySearchCriteria(userSearchCriteria);
	}
	
	@Override
	public List<ChangeRequest> findChangeRequestsByCode(String code) {
		return changeRequestMapper.findChangeRequestsByCode(code);
	}
	
	@Override
	public List<ChangeRequest> findChangeRequestsByLeadTerm(String leadTerm) {
		return changeRequestMapper.findChangeRequestsByLeadTerm(leadTerm);
	}

	@Override
	public ChangeRequestDTO findCourseGrainedChangeRequestDTOById(Long changeRequestId) {
		ChangeRequestDTO changeRequestDTO = changeRequestMapper.findCourseGrainedChangeRequestById(changeRequestId);
		List<QuestionForReviewer> questionForReviewers = changeRequestMapper
				.findReviewQuestionsForChangeRequest(changeRequestId);
		if (questionForReviewers != null) {
			for (QuestionForReviewer questionForReviewer : questionForReviewers) {
				if (questionForReviewer.getSentOutNotification() != null) {
					questionForReviewer.setBeenSentOut(true);
				}
			}
		}
		changeRequestDTO.setQuestionForReviewers(questionForReviewers);
		changeRequestDTO.setAdvices(changeRequestMapper.findAdvicesForChangeRequest(changeRequestId));

		if (changeRequestDTO.getStatus() == ChangeRequestStatus.DEFERRED) {
			ChangeRequest deferredTo = changeRequestMapper.findDeferedChangeRequestByOriginalId(changeRequestId);
			changeRequestDTO.setDeferredTo(deferredTo);
		}
		return changeRequestDTO;
	}

	@Override
	public ChangeRequestRealization findCurrentRunningRealizationByChangeRequestId(Long changeRequestId) {
		return changeRequestMapper.findCurrentRunningRealizationByChangeRequestId(changeRequestId);
	}

	@Override
	public ChangeRequest findLightWeightChangeRequestById(Long changeRequestId) {
		ChangeRequest changeRequest = changeRequestMapper.findChangeRequestById(changeRequestId);

		return changeRequest;
	}

	/*
	 * when user click the save button for update an existing change request (non-Javadoc)
	 * 
	 * @see
	 * ca.cihi.cims.service.ChangeRequestService#updateChangeRequest(ca.cihi.cims.model.changerequest.ChangeRequestDTO)
	 */

	@Override
	public int findNumOfChangeRequests(Long conceptId) {
		return changeRequestMapper.findNumOfChangeRequests(conceptId);
	}

	@Override
	public int findNumOfMyChangeRequests(Long userId) {
		return changeRequestMapper.findNumOfMyChangeRequests(userId);
	}

	@Override
	public List<ChangeRequest> findOpenChangeRequestsByClassificationAndVersionYear(String baseClassification,
			Long versionYear) {
		return changeRequestMapper.findOpenChangeRequestsByClassificationAndVersionYear(baseClassification,
				String.valueOf(versionYear));
	}

	@Override
	public List<ChangeRequest> findOpenTabularChangeRequestsByClassificationAndVersionYear(String baseClassification,
			Long versionYear) {
		return changeRequestMapper.findOpenTabularChangeRequestsByClassificationAndVersionYear(baseClassification,
				String.valueOf(versionYear));
	}

	@Override
	public ChangeRequestRealization findRunningRealization() {
		return changeRequestMapper.findRunningChangeRequestRealization();
	}

	public AdminService getAdminService() {
		return adminService;
	}

	@Override
	public void getAdviceForChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		String adviceRecipient = changeRequestDTO.getAdviceRecipient();
		Advice newAdvice = changeRequestDTO.getAdvice();
		newAdvice.setSenderId(currentUser.getUserId());
		newAdvice.setChangeRequestId(changeRequestDTO.getChangeRequestId());
		if (adviceRecipient.startsWith("DL_")) { // adviceRecipient to DL DL_1
			String[] adviceRecipientInfo = adviceRecipient.split("_");
			long dl_id = Long.valueOf(adviceRecipientInfo[1]);
			newAdvice.setDistributionListId(dl_id);
			newAdvice.setUserProfileId(null);
		} else {
			String[] adviceRecipientInfo = adviceRecipient.split("_");
			long user_id = Long.valueOf(adviceRecipientInfo[1]);
			newAdvice.setDistributionListId(null);
			newAdvice.setUserProfileId(user_id);
		}
		changeRequestMapper.insertAdvice(newAdvice);
		// post Advice Request notification to advisor
		notificationService.postAdviceRequestNotifcation(changeRequestDTO, newAdvice, currentUser);

	}

	public ChangeRequestHistoryService getChangeRequestHistoryService() {
		return changeRequestHistoryService;
	}

	public ChangeRequestMapper getChangeRequestMapper() {
		return changeRequestMapper;
	}

	public ChangeRequestSummaryService getChangeRequestSummaryService() {
		return changeRequestSummaryService;
	}

	public ContextOperations getContextOperations() {
		return contextOperations;
	}

	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public ContextService getContextService() {
		return contextService;
	}

	public FileService getFileService() {
		return fileService;
	}

	/*
	 * private ChangeRequest getLockedChangeRequest(ChangeRequest changeRequest) { return
	 * getLockedChangeRequest(changeRequest.getChangeRequestId(), changeRequest.getLastUpdatedTime().getTime()); }
	 * 
	 * /*private ChangeRequest getLockedChangeRequest(long changeRequestId, long lockedLastUpdatedTime) { ChangeRequest
	 * changeRequestInTable = findLightWeightChangeRequestById(changeRequestId);
	 * checkLockedChangeRequest(changeRequestInTable.getLastUpdatedTime().getTime(), lockedLastUpdatedTime); return
	 * changeRequestInTable; }
	 */

	public IncompleteReportService getIncompleteReportService() {
		return incompleteReportService;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public NotificationService getNotificationService() {
		return notificationService;
	}

	public SynchronizationService getSynchronizationService() {
		return synchronizationService;
	}

	/*
	 * private void handleCommentsForQuestionforReviewers(ChangeRequestDTO changeRequestDTO) { if
	 * (changeRequestDTO.getQuestionForReviewers() != null) { List<QuestionForReviewer> goodQuestions = new
	 * ArrayList<QuestionForReviewer>(); for (QuestionForReviewer questionForReviewer :
	 * changeRequestDTO.getQuestionForReviewers()) { if
	 * (StringUtils.isNotEmpty(questionForReviewer.getQuestionForReviewerTxt())) {
	 * questionForReviewer.setChangeRequestId(changeRequestDTO.getChangeRequestId());
	 * goodQuestions.add(questionForReviewer); } } if (goodQuestions.size() > 0) {
	 * changeRequestDTO.setQuestionForReviewers(goodQuestions);
	 * //changeRequestMapper.insertChangeRequestReviewerQuestions(changeRequestDTO); for (QuestionForReviewer
	 * goodQuestion:goodQuestions){ changeRequestMapper.insertChangeRequestReviewerQuestion(goodQuestion); } } } }
	 */

	private void handleAdvicesComment(ChangeRequestDTO changeRequestDTO, User currentUser) {
		List<Advice> advices = changeRequestDTO.getAdvices();
		if (advices != null) {
			for (Advice advice : advices) {
				List<UserComment> adviceComments = advice.getAdviceComments();
				if (adviceComments != null && adviceComments.size() > 0) {
					UserComment newAdviceComment = null;
					for (UserComment adviceComment : adviceComments) { // only have one comment a time
						if (adviceComment.getUserCommentId() == null) { // new added comment
							newAdviceComment = adviceComment;
							break;
						}
					}

					if (newAdviceComment != null && StringUtils.isNotEmpty(newAdviceComment.getUserCommentTxt())) {
						newAdviceComment.setChangeRequestId(changeRequestDTO.getChangeRequestId());
						newAdviceComment.setUserProfileId(currentUser.getUserId());
						newAdviceComment.setAdviceId(advice.getAdviceId());
						newAdviceComment.setCommentType(UserCommentType.A); // comment for advice
						changeRequestMapper.insertCommentForAdvice(newAdviceComment);
					}
				}
			}
		}
	}

	private void handleDiscussionAndComments(ChangeRequestDTO changeRequestDTO, User currentUser) {
		if (changeRequestDTO.getCommentDiscussions() != null && changeRequestDTO.getCommentDiscussions().size() > 0) {
			List<UserComment> validCommentDiscussions = new ArrayList<UserComment>();
			for (UserComment userComment : changeRequestDTO.getCommentDiscussions()) {
				if (StringUtils.isNotEmpty(userComment.getUserCommentTxt())) {
					userComment.setCommentType(UserCommentType.C);
					userComment.setUserProfileId(currentUser.getUserId());
					userComment.setChangeRequestId(changeRequestDTO.getChangeRequestId());
					validCommentDiscussions.add(userComment);
				}
			}
			if (validCommentDiscussions.size() > 0) {
				changeRequestDTO.setCommentDiscussions(validCommentDiscussions);
				for (UserComment userComment : validCommentDiscussions) {
					changeRequestMapper.insertChangeRequestCommentDiscussion(userComment);
				}
			}
		}
	}
	
	private void handleUpdateDiscussionAndComments(ChangeRequestDTO changeRequestDTO, User currentUser) {
		if (changeRequestDTO.getCommentDiscussions() != null && changeRequestDTO.getCommentDiscussions().size() > 0) {
			List<UserComment> validCommentDiscussions = new ArrayList<UserComment>();
			for (UserComment userComment : changeRequestDTO.getCommentDiscussions()) {
				if (StringUtils.isNotEmpty(userComment.getUserCommentTxt())) {
					userComment.setCommentType(UserCommentType.C);
					userComment.setUserProfileId(currentUser.getUserId());
					userComment.setChangeRequestId(changeRequestDTO.getChangeRequestId());
					userComment.setLastUpdatedTime(changeRequestDTO.getLastUpdatedTime());
					if(userComment.getUserCommentId() != null) {
					}
					if(userComment.getAdviceId() != null) {
					}
					
					validCommentDiscussions.add(userComment);
				}
			}
			if (validCommentDiscussions.size() > 0) {
				changeRequestDTO.setCommentDiscussions(validCommentDiscussions);
				for (UserComment userComment : validCommentDiscussions) {
					if (changeRequestDTO.getOwnerId() == currentUser.getUserId()) {
						if(userComment.getUserCommentId() == null) {
							changeRequestMapper.insertChangeRequestCommentDiscussion(userComment);
						} else {
							changeRequestMapper.updateChangeRequestCommentDiscussion(userComment);
						}
					} else {
						if(userComment.getUserCommentId() == null) {
							changeRequestMapper.insertChangeRequestCommentDiscussion(userComment);
						}
					}
				}
			}
		}
	}

	private void handleNewOtherFiles(ChangeRequestDTO changeRequestDTO) {
		List<CommonsMultipartFile> otherFiles = changeRequestDTO.getOtherFiles();
		List<String> otherFileNames = new ArrayList<String>();
		if (otherFiles != null) {
			for (CommonsMultipartFile otherFile : otherFiles) {
				if (otherFile != null) {
					FileItem otherFileItem = otherFile.getFileItem();
					if (StringUtils.isNotEmpty(otherFile.getOriginalFilename())) {
						otherFileNames.add(otherFile.getOriginalFilename());
						String physicalFileName = changeRequestDTO.getChangeRequestId() + "_other_"
								+ otherFile.getOriginalFilename();
						fileService.writeFileToLocal(otherFileItem, physicalFileName);
					}
				}
			}

			List<DocumentReference> otherAttachments = new ArrayList<DocumentReference>();
			for (String otherFileName : otherFileNames) {
				DocumentReference otherAttachment = new DocumentReference();
				otherAttachment.setFileName(otherFileName);
				otherAttachment.setReferenceType(DocumentReferenceType.OTHER_FILE);
				otherAttachments.add(otherAttachment);
			}
			if (otherAttachments.size() > 0) {
				changeRequestDTO.setOtherAttachments(otherAttachments);
				changeRequestMapper.insertChangeRequestOtherAttachments(changeRequestDTO);
			}
		}
	}

	private void handleNewUrcFiles(ChangeRequestDTO changeRequestDTO) {
		List<CommonsMultipartFile> urcFiles = changeRequestDTO.getUrcFiles();
		List<String> urcFileNames = new ArrayList<String>();
		if (urcFiles != null) {
			for (CommonsMultipartFile urcFile : urcFiles) {
				if (urcFile != null) {
					FileItem urcFileItem = urcFile.getFileItem();
					if (StringUtils.isNotEmpty(urcFile.getOriginalFilename())) {
						urcFileNames.add(urcFile.getOriginalFilename());
						String physicalFileName = changeRequestDTO.getChangeRequestId() + "_urc_"
								+ urcFile.getOriginalFilename();
						fileService.writeFileToLocal(urcFileItem, physicalFileName);
					}
				}
			}
			List<DocumentReference> urcAttachments = new ArrayList<DocumentReference>();
			for (String urcFileName : urcFileNames) {
				DocumentReference urcAttachment = new DocumentReference();
				urcAttachment.setFileName(urcFileName);
				urcAttachment.setReferenceType(DocumentReferenceType.URC_FILE);
				urcAttachments.add(urcAttachment);
			}
			if (urcAttachments.size() > 0) {
				changeRequestDTO.setUrcAttachments(urcAttachments);
				changeRequestMapper.insertChangeRequestUrcAttachments(changeRequestDTO);
			}
		}
	}

	private void handleOtherAttachments(ChangeRequestDTO oldChangeRequest, ChangeRequestDTO changeRequestDTO) {
		List<DocumentReference> remainOtherAttachments = changeRequestDTO.getOtherAttachments(); //
		List<DocumentReference> oldOtherAttachments = oldChangeRequest.getOtherAttachments();
		if (oldOtherAttachments != null && oldOtherAttachments.size() > 0) {
			List<DocumentReference> removedAttachments = new ArrayList<DocumentReference>();
			for (DocumentReference oldOtherAttachment : oldOtherAttachments) { // if it is not in remainAttachments, put
				// it into removed
				boolean stillThere = false;
				if (remainOtherAttachments != null) {
					for (DocumentReference remainAttachment : remainOtherAttachments) {
						if (remainAttachment.getDocumentReferenceId() != null) {
							if (oldOtherAttachment.getDocumentReferenceId().longValue() == remainAttachment
									.getDocumentReferenceId().longValue()) {
								stillThere = true;
								break;
							}
						}
					}
				}
				if (!stillThere) {
					removedAttachments.add(oldOtherAttachment);
				}
			}
			if (removedAttachments.size() > 0) { // has removed files, remove the files on the server, then remove them
				// in the document_reference
				for (DocumentReference removedAttachment : removedAttachments) {
					StringBuilder sb_fileName = new StringBuilder(
							String.valueOf(removedAttachment.getChangeRequestId()));
					sb_fileName.append("_other_");
					sb_fileName.append(removedAttachment.getFileName());
					String fileName = sb_fileName.toString();
					fileService.deleteFile(fileName);
					changeRequestMapper.deleteDocumentReferenceById(removedAttachment.getDocumentReferenceId());
				}
			}
		}
	}

	private void handleOtherLinks(ChangeRequestDTO oldChangeRequest, ChangeRequestDTO changeRequestDTO) {
		if (changeRequestDTO.getOtherLinks() != null) {
			List<DocumentReference> validOtherLinks = new ArrayList<DocumentReference>();
			for (DocumentReference otherLink : changeRequestDTO.getOtherLinks()) {
				if (StringUtils.isNotEmpty(otherLink.getUrl())) {
					otherLink.setReferenceType(DocumentReferenceType.OTHER_LINK);
					validOtherLinks.add(otherLink);
				}
			}
			if (validOtherLinks.size() > 0) {
				changeRequestDTO.setOtherLinks(validOtherLinks);
			} else {
				changeRequestDTO.setOtherLinks(null);
			}
		}
		List<DocumentReference> newOtherLinks = changeRequestDTO.getOtherLinks();
		List<DocumentReference> oldOtherLinks = oldChangeRequest.getOtherLinks();
		boolean sameOtherLinks = CimsUtils.areTwoDocumentReferencesListSame(newOtherLinks, oldOtherLinks);
		if (!sameOtherLinks) {
			// remove old other link, insert new other links
			changeRequestMapper.deleteChangeRequestOtherLinks(changeRequestDTO.getChangeRequestId());
			if (changeRequestDTO.getOtherLinks() != null && changeRequestDTO.getOtherLinks().size() > 0) {
				changeRequestMapper.insertChangeRequestOtherLinks(changeRequestDTO);
			}
		}
	}

	private void handleQuestionforReviewers(ChangeRequestDTO oldChangeRequest, ChangeRequestDTO changeRequestDTO) {
		List<QuestionForReviewer> oldQuestionForReviewers = oldChangeRequest.getQuestionForReviewers();
		List<QuestionForReviewer> removedQuestions = new ArrayList<QuestionForReviewer>();
		// List<QuestionForReviewer> goodQuestions = new ArrayList<QuestionForReviewer>();
		List<QuestionForReviewer> newAddedQuestions = new ArrayList<QuestionForReviewer>();
		List<QuestionForReviewer> updatedQuestions = new ArrayList<QuestionForReviewer>();
		if (changeRequestDTO.getQuestionForReviewers() != null) {
			for (QuestionForReviewer questionForReviewer : changeRequestDTO.getQuestionForReviewers()) {
				if (StringUtils.isNotEmpty(questionForReviewer.getQuestionForReviewerTxt())) {
					questionForReviewer.setChangeRequestId(changeRequestDTO.getChangeRequestId());
					// goodQuestions.add(questionForReviewer);
					if (questionForReviewer.getQuestionForReviewerId() == null) {
						newAddedQuestions.add(questionForReviewer);
					} else {
						updatedQuestions.add(questionForReviewer);

					}
				}
			}

			if (newAddedQuestions.size() > 0) {
				for (QuestionForReviewer newAddedQuestion : newAddedQuestions) {
					changeRequestMapper.insertChangeRequestReviewerQuestion(newAddedQuestion);
				}
			}
			if (updatedQuestions.size() > 0) {
				for (QuestionForReviewer updatedQuestion : updatedQuestions) {
					changeRequestMapper.updateChangeRequestReviewerQuestion(updatedQuestion);
				}
			}

		}
		for (QuestionForReviewer oldQuestionForReviewer : oldQuestionForReviewers) {
			boolean inNewQuestions = false;
			for (QuestionForReviewer updatedQuestion : updatedQuestions) {
				if (oldQuestionForReviewer.getQuestionForReviewerId().longValue() == updatedQuestion
						.getQuestionForReviewerId().longValue()) {
					inNewQuestions = true;
					break;
				}
			}
			if (!inNewQuestions) {
				removedQuestions.add(oldQuestionForReviewer);
			}
		}
		for (QuestionForReviewer removedQuestion : removedQuestions) {
			changeRequestMapper.deleteChangeRequestReviewerQuestion(removedQuestion.getQuestionForReviewerId());
		}

	}

	private void handleQuestionsComment(ChangeRequestDTO changeRequestDTO, User currentUser) {
		List<QuestionForReviewer> questions = changeRequestDTO.getQuestionForReviewers();
		if (questions != null) {
			for (QuestionForReviewer question : questions) {
				List<UserComment> questionComments = question.getQuestionComments();
				if (questionComments != null && questionComments.size() > 0) {
					UserComment newQuestionComment = null;
					for (UserComment questionComment : questionComments) {
						if (questionComment.getUserCommentId() == null) { // new added comment
							newQuestionComment = questionComment;
							break;
						}
					}

					if (newQuestionComment != null && StringUtils.isNotEmpty(newQuestionComment.getUserCommentTxt())) {
						newQuestionComment.setChangeRequestId(changeRequestDTO.getChangeRequestId());
						newQuestionComment.setUserProfileId(currentUser.getUserId());
						newQuestionComment.setReviewerQuestionId(question.getQuestionForReviewerId());
						newQuestionComment.setCommentType(UserCommentType.Q); // comment for question
						changeRequestMapper.insertCommentForReviewerQuestion(newQuestionComment);
					}
				}
			}
		}
	}

	private void handleReviewGroups(ChangeRequestDTO oldChangeRequest, ChangeRequestDTO changeRequestDTO) {
		List<Distribution> oldReviewGroups = oldChangeRequest.getReviewGroups();
		List<Distribution> newReviewGroups = changeRequestDTO.getReviewGroups();
		boolean sameReviewGroups = compareTwoReviewGroups(newReviewGroups, oldReviewGroups);
		if (!sameReviewGroups) {
			// remove old, insert new
			changeRequestMapper.deleteChangeRequestReviewGroups(changeRequestDTO.getChangeRequestId());
			changeRequestMapper.insertChangeRequestReviewGroups(changeRequestDTO);
		}
	}

	private void handleUrcAttachments(ChangeRequestDTO oldChangeRequest, ChangeRequestDTO changeRequestDTO) {
		List<DocumentReference> remainAttachments = changeRequestDTO.getUrcAttachments(); //
		List<DocumentReference> oldUrcAttachments = oldChangeRequest.getUrcAttachments();
		if (oldUrcAttachments != null && oldUrcAttachments.size() > 0) {
			List<DocumentReference> removedAttachments = new ArrayList<DocumentReference>();
			for (DocumentReference oldUrcAttachment : oldUrcAttachments) { // if it is not in remainAttachments, put it
				// into removed
				boolean stillThere = false;
				if (remainAttachments != null) {
					for (DocumentReference remainAttachment : remainAttachments) {
						if (remainAttachment.getDocumentReferenceId() != null) {
							if (oldUrcAttachment.getDocumentReferenceId().longValue() == remainAttachment
									.getDocumentReferenceId().longValue()) {
								stillThere = true;
								break;
							}
						}
					}
				}
				if (!stillThere) {
					removedAttachments.add(oldUrcAttachment);
				}
			}
			if (removedAttachments.size() > 0) { // has removed files, remove the files on the server, then remove them
				// in the document_reference
				for (DocumentReference removedAttachment : removedAttachments) {
					StringBuilder sb_fileName = new StringBuilder(
							String.valueOf(removedAttachment.getChangeRequestId()));
					sb_fileName.append("_urc_");
					sb_fileName.append(removedAttachment.getFileName());
					String fileName = sb_fileName.toString();
					fileService.deleteFile(fileName);
					changeRequestMapper.deleteDocumentReferenceById(removedAttachment.getDocumentReferenceId());
				}
			}
		}
	}

	private void handleURCLinks(ChangeRequestDTO oldChangeRequest, ChangeRequestDTO changeRequestDTO) {
		if (changeRequestDTO.getUrcLinks() != null) {
			List<DocumentReference> validUrcLinks = new ArrayList<DocumentReference>();
			for (DocumentReference urcLink : changeRequestDTO.getUrcLinks()) {
				if (StringUtils.isNotEmpty(urcLink.getUrl())) {
					urcLink.setReferenceType(DocumentReferenceType.URC_LINK);
					validUrcLinks.add(urcLink);
				}
			}
			if (validUrcLinks.size() > 0) {
				changeRequestDTO.setUrcLinks(validUrcLinks);
			} else {
				changeRequestDTO.setUrcLinks(null);
			}
		}
		List<DocumentReference> newUrcLinks = changeRequestDTO.getUrcLinks();
		List<DocumentReference> oldUrcLinks = oldChangeRequest.getUrcLinks();
		boolean sameUrcLinks = CimsUtils.areTwoDocumentReferencesListSame(newUrcLinks, oldUrcLinks);
		if (!sameUrcLinks) {
			// remove old urc link, insert new url links
			changeRequestMapper.deleteChangeRequestUrcLinks(changeRequestDTO.getChangeRequestId());
			if (changeRequestDTO.getUrcLinks() != null && changeRequestDTO.getUrcLinks().size() > 0) {
				changeRequestMapper.insertChangeRequestUrcLinks(changeRequestDTO);
			}
		}
	}

	@Override
	public boolean isChangeRequestNameExist(String name) {
		return Boolean.valueOf(changeRequestMapper.isChangeRequestNameExist(name.trim()));
	}

	@Override
	public boolean isChangeRequestNameExistInContext(String name, long baseContextId) {
		return Boolean.valueOf(changeRequestMapper.isChangeRequestNameExistInContext(name, baseContextId));
	}


	@Override
	public boolean isIncomplete(long changeRequestId) {
		ChangeRequest changeRequest = findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		if (changeContext != null) {
			Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedConceptElementCodes(changeRequestId, maxStructureId);
			List<String> codeList = new ArrayList<String>();
			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				String code = conceptModification.getCode();

				if (!codeList.contains(code)) {
					codeList.add(code);
					List<IncompleteProperty> properties = new ArrayList<IncompleteProperty>();
					if (ChangeRequestCategory.T.equals(changeRequest.getCategory())) {
						properties.addAll(incompleteReportService.checkTabularConcept(changeContext.getContextId(),
								conceptId, changeContext.getIsVersionYear(), code));
					} else if (ChangeRequestCategory.I.equals(changeRequest.getCategory())) {
						properties.addAll(incompleteReportService.checkIndexConcept(changeContext.getContextId(),
								conceptId, code));
					} else if (ChangeRequestCategory.S.equals(changeRequest.getCategory())) {
						properties.addAll(incompleteReportService.checkSupplementConcept(changeContext.getContextId(),
								conceptId, code));
					}
					if (properties != null && properties.size() > 0) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isSameChangeRequestNameExist(String name, Long changeRequestId) {
		return Boolean.valueOf(changeRequestMapper.isSameChangeRequestNameExist(name.trim(), changeRequestId));
	}

	@Override
	public void publishAllChangeRequestsForYear(String versionCode, User currentUse) {
		ContextIdentifier icdBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA",
				versionCode);
		ContextIdentifier cciBaseContext = lookupService.findBaseContextIdentifierByClassificationAndYear("CCI",
				versionCode);
		changeRequestMapper.publishAllChangeRequestsForBaseContext(icdBaseContext.getContextId(),
				currentUse.getUserId());
		changeRequestMapper.publishAllChangeRequestsForBaseContext(cciBaseContext.getContextId(),
				currentUse.getUserId());

	}

	@Override
	@Transactional
	public void qaDoneChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to DL_Reviewer
		changeRequest.setAssigneeDLId(Distribution.DL_ID_Reviewer);
		changeRequest.setAssigneeUserId(null);
		changeRequest.setStatus(ChangeRequestStatus.ITERATIVE_QA_DONE);
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());

		// post Pick-up Approval notification to French content developer or English content developer
		notificationService.postPickupApprovalNotifcation(changeRequest, currentUser);
	}

	@Override
	@Transactional
	public void readyForAccept(ChangeRequestDTO changeRequestDTO, User currentUser) {
		changeRequestDTO.setAssignorId(currentUser.getUserId());
		// assign to DL_Reviewer
		changeRequestDTO.setAssigneeDLId(Distribution.DL_ID_Reviewer);
		changeRequestDTO.setAssigneeUserId(null);
		changeRequestDTO.setRationaleForIncomplete(null);
		changeRequestDTO.setStatus(ChangeRequestStatus.VALID_READY);
		updateChangeRequest(changeRequestDTO, currentUser);
		// post notification to reviewers DL_ID_Reviewer
		notificationService.postReadyForAcceptanceNotifcation(changeRequestDTO, currentUser);

	}

	@Override
	@Transactional
	public void readyForRealizeChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to DL_Reviewer
		changeRequest.setAssigneeDLId(Distribution.DL_ID_Reviewer);
		changeRequest.setAssigneeUserId(null);
		if (changeRequest.getStatus() == ChangeRequestStatus.ACCEPTED_INCOMPLETE
				|| changeRequest.getStatus() == ChangeRequestStatus.ACCEPTED_DRAFT) {
			changeRequest.setStatus(ChangeRequestStatus.ACCEPTED_READY_FOR_REALIZATION);
		}
		if (changeRequest.getStatus() == ChangeRequestStatus.TRANSLATION_DONE) {
			changeRequest.setStatus(ChangeRequestStatus.VALIDATION_DONE);
		}
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
		// post send back notification to Owner
		notificationService.postPickupRealizationChangeRequestNotifcation(changeRequest, currentUser);
	}

	@Override
	@Transactional
	public void readyForTranslationChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to French developer
		changeRequest.setAssigneeDLId(Distribution.DL_ID_FRContentDeveloper);
		changeRequest.setAssigneeUserId(null);
		changeRequest.setStatus(ChangeRequestStatus.ACCEPTED_READY_FOR_TRANSLATION);
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
		// post Pick-up Translation notification to French content developer
		notificationService.postPickupTranslationChangeRequestNotifcation(changeRequest, currentUser);
	}

	@Override
	@Transactional
	public void readyForValidationChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to French developer
		changeRequest.setAssigneeDLId(Distribution.DL_ID_FRContentDeveloper);
		changeRequest.setAssigneeUserId(null);
		changeRequest.setStatus(ChangeRequestStatus.TRANSLATION_DONE);
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
		// post Pick-up Validation notification to French content developer
		notificationService.postPickupValidationChangeRequestNotifcation(changeRequest, currentUser);
	}

	@Override
	@Transactional
	public void realizeChangeRequest(ChangeRequest changeRequest, ChangeRequestRealization realization, User currentUser) {
		changeRequestMapper.synchronizeRealization();
		realization.setRealizationStatus(ChangeRequestRealizationStatus.PROCESS_BEGINS);
		this.updateChangeRequestRealization(realization);
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		ContextIdentifier contextId = lookupService.findOpenContextByChangeRquestId(changeRequest.getChangeRequestId());
		ContextAccess context = contextProvider.findContext(contextId);
		boolean realizationfailed = false;
		// sync view
		try {
			synchronizationService.synchronize(new OptimisticLock(Long.MIN_VALUE), currentUser,
					changeRequest.getChangeRequestId());
		} catch (Exception e) {
			realizationfailed = true;
			realization.setRealizationStatus(ChangeRequestRealizationStatus.PRE_PROCESS_FAILED);
			realization.setFailedReason("synchronize view failed");
			updateChangeRequestRealization(realization);
			return;
		}
		// check incomplete
		if (changeRequestSummaryService.hasIncompleteProperties(changeRequest)) {
			realizationfailed = true;
			realization.setRealizationStatus(ChangeRequestRealizationStatus.PROCESS_FAILED);
			realization.setFailedReason("Change request cannot be realized with incompletes");
			updateChangeRequestRealization(realization);
			return;
		} else {
			// check conflicts and Realize the change request
			// Realize the change request , we realize the change request which is not from admin screen , so
			// isAdmin=false
			HashMap<ElementVersion, ElementVersion> elementVersions = context.realizeChangeContext(false);

			// if no conflicts, create a new open context for change request
			if (elementVersions.isEmpty()) {
				// Get the base classification
				ContextIdentifier baseContextId = contextOperations.findContextById(contextId.getBaseClassification(),
						contextId.getBaseStructureId());
				// create change context
				contextProvider.createChangeContext(baseContextId, changeRequest.getChangeRequestId());
				realization.setRealizationStatus(ChangeRequestRealizationStatus.PROCESS_ENDS);
				updateChangeRequestRealization(realization);
			} else {
				realizationfailed = true;
				realization.setRealizationStatus(ChangeRequestRealizationStatus.PROCESS_FAILED);
				realization.setFailedReason("Change request cannot be realized with conflicts");
				updateChangeRequestRealization(realization);
				return;
			}
			if (!realizationfailed) {
				updateChangeRequestToRealizedAndSendNotifications(changeRequest, changeRequestInTable, currentUser);
			}
		}
	}

	@Override
	@Transactional
	public void rejectChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		// set assignee to system
		changeRequestDTO.setAssigneeUserId(User.USER_ID_SYSTEM);
		changeRequestDTO.setAssigneeDLId(null);
		changeRequestDTO.setStatus(ChangeRequestStatus.REJECTED);
		updateChangeRequest(changeRequestDTO, currentUser);
		// delete context and associated changes if it has
		ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(changeRequestDTO
				.getChangeRequestId());
		if (contextIdentifier != null) {
			contextService.deleteContext(contextIdentifier.getContextId());
		}
	}

	@Override
	public List<String> searchPatternTopic(String searchString, Integer maxResults) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("searchString", searchString + "%");
		parameters.put("maxResults", maxResults);
		return changeRequestMapper.searchPatternTopic(parameters);
	}

	@Override
	public List<String> searchPatternTopicByContext(String searchString, Collection<Long> searchContext,
			Integer maxResults) {
		searchString = searchString + "%";
		return changeRequestMapper.searchPatternTopicByContext(searchString, searchContext, maxResults);
	}

	@Override
	public List<String> searchPatternTopicByContext(String searchString, Long searchContext, Integer maxResults) {
		searchString = searchString + "%";
		return changeRequestMapper.searchPatternTopicByContext(searchString, searchContext, maxResults);
	}

	@Override
	@Transactional
	public void sendBackChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequest changeRequestInTable = findLightWeightChangeRequestById(changeRequest.getChangeRequestId());
		String sendBackMessage = changeRequest.getRationaleForIncomplete();
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to Owner
		changeRequest.setAssigneeDLId(null);
		changeRequest.setAssigneeUserId(changeRequest.getOwnerId());
		if (changeRequest.getStatus() == ChangeRequestStatus.VALID_READY) {
			changeRequest.setStatus(ChangeRequestStatus.VALID_INCOMPLETE);
		} else {
			// if (changeRequest.getStatus() == ChangeRequestStatus.ACCEPTED_READY_FOR_REALIZATION
			// ||changeRequest.getStatus()==ChangeRequestStatus.ACCEPTED_READY_FOR_TRANSLATION){
			changeRequest.setStatus(ChangeRequestStatus.ACCEPTED_INCOMPLETE);
		}
		changeRequest.setRationaleForIncomplete(sendBackMessage);
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
		// post send back notification to Owner
		notificationService.postSendBackChangeRequestNotifcation(changeRequest, currentUser);
	}

	@Override
	@Transactional
	public void sendQuestionForReviewer(ChangeRequestDTO changeRequestDTO, int questionIndex, User currentUser) {
		Long changeRequestId = changeRequestDTO.getChangeRequestId();
		ChangeRequestDTO oldChangeRequest = findCourseGrainedChangeRequestDTOById(changeRequestId);
		updateChangeRequest(changeRequestDTO, oldChangeRequest, currentUser);
		List<QuestionForReviewer> questionForReviewers = changeRequestDTO.getQuestionForReviewers();
		QuestionForReviewer sendQuestion = questionForReviewers.get(questionIndex);
		// send notification to reviewer
		if (StringUtils.isNotEmpty(sendQuestion.getQuestionForReviewerTxt())) {
			notificationService.postReviewRequestNotifcation(changeRequestDTO, sendQuestion, currentUser);
		}
		// need post notification first, otherwise, can not see question has been sent or not
		changeRequestHistoryAudit(oldChangeRequest, currentUser);
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	public void setChangeRequestHistoryService(ChangeRequestHistoryService changeRequestHistoryService) {
		this.changeRequestHistoryService = changeRequestHistoryService;
	}

	@Override
	public void setChangeRequestMapper(ChangeRequestMapper changeRequestMapper) {
		this.changeRequestMapper = changeRequestMapper;
	}

	public void setChangeRequestSummaryService(ChangeRequestSummaryService changeRequestSummaryService) {
		this.changeRequestSummaryService = changeRequestSummaryService;
	}

	public void setContextOperations(ContextOperations contextOperations) {
		this.contextOperations = contextOperations;
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setContextService(ContextService contextService) {
		this.contextService = contextService;
	}

	public void setFileService(FileService fileService) {
		this.fileService = fileService;
	}

	@Autowired
	public void setIncompleteReportService(IncompleteReportService incompleteReportService) {
		this.incompleteReportService = incompleteReportService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public void setSynchronizationService(SynchronizationService synchronizationService) {
		this.synchronizationService = synchronizationService;
	}

	// submit by initiator
	@Override
	@Transactional
	public void submitChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		changeRequestDTO.setStatus(ChangeRequestStatus.NEW_INITSUBMIT_NO_OWNER);
		// assign it to DL
		if ("FRA".equalsIgnoreCase(changeRequestDTO.getLanguageCode())) {
			changeRequestDTO.setAssigneeDLId(Distribution.DL_ID_FRContentDeveloper);
		} else {
			changeRequestDTO.setAssigneeDLId(Distribution.DL_ID_ENContentDeveloper);
		}
		changeRequestDTO.setAssigneeUserId(null);
		changeRequestDTO.setOwnerId(User.USER_ID_SYSTEM);

		updateChangeRequest(changeRequestDTO, currentUser);
		ContextIdentifier baseContext = lookupService
				.findContextIdentificationById(changeRequestDTO.getBaseContextId());
		changeRequestDTO.setBaseVersionCode(baseContext.getVersionCode()); // version code is needed by postPickupNew
		// Notification
		// need post notification to DL-English Content Developer or DL-French Content Developer
		notificationService.postPickupNewNotifcation(changeRequestDTO, currentUser);

	}

	@Override
	@Transactional
	public void takeOverChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		// User currentUser =changeRequestDTO.getCurrentUser();
		changeRequestDTO.setAssigneeDLId(null);
		changeRequestDTO.setAssigneeUserId(currentUser.getUserId());
		changeRequestDTO.setAssignorId(currentUser.getUserId());
		// only change owner if the owner is System
		if (User.USER_ID_SYSTEM.longValue() == changeRequestDTO.getOwnerId().longValue()) {
			changeRequestDTO.setOwnerId(currentUser.getUserId());
		}
		if (ChangeRequestStatus.NEW_INITSUBMIT_NO_OWNER == changeRequestDTO.getStatus()
				|| ChangeRequestStatus.NEW_INIT_NO_OWNER == changeRequestDTO.getStatus()) {
			changeRequestDTO.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
		}
		updateChangeRequest(changeRequestDTO, currentUser);
		// remove the Pick-up Change Request Pick-Up notification
		notificationService.removeChangeRequestPickupNotification(changeRequestDTO.getChangeRequestId());

	}

	@Override
	public void transferChangeRequestOwnerShip(ChangeRequestDTO changeRequestDTO, User currentUser) {
		Long transferedTo = changeRequestDTO.getTransferedTo();
		changeRequestDTO.setOwnerId(transferedTo);
		updateChangeRequest(changeRequestDTO, currentUser);
		// post Change Request Ownership notification
		notificationService.postChangeRequestOwnershipNotifcation(changeRequestDTO, currentUser);
	}

	private void updateChangeRequest(ChangeRequestDTO changeRequestDTO, ChangeRequestDTO oldChangeRequest,
			User currentUser) {
		Long changeRequestId = changeRequestDTO.getChangeRequestId();
		changeRequestDTO.setLastUpdatedByUserId(currentUser.getUserId());
		/*
		 * 1.update the change request table, but don't update BASE_CONTEXT_ID( classification, version) , category,
		 * DEFERRED_CHANGE_REQUEST_ID,
		 */

		int rowUpdated = changeRequestMapper.updateChangeRequest(changeRequestDTO);
		if (rowUpdated == 0) {
			throw new ConcurrentUpdateException("The change request was updated by others");
		}

		/*
		 * 2, handle evolution info, remove it first, if required, then insert it
		 */
		changeRequestMapper.deleteChangeRequestEvolution(changeRequestId);
		if (changeRequestDTO.isEvolutionRequired()) {
			changeRequestDTO.getEvolutionInfo().setChangeRequestId(changeRequestId);
			changeRequestMapper.insertChangeRequestEvolution(changeRequestDTO.getEvolutionInfo());
		}

		/*
		 * 3.update the CHANGE_REQUEST_REVIEW_GROUP Table, compare the old list with the new list, as we need audit the
		 * changes in the change request history screen
		 */
		handleReviewGroups(oldChangeRequest, changeRequestDTO);

		/*
		 * 4. deal with questionForReviewers, comments for questions if question has not been sent out, user can
		 * remove/update it. if the question has been sent, then read only, need handle removed question, new added
		 * question
		 */
		handleQuestionforReviewers(oldChangeRequest, changeRequestDTO);

		/*
		 * 5. deal with coding questions, user can update, remove or add coding questions compare the old list with the
		 * new list, as we need audit the changes in the change request history screen
		 */
		List<DocumentReference> oldCodingQuestions = oldChangeRequest.getCodingQuestions();
		List<DocumentReference> newCodingQuestions = changeRequestDTO.getCodingQuestions();
		if (newCodingQuestions != null) {
			filterCodingQuestions(changeRequestDTO);
		}
		newCodingQuestions = changeRequestDTO.getCodingQuestions();

		boolean sameCodingQuestions = CimsUtils
				.areTwoDocumentReferencesListSame(oldCodingQuestions, newCodingQuestions);
		if (!sameCodingQuestions) {
			// remove old one and insert new one
			changeRequestMapper.deleteChangeRequestCodingQuestions(changeRequestDTO.getChangeRequestId());
			if (newCodingQuestions != null && newCodingQuestions.size() > 0) {
				changeRequestMapper.insertChangeRequestCodingQuestions(changeRequestDTO);
			}
		}

		/* 6. deal with urc links */
		handleURCLinks(oldChangeRequest, changeRequestDTO);

		/*
		 * 7. deal with urc files if user remove files, the urcAttachments will be less then old attachments , find
		 * removed file names first if changerequest has attached files, those are new files,
		 */
		handleUrcAttachments(oldChangeRequest, changeRequestDTO);
		handleNewUrcFiles(changeRequestDTO);

		/* 8. deal with other links */
		handleOtherLinks(oldChangeRequest, changeRequestDTO);
		/*
		 * 9. deal with other files if user remove files, the otherAttachments will be less then old attachments , find
		 * removed file names first if changerequest has attached files, those are new files,
		 */
		handleOtherAttachments(oldChangeRequest, changeRequestDTO);
		handleNewOtherFiles(changeRequestDTO);

		// the following three sections can be updated by non-assignee

		/*
		 * 10. insert the new added user comments, comments can't be updated, so disable the old comments in the jsp
		 * page
		 */
		handleUpdateDiscussionAndComments(changeRequestDTO, currentUser);

		/* 11, handle advice comments, the comments are all new , as the old comments are disabled */
		handleAdvicesComment(changeRequestDTO, currentUser);
		/* 12, handle comments for question review */
		handleQuestionsComment(changeRequestDTO, currentUser);

		// audit, Async
		// changeRequestHistoryService.createChangeRequestHistoryForUpdating(oldChangeRequest, currentUser);

	}

	@Override
	@Transactional
	public void updateChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		Long changeRequestId = changeRequestDTO.getChangeRequestId();
		// change request in the table
		ChangeRequestDTO oldChangeRequest = findCourseGrainedChangeRequestDTOById(changeRequestId);
		/*
		 * remove this because the check is done in front
		 * checkLockedChangeRequest(changeRequestDTO.getLastUpdatedTime().getTime(),
		 * oldChangeRequest.getLastUpdatedTime() .getTime());
		 */
		updateChangeRequest(changeRequestDTO, oldChangeRequest, currentUser);
		// async
		changeRequestHistoryAudit(oldChangeRequest, currentUser);
	}

	@Override
	@Transactional
	public void updateChangeRequestLastUpdateTime(long changeRequestId, User currentUser,
			OptimisticLock changeRequestLock) {
		if (changeRequestLock.getTimestamp() != Long.MIN_VALUE) {
			Date lastUpdateDate = new Date();
			int rowUpdated = changeRequestMapper.updateChangeRequestLastUpdateTime(changeRequestId, lastUpdateDate,
					currentUser.getUserId(), new Date(changeRequestLock.getTimestamp()));
			if (rowUpdated == 0) {
				throw new ConcurrentUpdateException("The change request was updated by others");
			}
			changeRequestLock.setTimestamp(lastUpdateDate);
		}
	}

	@Override
	@Transactional
	public void updateChangeRequestRealization(ChangeRequestRealization changeRequestRealization) {
		changeRequestMapper.updateChangeRequestRealization(changeRequestRealization);
	}

	@Transactional
	private void updateChangeRequestToRealizedAndSendNotifications(ChangeRequest changeRequest,
			ChangeRequest changeRequestInTable, User currentUser) {
		changeRequest.copyFrom(changeRequestInTable);
		changeRequest.setAssignorId(currentUser.getUserId());
		// assign to French developer
		if ("FRA".equalsIgnoreCase(changeRequest.getLanguageCode())) {
			changeRequest.setAssigneeDLId(Distribution.DL_ID_FRContentDeveloper);
		} else {
			changeRequest.setAssigneeDLId(Distribution.DL_ID_ENContentDeveloper);
		}
		changeRequest.setAssigneeUserId(null);
		changeRequest.setStatus(ChangeRequestStatus.REALIZED);
		updateLightWeightChangeRequest(changeRequest, currentUser);
		// remove any pickup notifications
		notificationService.removeChangeRequestPickupNotification(changeRequest.getChangeRequestId());
		// post Pick-up QA notification to French content developer or English content developer
		notificationService.postPickupQANotifcation(changeRequest, currentUser);
	}

	/*
	 * this method update change request table only, usually get called by clicking the buttons on the classification
	 * change summary screen
	 */
	@Override
	@Transactional
	public void updateLightWeightChangeRequest(ChangeRequest changeRequest, User currentUser) {
		Long changeRequestId = changeRequest.getChangeRequestId();
		// change request in the table
		ChangeRequestDTO oldChangeRequest = findCourseGrainedChangeRequestDTOById(changeRequestId);
		int rowUpdated = changeRequestMapper.updateChangeRequest(changeRequest);
		if (rowUpdated == 0) {
			throw new ConcurrentUpdateException("The change request was updated by others");
		}
		// async
		changeRequestHistoryAudit(oldChangeRequest, currentUser);
	}

	@Override
	@Transactional
	public void validateChangeRequest(ChangeRequestDTO changeRequestDTO, User currentUser) {
		// hidden save first
		changeRequestDTO.setStatus(ChangeRequestStatus.VALID_DRAFT);
		updateChangeRequest(changeRequestDTO, currentUser);
		ContextIdentifier baseContext = lookupService
				.findContextIdentificationById(changeRequestDTO.getBaseContextId());
		contextProvider.createChangeContext(baseContext, changeRequestDTO.getChangeRequestId());
	}

}
