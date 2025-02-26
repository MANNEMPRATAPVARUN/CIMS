package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

public class CCIGenericAttributeAudit extends AuditData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4689814466267452687L;
	private String referenceCode;
	private String code;

	public String getCode() {
		return code;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}
}
