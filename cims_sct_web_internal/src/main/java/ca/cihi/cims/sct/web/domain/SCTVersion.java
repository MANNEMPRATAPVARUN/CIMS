package ca.cihi.cims.sct.web.domain;

import java.io.Serializable;

public class SCTVersion implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String versionCode;
	private String versionDesc;
	private String effectiveDate;
	private String statusCode;
	private String version;
	
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionDesc() {
		return versionDesc;
	}
	public void setVersionDesc(String versionDesc) {
		this.versionDesc = versionDesc;
	}
	public String getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	
   
}
