package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;

public class RefsetLightBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long contextId;
	private Long elementId;
	private Long elementVersionId;
	
	public Long getContextId() {
		return contextId;
	}
	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}
	public Long getElementId() {
		return elementId;
	}
	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}
	public Long getElementVersionId() {
		return elementVersionId;
	}
	public void setElementVersionId(Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

}
