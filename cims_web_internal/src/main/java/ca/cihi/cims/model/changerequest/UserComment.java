package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.Date;

import ca.cihi.cims.model.User;

public class UserComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long userCommentId;
	//@Size(max = 4000, message = "comments can not be over 4000 characters")
	private String userCommentTxt;

	private Long changeRequestId;
	// private Long notificationId;

	private Long reviewerQuestionId; // FK REVIEWER_QUESTION_ID
	private Long adviceId; // FK to ADVICE
	private Long userProfileId;
	private User commmentUser;

	private Date lastUpdatedTime;

	private UserCommentType commentType;

	public Long getAdviceId() {
		return adviceId;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public UserCommentType getCommentType() {
		return commentType;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public Long getReviewerQuestionId() {
		return reviewerQuestionId;
	}

	public Long getUserCommentId() {
		return userCommentId;
	}

	public String getUserCommentTxt() {
		return userCommentTxt;
	}

	public Long getUserProfileId() {
		return userProfileId;
	}

	public void setAdviceId(Long adviceId) {
		this.adviceId = adviceId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setCommentType(UserCommentType commentType) {
		this.commentType = commentType;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	public void setReviewerQuestionId(Long reviewerQuestionId) {
		this.reviewerQuestionId = reviewerQuestionId;
	}

	public void setUserCommentId(Long userCommentId) {
		this.userCommentId = userCommentId;
	}

	public void setUserCommentTxt(String userCommentTxt) {
		this.userCommentTxt = userCommentTxt;
	}

	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public User getCommmentUser() {
		return commmentUser;
	}

	public void setCommmentUser(User commmentUser) {
		this.commmentUser = commmentUser;
	}

}
