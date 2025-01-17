package ca.cihi.cims.web.bean;

import java.util.Date;


public class LogMessage {
    
    private String classification;
    private String fiscalYear;
    private String message;
    private Date messageDate;
       
    public String getClassification() {
        return classification;
    }
    
    public void setClassification(String classification) {
        this.classification = classification;
    }
    
    public String getFiscalYear() {
        return fiscalYear;
    }
    
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }
    
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
    public Date getMessageDate() {
        return messageDate;
    }
    
    public void setMessageDate(Date messageDate) {
        this.messageDate = messageDate;
    }
}
