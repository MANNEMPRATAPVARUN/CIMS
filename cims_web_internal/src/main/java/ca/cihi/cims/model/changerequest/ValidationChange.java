package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

import ca.cihi.cims.content.cci.CciValidationXml;
import ca.cihi.cims.content.icd.IcdValidationXml;

public class ValidationChange implements Serializable {

	private static final long serialVersionUID = -7894708926017852031L;

	private String value;
	private String dataHolding;
	private String status;
	private IcdValidationXml icdValidationXml;
	private CciValidationXml cciValidationXml;

	@Override
	public boolean equals(final Object object) {
		boolean isEqual = false;
		if (object instanceof ValidationChange) {
			final ValidationChange aValidationChange = (ValidationChange) object;
			isEqual = (this.value == aValidationChange.value || this.value != null && aValidationChange.value != null
					&& this.value.trim().equalsIgnoreCase(aValidationChange.value.trim()))
					&& (this.status == aValidationChange.status || this.status != null && aValidationChange != null
							&& this.status == aValidationChange.status)
					&& (this.dataHolding == aValidationChange.dataHolding || this.dataHolding != null
							&& aValidationChange.dataHolding != null
							&& this.dataHolding.trim().equalsIgnoreCase(aValidationChange.dataHolding.trim()))
					&& (this.icdValidationXml == aValidationChange.icdValidationXml || this.icdValidationXml != null
							&& aValidationChange.icdValidationXml != null
							&& this.icdValidationXml.equals(aValidationChange.icdValidationXml))
					&& (this.cciValidationXml == aValidationChange.cciValidationXml || this.cciValidationXml != null
							&& aValidationChange.cciValidationXml != null
							&& this.cciValidationXml.equals(aValidationChange.cciValidationXml));
		}
		return isEqual;
	}

	public CciValidationXml getCciValidationXml() {
		return cciValidationXml;
	}

	public String getDataHolding() {
		return dataHolding;
	}

	public IcdValidationXml getIcdValidationXml() {
		return icdValidationXml;
	}

	public String getStatus() {
		return status;
	}

	public String getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (status == null ? 0 : status.hashCode());
		result = prime * result + (value == null ? 0 : value.trim().hashCode());
		result = prime * result + (dataHolding == null ? 0 : dataHolding.trim().hashCode());
		result = prime * result + (cciValidationXml == null ? 0 : cciValidationXml.hashCode());
		result = prime * result + (icdValidationXml == null ? 0 : icdValidationXml.hashCode());
		return result;
	}

	public void setCciValidationXml(final CciValidationXml cciValidationXml) {
		this.cciValidationXml = cciValidationXml;
	}

	public void setDataHolding(final String dataHolding) {
		this.dataHolding = dataHolding;
	}

	public void setIcdValidationXml(final IcdValidationXml icdValidationXml) {
		this.icdValidationXml = icdValidationXml;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setValue(final String value) {
		this.value = value;
	}
}
