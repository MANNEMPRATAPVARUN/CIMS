package ca.cihi.cims.model;

public class AttributeInfo {

	// The status code
	private String statusRef;

	// The location code
	private String locationRef;

	// The extent code
	private String extentRef;

	private boolean isStatusRefMandatory;

	private boolean isLocationRefMandatory;

	private boolean isExtentRefMandatory;

	public String getExtentRef() {
		return extentRef;
	}

	public String getLocationRef() {
		return locationRef;
	}

	public String getStatusRef() {
		return statusRef;
	}

	public boolean isEmpty() {
		return (extentRef == null || extentRef.isEmpty()) && (statusRef == null || statusRef.isEmpty())
				&& (locationRef == null || locationRef.isEmpty());

	}

	public boolean isExtentRefMandatory() {
		return isExtentRefMandatory;
	}

	public boolean isLocationRefMandatory() {
		return isLocationRefMandatory;
	}

	public boolean isStatusRefMandatory() {
		return isStatusRefMandatory;
	}

	public void setExtentRef(final String extentRef) {
		this.extentRef = extentRef;
	}

	public void setExtentRefMandatory(final boolean isExtentRefMandatory) {
		this.isExtentRefMandatory = isExtentRefMandatory;
	}

	public void setLocationRef(final String locationRef) {
		this.locationRef = locationRef;
	}

	public void setLocationRefMandatory(final boolean isLocationRefMandatory) {
		this.isLocationRefMandatory = isLocationRefMandatory;
	}

	public void setStatusRef(final String statusRef) {
		this.statusRef = statusRef;
	}

	public void setStatusRefMandatory(final boolean isStatusRefMandatory) {
		this.isStatusRefMandatory = isStatusRefMandatory;
	}
}
