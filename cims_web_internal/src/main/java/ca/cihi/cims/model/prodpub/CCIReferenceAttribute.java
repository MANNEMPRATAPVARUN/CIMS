package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.util.List;

public class CCIReferenceAttribute implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5708658459316376193L;
	private String code;
	private String description;
	private String mandatoryIndicator;

	private List<CCIGenericAttribute> genericAttributes;

	private String codeType;
	private String status;

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CCIReferenceAttribute other = (CCIReferenceAttribute) obj;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (codeType == null) {
			if (other.codeType != null) {
				return false;
			}
		} else if (!codeType.equals(other.codeType)) {
			return false;
		}
		if (genericAttributes == null) {
			if (other.genericAttributes != null) {
				return false;
			}
		} else if (!genericAttributes.equals(other.genericAttributes)) {
			return false;
		}
		if (mandatoryIndicator == null) {
			if (other.mandatoryIndicator != null) {
				return false;
			}
		} else if (!mandatoryIndicator.equals(other.mandatoryIndicator)) {
			return false;
		}
		if (status == null) {
			if (other.status != null) {
				return false;
			}
		} else if (!status.equals(other.status)) {
			return false;
		}
		return true;
	}

	public String getCode() {
		return code;
	}

	public String getCodeType() {
		return codeType;
	}

	public String getDescription() {
		return description;
	}

	public List<CCIGenericAttribute> getGenericAttributes() {
		return genericAttributes;
	}

	public String getMandatoryIndicator() {
		return mandatoryIndicator;
	}

	public String getStatus() {
		return status;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((codeType == null) ? 0 : codeType.hashCode());
		result = prime * result + ((genericAttributes == null) ? 0 : genericAttributes.hashCode());
		result = prime * result + ((mandatoryIndicator == null) ? 0 : mandatoryIndicator.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		return result;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setGenericAttributes(List<CCIGenericAttribute> genericAttributes) {
		this.genericAttributes = genericAttributes;
	}

	public void setMandatoryIndicator(String mandatoryIndicator) {
		this.mandatoryIndicator = mandatoryIndicator;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
