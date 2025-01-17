package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.data.mapper.ChangeRequestMapper;
import ca.cihi.cims.data.mapper.NotificationMapper;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.model.notification.NotificationDistributionList;
import ca.cihi.cims.model.notification.NotificationTypeCode;
import ca.cihi.cims.model.notification.NotificationUserProfile;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;

public class NotificationServiceImpl implements NotificationService {

	// private SqlSessionTemplate sqlSessionBatch;
	// private SqlSessionFactory sqlSessionFactory;

	private static final int MAX_NOTIFICATION_MSG_SIZE = 500;
	private static final String ELLIPSIS = " ...";
	private static final int TRUNCATED_NOTIFICATION_MSG_SIZE = MAX_NOTIFICATION_MSG_SIZE - ELLIPSIS.length();

	private static final String P_HTML_TAG = "<p>";
	private static final String P_HTML_TAG_ELLIPSIS = ELLIPSIS + "</p>";
	private static final int P_HTML_TAG_TRUNCATED_NOTIFICATION_MSG_SIZE = MAX_NOTIFICATION_MSG_SIZE
			- P_HTML_TAG_ELLIPSIS.length();

	private static final String WRAPUP_ICD = "The ICD-10-CA classification tables package will be generated soon. Changes to tabular list and code validations are restricted.";
	private static final String WRAPUP_CCI = "The CCI classification tables package will be generated soon. Changes to tabular list and code validations are restricted.";
	private static final String WRAPUP_BTH = "The ICD-10-CA & CCI classification tables package will be generated soon. Changes to tabular list and code validations are restricted.";
	private static final String WRAPUP_ICD_A = "The ICD-10-CA classification table package will be generated soon.  Changes to generic attributes, reference values, in-context generic description and components are restricted";
	private static final String WRAPUP_CCI_A = "The CCI classification table package will be generated soon.  Changes to generic attributes, reference values, in-context generic description and components are restricted";
	private static final String WRAPUP_BTH_A = "The ICD-10-CA & CCI classification table package will be generated soon.  Changes to generic attributes, reference values, in-context generic description and components are restricted";

	private NotificationMapper notificationMapper;

	private ChangeRequestMapper changeRequestMapper;
	private AdminMapper adminMapper;
	private LookupService lookupService;

	// ------------------------------------------------------------------------

	private void completeAdviceRequestTask(NotificationDTO notification, long currentUserId) {
		notification.setCompletionInd(true);
		notificationMapper.updateNotification(notification);
		notificationMapper.disableNotification(notification.getNotificationId()); // disable it

		NotificationDTO notificationToSender = new NotificationDTO();
		User recipient = new User();
		recipient.setUserId(notification.getSenderId()); // set the recipient to sender

		notificationToSender.setNotificationTypeCode(NotificationTypeCode.AP); // advice provided
		notificationToSender.setSenderId(currentUserId);
		notificationToSender.setChangeRequestId(notification.getChangeRequestId());
		notificationToSender.setFiscalYear(notification.getFiscalYear());
		notificationToSender.setOriginalNotificationId(notification.getNotificationId());
		notificationToSender.setCreatedDate(Calendar.getInstance().getTime());
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta apTypeMeta = notificationTypeMap.get(NotificationTypeCode.AP);
		notificationToSender.setSubject(apTypeMeta.getDefaultSubject());
		notificationToSender.setMessage(apTypeMeta.getDefaultMessage());
		notificationToSender.setCompletionRequiredInd(apTypeMeta.isCompletionRequiredInd());
		notificationToSender.setRecipient(recipient);
		postNotificationToOneRecipient(notificationToSender);

	}

	private void completeAttributeReferenceValueRequestTask(NotificationDTO notification, long currentUserId) {
		notification.setCompletionInd(true);
		notificationMapper.updateNotification(notification);
		notificationMapper.disableNotification(notification.getNotificationId()); // disable it
		NotificationDTO notificationToSender = new NotificationDTO();
		User recipient = new User();
		recipient.setUserId(notification.getSenderId()); // set the recipient to sender
		notificationToSender.setNotificationTypeCode(NotificationTypeCode.RVRC); // AttributeReferenceValueRequestTask
		notificationToSender.setSenderId(currentUserId);
		notificationToSender.setChangeRequestId(notification.getChangeRequestId());
		notificationToSender.setFiscalYear(notification.getFiscalYear());
		notificationToSender.setOriginalNotificationId(notification.getNotificationId());
		notificationToSender.setCreatedDate(Calendar.getInstance().getTime());
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta rcTypeMeta = notificationTypeMap.get(NotificationTypeCode.RVRC);
		notificationToSender.setSubject(rcTypeMeta.getDefaultSubject());
		notificationToSender.setMessage(rcTypeMeta.getDefaultMessage());
		notificationToSender.setCompletionRequiredInd(rcTypeMeta.isCompletionRequiredInd());
		notificationToSender.setRecipient(recipient);
		postNotificationToOneRecipient(notificationToSender);

	}

