package ca.cihi.cims.model.reports;

import java.io.Serializable;

public class ModifiedValidCodeModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4791037366222233781L;
	private String codeValue;
	private String currentFlag;
	private String priorFlag;
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	public String getCurrentFlag() {
		return currentFlag;
	}
	public void setCurrentFlag(String currentFlag) {
		this.currentFlag = currentFlag;
	}
	public String getPriorFlag() {
		return priorFlag;
	}
	public void setPriorFlag(String priorFlag) {
		this.priorFlag = priorFlag;
	}

}
