package ca.cihi.cims.content.cci;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import ca.cihi.cims.content.shared.ValidationXml;

/**
 * FIXME: @XmlType.propOrder is working on elements but not on attributes <br/>
 * https://community.oracle.com/thread/977397 <br/>
 * or attributes must be reverse ordered
 * 
 * @author adenysenko
 */
@XmlRootElement(name = "validation")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "language", "classification" //
		, "elementId", "genderCode", "genderDescriptionEng", "genderDescriptionFra", "ageRange" //
		, "statusReferenceCode", "locationReferenceCode", "extentReferenceCode" })
public class CciValidationXml extends ValidationXml {

	@XmlElement(name = "STATUS_REF")
	private String statusReferenceCode;

	@XmlElement(name = "LOCATION_REF")
	private String locationReferenceCode;

	@XmlElement(name = "EXTENT_REF")
	private String extentReferenceCode;

	// ----------------------------------------------------------

	@Override
	public boolean equals(Object object) {
		boolean isEqual = false;
		if (object instanceof CciValidationXml) {
			CciValidationXml validationXml = (CciValidationXml) object;
			isEqual = super.equals(object)//
					&& this.statusReferenceCode.equals(validationXml.statusReferenceCode)
					&& this.extentReferenceCode.equals(validationXml.extentReferenceCode)
					&& this.locationReferenceCode.equals(validationXml.locationReferenceCode);
		}
		return isEqual;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (extentReferenceCode == null ? 0 : extentReferenceCode.hashCode());
		result = prime * result + (locationReferenceCode == null ? 0 : locationReferenceCode.hashCode());
		result = prime * result + (statusReferenceCode == null ? 0 : statusReferenceCode.hashCode());
		return result;
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