	private void completeComponentRequestTask(NotificationDTO notification, long currentUserId) {
		notification.setCompletionInd(true);
		notificationMapper.updateNotification(notification);
		notificationMapper.disableNotification(notification.getNotificationId()); // disable it
		NotificationDTO notificationToSender = new NotificationDTO();
		User recipient = new User();
		recipient.setUserId(notification.getSenderId()); // set the recipient to sender
		notificationToSender.setNotificationTypeCode(NotificationTypeCode.CRC); // component request complete
		notificationToSender.setSenderId(currentUserId);
		notificationToSender.setChangeRequestId(notification.getChangeRequestId());
		notificationToSender.setFiscalYear(notification.getFiscalYear());
		notificationToSender.setOriginalNotificationId(notification.getNotificationId());
		notificationToSender.setCreatedDate(Calendar.getInstance().getTime());
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta rcTypeMeta = notificationTypeMap.get(NotificationTypeCode.CRC);
		notificationToSender.setSubject(rcTypeMeta.getDefaultSubject());
		notificationToSender.setMessage(rcTypeMeta.getDefaultMessage());
		notificationToSender.setCompletionRequiredInd(rcTypeMeta.isCompletionRequiredInd());
		notificationToSender.setRecipient(recipient);
		postNotificationToOneRecipient(notificationToSender);

	}

	private void completeReviewChangeRquestTask(NotificationDTO notification, long currentUserId) {
		notification.setCompletionInd(true);
		notificationMapper.updateNotification(notification);
		notificationMapper.disableNotification(notification.getNotificationId()); // disable it

		NotificationDTO notificationToSender = new NotificationDTO();
		User recipient = new User();
		recipient.setUserId(notification.getSenderId()); // set the recipient to sender
		notificationToSender.setNotificationTypeCode(NotificationTypeCode.RC); // review complete
		notificationToSender.setSenderId(currentUserId);
		notificationToSender.setChangeRequestId(notification.getChangeRequestId());
		notificationToSender.setFiscalYear(notification.getFiscalYear());
		notificationToSender.setOriginalNotificationId(notification.getNotificationId());
		notificationToSender.setCreatedDate(Calendar.getInstance().getTime());
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta rcTypeMeta = notificationTypeMap.get(NotificationTypeCode.RC);

		notificationToSender.setSubject(rcTypeMeta.getDefaultSubject());
		notificationToSender.setMessage(rcTypeMeta.getDefaultMessage());
		notificationToSender.setCompletionRequiredInd(rcTypeMeta.isCompletionRequiredInd());
		notificationToSender.setRecipient(recipient);
		postNotificationToOneRecipient(notificationToSender);

	}

	/*
	 * the task can be New Component Request, New Attribute and/or Reference Value Request, Review Request,Advice
	 * Request (non-Javadoc)
	 * 
	 * @see ca.cihi.cims.service.NotificationService#completeTask(long, long)
	 */
	@Override
	public void completeTask(long notificationId, long currentUserId) {
		NotificationDTO notification = notificationMapper.findNotificationByPrimaryKey(notificationId);
		if (NotificationTypeCode.RR == notification.getNotificationTypeCode()) { // Review Request
			completeReviewChangeRquestTask(notification, currentUserId);
		}
		if (NotificationTypeCode.AR == notification.getNotificationTypeCode()) { // Advice Request
			completeAdviceRequestTask(notification, currentUserId);
		}
		if (NotificationTypeCode.NCR == notification.getNotificationTypeCode()) { // New Component Request
			completeComponentRequestTask(notification, currentUserId);
		}
		if (NotificationTypeCode.NRVR == notification.getNotificationTypeCode()) { // New Attribute and/or Reference
			// Value Request
			completeAttributeReferenceValueRequestTask(notification, currentUserId);
		}
	}

	@Override
	public NotificationDTO findNotifcationById(long notificationId) {
		return notificationMapper.findNotificationByPrimaryKey(notificationId);
	}

	@Override
	public List<NotificationDTO> findNotificationsByUserId(long userId) {
		return notificationMapper.findNotificationsByUserId(userId);
	}

	@Override
	public List<NotificationDTO> findNotificationsByUserSerachCriteria(UserSearchCriteria userSearchCriteria) {
		return notificationMapper.findNotificationsByUserSearchCriteria(userSearchCriteria);
	}

	@Override
	public int findNumOfMyNotifications(Long userId) {
		return notificationMapper.findNumOfMyNotifications(userId);
	}

	public AdminMapper getAdminMapper() {
		return adminMapper;
	}

	public ChangeRequestMapper getChangeRequestMapper() {
		return changeRequestMapper;
	}

