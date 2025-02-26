package ca.cihi.cims.web.bean.refset;

public class PickListOutputConfigBean extends RefsetLightBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long picklistElementId;
	private Long picklistElementVersionId;
	
	public Long getPicklistElementId() {
		return picklistElementId;
	}
	public void setPicklistElementId(Long picklistElementId) {
		this.picklistElementId = picklistElementId;
	}
	public Long getPicklistElementVersionId() {
		return picklistElementVersionId;
	}
	public void setPicklistElementVersionId(Long picklistElementVersionId) {
		this.picklistElementVersionId = picklistElementVersionId;
	}
	
}
