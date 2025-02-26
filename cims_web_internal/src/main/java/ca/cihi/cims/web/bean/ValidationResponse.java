package ca.cihi.cims.web.bean;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ValidationResponse {

	public enum Status {
		FAIL, SUCCESS;
	}

	public static ValidationResponse fail(String errorMessage) {
		ValidationResponse r = new ValidationResponse();
		r.setStatus(Status.FAIL);
		r.setErrorMessageList(new ArrayList<ObjectError>());
		r.getErrorMessageList().add(new FieldError("error", "error", errorMessage));
		return r;
	}

	public static ValidationResponse success(Object value) {
		ValidationResponse r = new ValidationResponse();
		r.setStatus(Status.SUCCESS);
		r.setValue(value);
		return r;
	}

	private String value;
	private String status;
	private boolean contextFrozen; // TODO: Not an ideal place to put this
	private List<ObjectError> errorMessageList;

	// --------------------------------------------------

	public List<ObjectError> getErrorMessageList() {
		return this.errorMessageList;
	}

	public String getStatus() {
		return status;
	}

	public String getValue() {
		return value;
	}

	public boolean isContextFrozen() {
		return contextFrozen;
	}

	public void setContextFrozen(boolean isContextFrozen) {
		this.contextFrozen = isContextFrozen;
	}

	@SuppressWarnings("unchecked")
	public void setErrorMessageList(List<? extends ObjectError> errorMessageList) {
		this.errorMessageList = (List<ObjectError>) errorMessageList;
	}

	public void setStatus(Status status) {
		this.status = status == null ? null : status.name();
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setValue(Object value) {
		this.value = value == null ? null : value.toString();
	}

}
