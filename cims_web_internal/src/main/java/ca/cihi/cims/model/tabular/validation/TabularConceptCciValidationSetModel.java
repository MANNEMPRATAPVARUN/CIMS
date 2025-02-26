package ca.cihi.cims.model.tabular.validation;

public class TabularConceptCciValidationSetModel extends TabularConceptValidationSetModel {

	private String statusReferenceCode;
	private String locationReferenceCode;
	private String deliveryReferenceCode;
	private String extentReferenceCode;

	// -------------------------------------------------------------------

	public String getDeliveryReferenceCode() {
		return deliveryReferenceCode;
	}

	public String getExtentReferenceCode() {
		return extentReferenceCode;
	}

	public String getLocationReferenceCode() {
		return locationReferenceCode;
	}

	public String getStatusReferenceCode() {
		return statusReferenceCode;
	}

	public void setDeliveryReferenceCode(String deliveryReferenceCode) {
		this.deliveryReferenceCode = deliveryReferenceCode;
	}

	public void setExtentReferenceCode(String extentReferenceCode) {
		this.extentReferenceCode = extentReferenceCode;
	}

	public void setLocationReferenceCode(String locationReferenceCode) {
		this.locationReferenceCode = locationReferenceCode;
	}

	public void setStatusReferenceCode(String statusReferenceCode) {
		this.statusReferenceCode = statusReferenceCode;
	}

}