	// as Cannot change the ExecutorType when there is an existing transaction, this method will be removed, Do Not do
	// in Batch mode
	/*
	 * private void insertNotificationUserProfiles(List<NotificationUserProfile> notificationUserProfiles ){
	 * SqlSessionTemplate sqlSessionBatch= new SqlSessionTemplate(sqlSessionFactory,ExecutorType.BATCH); for
	 * (NotificationUserProfile notificationUserProfile:notificationUserProfiles){
	 * sqlSessionBatch.insert("ca.cihi.cims.data.mapper.NotificationMapper.insertNotificationUserProfile",
	 * notificationUserProfile); } sqlSessionBatch.commit(); sqlSessionBatch.close(); }
	 * 
	 * private void insertNotificationDistributionLists(List<NotificationDistributionList> notificationDistributionLists
	 * ){ SqlSessionTemplate sqlSessionBatch= new SqlSessionTemplate(sqlSessionFactory,ExecutorType.BATCH); for
	 * (NotificationDistributionList notificationDistributionList:notificationDistributionLists){
	 * sqlSessionBatch.insert("ca.cihi.cims.data.mapper.NotificationMapper.insertNotificationDistributionList",
	 * notificationDistributionList); } sqlSessionBatch.commit(); sqlSessionBatch.close(); }
	 */

	public LookupService getLookupService() {
		return lookupService;
	}

	public NotificationMapper getNotificationMapper() {
		return notificationMapper;
	}

	private void insertNotification(NotificationDTO notificationDTO) {

		// START Fix for Incident # 28449.
		// Seek Advice function in the Change request
		// java.sql.SQLException: ORA-12899: value too large for column "CIMS"."NOTIFICATION"."MESSAGE" (actual: 1006,
		// maximum: 500)

		// Column NOTIFICATION.MESSAGE Has Length of 500.
		// When a message larger than length 500 is inserted into the field NOTIFICATION.MESSAGE SQL Exception happens

		// Hence we Truncate the message before the INSERT into MESSAGE table

		String originalMessage = notificationDTO.getMessage();
		String truncatedMessage = null;

		if (originalMessage != null && originalMessage.length() > MAX_NOTIFICATION_MSG_SIZE) {

			if (originalMessage.toLowerCase().startsWith(P_HTML_TAG)) {
				// if the notification message starts with a <p>
				truncatedMessage = originalMessage.substring(0, P_HTML_TAG_TRUNCATED_NOTIFICATION_MSG_SIZE)
						+ P_HTML_TAG_ELLIPSIS;
			} else {
				truncatedMessage = originalMessage.substring(0, TRUNCATED_NOTIFICATION_MSG_SIZE) + ELLIPSIS;
			}

			notificationDTO.setMessage(truncatedMessage);

		}
		// insert notification into DB. SQL Exception was happening at this line before the fix
		notificationMapper.insertNotification(notificationDTO);

		// put back the original message into the DTO to maintain a consistent object in the application
		if (truncatedMessage != null) {// if there was truncation
			notificationDTO.setMessage(originalMessage);
		}
		// END Fix for Incident # 28449.

	}

	private void insertNotificationDistributionLists(List<NotificationDistributionList> notificationDistributionLists) {
		for (NotificationDistributionList notificationDistributionList : notificationDistributionLists) {
			notificationMapper.insertNotificationDistributionList(notificationDistributionList);
		}
	}

	private void insertNotificationUserProfiles(List<NotificationUserProfile> notificationUserProfiles) {
		for (NotificationUserProfile notificationUserProfile : notificationUserProfiles) {
			notificationMapper.insertNotificationUserProfile(notificationUserProfile);
		}
	}

