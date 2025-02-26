package ca.cihi.cims.model.notification;

import java.io.Serializable;

public class NotificationDistributionList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long notificationDistribListId;  //PK
	
	private Long notificationId;   // FK to Notification
	
	private Long distributionListId ;   // FK to Distribution List 

	public Long getNotificationDistribListId() {
		return notificationDistribListId;
	}

	public void setNotificationDistribListId(Long notificationDistribListId) {
		this.notificationDistribListId = notificationDistribListId;
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getDistributionListId() {
		return distributionListId;
	}

	public void setDistributionListId(Long distributionListId) {
		this.distributionListId = distributionListId;
	}

}
