package ca.cihi.cims.dao.bean;

import java.io.Serializable;

public class AsotETLLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4730922814305510616L;
	private Long asotETLLogId;
	private String asotETLLog;
	private String startDate;
	private Long publicationReleaseId;
	private String asotETLLogStatusCode;
	private String asotETLLogTypeCode;

	public String getAsotETLLog() {
		return asotETLLog;
	}

	public Long getAsotETLLogId() {
		return asotETLLogId;
	}

	public String getAsotETLLogStatusCode() {
		return asotETLLogStatusCode;
	}

	public String getAsotETLLogTypeCode() {
		return asotETLLogTypeCode;
	}

	public Long getPublicationReleaseId() {
		return publicationReleaseId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setAsotETLLog(String asotETLLog) {
		this.asotETLLog = asotETLLog;
	}

	public void setAsotETLLogId(Long asotETLLogId) {
		this.asotETLLogId = asotETLLogId;
	}

	public void setAsotETLLogStatusCode(String asotETLLogStatusCode) {
		this.asotETLLogStatusCode = asotETLLogStatusCode;
	}

	public void setAsotETLLogTypeCode(String asotETLLogTypeCode) {
		this.asotETLLogTypeCode = asotETLLogTypeCode;
	}

	public void setPublicationReleaseId(Long publicationReleaseId) {
		this.publicationReleaseId = publicationReleaseId;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}