	@Override
	public void postAcceptChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.CRACPT); // Change Request Accepted
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta cracptTypeMeta = notificationTypeMap
				.get(NotificationTypeCode.CRACPT);
		notificationDTO.setSubject(cracptTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(cracptTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(cracptTypeMeta.isCompletionRequiredInd());

		User recipient = new User();
		recipient.setUserId(changeRequest.getOwnerId());
		notificationDTO.setRecipient(recipient);
		postNotificationToOneRecipient(notificationDTO);
	}

	@Override
	public void postAdviceRequestNotifcation(ChangeRequestDTO changeRequestDTO, Advice advice, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.AR); // Advice Request
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta arTypeMeta = notificationTypeMap.get(NotificationTypeCode.AR);

		notificationDTO.setSubject(arTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(arTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(arTypeMeta.isCompletionRequiredInd());
		// very import, link to the advice table
		notificationDTO.setAdviceId(advice.getAdviceId());
		if (advice.getDistributionListId() != null) { // get advice from a group
			List<Distribution> dls = new ArrayList<Distribution>();
			Distribution dl = new Distribution();
			dl.setDistributionlistid(advice.getDistributionListId());
			dls.add(dl);
			notificationDTO.setDlRecipients(dls);
			postNotificationToDLs(notificationDTO);
		} else {
			User recipient = new User();
			recipient.setUserId(advice.getUserProfileId());
			notificationDTO.setRecipient(recipient);
			postNotificationToOneRecipient(notificationDTO);
		}
	}

	@Override
	public void postChangeRequestAssignmentNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.CRA); // Change Request Assignment /assign to
		// INDIVIDUAL
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta craTypeMeta = notificationTypeMap.get(NotificationTypeCode.CRA);
		notificationDTO.setSubject(craTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(craTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(craTypeMeta.isCompletionRequiredInd());
		User recipient = new User();
		recipient.setUserId(changeRequestDTO.getAssigneeUserId());
		notificationDTO.setRecipient(recipient);
		postNotificationToOneRecipient(notificationDTO);
	}

	@Override
	public void postChangeRequestOwnershipNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.CRTO); // Change Request Ownership
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta crtoTypeMeta = notificationTypeMap.get(NotificationTypeCode.CRTO);
		notificationDTO.setSubject(crtoTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(crtoTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(crtoTypeMeta.isCompletionRequiredInd());
		User recipient = new User();
		recipient.setUserId(changeRequestDTO.getOwnerId());
		notificationDTO.setRecipient(recipient);
		postNotificationToOneRecipient(notificationDTO);

	}

	@Override
	public void postChangeRequestPickUpNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.CRP); // Change Request Pick-Up/ Group Assign
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta crpTypeMeta = notificationTypeMap.get(NotificationTypeCode.CRP);
		notificationDTO.setSubject(crpTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(crpTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(crpTypeMeta.isCompletionRequiredInd());
		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(changeRequestDTO.getAssigneeDLId());
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void postNewComponentRequestNotifcation(long changeRequestId, String message, long currentUserId) {
		NotificationDTO notification = new NotificationDTO();
		notification.setNotificationTypeCode(NotificationTypeCode.NCR); // new component request
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta ncrTypeMeta = notificationTypeMap.get(NotificationTypeCode.NCR);
		notification.setSubject(ncrTypeMeta.getDefaultSubject());

		notification.setMessage(message);

		notification.setChangeRequestId(changeRequestId);
		notification.setSenderId(currentUserId);
		notification.setCompletionRequiredInd(ncrTypeMeta.isCompletionRequiredInd());

		ChangeRequest changeRequest = changeRequestMapper.findChangeRequestById(changeRequestId);
		
		notification.setFiscalYear(changeRequest.getBaseVersionCode());

		List<Distribution> dlRecipients = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(Distribution.DL_ID_ADMINISTRATOR); // new component request notification to
		// DL_ADMINISTRATOR
		dlRecipients.add(dl);
		notification.setDlRecipients(dlRecipients);

		postNotificationToDLs(notification);

	}
	
	@Override
	public void postNewCommentNotifcationToGroup(long changeRequestId, String message, long currentUserId, long distributionListId) {
		String subject = "New Comment Added";
		NotificationDTO notification = new NotificationDTO();
		notification.setNotificationTypeCode(NotificationTypeCode.NCR); // new component request
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta ncrTypeMeta = notificationTypeMap.get(NotificationTypeCode.NCR);
		notification.setSubject(subject);

		notification.setMessage(message);

		notification.setChangeRequestId(changeRequestId);
		notification.setSenderId(currentUserId);
		notification.setCompletionRequiredInd(ncrTypeMeta.isCompletionRequiredInd());

		ChangeRequest changeRequest = changeRequestMapper.findChangeRequestById(changeRequestId);
		
		notification.setFiscalYear(changeRequest.getBaseVersionCode());

		List<Distribution> dlRecipients = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(distributionListId); // new component request notification to appropriate DL group
		dlRecipients.add(dl);
		notification.setDlRecipients(dlRecipients);

		postNotificationToDLWithoutSender(notification, currentUserId); //exclude notification from being sent to sender
		
	}
	
	@Override
	public void postNewCommentNotifcation(long changeRequestId, String message, long currentUserId, long targetUserId) {
		String subject = "New Comment Added";
		NotificationDTO notification = new NotificationDTO();
		notification.setNotificationTypeCode(NotificationTypeCode.NCR); // new component request
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta ncrTypeMeta = notificationTypeMap.get(NotificationTypeCode.NCR);
		notification.setSubject(subject);

		notification.setMessage(message);

		notification.setChangeRequestId(changeRequestId);
		notification.setSenderId(currentUserId);
		notification.setCompletionRequiredInd(ncrTypeMeta.isCompletionRequiredInd());

		ChangeRequest changeRequest = changeRequestMapper.findChangeRequestById(changeRequestId);
		
		notification.setFiscalYear(changeRequest.getBaseVersionCode());

		User recipient = new User();
		recipient.setUserId(targetUserId);
		notification.setRecipient(recipient);
		postNotificationToOneRecipient(notification);
		
	}

	@Override
	public void postNotificationToDLs(NotificationDTO notificationDTO) {
		insertNotification(notificationDTO);
		List<Distribution> dls = notificationDTO.getDlRecipients();
		List<NotificationDistributionList> notificationDistributionLists = new ArrayList<NotificationDistributionList>();
		List<Long> distributionIds = new ArrayList<Long>();
		for (Distribution dl : dls) {
			NotificationDistributionList ndl = new NotificationDistributionList();
			ndl.setNotificationId(notificationDTO.getNotificationId());
			ndl.setDistributionListId(dl.getDistributionlistid());
			notificationDistributionLists.add(ndl);
			distributionIds.add(dl.getDistributionlistid());
		}

		insertNotificationDistributionLists(notificationDistributionLists);

		List<Long> userIds = adminMapper.findDistinctUserIdsInDistributionListIds(distributionIds);
		List<NotificationUserProfile> notificationUserProfiles = new ArrayList<NotificationUserProfile>();
		for (Long userId : userIds) {
			NotificationUserProfile notificationUserProfile = new NotificationUserProfile();
			notificationUserProfile.setNotificationId(notificationDTO.getNotificationId());
			notificationUserProfile.setUserProfileId(userId);
			notificationUserProfiles.add(notificationUserProfile);
		}
		insertNotificationUserProfiles(notificationUserProfiles);

	}
	
	@Override
	public void postNotificationToDLWithoutSender(NotificationDTO notificationDTO, Long senderId) {
		insertNotification(notificationDTO);
		List<Distribution> dls = notificationDTO.getDlRecipients();
		List<NotificationDistributionList> notificationDistributionLists = new ArrayList<NotificationDistributionList>();
		List<Long> distributionIds = new ArrayList<Long>();
		for (Distribution dl : dls) {
			NotificationDistributionList ndl = new NotificationDistributionList();
			ndl.setNotificationId(notificationDTO.getNotificationId());
			ndl.setDistributionListId(dl.getDistributionlistid());
			notificationDistributionLists.add(ndl);
			distributionIds.add(dl.getDistributionlistid());
		}

		insertNotificationDistributionLists(notificationDistributionLists);

		List<Long> userIds = adminMapper.findDistinctUserIdsInDistributionListIds(distributionIds);
		List<NotificationUserProfile> notificationUserProfiles = new ArrayList<NotificationUserProfile>();
		for (Long userId : userIds) {
			if(userId != senderId){
				NotificationUserProfile notificationUserProfile = new NotificationUserProfile();
				notificationUserProfile.setNotificationId(notificationDTO.getNotificationId());
				notificationUserProfile.setUserProfileId(userId);
				notificationUserProfiles.add(notificationUserProfile);
			}
		}
		insertNotificationUserProfiles(notificationUserProfiles);
	}
	
	/*
	 * post notification to a custom list of recipients
	 */
	@Override
	public void postNotificationToOneRecipient(NotificationDTO notifcationDTO) {
		insertNotification(notifcationDTO);
		NotificationUserProfile notificationUserProfile = new NotificationUserProfile();
		notificationUserProfile.setUserProfileId(notifcationDTO.getRecipient().getUserId());
		notificationUserProfile.setNotificationId(notifcationDTO.getNotificationId());
		notificationMapper.insertNotificationUserProfile(notificationUserProfile);

	}

	/*
	 * The notification sent to DL-Internal Release, DL-Preliminary Release and DL-Official Release are via emailwhile
	 * the notification sent to DL-English Content Developer, DL-French Content Developer and DL-Administrator are
	 * posted on CIMS home page. (non-Javadoc)
	 * 
	 * @see ca.cihi.cims.service.NotificationService#postPackageReleaseNotifcation(ca.cihi.cims.model.prodpub.
	 * GenerateReleaseTablesCriteria, ca.cihi.cims.model.User)
	 */
	@Override
	public void postPackageReleaseNotifcation(GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setFiscalYear(String.valueOf(generateTablesCriteria.getCurrentOpenYear()));
		notificationDTO.setChangeRequestId(null);
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		List<Distribution> dls = new ArrayList<Distribution>();
		ca.cihi.cims.model.meta.NotificationTypeMeta prTypeMeta = null;
		// Distribution dl_PreliminaryRelease = new Distribution();
		// Distribution dl_OfficialRelease = new Distribution();
		Distribution dl_Administrator = new Distribution();
		Distribution dl_EnglishContentDeveloper = new Distribution();
		Distribution dl_FrenchContentDeveloper = new Distribution();

		// dl_PreliminaryRelease.setDistributionlistid(Distribution.DL_ID_PreliminaryRelease);
		// dl_OfficialRelease.setDistributionlistid(Distribution.DL_ID_OfficialRelease);
		dl_Administrator.setDistributionlistid(Distribution.DL_ID_ADMINISTRATOR);
		dl_EnglishContentDeveloper.setDistributionlistid(Distribution.DL_ID_ENContentDeveloper);
		dl_FrenchContentDeveloper.setDistributionlistid(Distribution.DL_ID_FRContentDeveloper);
		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY_INTERNAL_QA.equals(generateTablesCriteria
				.getReleaseType())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.PRPIQA);
			prTypeMeta = notificationTypeMap.get(NotificationTypeCode.PRPIQA);
			// dls.add(dl_PreliminaryRelease);
			dls.add(dl_EnglishContentDeveloper);
			dls.add(dl_FrenchContentDeveloper);

		}

		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL_INTERNAL_QA.equals(generateTablesCriteria
				.getReleaseType())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.PROIQA);
			prTypeMeta = notificationTypeMap.get(NotificationTypeCode.PROIQA);
			// dls.add(dl_PreliminaryRelease);
			dls.add(dl_EnglishContentDeveloper);
			dls.add(dl_FrenchContentDeveloper);

		}

		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY.equals(generateTablesCriteria.getReleaseType())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.PRP);
			prTypeMeta = notificationTypeMap.get(NotificationTypeCode.PRP);
			// dls.add(dl_PreliminaryRelease);
			dls.add(dl_EnglishContentDeveloper);
			dls.add(dl_FrenchContentDeveloper);
		}

		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL.equals(generateTablesCriteria.getReleaseType())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.PRO);
			prTypeMeta = notificationTypeMap.get(NotificationTypeCode.PRO);
			// dls.add(dl_OfficialRelease);
			dls.add(dl_Administrator);
		}

		notificationDTO.setSubject(prTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(prTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(prTypeMeta.isCompletionRequiredInd());
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void postPickupApprovalNotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PAPPROVAL); // Pickup Approval
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta papprovalTypeMeta = notificationTypeMap
				.get(NotificationTypeCode.PAPPROVAL);
		notificationDTO.setSubject(papprovalTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(papprovalTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(papprovalTypeMeta.isCompletionRequiredInd());
		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(Distribution.DL_ID_Reviewer); // DL003 DL-Reviewer
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void postPickupNewNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser) {
		// User currentUser = changeRequestDTO.getCurrentUser();
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PUN); // pickup new
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta punTypeMeta = notificationTypeMap.get(NotificationTypeCode.PUN);
		notificationDTO.setSubject(punTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(punTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(punTypeMeta.isCompletionRequiredInd());

		List<Distribution> dls = new ArrayList<Distribution>();

		if ("FRA".equalsIgnoreCase(changeRequestDTO.getLanguageCode())) {
			Distribution dl = new Distribution();
			dl.setDistributionlistid(Distribution.DL_ID_FRContentDeveloper);
			dls.add(dl);
		} else {
			Distribution dl = new Distribution();
			dl.setDistributionlistid(Distribution.DL_ID_ENContentDeveloper);
			dls.add(dl);
		}
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void postPickupQANotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PUQA); // Pick-up QA
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta puqaTypeMeta = notificationTypeMap.get(NotificationTypeCode.PUQA);
		notificationDTO.setSubject(puqaTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(puqaTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(puqaTypeMeta.isCompletionRequiredInd());
		List<Distribution> dls = new ArrayList<Distribution>();

		Distribution dl = new Distribution();
		if ("FRA".equalsIgnoreCase(changeRequest.getLanguageCode())) {
			dl.setDistributionlistid(Distribution.DL_ID_FRContentDeveloper); // DL_ID_FRContentDeveloper
		} else {
			dl.setDistributionlistid(Distribution.DL_ID_ENContentDeveloper); // DL_ID_ENContentDeveloper
		}
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);
	}

	@Override
	public void postPickupRealizationChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PUR); // Pickup Realization
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta purTypeMeta = notificationTypeMap.get(NotificationTypeCode.PUR);
		notificationDTO.setSubject(purTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(purTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(purTypeMeta.isCompletionRequiredInd());
		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(Distribution.DL_ID_Reviewer); // DL003 DL-Reviewer
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void postPickupTranslationChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PUT); // Pickup Translation
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta putTypeMeta = notificationTypeMap.get(NotificationTypeCode.PUT);
		notificationDTO.setSubject(putTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(putTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(putTypeMeta.isCompletionRequiredInd());
		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(Distribution.DL_ID_FRContentDeveloper); // DL_ID_FRContentDeveloper
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);
	}

	@Override
	public void postPickupValidationChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PUV); // Pickup Validation
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta puvTypeMeta = notificationTypeMap.get(NotificationTypeCode.PUV);
		notificationDTO.setSubject(puvTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(puvTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(puvTypeMeta.isCompletionRequiredInd());
		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(Distribution.DL_ID_FRContentDeveloper); // DL_ID_FRContentDeveloper
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);
	}

	@Override
	public void postReadyForAcceptanceNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.PUA); // Ready for Acceptance (Pickup Acceptance)
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta puaTypeMeta = notificationTypeMap.get(NotificationTypeCode.PUA);
		notificationDTO.setSubject(puaTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(puaTypeMeta.getDefaultMessage());
		notificationDTO.setCompletionRequiredInd(puaTypeMeta.isCompletionRequiredInd());

		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(puaTypeMeta.getPredefinedRecipientDLId()); // DL003 DL-Reviewer
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void postReviewRequestNotifcation(ChangeRequestDTO changeRequestDTO,
			QuestionForReviewer questionForReviewer, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.RR); // Review Request
		notificationDTO.setFiscalYear(changeRequestDTO.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequestDTO.getChangeRequestId());
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta rrTypeMeta = notificationTypeMap.get(NotificationTypeCode.RR);

		notificationDTO.setSubject(rrTypeMeta.getDefaultSubject());

		notificationDTO.setMessage(rrTypeMeta.getDefaultMessage());

		notificationDTO.setCompletionRequiredInd(rrTypeMeta.isCompletionRequiredInd());
		// very import, link to the review_question table
		notificationDTO.setQuestionForReviewerId(questionForReviewer.getQuestionForReviewerId());

		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(questionForReviewer.getReviewerId());
		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);
	}

	@Override
	public void postSendBackChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.CRI); // Change Request Incomplete
		notificationDTO.setFiscalYear(changeRequest.getBaseVersionCode());
		notificationDTO.setChangeRequestId(changeRequest.getChangeRequestId());

		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta criTypeMeta = notificationTypeMap.get(NotificationTypeCode.CRI);
		notificationDTO.setSubject(criTypeMeta.getDefaultSubject());
		notificationDTO.setMessage(changeRequest.getRationaleForIncomplete());
		notificationDTO.setCompletionRequiredInd(criTypeMeta.isCompletionRequiredInd());
		User recipient = new User();
		recipient.setUserId(changeRequest.getOwnerId());
		notificationDTO.setRecipient(recipient);
		postNotificationToOneRecipient(notificationDTO);

	}

	@Override
	@Transactional
	public void postWrapupWorkNotifcationToAdministrator(GenerateReleaseTablesCriteria generateTablesCriteria,
			User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setFiscalYear(String.valueOf(generateTablesCriteria.getCurrentOpenYear()));
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta wrapupWorkTypeMeta = null;
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equals(generateTablesCriteria.getClassification())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.WRPICD);
			wrapupWorkTypeMeta = notificationTypeMap.get(NotificationTypeCode.WRPICD);
			notificationDTO.setMessage(WRAPUP_ICD_A);
		}
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equals(generateTablesCriteria.getClassification())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.WRPCCI);
			wrapupWorkTypeMeta = notificationTypeMap.get(NotificationTypeCode.WRPCCI);
			notificationDTO.setMessage(WRAPUP_CCI_A);
		}
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(generateTablesCriteria.getClassification())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.WRPBTH);
			wrapupWorkTypeMeta = notificationTypeMap.get(NotificationTypeCode.WRPBTH);
			notificationDTO.setMessage(WRAPUP_BTH_A);
		}
		notificationDTO.setSubject(wrapupWorkTypeMeta.getDefaultSubject());
		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl = new Distribution();
		dl.setDistributionlistid(Distribution.DL_ID_ADMINISTRATOR);

		dls.add(dl);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	/*
	 * before generate tables, send notification to all content developer to wrap up their work : send all requests for
	 * approval send notification to all reviewers,: to approve the change requests
	 */
	@Override
	@Transactional
	public void postWrapupWorkNotifcationToContentDeveloperAndReviewer(
			GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser) {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setSenderId(currentUser.getUserId());
		notificationDTO.setFiscalYear(String.valueOf(generateTablesCriteria.getCurrentOpenYear()));
		Map<NotificationTypeCode, ca.cihi.cims.model.meta.NotificationTypeMeta> notificationTypeMap = lookupService
				.loadAllNotificationTypeMetaToMap();
		ca.cihi.cims.model.meta.NotificationTypeMeta wrapupWorkTypeMeta = null;
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equals(generateTablesCriteria.getClassification())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.WRPICD);
			wrapupWorkTypeMeta = notificationTypeMap.get(NotificationTypeCode.WRPICD);
			notificationDTO.setMessage(WRAPUP_ICD);
		}
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equals(generateTablesCriteria.getClassification())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.WRPCCI);
			wrapupWorkTypeMeta = notificationTypeMap.get(NotificationTypeCode.WRPCCI);
			notificationDTO.setMessage(WRAPUP_CCI);
		}
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(generateTablesCriteria.getClassification())) {
			notificationDTO.setNotificationTypeCode(NotificationTypeCode.WRPBTH);
			wrapupWorkTypeMeta = notificationTypeMap.get(NotificationTypeCode.WRPBTH);
			notificationDTO.setMessage(WRAPUP_BTH);
		}
		notificationDTO.setSubject(wrapupWorkTypeMeta.getDefaultSubject());

		List<Distribution> dls = new ArrayList<Distribution>();
		Distribution dl_ENContentDeveloper = new Distribution();
		dl_ENContentDeveloper.setDistributionlistid(Distribution.DL_ID_ENContentDeveloper);
		Distribution dl_FRContentDeveloper = new Distribution();
		dl_FRContentDeveloper.setDistributionlistid(Distribution.DL_ID_FRContentDeveloper);
		Distribution dl_Reviewer = new Distribution();
		dl_Reviewer.setDistributionlistid(Distribution.DL_ID_Reviewer);
		dls.add(dl_ENContentDeveloper);
		dls.add(dl_FRContentDeveloper);
		dls.add(dl_Reviewer);
		notificationDTO.setDlRecipients(dls);
		postNotificationToDLs(notificationDTO);

	}

	@Override
	public void removeAllNotificationsForYear(String fiscalYear) {
		notificationMapper.disableAllNotificationsForYear(fiscalYear);
	}

	@Override
	public void removeChangeRequestPickupNotification(long changeRequestId) {
		notificationMapper.disableChangeRequestPickupNotification(changeRequestId);
	}

	@Override
	public void removeMyNotification(long userId, long notificationId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("userId", userId);
		parameters.put("notificationId", notificationId);
		notificationMapper.disableMyNotification(parameters);
	}

	@Override
	public void removeMyNotifications(User currentUser, List<Long> notificationIds) {
		notificationMapper.disableMyNotifications(currentUser, notificationIds);
	}

	@Override
	public void removeNotification(long notificationId) {
		notificationMapper.disableNotification(notificationId);
	}

	@Override
	public void removePickupAcceptanceNotification(long changeRequestId) {
		notificationMapper.disablePickupAcceptanceNotification(changeRequestId);
	}

	@Override
	public void removePickupNewNotification(long changeRequestId) {
		notificationMapper.disablePickupNewNotification(changeRequestId);
	}

	@Override
	public void removeWrapupWorkNotifcation(GenerateReleaseTablesCriteria generateTablesCriteria) {
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_ICD.equals(generateTablesCriteria.getClassification())) {
			notificationMapper.disableWrapupWorkNotification(NotificationTypeCode.WRPICD);
		}
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_CCI.equals(generateTablesCriteria.getClassification())) {
			notificationMapper.disableWrapupWorkNotification(NotificationTypeCode.WRPCCI);
		}
		if (GenerateReleaseTablesCriteria.CLASSIFICATION_TYPE_BOTH.equals(generateTablesCriteria.getClassification())) {
			notificationMapper.disableWrapupWorkNotification(NotificationTypeCode.WRPBTH);
		}
	}

	@Override
	public void reviewChangeRquestTaskComplete(long notificationId, long currentUserId) {
		NotificationDTO notification = notificationMapper.findNotificationByPrimaryKey(notificationId);
		notification.setCompletionInd(true);
		notificationMapper.updateNotification(notification);
		notificationMapper.disableNotification(notificationId); // disable it
		NotificationDTO notificationToSender = new NotificationDTO();
		User recipient = new User();
		recipient.setUserId(notification.getSenderId()); // set the recipient to sender
		// notificationToSender.setNotificationTypeCode(NotificationTypeCode.TASKCOMP);
		notificationToSender.setSubject("Re:" + notification.getSubject());
		notificationToSender.setMessage("The requested task bellow has been completed: " + notification.getMessage());
		notificationToSender.setSenderId(currentUserId);
		notificationToSender.setChangeRequestId(notification.getChangeRequestId());
		notificationToSender.setFiscalYear(notification.getFiscalYear());
		notificationToSender.setOriginalNotificationId(notification.getNotificationId());
		notificationToSender.setCompletionRequiredInd(false);
		notificationToSender.setCreatedDate(Calendar.getInstance().getTime());
		notificationToSender.setRecipient(recipient);
		postNotificationToOneRecipient(notificationToSender);

	}

	public void setAdminMapper(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}

	public void setChangeRequestMapper(ChangeRequestMapper changeRequestMapper) {
		this.changeRequestMapper = changeRequestMapper;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setNotificationMapper(NotificationMapper notificationMapper) {
		this.notificationMapper = notificationMapper;
	}

	/*
	 * public SqlSessionTemplate getSqlSessionBatch() { return sqlSessionBatch; }
	 * 
	 * 
	 * public void setSqlSessionBatch(SqlSessionTemplate sqlSessionBatch) { this.sqlSessionBatch = sqlSessionBatch; }
	 * 
	 * 
	 * public SqlSessionFactoryBean getSqlSessionFactory() { return sqlSessionFactory; }
	 * 
	 * 
	 * public void setSqlSessionFactory(SqlSessionFactoryBean sqlSessionFactory) { this.sqlSessionFactory =
	 * sqlSessionFactory; }
	 */

}
