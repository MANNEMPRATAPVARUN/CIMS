package ca.cihi.cims.model;

import java.io.Serializable;

/*
 * the model is for query out the unused components, reference and attribute
 */
public class ComponentAndAttributeElementModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long elementId;
	private Integer classId;
	private String elementUUID;
	private String notes;
	private String code;
	private String section; // used for component
	private String type; // used for reference and attribute; and component type

	public Integer getClassId() {
		return classId;
	}

	public String getCode() {
		return code;
	}

	public Long getElementId() {
		return elementId;
	}

	public String getElementUUID() {
		return elementUUID;
	}

	public String getNotes() {
		return notes;
	}

	public String getSection() {
		return section;
	}

	public String getType() {
		return type;
	}

	public void setClassId(Integer classId) {
		this.classId = classId;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setElementUUID(String elementUUID) {
		this.elementUUID = elementUUID;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void setType(String type) {
		this.type = type;
	}

}
