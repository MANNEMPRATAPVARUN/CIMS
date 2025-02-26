package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;

public class RefsetBaseBean implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5631784007248372439L;
	private Long contextId;
	private Long elementId;
	private Long elementVersionId;
	private String actionType;
	private String categoryName;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

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

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

}
