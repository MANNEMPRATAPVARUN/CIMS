package ca.cihi.cims.model.notification;

import java.util.Date;


public class Notification {
	private static final long serialVersionUID = -8967166170838549349L;
	// public static final String NTF_MSG_NEWCHANGE="a new change request created";
	// public static final String NTF_SUB_NEWCHANGE="a new request";

	private Long notificationId; // keep it Long for all Ids
	private NotificationTypeCode notificationTypeCode;

	private String fiscalYear; // ? fiscalYear
	private String subject;
	private String message;
	// private String sender;
	private Long senderId; // FK to User_Profile
	// private String recipient;
	// private String notificationType;

	private Long changeRequestId; // FK to Change Request

	private Long questionForReviewerId; // FK to Question_For_Review

	private Long adviceId;

	private boolean completionRequiredInd; // Y or N true/false

	private boolean completionInd; // Y or N true/false

	private Long originalNotificationId;

	private Date createdDate;

	private Date lastUpdatedTime;

	public Long getAdviceId() {
		return adviceId;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getFiscalYear() {
		return fiscalYear;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public String getMessage() {
		return message;
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public NotificationTypeCode getNotificationTypeCode() {
		return notificationTypeCode;
	}

	public Long getOriginalNotificationId() {
		return originalNotificationId;
	}

	public Long getQuestionForReviewerId() {
		return questionForReviewerId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public String getSubject() {
		return subject;
	}

	public boolean isCompletionInd() {
		return completionInd;
	}

	public boolean isCompletionRequiredInd() {
		return completionRequiredInd;
	}

	public void setAdviceId(Long adviceId) {
		this.adviceId = adviceId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setCompletionInd(boolean completionInd) {
		this.completionInd = completionInd;
	}

	public void setCompletionRequiredInd(boolean completionRequiredInd) {
		this.completionRequiredInd = completionRequiredInd;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public void setNotificationTypeCode(NotificationTypeCode notificationTypeCode) {
		this.notificationTypeCode = notificationTypeCode;
	}

	public void setOriginalNotificationId(Long originalNotificationId) {
		this.originalNotificationId = originalNotificationId;
	}

	public void setQuestionForReviewerId(Long questionForReviewerId) {
		this.questionForReviewerId = questionForReviewerId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
