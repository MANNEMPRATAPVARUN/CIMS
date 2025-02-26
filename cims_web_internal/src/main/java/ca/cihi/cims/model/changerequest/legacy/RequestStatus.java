package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;

public class RequestStatus implements Serializable {
	private static final long serialVersionUID = 201411121841L;

    private String requestStatusCode;
    private String requestStatusDesc;

	public String getRequestStatusCode() {
		return requestStatusCode;
	}

	public void setRequestStatusCode(String requestStatusCode) {
		this.requestStatusCode = requestStatusCode;
	}

	public String getRequestStatusDesc() {
		return requestStatusDesc;
	}

	public void setRequestStatusDesc(String requestStatusDesc) {
		this.requestStatusDesc = requestStatusDesc;
	}


}
