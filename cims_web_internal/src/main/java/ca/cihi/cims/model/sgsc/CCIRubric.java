package ca.cihi.cims.model.sgsc;

import java.io.Serializable;

public class CCIRubric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6657649577958171199L;
	private String rubricCode;
	private String interventionCode;
	private String groupCode;
	private Long elementId;
	private String containingPath;

	public String getContainingPath() {
		return containingPath;
	}

	public void setContainingPath(String containingPath) {
		this.containingPath = containingPath;
	}

	public Long getElementId() {
		return elementId;
	}

	public String getGroupCode() {
		return groupCode;
	}

	public String getInterventionCode() {
		return interventionCode;
	}

	public String getRubricCode() {
		return rubricCode;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setGroupCode(String groupCode) {
		this.groupCode = groupCode;
	}

	public void setInterventionCode(String interventionCode) {
		this.interventionCode = interventionCode;
	}

	public void setRubricCode(String rubricCode) {
		this.rubricCode = rubricCode;
	}

	@Override
	public String toString() {
		return "CCIRubric [rubricCode=" + rubricCode + ", interventionCode=" + interventionCode + ", groupCode="
				+ groupCode + ", elementId=" + elementId + "]";
	}
}
