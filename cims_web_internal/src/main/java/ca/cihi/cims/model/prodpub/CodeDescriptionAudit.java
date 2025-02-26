package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

public class CodeDescriptionAudit implements Serializable, Comparable<CodeDescriptionAudit> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;
	private String currentDesc;
	private String previousDesc;

	public CodeDescriptionAudit(String code, String currentDesc, String previousDesc) {
		this.code = code;
		this.currentDesc = currentDesc;
		this.previousDesc = previousDesc;
	}

	@Override
	public int compareTo(CodeDescriptionAudit other) {

		return this.code.compareTo(other.getCode());
	}

	public String getCode() {
		return code;
	}

	public String getCurrentDesc() {
		return currentDesc;
	}

	public String getPreviousDesc() {
		return previousDesc;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCurrentDesc(String currentDesc) {
		this.currentDesc = currentDesc;
	}

	public void setPreviousDesc(String previousDesc) {
		this.previousDesc = previousDesc;
	}

}
