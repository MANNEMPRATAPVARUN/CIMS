package ca.cihi.cims.web.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.ObjectError;

public class CIMSWebResponse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -5802814742421537113L;

	private static final String STATUS_FAILED = "FAILED";
	private static final String STATUS_SUCCESS = "SUCCESS";
	private String status = STATUS_SUCCESS;
	private List<ResponseError> errors;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<ResponseError> getErrors() {
		return errors;
	}

	public void setErrors(List<ResponseError> errors) {
		this.errors = errors;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	private String message;
	private Object result;

	public static CIMSWebResponse buildSuccessResponse() {
		return new CIMSWebResponse();
	}

	public static CIMSWebResponse buildSuccessResponse(Object result, String message) {
		CIMSWebResponse response = new CIMSWebResponse();
		response.setMessage(message);
		response.setResult(result);
		response.setStatus(STATUS_SUCCESS);
		return response;
	}

	public static CIMSWebResponse buildFailureResponse(Exception e) {
		CIMSWebResponse response = new CIMSWebResponse();
		response.setStatus(STATUS_FAILED);
		response.setMessage(e.getMessage());
		return response;
	}

	public static CIMSWebResponse buildFailureResponse(List<ObjectError> allErrors) {
		CIMSWebResponse response = new CIMSWebResponse();
		response.setStatus(STATUS_FAILED);
		List<ResponseError> errorMesages = new ArrayList<>();
		for (ObjectError objectError : allErrors) {
			ResponseError error = new ResponseError();
			error.setCode(1);
			error.setMessage(objectError.getDefaultMessage());
			errorMesages.add(error);
		}
		response.setErrors(errorMesages);
		return response;
	}

	public static CIMSWebResponse buildFailureResponse(String message) {
		CIMSWebResponse response = new CIMSWebResponse();
		response.setStatus(STATUS_FAILED);
		response.setMessage(message);
		return response;
	}
}
