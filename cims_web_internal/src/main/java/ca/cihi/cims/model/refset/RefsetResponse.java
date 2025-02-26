package ca.cihi.cims.model.refset;

import java.io.Serializable;
import java.util.List;

import ca.cihi.cims.web.bean.refset.SupplementViewBean;

public class RefsetResponse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String status;

	private List<String> errors;

	private String token;

	private Long contextId;

	private Long elementId;

	private Long elementVersionId;

	private String categoryName;
	
	private String errorType;

	private boolean sublist;
	
	private SupplementViewBean supplementViewBean;
	
	
	/**
	 * Display message to user on successful message
	 */
	private String message;

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}

	public boolean isSublist() {
		return sublist;
	}

	public void setSublist(boolean sublist) {
		this.sublist = sublist;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public Long getElementId() {
		return elementId;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public Long getElementVersionId() {
		return elementVersionId;
	}

	public void setElementVersionId(Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public SupplementViewBean getSupplementViewBean() {
		return supplementViewBean;
	}

	public void setSupplementViewBean(SupplementViewBean supplementViewBean) {
		this.supplementViewBean = supplementViewBean;
	}
	
	
	

}
