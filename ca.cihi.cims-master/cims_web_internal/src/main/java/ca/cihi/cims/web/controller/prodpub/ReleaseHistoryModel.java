package ca.cihi.cims.web.controller.prodpub;

import java.io.Serializable;
import java.util.Date;

public class ReleaseHistoryModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String versionYear;
	private Date icdSnapShotDate;
	private Date cciSnapShotDate;
	private String preliminaryInternalQARelease;
	private Long preliminaryInternalQAReleaseId;
	private String preliminaryRelease;
	private Long preliminaryReleaseId;
	private String officialInternalQARelease;
	private Long officialInternalQAReleaseId;
	private String officialRelease;
	private Long officialReleaseId;

	private boolean emailPreliminaryInternalQARelease;
	private boolean emailPreliminaryRelease;
	private boolean emailOfficialInternalQARelease;
	private boolean emailOfficialRelease;

	public Date getCciSnapShotDate() {
		return cciSnapShotDate;
	}

	public Date getIcdSnapShotDate() {
		return icdSnapShotDate;
	}

	public String getOfficialInternalQARelease() {
		return officialInternalQARelease;
	}

	public Long getOfficialInternalQAReleaseId() {
		return officialInternalQAReleaseId;
	}

	public String getOfficialRelease() {
		return officialRelease;
	}

	public Long getOfficialReleaseId() {
		return officialReleaseId;
	}

	public String getPreliminaryInternalQARelease() {
		return preliminaryInternalQARelease;
	}

	public Long getPreliminaryInternalQAReleaseId() {
		return preliminaryInternalQAReleaseId;
	}

	public String getPreliminaryRelease() {
		return preliminaryRelease;
	}

	public Long getPreliminaryReleaseId() {
		return preliminaryReleaseId;
	}

	public String getVersionYear() {
		return versionYear;
	}

	public boolean isEmailOfficialInternalQARelease() {
		return emailOfficialInternalQARelease;
	}

	public boolean isEmailOfficialRelease() {
		return emailOfficialRelease;
	}

	public boolean isEmailPreliminaryInternalQARelease() {
		return emailPreliminaryInternalQARelease;
	}

	public boolean isEmailPreliminaryRelease() {
		return emailPreliminaryRelease;
	}

	public void setCciSnapShotDate(Date cciSnapShotDate) {
		this.cciSnapShotDate = cciSnapShotDate;
	}

	public void setEmailOfficialInternalQARelease(boolean emailOfficialInternalQARelease) {
		this.emailOfficialInternalQARelease = emailOfficialInternalQARelease;
	}

	public void setEmailOfficialRelease(boolean emailOfficialRelease) {
		this.emailOfficialRelease = emailOfficialRelease;
	}

	public void setEmailPreliminaryInternalQARelease(boolean emailPreliminaryInternalQARelease) {
		this.emailPreliminaryInternalQARelease = emailPreliminaryInternalQARelease;
	}

	public void setEmailPreliminaryRelease(boolean emailPreliminaryRelease) {
		this.emailPreliminaryRelease = emailPreliminaryRelease;
	}

	public void setIcdSnapShotDate(Date icdSnapShotDate) {
		this.icdSnapShotDate = icdSnapShotDate;
	}

	public void setOfficialInternalQARelease(String officialInternalQARelease) {
		this.officialInternalQARelease = officialInternalQARelease;
	}

	public void setOfficialInternalQAReleaseId(Long officialInternalQAReleaseId) {
		this.officialInternalQAReleaseId = officialInternalQAReleaseId;
	}

	public void setOfficialRelease(String officialRelease) {
		this.officialRelease = officialRelease;
	}

	public void setOfficialReleaseId(Long officialReleaseId) {
		this.officialReleaseId = officialReleaseId;
	}

	public void setPreliminaryInternalQARelease(String preliminaryInternalQARelease) {
		this.preliminaryInternalQARelease = preliminaryInternalQARelease;
	}

	public void setPreliminaryInternalQAReleaseId(Long preliminaryInternalQAReleaseId) {
		this.preliminaryInternalQAReleaseId = preliminaryInternalQAReleaseId;
	}

	public void setPreliminaryRelease(String preliminaryRelease) {
		this.preliminaryRelease = preliminaryRelease;
	}

	public void setPreliminaryReleaseId(Long preliminaryReleaseId) {
		this.preliminaryReleaseId = preliminaryReleaseId;
	}

	public void setVersionYear(String versionYear) {
		this.versionYear = versionYear;
	}

}
