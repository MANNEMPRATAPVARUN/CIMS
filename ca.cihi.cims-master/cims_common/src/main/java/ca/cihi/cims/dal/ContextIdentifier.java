package ca.cihi.cims.dal;

import java.util.Date;

import ca.cihi.cims.ContextStatus;
import ca.cihi.cims.FreezingStatus;

/**
 * The DAL encapsulates the identification for a particular context (a version, a change request) in this class.
 */
public class ContextIdentifier {

	private long contextId;
	private String baseClassification;
	private String versionCode;
	private Long baseStructureId;
	private String contextStatus;
	private Date contextStatusDate;
	private Boolean isVersionYear;
	private Long requestId;
	private Long freezingStatusId;

	private FreezingStatus freezingStatus;

	// ------------------------------------------------------------------

	public ContextIdentifier() {
	}

	public ContextIdentifier(long contextId, String versionCode, String baseClassfication, Long baseStructureId,
			String contextStatus, Date contextStatusDate, Boolean isVersionYear, Long requestId, Long freezingStatusId) {
		this.requestId = requestId;
		this.contextId = contextId;
		this.versionCode = versionCode;
		this.baseClassification = baseClassfication;
		this.baseStructureId = baseStructureId;
		this.contextStatus = contextStatus;
		this.contextStatusDate = contextStatusDate;
		this.isVersionYear = isVersionYear;
		this.freezingStatusId = freezingStatusId;
	}

	public void closeContext() {
		setContextStatus(ContextStatus.CLOSED.name());
		setContextStatusDate(new Date());
	}

	public String getBaseClassification() {
		return baseClassification;
	}

	public Long getBaseStructureId() {
		return baseStructureId;
	}

	public long getContextId() {
		return contextId;
	}

	public String getContextStatus() {
		return contextStatus;
	}

	public Date getContextStatusDate() {
		return contextStatusDate;
	}

	public FreezingStatus getFreezingStatus() {
		return freezingStatus;
	}

	public Boolean getIsVersionYear() {
		return isVersionYear;
	}

	public Long getRequestId() {
		return requestId;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public boolean isChangeContext() {
		return baseStructureId != null;
	}

	public boolean isContextOpen() {
		return contextStatus.equalsIgnoreCase(ContextStatus.OPEN.name());
	}

	public Boolean isVersionYear() {
		return isVersionYear;
	}

	public void setBaseClassification(String baseClassification) {
		this.baseClassification = baseClassification;
	}

	public void setBaseStructureId(Long baseStructureId) {
		this.baseStructureId = baseStructureId;
	}

	public void setContextId(long contextId) {
		this.contextId = contextId;
	}

	public void setContextStatus(String contextStatus) {
		this.contextStatus = contextStatus;
	}

	public void setContextStatusDate(Date contextStatusDate) {
		this.contextStatusDate = contextStatusDate;
	}

	public void setFreezingStatus(FreezingStatus freezingStatus) {
		this.freezingStatus = freezingStatus;
	}

	public void setIsVersionYear(Boolean isVersionYear) {
		this.isVersionYear = isVersionYear;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public String toString() {
		return String.format("Context[contextId=%s,classification=%s,versionCode=%s]",
				(baseStructureId == null ? contextId : contextId + " extending " + baseStructureId),
				baseClassification, versionCode);
	}

	public Long getFreezingStatusId() {
		return freezingStatusId;
	}

	public void setFreezingStatusId(Long freezingStatusId) {
		this.freezingStatusId = freezingStatusId;
	}

}
