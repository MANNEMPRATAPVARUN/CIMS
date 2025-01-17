package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.util.List;

public class CCIReferenceAudit implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3927546414185589791L;
	private String code;
	private String dhcode;
	private List<CCIGenericAttribute> genericAttributes;
	private String newReferenceValue;
	private String oldReferenceValue;
	private String mandatoryIndicator;

	public String getCode() {
		return code;
	}

	public String getDhcode() {
		return dhcode;
	}

	public List<CCIGenericAttribute> getGenericAttributes() {
		return genericAttributes;
	}

	public String getMandatoryIndicator() {
		return mandatoryIndicator;
	}

	public String getNewReferenceValue() {
		return newReferenceValue;
	}

	public String getOldReferenceValue() {
		return oldReferenceValue;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDhcode(String dhcode) {
		this.dhcode = dhcode;
	}

	public void setGenericAttributes(List<CCIGenericAttribute> genericAttributes) {
		this.genericAttributes = genericAttributes;
	}

	public void setMandatoryIndicator(String mandatoryIndicator) {
		this.mandatoryIndicator = mandatoryIndicator;
	}

	public void setNewReferenceValue(String newReferenceValue) {
		this.newReferenceValue = newReferenceValue;
	}

	public void setOldReferenceValue(String oldReferenceValue) {
		this.oldReferenceValue = oldReferenceValue;
	}
}
