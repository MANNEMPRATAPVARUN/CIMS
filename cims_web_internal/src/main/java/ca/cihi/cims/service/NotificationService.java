package ca.cihi.cims.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.Advice;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.QuestionForReviewer;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;

public interface NotificationService {

	@Transactional
	void completeTask(long notificationId, long currentUserId);

	// NotificationDTO findNotifcationByChangeRequestIdAndNotificationType(long notificationId,NotificationTypeCode
	// typeCode);

	NotificationDTO findNotifcationById(long notificationId);

	List<NotificationDTO> findNotificationsByUserId(long userId);

	List<NotificationDTO> findNotificationsByUserSerachCriteria(UserSearchCriteria userSearchCriteria);

	int findNumOfMyNotifications(Long userId);

	@Transactional
	void postAcceptChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postAdviceRequestNotifcation(ChangeRequestDTO changeRequestDTO, Advice advice, User currentUser);

	@Transactional
	void postChangeRequestAssignmentNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void postChangeRequestOwnershipNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void postChangeRequestPickUpNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void postNewComponentRequestNotifcation(long changeRequestId, String message, long currentUserId);
	
	@Transactional
	void postNewCommentNotifcationToGroup(long changeRequestId, String message, long currentUserId, long distributionListId);
	
	@Transactional
	void postNewCommentNotifcation(long changeRequestId, String message, long currentUserId, long targetUserId);

	@Transactional
	void postNotificationToDLs(NotificationDTO notificationDTO);

	@Transactional
	void postNotificationToOneRecipient(NotificationDTO notificationDTO);
	
	@Transactional
	void postNotificationToDLWithoutSender(NotificationDTO notificationDTO, Long senderId);

	@Transactional
	void postPackageReleaseNotifcation(GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser);

	@Transactional
	void postPickupApprovalNotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postPickupNewNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser);

	@Transactional
	void postPickupQANotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postPickupRealizationChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postPickupTranslationChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postPickupValidationChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postReadyForAcceptanceNotifcation(ChangeRequestDTO changeRequestDTO, User currentUser); // Pickup Acceptance
																									// Notification

	@Transactional
	void postReviewRequestNotifcation(ChangeRequestDTO changeRequestDTO, QuestionForReviewer questionForReviewer,
			User currentUser);

	@Transactional
	void postSendBackChangeRequestNotifcation(ChangeRequest changeRequest, User currentUser);

	@Transactional
	void postWrapupWorkNotifcationToAdministrator(GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser);

	@Transactional
	void postWrapupWorkNotifcationToContentDeveloperAndReviewer(GenerateReleaseTablesCriteria generateTablesCriteria,
			User currentUser);

	@Transactional
	void removeAllNotificationsForYear(String fiscalYear);

	@Transactional
	void removeChangeRequestPickupNotification(long changeRequestId);

	@Transactional
	void removeMyNotification(long userId, long notificationId);

	@Transactional
	void removeMyNotifications(User currentUser, List<Long> notificationIds);

	@Transactional
	void removeNotification(long notificationId);

	@Transactional
	void removePickupAcceptanceNotification(long changeRequestId);

	@Transactional
	void removePickupNewNotification(long changeRequestId);

	@Transactional
	void removeWrapupWorkNotifcation(GenerateReleaseTablesCriteria generateTablesCriteria);

	/*
	 * @Transactional void insertNotificationUserProfiles(List<NotificationUserProfile> notificationUserProfiles );
	 * 
	 * @Transactional void insertNotificationDistributionList(NotificationDistributionList notificationDistributionList
	 * );
	 * 
	 * @Transactional void insertNotificationDistributionLists(List<NotificationDistributionList>
	 * notificationDistributionLists );
	 */
	@Transactional
	void reviewChangeRquestTaskComplete(long notificationId, long currentUserId);

	// void setNotificationMapper(NotificationMapper notificationMapper);

	// void setNotificationMapper(org.mybatis.spring.mapper.MapperFactoryBean notificationMapper);

}
