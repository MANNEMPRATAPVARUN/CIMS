package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

public class CodeValidationAudit extends AuditData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8134961035440114994L;
	private String code;
	private String dhcode;
	private ValidationRuleSet validationRuleSet;

	public String getCode() {
		return code;
	}

	public String getDhcode() {
		return dhcode;
	}

	public ValidationRuleSet getValidationRuleSet() {
		return validationRuleSet;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDhcode(String dhcode) {
		this.dhcode = dhcode;
	}

	public void setValidationRuleSet(ValidationRuleSet validationRuleSet) {
		this.validationRuleSet = validationRuleSet;
	}
}
