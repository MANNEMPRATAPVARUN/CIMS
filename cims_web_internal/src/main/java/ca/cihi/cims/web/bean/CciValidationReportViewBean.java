package ca.cihi.cims.web.bean;

import java.util.Collection;

import ca.cihi.cims.model.CciCodeValidation;

/**
 * This is the view bean for code validation report.
 * 
 * @author wxing
 *
 */
public class CciValidationReportViewBean {
    
    private String conceptCode;    
    private Collection<CciCodeValidation> validations;
    
    public String getConceptCode() {
        return conceptCode;
    }
    
    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

  
    public Collection<CciCodeValidation> getValidations() {
        return validations;
    }

    
    public void setValidations(Collection<CciCodeValidation> validations) {
        this.validations = validations;
    }

    
}
