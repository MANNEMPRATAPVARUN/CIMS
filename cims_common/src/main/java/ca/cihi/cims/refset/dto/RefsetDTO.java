package ca.cihi.cims.refset.dto;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.service.concept.RefsetVersion;

/**
 *
 * @author lzhu
 * @version 1.0
 * @created 12-Jun-2016 2:29:30 PM
 *
 */
public class RefsetDTO extends ElementDTO implements RefsetVersion {

	/**
	 *
	 */
	private static final long serialVersionUID = -1526438062107500847L;
	private String assigneeName;	
	private Long assigneeId;
	private String categoryName;
	private String refsetName;
	private ElementIdentifier contextIdentifier;
	private RefsetStatus refsetStatus;
    //private String refsetVersionName;
    public String versionCode;	
	public String refsetCode;	
	public Short effectiveYearFrom;	
	public Short effectiveYearTo;

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getRefsetCode() {
		return refsetCode;
	}

	public void setRefsetCode(String refsetCode) {
		this.refsetCode = refsetCode;
	}

	public Short getEffectiveYearFrom() {
		return effectiveYearFrom;
	}

	public void setEffectiveYearFrom(Short effectiveYearFrom) {
		this.effectiveYearFrom = effectiveYearFrom;
	}

	public Short getEffectiveYearTo() {
		return effectiveYearTo;
	}

	public void setEffectiveYearTo(Short effectiveYearTo) {
		this.effectiveYearTo = effectiveYearTo;
	}
	
	public Long getAssigneeId() {
		return assigneeId;
	}

	public void setAssigneeId(Long assigneeId) {
		this.assigneeId = assigneeId;
	}

	@Override
	public String getCategoryName() {
		return categoryName;
	}
	
	@Override
	public ElementIdentifier getContextIdentifier() {
		return contextIdentifier;
	}
	
	public void setContextIdentifier(ElementIdentifier contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}
	
	@Override
	public ElementIdentifier getRefsetIdentifier() {
		return this.getElementIdentifier();
	}

	@Override
	public RefsetStatus getRefsetStatus() {
		return refsetStatus;
	}	

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public void setRefsetStatus(RefsetStatus refsetStatus) {
		this.refsetStatus = refsetStatus;
	}
	
	public String getAssigneeName() {
		return assigneeName;
	}

	public void setAssigneeName(String assigneeName) {
		this.assigneeName = assigneeName;
	}

	public String getRefsetName() {
		return refsetName;
	}

	public void setRefsetName(String refsetName) {
		this.refsetName = refsetName;
	}

	/*
	public String getRefsetVersionName() {
		return refsetVersionName;
	}

	public void setRefsetVersionName(String refsetVersionName) {
		this.refsetVersionName = refsetVersionName;
	}
	*/
	
}
