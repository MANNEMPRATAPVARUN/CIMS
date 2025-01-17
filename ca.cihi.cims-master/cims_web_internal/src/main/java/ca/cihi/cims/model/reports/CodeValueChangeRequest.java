package ca.cihi.cims.model.reports;

import java.io.Serializable;

public class CodeValueChangeRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8246110514237822498L;
	private String codeValue;
	private Long changeRequestId;
	private String changeRequestName;

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public String getChangeRequestName() {
		return changeRequestName;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setChangeRequestName(String changeRequestName) {
		this.changeRequestName = changeRequestName;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
}
