package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;

public class LegacyRequestResultsModel implements Serializable {
	private static final long serialVersionUID = 201410241050L;

	protected Long requestId;

	protected String requestName;

	protected String versionCode;  // version year

	protected String classificationTitleCode;

	protected String language;

	protected String requestStatus;

	//protected String sectionCode;
	protected String sectionDesc;

	protected String changeNature;

	protected String changeType;

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getClassificationTitleCode() {
		return classificationTitleCode;
	}

	public void setClassificationTitleCode(String classificationTitleCode) {
		this.classificationTitleCode = classificationTitleCode;
	}
	
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}

/*	
	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
*/
	public String getSectionDesc() {
		return sectionDesc;
	}

	public void setSectionDesc(String sectionDesc) {
		this.sectionDesc = sectionDesc;
	}
	
	public String getChangeNature() {
		return changeNature;
	}

	public void setChangeNature(String changeNature) {
		this.changeNature = changeNature;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

}
