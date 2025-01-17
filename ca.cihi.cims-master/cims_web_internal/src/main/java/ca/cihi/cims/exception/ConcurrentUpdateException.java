package ca.cihi.cims.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import ca.cihi.cims.CIMSException;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "change.request.concurrent.update")
public class ConcurrentUpdateException extends CIMSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ConcurrentUpdateException(String message) {
		super(message);
	}
}
