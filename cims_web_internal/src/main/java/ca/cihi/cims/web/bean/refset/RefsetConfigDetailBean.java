package ca.cihi.cims.web.bean.refset;

import ca.cihi.cims.model.refset.ActionType;

/**
 * @author lzhu
 */
public class RefsetConfigDetailBean extends RefsetLightBean {

	private static final long serialVersionUID = 1L;

	private String refsetCode;
	private String refsetNameENG;
	private String refsetNameFRE;

	private Long categoryId;
	private String categoryName;
	private Integer effectiveYearFrom;
	private Integer effectiveYearTo;
	private String ICD10CAYear;
	private String CCIYear;
	private String SCTVersionCode;
	private String SCTVersionDesc;
	// private SCTVersion sctVersion;
	private String definition;
	private String notes;

	private String assignee;
	private String status;
	private String versionCode;
	private String versionStatus;
	private String versionName;

	private String refsetVersionName;
	private boolean readOnly;
	private boolean adminRole;
	private boolean refsetDeveloperRole;
	private boolean checkAssignee;
	private ActionType actionType;
	private String newAssignee;
	private String displayAssignee;
	private String ICD10CAContextInfo;
	private String versionType;

	private String oldSCTVersionCode;
	private String oldCCIYear;
	private String oldICD10CAYear;
	private Integer oldEffectiveYearFrom;

	public Integer getOldEffectiveYearFrom() {
		return oldEffectiveYearFrom;
	}

	public void setOldEffectiveYearFrom(Integer oldEffectiveYearFrom) {
		this.oldEffectiveYearFrom = oldEffectiveYearFrom;
	}

	private boolean latestClosedVersion;

	public boolean isLatestClosedVersion() {
		return latestClosedVersion;
	}

	public void setLatestClosedVersion(boolean latestClosedVersion) {
		this.latestClosedVersion = latestClosedVersion;
	}

	private boolean openVersionExists;

	public boolean isOpenVersionExists() {
		return openVersionExists;
	}

	public void setOpenVersionExists(boolean openVersionExists) {
		this.openVersionExists = openVersionExists;
	}

	public String getOldSCTVersionCode() {
		return oldSCTVersionCode;
	}

	public void setOldSCTVersionCode(String oldSCTVersionCode) {
		this.oldSCTVersionCode = oldSCTVersionCode;
	}

	public String getOldCCIYear() {
		return oldCCIYear;
	}

	public void setOldCCIYear(String oldCCIYear) {
		this.oldCCIYear = oldCCIYear;
	}

	public String getOldICD10CAYear() {
		return oldICD10CAYear;
	}

	public void setOldICD10CAYear(String oldICD10CAYear) {
		this.oldICD10CAYear = oldICD10CAYear;
	}

	public String getVersionType() {
		return versionType;
	}

	public void setVersionType(String versionType) {
		this.versionType = versionType;
	}

	public boolean getCheckAssignee() {
		return checkAssignee;
	}

	public void setCheckAssignee(boolean checkAssignee) {
		this.checkAssignee = checkAssignee;
	}

	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
	}

	public String getICD10CAContextInfo() {
		return ICD10CAContextInfo;
	}

	public void setICD10CAContextInfo(String iCD10CAContextInfo) {
		ICD10CAContextInfo = iCD10CAContextInfo;
	}

	public String getCCIContextInfo() {
		return CCIContextInfo;
	}

	public void setCCIContextInfo(String cCIContextInfo) {
		CCIContextInfo = cCIContextInfo;
	}

	private String CCIContextInfo;

	public String getDisplayAssignee() {
		return displayAssignee;
	}

	public void setDisplayAssignee(String displayAssignee) {
		this.displayAssignee = displayAssignee;
	}

	private boolean pickListContent;

	public boolean getPickListContent() {
		return pickListContent;
	}

	public void setPickListContent(boolean pickListContent) {
		this.pickListContent = pickListContent;
	}

	public String getNewAssignee() {
		return newAssignee;
	}

	public void setNewAssignee(String newAssignee) {
		this.newAssignee = newAssignee;
	}

	public boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean getAdminRole() {
		return adminRole;
	}

	public void setAdminRole(boolean adminRole) {
		this.adminRole = adminRole;
	}

	public String getRefsetVersionName() {
		return refsetVersionName;
	}

	public void setRefsetVersionName(String refsetVersionName) {
		this.refsetVersionName = refsetVersionName;
	}

	/*
	 * public Long getContextId() { return contextId; } public void setContextId(Long contextId) { this.contextId =
	 * contextId; } public Long getElementId() { return elementId; } public void setElementId(Long elementId) {
	 * this.elementId = elementId; } public Long getElementVersionId() { return elementVersionId; } public void
	 * setElementVersionId(Long elementVersionId) { this.elementVersionId = elementVersionId; }
	 */
	public String getRefsetCode() {
		return refsetCode;
	}

	public void setRefsetCode(String refsetCode) {
		this.refsetCode = refsetCode;
	}

	public String getRefsetNameENG() {
		return refsetNameENG;
	}

	public void setRefsetNameENG(String refsetNameENG) {
		this.refsetNameENG = refsetNameENG;
	}

	public String getRefsetNameFRE() {
		return refsetNameFRE;
	}

	public void setRefsetNameFRE(String refsetNameFRE) {
		this.refsetNameFRE = refsetNameFRE;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getEffectiveYearFrom() {
		return effectiveYearFrom;
	}

	public void setEffectiveYearFrom(Integer effectiveYearFrom) {
		this.effectiveYearFrom = effectiveYearFrom;
	}

	public Integer getEffectiveYearTo() {
		return effectiveYearTo;
	}

	public void setEffectiveYearTo(Integer effectiveYearTo) {
		this.effectiveYearTo = effectiveYearTo;
	}

	public String getICD10CAYear() {
		return ICD10CAYear;
	}

	public void setICD10CAYear(String iCD10CAYear) {
		ICD10CAYear = iCD10CAYear;
	}

	public String getCCIYear() {
		return CCIYear;
	}

	public void setCCIYear(String cCIYear) {
		CCIYear = cCIYear;
	}

	public String getSCTVersionCode() {
		return SCTVersionCode;
	}

	public void setSCTVersionCode(String sCTVersionCode) {
		SCTVersionCode = sCTVersionCode;
	}

	public String getDefinition() {
		return definition;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getAssignee() {
		return assignee;
	}

	public void setAssignee(String assignee) {
		this.assignee = assignee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionStatus() {
		return versionStatus;
	}

	public void setVersionStatus(String versionStatus) {
		this.versionStatus = versionStatus;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getSCTVersionDesc() {
		return SCTVersionDesc;
	}

	public void setSCTVersionDesc(String sCTVersionDesc) {
		SCTVersionDesc = sCTVersionDesc;
	}

	public boolean getRefsetDeveloperRole() {
		return refsetDeveloperRole;
	}

	public void setRefsetDeveloperRole(boolean refsetDeveloperRole) {
		this.refsetDeveloperRole = refsetDeveloperRole;
	}

}
