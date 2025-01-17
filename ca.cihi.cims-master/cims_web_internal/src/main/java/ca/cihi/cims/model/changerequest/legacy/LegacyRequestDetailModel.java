package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class LegacyRequestDetailModel extends LegacyRequestResultsModel implements Serializable {
	private static final long serialVersionUID = 201410311650L;

	// Creation Date
	private Date requestDate;

	// Created By
    private String requestByUser;  // CIHI_USER table (user_id)	
	
	// Requestor Type
	private String requestorType;  // REQUESTOR_TYPE table (requestor_type_code)
	
	// Change Description
	private String requestDescTxt;  // CLOB

	// Change Description Francais
	private String requestFDescTxt;  // CLOB

	// Review Required
	private String reviewDesc; // REVIEW table (review_code)
	
    // Maintenance Notes
	private String  maintenanceNote;  // CLOB
	
	private String requestRationalTxt; // CLOB
	
	// Disposed By
    private String disposeByUser;  // CIHI_USER table (user_id)	
	
    //Rationale for Decision
	private String disposeRationalTxt; // CLOB
    
    // Last Modified Date
	private Date lastModifiedDate;

    // Last Modified By
    private String lastModifiedUser;  // CIHI_USER table (user_id)	
	
	private String conversionEvolutionNote; 
	
	// Notes
    private String note;

	private List<String> queryRefNums;  // REQUEST_QUERY_REF table

	private List<String> attachmentFileNames;


	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestByUser() {
		return requestByUser;
	}

	public void setRequestByUser(String requestByUser) {
		this.requestByUser = requestByUser;
	}
	
	public String getRequestorType() {
		return requestorType;
	}

	public void setRequestorType(String requestorType) {
		this.requestorType = requestorType;
	}
	
	public String getRequestDescTxt() {
		return requestDescTxt;
	}

	public void setRequestDescTxt(String requestDescTxt) {
		this.requestDescTxt = requestDescTxt;
	}

	public String getRequestFDescTxt() {
		return requestFDescTxt;
	}

	public void setRequestFDescTxt(String requestFDescTxt) {
		this.requestFDescTxt = requestFDescTxt;
	}

	public String getReviewDesc() {
		return reviewDesc;
	}

	public void setReviewDesc(String reviewDesc) {
		this.reviewDesc = reviewDesc;
	}
	
	public String getMaintenanceNote() {
		return maintenanceNote;
	}

	public void setMaintenanceNote(String maintenanceNote) {
		this.maintenanceNote = maintenanceNote;
	}

	public String getRequestRationalTxt() {
		return requestRationalTxt;
	}

	public void setRequestRationalTxt(String requestRationalTxt) {
		this.requestRationalTxt = requestRationalTxt;
	}

	public String getDisposeByUser() {
		return disposeByUser;
	}

	public void setDisposeByUser(String disposeByUser) {
		this.disposeByUser = disposeByUser;
	}
	
	public String getDisposeRationalTxt() {
		return disposeRationalTxt;
	}

	public void setDisposeRationalTxt(String disposeRationalTxt) {
		this.disposeRationalTxt = disposeRationalTxt;
	}
	
	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
	
	public String getLastModifiedUser() {
		return lastModifiedUser;
	}

	public void setLastModifiedUser(String lastModifiedUser) {
		this.lastModifiedUser = lastModifiedUser;
	}
	
	public String getConversionEvolutionNote() {
		return conversionEvolutionNote;
	}

	public void setConversionEvolutionNote(String conversionEvolutionNote) {
		this.conversionEvolutionNote = conversionEvolutionNote;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public List<String> getAttachmentFileNames() {
		return attachmentFileNames;
	}

	public void setAttachmentFileNames(List<String> attachmentFileNames) {
		this.attachmentFileNames = attachmentFileNames;
	}

	public List<String> getQueryRefNums() {
		return queryRefNums;
	}

	public void setQueryRefNums(List<String> queryRefNums) {
		this.queryRefNums = queryRefNums;
	}

	
}
