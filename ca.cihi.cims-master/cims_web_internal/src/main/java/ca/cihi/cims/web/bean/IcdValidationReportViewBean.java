package ca.cihi.cims.web.bean;

import java.util.Collection;

import ca.cihi.cims.model.IcdCodeValidation;

/**
 * This is the view bean for code validation report.
 * 
 * @author wxing
 * 
 */
public class IcdValidationReportViewBean {

	private String conceptCode;
	private Collection<IcdCodeValidation> validations;

	public String getConceptCode() {
		return conceptCode;
	}

	public Collection<IcdCodeValidation> getValidations() {
		return validations;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public void setValidations(Collection<IcdCodeValidation> validations) {
		this.validations = validations;
	}

}
