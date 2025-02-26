package ca.cihi.cims.dal;

import java.sql.Date;

public class StructureVersion extends ElementVersion {

	private long structureId;
	private long classId;
	private Long baseStructureId;
	private String contextStatus;
	private Date contextStatusDate;
	private Boolean isVersionYear;
	private Long changeRequestId;
	private Long freezingStatusId;

	// -----------------------------------------------

	public Long getBaseStructureId() {
		return baseStructureId;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	@Override
	public long getClassId() {
		return classId;
	}

	public String getContextStatus() {
		return contextStatus;
	}

	public Date getContextStatusDate() {
		return contextStatusDate;
	}

	public long getStructureId() {
		return structureId;
	}

	public Boolean isVersionYear() {
		return isVersionYear;
	}

	public void setBaseStructureId(Long baseStructureId) {
		this.baseStructureId = baseStructureId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setClassId(long classId) {
		this.classId = classId;
	}

	public void setContextStatus(String contextStatus) {
		this.contextStatus = contextStatus;
	}

	public void setContextStatusDate(Date contextStatusDate) {
		this.contextStatusDate = contextStatusDate;
	}

	public void setIsVersionYear(Boolean isVersionYear) {
		this.isVersionYear = isVersionYear;
	}

	public void setStructureId(long structureId) {
		this.structureId = structureId;
	}

	public Long getFreezingStatusId() {
		return freezingStatusId;
	}

	public void setFreezingStatusId(Long freezingStatusId) {
		this.freezingStatusId = freezingStatusId;
	}

}
