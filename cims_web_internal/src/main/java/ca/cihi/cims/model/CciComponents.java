package ca.cihi.cims.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class CciComponents implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String baseClassification;

	@NotEmpty
	private String versionCode;

	@NotEmpty
	private String status;

	@NotEmpty
	private String section;

	private String sectionTitle;

	private boolean contextFrozen;

	public CciComponents() {
		super();
		setContextFrozen(false);
	}

	public String getBaseClassification() {
		return baseClassification;
	}

	public String getSection() {
		return section;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public String getStatus() {
		return status;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public boolean isContextFrozen() {
		return contextFrozen;
	}

	public void setBaseClassification(String baseClassification) {
		this.baseClassification = baseClassification;
	}

	public void setContextFrozen(boolean contextFrozen) {
		this.contextFrozen = contextFrozen;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public String toString() {
		return "CciComponents [baseClassification=" + baseClassification + ", versionCode=" + versionCode + ", status="
				+ status + ", section=" + section + ", sectionTitle=" + sectionTitle + ", contextFrozen="
				+ contextFrozen + "]";
	}

}
