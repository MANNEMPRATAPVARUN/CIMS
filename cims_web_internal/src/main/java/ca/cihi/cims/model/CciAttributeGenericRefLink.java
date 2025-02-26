package ca.cihi.cims.model;

import java.io.Serializable;

public class CciAttributeGenericRefLink implements Serializable {

	private static final long serialVersionUID = 1L;

	private String genericAttributeCode;
	private String referenceAttributeCode;
	private String descriptionEng;
	private String inContextDescriptionEng;
	private String status;

	public CciAttributeGenericRefLink() {
		// TODO Auto-generated constructor stub
	}

	public CciAttributeGenericRefLink(String genericAttributeCode, String referenceAttributeCode,
			String descriptionEng, String inContextDescriptionEng, String status) {
		super();
		this.genericAttributeCode = genericAttributeCode;
		this.referenceAttributeCode = referenceAttributeCode;
		this.descriptionEng = descriptionEng;
		this.inContextDescriptionEng = inContextDescriptionEng;
		this.status = status;
	}

	public String getDescriptionEng() {
		return descriptionEng;
	}

	public String getGenericAttributeCode() {
		return genericAttributeCode;
	}

	public String getInContextDescriptionEng() {
		return inContextDescriptionEng;
	}

	public String getReferenceAttributeCode() {
		return referenceAttributeCode;
	}

	public String getStatus() {
		return status;
	}

	public void setDescriptionEng(String descriptionEng) {
		this.descriptionEng = descriptionEng;
	}

	public void setGenericAttributeCode(String genericAttributeCode) {
		this.genericAttributeCode = genericAttributeCode;
	}

	public void setInContextDescriptionEng(String inContextDescriptionEng) {
		this.inContextDescriptionEng = inContextDescriptionEng;
	}

	public void setReferenceAttributeCode(String referenceAttributeCode) {
		this.referenceAttributeCode = referenceAttributeCode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "CciAttributeGenericRefLink [genericAttributeCode=" + genericAttributeCode + ", referenceAttributeCode="
				+ referenceAttributeCode + ", descriptionEng=" + descriptionEng + ", inContextDescriptionEng="
				+ inContextDescriptionEng + ", status=" + status + "]";
	}

}
