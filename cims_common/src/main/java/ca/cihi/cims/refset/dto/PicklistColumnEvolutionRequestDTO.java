package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class PicklistColumnEvolutionRequestDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long refsetContextId;
	private Long baseRefsetContextId;
	private Long picklistElementId;
	private Long cciContextId;
	private Long icd10caContextId;
	private Long picklistOutputId;
	private String versionCode;
	private String baseVersionCode;
	
	public String getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	public String getBaseVersionCode() {
		return baseVersionCode;
	}
	public void setBaseVersionCode(String baseVersionCode) {
		this.baseVersionCode = baseVersionCode;
	}
	public Long getPicklistOutputId() {
		return picklistOutputId;
	}
	public void setPicklistOutputId(Long picklistOutputId) {
		this.picklistOutputId = picklistOutputId;
	}
	public Long getRefsetContextId() {
		return refsetContextId;
	}
	public void setRefsetContextId(Long refsetContextId) {
		this.refsetContextId = refsetContextId;
	}
	public Long getBaseRefsetContextId() {
		return baseRefsetContextId;
	}
	public void setBaseRefsetContextId(Long baseRefsetContextId) {
		this.baseRefsetContextId = baseRefsetContextId;
	}
	public Long getPicklistElementId() {
		return picklistElementId;
	}
	public void setPicklistElementId(Long picklistElementId) {
		this.picklistElementId = picklistElementId;
	}
	public Long getCciContextId() {
		return cciContextId;
	}
	public void setCciContextId(Long cciContextId) {
		this.cciContextId = cciContextId;
	}
	public Long getIcd10caContextId() {
		return icd10caContextId;
	}
	public void setIcd10caContextId(Long icd10caContextId) {
		this.icd10caContextId = icd10caContextId;
	}
	
	
}
