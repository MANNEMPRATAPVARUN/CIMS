package ca.cihi.cims.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.meta.NotificationTypeMeta;
import ca.cihi.cims.model.notification.Notification;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.model.notification.NotificationDistributionList;
import ca.cihi.cims.model.notification.NotificationTypeCode;
import ca.cihi.cims.model.notification.NotificationUserProfile;

public interface NotificationMapper {

	int disableAllNotificationsForYear(String fiscalYear);

	int disableChangeRequestPickupNotification(Long changeRequestId); // for 'PUN' and 'CRP'

	int disableMyNotification(java.util.Map<String, Object> map); // set it to disabled, for user to remove
																	// mynotification

	// int updateNotification(Notification record);

	int disableMyNotifications(@Param("currentUser") User currentUser,
			@Param("notificationIds") List<Long> notificationIds); // set it to disabled, for user to remove
																	// mynotification

	int disableNotification(Long notificationId);

	int disablePickupAcceptanceNotification(Long changeRequestId); // for 'PUA', Pickup Acceptance

	int disablePickupNewNotification(Long changeRequestId);

	int disableWrapupWorkNotification(NotificationTypeCode notificationTypeCode);

	List<NotificationTypeMeta> findAllNotificationTypeMeta();

	NotificationDTO findNotificationByPrimaryKey(Long notificationId);

	List<NotificationDTO> findNotificationsByUserId(Long userId);

	List<NotificationDTO> findNotificationsByUserSearchCriteria(UserSearchCriteria userSearchCriteria);

	int findNumOfMyNotifications(Long userId);

	// int insertNotifaction(Notification record);

	int insertNotification(Notification notification);

	int insertNotificationDistributionList(NotificationDistributionList ndl);

	int insertNotificationUserProfile(NotificationUserProfile notificationUserProfile);

	int updateNotification(Notification notification);

}
