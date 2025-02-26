package ca.cihi.cims.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.cihi.cims.CIMSException;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "closeyear.concurrent.update")
public class ConcurrentCloseYearException extends CIMSException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConcurrentCloseYearException(Exception e) {
		super(e);
	}

	public ConcurrentCloseYearException(String message) {
		super(message);
	}

	public ConcurrentCloseYearException(String message, Exception e) {
		super(message, e);
	}
}
