package ca.cihi.cims.model.meta;

import java.io.Serializable;

import ca.cihi.cims.model.notification.NotificationTypeCode;
/*
 * this class mapped to NOTIFICATION_TYPE table
 */
public class NotificationTypeMeta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private NotificationTypeCode notificationTypeCode;
	
	private String defaultSubject;
	
	private String defaultMessage;
	
	private boolean completionRequiredInd;
	
	private boolean bulkDeleteInd;
	
	private Long predefinedRecipientDLId;

	

	public String getDefaultSubject() {
		return defaultSubject;
	}

	public void setDefaultSubject(String defaultSubject) {
		this.defaultSubject = defaultSubject;
	}

	public String getDefaultMessage() {
		return defaultMessage;
	}

	public void setDefaultMessage(String defaultMessage) {
		this.defaultMessage = defaultMessage;
	}

	public boolean isCompletionRequiredInd() {
		return completionRequiredInd;
	}

	public void setCompletionRequiredInd(boolean completionRequiredInd) {
		this.completionRequiredInd = completionRequiredInd;
	}

	public boolean isBulkDeleteInd() {
		return bulkDeleteInd;
	}

	public void setBulkDeleteInd(boolean bulkDeleteInd) {
		this.bulkDeleteInd = bulkDeleteInd;
	}

	public Long getPredefinedRecipientDLId() {
		return predefinedRecipientDLId;
	}

	public void setPredefinedRecipientDLId(Long predefinedRecipientDLId) {
		this.predefinedRecipientDLId = predefinedRecipientDLId;
	}

	public NotificationTypeCode getNotificationTypeCode() {
		return notificationTypeCode;
	}

	public void setNotificationTypeCode(NotificationTypeCode notificationTypeCode) {
		this.notificationTypeCode = notificationTypeCode;
	}
	
	
	

}
