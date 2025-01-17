package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;

public class Advice implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long adviceId;
	private Long changeRequestId; // FK to change request table
	private String subject;
	//@Size(max = 150, message = "message should be less then 150 characters")
	private String message;
	private Long distributionListId; // FK to Distribution_LIST table , get advice from DL
	private Long userProfileId; // FK to user_profile, get advice from a user
	private Long senderId ;
	private User sender;
	private User userAdvisor;
	private Distribution dlAdvisor;

	private Date lastUpdatedTime;

	private List<UserComment> adviceComments;

	public List<UserComment> getAdviceComments() {
		return adviceComments;
	}

	public Long getAdviceId() {
		return adviceId;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public Long getDistributionListId() {
		return distributionListId;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public String getMessage() {
		return message;
	}

	public String getSubject() {
		return subject;
	}

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setAdviceComments(List<UserComment> adviceComments) {
		this.adviceComments = adviceComments;
	}

	public void setAdviceId(Long adviceId) {
		this.adviceId = adviceId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setDistributionListId(Long distributionListId) {
		this.distributionListId = distributionListId;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public Long getSenderId() {
		return senderId;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getUserAdvisor() {
		return userAdvisor;
	}

	public void setUserAdvisor(User userAdvisor) {
		this.userAdvisor = userAdvisor;
	}

	public Distribution getDlAdvisor() {
		return dlAdvisor;
	}

	public void setDlAdvisor(Distribution dlAdvisor) {
		this.dlAdvisor = dlAdvisor;
	}

}
