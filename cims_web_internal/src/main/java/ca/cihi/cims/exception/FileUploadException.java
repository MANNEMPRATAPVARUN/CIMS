package ca.cihi.cims.exception;

import ca.cihi.cims.CIMSException;

public class FileUploadException extends CIMSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileUploadException(Exception e) {
		super(e);

	}

	public FileUploadException(String message) {
		super(message);
	}
}
