package ca.cihi.cims.model;

import java.io.Serializable;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class CciAttributeReferenceInContextModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * Important. This attributeElementId is neither the Generic or Reference Attribute element Id. Instead, it is the
	 * element Id for the 'Attribute' concept, which is the CPV concept between them. In other words, it is the element
	 * Id of the In-context CPV between them.
	 */
	// @NotEmpty
	private long attributeElementId;

	@NotEmpty
	private String genericAttributeCode;

	@Size(min = 1, max = 255, message = "English Generic Description must be between 1 and 255 characters")
	private String descriptionEng;

	@Size(min = 1, max = 255, message = "French Generic Description must be between 1 and 255 characters")
	private String descriptionFra;

	private String status;

	public CciAttributeReferenceInContextModel() {

	}

	public CciAttributeReferenceInContextModel(long attributeElementId, String genericAttributeCode,
			String descriptionEng, String descriptionFra, String status) {
		super();
		this.attributeElementId = attributeElementId;
		this.genericAttributeCode = genericAttributeCode;
		this.descriptionEng = descriptionEng;
		this.descriptionFra = descriptionFra;
		this.status = status;
	}

	public long getAttributeElementId() {
		return attributeElementId;
	}

	public String getDescriptionEng() {
		return descriptionEng;
	}

	public String getDescriptionFra() {
		return descriptionFra;
	}

	public String getGenericAttributeCode() {
		return genericAttributeCode;
	}

	public String getStatus() {
		return status;
	}

	public void setAttributeElementId(long attributeElementId) {
		this.attributeElementId = attributeElementId;
	}

	public void setDescriptionEng(String descriptionEng) {
		this.descriptionEng = descriptionEng;
	}

	public void setDescriptionFra(String descriptionFra) {
		this.descriptionFra = descriptionFra;
	}

	public void setGenericAttributeCode(String genericAttributeCode) {
		this.genericAttributeCode = genericAttributeCode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CciAttributeReferenceInContextModel [attributeElementId=" + attributeElementId
				+ ", genericAttributeCode=" + genericAttributeCode + ", descriptionEng=" + descriptionEng
				+ ", descriptionFra=" + descriptionFra + ", status=" + status + "]";
	}

}
