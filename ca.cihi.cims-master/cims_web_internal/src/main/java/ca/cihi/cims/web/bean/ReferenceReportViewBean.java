package ca.cihi.cims.web.bean;

import java.util.Collection;

import ca.cihi.cims.model.CodeDescription;

/**
 * This is the view bean for reference value report.
 * 
 * @author wxing
 *
 */
public class ReferenceReportViewBean {
    
    private String refCode; 
    private Collection<CodeDescription> attributes;    
    private String refNote;
    
    public String getRefCode() {
        return refCode;
    }
    
    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }
    
    public Collection<CodeDescription> getAttributes() {
        return attributes;
    }
    
    public void setAttributes(Collection<CodeDescription> attributes) {
        this.attributes = attributes;
    }

    
    public String getRefNote() {
        return refNote;
    }

    
    public void setRefNote(String refNote) {
        this.refNote = refNote;
    }
    
}
