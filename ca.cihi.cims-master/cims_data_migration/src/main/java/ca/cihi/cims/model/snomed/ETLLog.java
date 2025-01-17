package ca.cihi.cims.model.snomed;

import java.io.Serializable;
import java.util.Date;

public class ETLLog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private Long runId;
	private String messageDate;
	private String message;
	private String sctVersionCode;
		
	public String getMessageDate() {
		return messageDate;
	}
	public void setMessageDate(String messageDate) {
		this.messageDate = messageDate;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getRunId() {
		return runId;
	}
	public void setRunId(Long runId) {
		this.runId = runId;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getSctVersionCode() {
		return sctVersionCode;
	}
	public void setSctVersionCode(String sctVersionCode) {
		this.sctVersionCode = sctVersionCode;
	}	

}
