package ca.cihi.cims.model.notification;

import java.io.Serializable;

public class NotificationUserProfile implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long notificationUserProfileId;  //PK
	
	private Long notificationId;     // FK to Notification
	
	private Long userProfileId;      // FK to User Profile

	public Long getNotificationUserProfileId() {
		return notificationUserProfileId;
	}

	public void setNotificationUserProfileId(Long notificationUserProfileId) {
		this.notificationUserProfileId = notificationUserProfileId;
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}
	

}
