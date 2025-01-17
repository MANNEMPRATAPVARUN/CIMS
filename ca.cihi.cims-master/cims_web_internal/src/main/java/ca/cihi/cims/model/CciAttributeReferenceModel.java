package ca.cihi.cims.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.validator.constraints.NotEmpty;

import ca.cihi.cims.Language;
import ca.cihi.cims.content.cci.CciReferenceAttribute;

public class CciAttributeReferenceModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Converts a CciReferenceAttribute wrapper class to a CciAttributeReferenceModel. Sets the newly created flag as
	 * false
	 * 
	 * @param attr
	 * @return
	 */
	public static CciAttributeReferenceModel convert(CciReferenceAttribute attr) {

		CciAttributeReferenceModel model = new CciAttributeReferenceModel();

		model.setElementId(attr.getElementId());
		model.setCode(attr.getCode());
		model.setDescriptionEng(attr.getDescription(Language.ENGLISH.getCode()));
		model.setDescriptionFra(attr.getDescription(Language.FRENCH.getCode()));
		model.setStatus(attr.getStatus());
		model.setMandatory(BooleanUtils.toStringYesNo(attr.isMandatory()));
		model.setNewlyCreated(false);

		return model;
	}

	/**
	 * Converts a CciReferenceAttribute wrapper class to a CciAttributeReferenceModel. Requires the newly created flag
	 * as an additional parameter
	 * 
	 * @param attr
	 * @return
	 */
	public static CciAttributeReferenceModel convert(CciReferenceAttribute attr, boolean isNewlyCreated) {

		CciAttributeReferenceModel model = new CciAttributeReferenceModel();

		model.setElementId(attr.getElementId());
		model.setCode(attr.getCode());
		model.setDescriptionEng(attr.getDescription(Language.ENGLISH.getCode()));
		model.setDescriptionFra(attr.getDescription(Language.FRENCH.getCode()));
		model.setStatus(attr.getStatus());
		model.setMandatory(BooleanUtils.toStringYesNo(attr.isMandatory()));
		model.setNewlyCreated(isNewlyCreated);

		return model;
	}

	@Size(min = 1, max = 255)
	private String descriptionEng;

	@Size(min = 1, max = 255)
	private String descriptionFra;

	@NotEmpty
	private String status;

	@NotEmpty
	private String code;

	// @NotEmpty
	private long elementId;

	@NotEmpty
	private String mandatory;

	private boolean isNewlyCreated;

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

	public String getMandatory() {
		return mandatory;
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

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public void setNewlyCreated(boolean isNewlyCreated) {
		this.isNewlyCreated = isNewlyCreated;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CciAttributeReferenceModel [descriptionEng=" + descriptionEng + ", descriptionFra=" + descriptionFra
				+ ", status=" + status + ", code=" + code + ", elementId=" + elementId + ", mandatory=" + mandatory
				+ ", isNewlyCreated=" + isNewlyCreated + "]";
	}

}
