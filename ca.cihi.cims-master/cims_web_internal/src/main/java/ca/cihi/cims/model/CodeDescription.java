package ca.cihi.cims.model;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

/**
 * 
 * @author wxing
 * 
 */
public class CodeDescription extends BaseSerializableCloneableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	private String description;
	private String note;

	// -----------------------------------------------------

	public CodeDescription() {
	}

	public CodeDescription(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public String getNote() {
		return note;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setNote(String note) {
		this.note = note;
	}

	/*
	 * @Override public int compareTo(CodeAttribute other) { // return new CompareToBuilder().append(getCode(),
	 * other.getCode()).toComparison(); return this.code.compareTo(other.getCode()); }
	 */

}
