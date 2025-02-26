package ca.cihi.cims.model.reports;

import java.io.Serializable;
import java.util.Date;

public class ChangeRequestSendBack implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4228503010809301240L;
	private String changeRequestId;
	private String classification;
	private Date sendBackDate;
	private String language;
	private String owner;
	private String fromStatus;
	private String sendBackReason;

	public String getChangeRequestId() {
		return changeRequestId;
	}

	public String getClassification() {
		return classification;
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public String getLanguage() {
		return language;
	}

	public String getOwner() {
		return owner;
	}

	public Date getSendBackDate() {
		return sendBackDate;
	}

	public String getSendBackReason() {
		return sendBackReason;
	}

	public void setChangeRequestId(String changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setSendBackDate(Date sendBackDate) {
		this.sendBackDate = sendBackDate;
	}

	public void setSendBackReason(String sendBackReason) {
		this.sendBackReason = sendBackReason;
	}
}
