package ca.cihi.cims.dal;

import java.sql.Date;

import org.apache.commons.lang.builder.ToStringBuilder;

import ca.cihi.cims.dal.annotations.RequiredForUpdate;

public abstract class ElementVersion {

	private long elementId;
	private Long elementVersionId;
	private String className;
	private String businessKey;
	private Long changedFromVersionId;
	private Long originatingContextId;	

	@RequiredForUpdate
	private String status;

	private long classId;
	private String versionCode;
	private Date versionTimeStamp;

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public Date getVersionTimeStamp() {
		return versionTimeStamp;
	}

	public void setVersionTimeStamp(Date versionTimeStamp) {
		this.versionTimeStamp = versionTimeStamp;
	}

	public long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public long getElementId() {
		return elementId;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	/**
	 * This should NOT be used outside the DAL, as this is an internal matter.
	 */
	public Long getElementVersionId() {
		return elementVersionId;
	}

	public void setElementVersionId(Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBusinessKey() {
		return businessKey;
	}

	public void setBusinessKey(String businessKey) {
		this.businessKey = businessKey;
	}

	public Long getChangedFromVersionId() {
		return changedFromVersionId;
	}

	public void setChangedFromVersionId(Long changedFromVersionId) {
		this.changedFromVersionId = changedFromVersionId;
	}

	public Long getOriginatingContextId() {
		return originatingContextId;
	}

	public void setOriginatingContextId(Long originatingContextId) {
		this.originatingContextId = originatingContextId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
//	@Override
//    public int hashCode() { 
//    	return getBusinessKey().hashCode(); 
//    }
//    
//	@Override
//    public boolean equals(Object o) {
//        ElementVersion other = (ElementVersion) o;
//        return other.getBusinessKey().equals(getBusinessKey());
//    }
    	
	
}
