package ca.cihi.cims.model;

import java.io.Serializable;

public class CciAttributeReferenceRefLink implements Serializable {

	private static final long serialVersionUID = 1L;

	private String tabularCode;
	private String referenceAttributeCode;
	private String descriptionEng;
	private String status;

	public CciAttributeReferenceRefLink() {
		// TODO Auto-generated constructor stub
	}

	public CciAttributeReferenceRefLink(String referenceAttributeCode, String tabularCode, String descriptionEng,
			String status) {
		super();
		this.tabularCode = tabularCode;
		this.referenceAttributeCode = referenceAttributeCode;
		this.descriptionEng = descriptionEng;
		this.status = status;
	}

	public String getDescriptionEng() {
		return descriptionEng;
	}

	public String getReferenceAttributeCode() {
		return referenceAttributeCode;
	}

	public String getStatus() {
		return status;
	}

	public String getTabularCode() {
		return tabularCode;
	}

	public void setDescriptionEng(String descriptionEng) {
		this.descriptionEng = descriptionEng;
	}

	public void setReferenceAttributeCode(String referenceAttributeCode) {
		this.referenceAttributeCode = referenceAttributeCode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTabularCode(String tabularCode) {
		this.tabularCode = tabularCode;
	}

	@Override
	public String toString() {
		return "CciAttributeReferenceRefLink [tabularCode=" + tabularCode + ", referenceAttributeCode="
				+ referenceAttributeCode + ", descriptionEng=" + descriptionEng + ", status=" + status + "]";
	}

}
