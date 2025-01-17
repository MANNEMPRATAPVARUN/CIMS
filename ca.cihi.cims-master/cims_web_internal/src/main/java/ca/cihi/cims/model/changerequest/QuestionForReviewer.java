package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.notification.Notification;

public class QuestionForReviewer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long questionForReviewerId; // PK
	private Long changeRequestId;
	private Integer questionNum;

	private String questionForReviewerTxt;
	private Long reviewerId; // distribution list Id
	private Date lastUpdatedTime;
	private Notification sentOutNotification;  // if a question has been sent out, one notification will be created

	private Distribution reviewer;
	public Distribution getReviewer() {
		return reviewer;
	}

	public void setReviewer(Distribution reviewer) {
		this.reviewer = reviewer;
	}

	private boolean beenSentOut;

	private List<UserComment> questionComments;

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public List<UserComment> getQuestionComments() {
		return questionComments;
	}

	public Long getQuestionForReviewerId() {
		return questionForReviewerId;
	}

	public String getQuestionForReviewerTxt() {
		return questionForReviewerTxt;
	}

	public Integer getQuestionNum() {
		return questionNum;
	}

	public Long getReviewerId() {
		return reviewerId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public void setQuestionComments(List<UserComment> questionComments) {
		this.questionComments = questionComments;
	}

	public void setQuestionForReviewerId(Long questionForReviewerId) {
		this.questionForReviewerId = questionForReviewerId;
	}

	public void setQuestionForReviewerTxt(String questionForReviewerTxt) {
		this.questionForReviewerTxt = questionForReviewerTxt;
	}

	public void setQuestionNum(Integer questionNum) {
		this.questionNum = questionNum;
	}

	public void setReviewerId(Long reviewerId) {
		this.reviewerId = reviewerId;
	}

	public Notification getSentOutNotification() {
		return sentOutNotification;
	}

	public void setSentOutNotification(Notification sentOutNotification) {
		this.sentOutNotification = sentOutNotification;
	}



	public void setBeenSentOut(boolean beenSentOut) {
		this.beenSentOut = beenSentOut;
	}

	public boolean isBeenSentOut() {
		return beenSentOut;
	}

}
