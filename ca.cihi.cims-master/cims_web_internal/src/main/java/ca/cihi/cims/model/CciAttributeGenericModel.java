package ca.cihi.cims.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import ca.cihi.cims.Language;
import ca.cihi.cims.content.cci.CciGenericAttribute;

public class CciAttributeGenericModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Converts a CciGenericAttribute wrapper class to a CciAttributeGenericModel. Sets the newly created flag as false
	 * 
	 * @param attr
	 * @return
	 */
	public static CciAttributeGenericModel convert(CciGenericAttribute attr) {
		CciAttributeGenericModel model = new CciAttributeGenericModel();
		model.setElementId(attr.getElementId());
		model.setCode(attr.getCode());
		model.setDescriptionEng(attr.getDescription(Language.ENGLISH.getCode()));
		model.setDescriptionFra(attr.getDescription(Language.FRENCH.getCode()));
		model.setStatus(attr.getStatus());
		model.setNewlyCreated(false);
		return model;
	}

	/**
	 * Converts a CciGenericAttribute wrapper class to a CciAttributeGenericModel. Requires the newly created flag as an
	 * additional parameter
	 * 
	 * @param attr
	 * @return
	 */
	public static CciAttributeGenericModel convert(CciGenericAttribute attr, boolean isNewlyCreated) {
		CciAttributeGenericModel model = new CciAttributeGenericModel();
		model.setElementId(attr.getElementId());
		model.setCode(attr.getCode());
		model.setDescriptionEng(attr.getDescription(Language.ENGLISH.getCode()));
		model.setDescriptionFra(attr.getDescription(Language.FRENCH.getCode()));
		model.setStatus(attr.getStatus());
		model.setNewlyCreated(isNewlyCreated);
		return model;
	}

	// -----------------------------------------------------

	@Size(min = 1, max = 255, message = "English description may not be empty or greater than 255 characters")
	private String descriptionEng;

	@Size(min = 1, max = 255, message = "French description may not be empty or greater than 255 characters")
	private String descriptionFra;

	@NotEmpty(message = "Status may not be empty")
	private String status;

	@NotEmpty(message = "Code may not be empty")
	private String code;

	// @NotEmpty
	private long elementId;

	private boolean isNewlyCreated;

	// -----------------------------------------------------

	public String getCode() {
		return code;
	}

	public String getDescriptionEng() {
		return descriptionEng;
	}

	public String getDescriptionFra() {
		return descriptionFra;
	}

	public long getElementId() {
		return elementId;
	}

	// Otherwise you can't reference property isNewlyCreated in JSTL
	public boolean getisNewlyCreated() {
		return isNewlyCreated;
	}

	public String getStatus() {
		return status;
	}

	public boolean isNewlyCreated() {
		return isNewlyCreated;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescriptionEng(String descriptionEng) {
		this.descriptionEng = descriptionEng;
	}

	public void setDescriptionFra(String descriptionFra) {
		this.descriptionFra = descriptionFra;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setNewlyCreated(boolean isNewlyCreated) {
		this.isNewlyCreated = isNewlyCreated;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CciAttributeGenericModel [descriptionEng=" + descriptionEng + ", descriptionFra=" + descriptionFra
				+ ", status=" + status + ", code=" + code + ", elementId=" + elementId + ", isNewlyCreated="
				+ isNewlyCreated + "]";
	}
}
