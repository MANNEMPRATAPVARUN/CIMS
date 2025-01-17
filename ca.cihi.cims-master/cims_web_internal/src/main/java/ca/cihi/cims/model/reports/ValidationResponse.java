package ca.cihi.cims.model.reports;

import java.io.Serializable;
import java.util.List;

public class ValidationResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8793978340861213737L;

	private String status;
	
	private List<String> errors;
	
	private String token;

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
}
