package ca.cihi.cims.web.bean;

import java.io.Serializable;

public class ResponseError implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7551484216491332120L;
	private int code;
	private String message;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
