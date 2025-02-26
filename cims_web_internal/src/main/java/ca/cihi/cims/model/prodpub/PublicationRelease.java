package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import ca.cihi.cims.model.User;

public class PublicationRelease implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long releaseId;
	private String fiscalYear;
	private String releaseNote;
	private Integer versionCodeNumber;
	private Long createdByUserId;
	private Date createdDate;
	private ReleaseType releaseType;
	private GenerateFileStatus status;
	private String failedReason;
	private boolean notificationSent;
	private AsotStatus asotStatus;

	private List<PublicationSnapShot> publicationSnapShots;
	private User releasedBy;

	public AsotStatus getAsotStatus() {
		return asotStatus;
	}

	public Long getCreatedByUserId() {
		return createdByUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public String getFiscalYear() {
		return fiscalYear;
	}

	public ReleaseType getNextReleaseType() {
		ReleaseType nextReleaseType = null;
		if (ReleaseType.PRELIMINARY_INTERNAL_QA == releaseType) {
			nextReleaseType = ReleaseType.PRELIMINARY;
		} else {
			nextReleaseType = ReleaseType.OFFICIAL;
		}

		return nextReleaseType;
	}

	public List<PublicationSnapShot> getPublicationSnapShots() {
		return publicationSnapShots;
	}

	public User getReleasedBy() {
		return releasedBy;
	}

	public String getReleaseFileName() {
		String releaseFileName = null;
		StringBuilder sb_releaseFileName = new StringBuilder();
		sb_releaseFileName.append(releaseType.getReleaseTypeCode()).append("_");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sb_releaseFileName.append(sdf.format(getCreatedDate()));
		if (getReleaseType().compareTo(ReleaseType.PRELIMINARY) == 0) {
			sb_releaseFileName.append("_");
			sb_releaseFileName.append("V0.");
			sb_releaseFileName.append(getVersionCodeNumber());
		}
		if (getReleaseType().compareTo(ReleaseType.OFFICIAL) == 0) {
			sb_releaseFileName.append("_");
			sb_releaseFileName.append("V1.");
			sb_releaseFileName.append(getVersionCodeNumber());
		}
		sb_releaseFileName.append(".zip");
		releaseFileName = sb_releaseFileName.toString();
		return releaseFileName;
	}

	public Long getReleaseId() {
		return releaseId;
	}

	public String getReleaseNote() {
		return releaseNote;
	}

	public String getReleaseNum() {
		String releaseNum = null;
		if (getReleaseType().compareTo(ReleaseType.PRELIMINARY_INTERNAL_QA) == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd");
			releaseNum = sdf.format(getCreatedDate());
		}
		if (getReleaseType().compareTo(ReleaseType.PRELIMINARY) == 0) {
			StringBuilder sb_preliminaryRelease = new StringBuilder();
			sb_preliminaryRelease.append(getFiscalYear()).append("_");
			sb_preliminaryRelease.append("V0.");
			sb_preliminaryRelease.append(getVersionCodeNumber());
			releaseNum = sb_preliminaryRelease.toString();
		}
		if (getReleaseType().compareTo(ReleaseType.OFFICIAL_INTERNAL_QA) == 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd");
			releaseNum = sdf.format(getCreatedDate());
		}
		if (getReleaseType().compareTo(ReleaseType.OFFICIAL) == 0) {

			StringBuilder sb_officialRelease = new StringBuilder();
			sb_officialRelease.append(getFiscalYear()).append("_");
			sb_officialRelease.append("V1.");
			sb_officialRelease.append(getVersionCodeNumber());
			releaseNum = sb_officialRelease.toString();
		}
		return releaseNum;
	}

	public ReleaseType getReleaseType() {
		return releaseType;
	}

	public GenerateFileStatus getStatus() {
		return status;
	}

	public Integer getVersionCodeNumber() {
		return versionCodeNumber;
	}

	public boolean isNotificationSent() {
		return notificationSent;
	}

	public void setAsotStatus(AsotStatus asotStatus) {
		this.asotStatus = asotStatus;
	}

	public void setCreatedByUserId(Long createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public void setNotificationSent(boolean notificationSent) {
		this.notificationSent = notificationSent;
	}

	public void setPublicationSnapShots(List<PublicationSnapShot> publicationSnapShots) {
		this.publicationSnapShots = publicationSnapShots;
	}

	public void setReleasedBy(User releasedBy) {
		this.releasedBy = releasedBy;
	}

	public void setReleaseId(Long releaseId) {
		this.releaseId = releaseId;
	}

	public void setReleaseNote(String releaseNote) {
		this.releaseNote = releaseNote;
	}

	public void setReleaseType(ReleaseType releaseType) {
		this.releaseType = releaseType;
	}

	public void setStatus(GenerateFileStatus status) {
		this.status = status;
	}

	public void setVersionCodeNumber(Integer versionCodeNumber) {
		this.versionCodeNumber = versionCodeNumber;
	}

}
