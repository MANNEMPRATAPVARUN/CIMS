package ca.cihi.cims.model;

import java.io.Serializable;

import org.hibernate.validator.constraints.NotEmpty;

public class CciAttributes implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String baseClassification;

	@NotEmpty
	private String versionCode;

	@NotEmpty
	private String status;

	@NotEmpty
	private String attributeType;

	@NotEmpty
	private String attributeViewType;

	private boolean contextFrozen;

	public CciAttributes() {
		super();
		setContextFrozen(false);
	}

	public String getAttributeType() {
		return attributeType;
	}

	public String getAttributeViewType() {
		return attributeViewType;
	}

	public String getBaseClassification() {
		return baseClassification;
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

	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}

	public void setAttributeViewType(String attributeViewType) {
		this.attributeViewType = attributeViewType;
	}

	public void setBaseClassification(String baseClassification) {
		this.baseClassification = baseClassification;
	}

	public void setContextFrozen(boolean contextFrozen) {
		this.contextFrozen = contextFrozen;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	@Override
	public String toString() {
		return "CciAttributes [baseClassification=" + baseClassification + ", versionCode=" + versionCode + ", status="
				+ status + ", attributeType=" + attributeType + ", attributeViewType=" + attributeViewType
				+ ", contextFrozen=" + contextFrozen + "]";
	}

}
