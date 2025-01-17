package ca.cihi.cims.exception;

import ca.cihi.cims.CIMSException;

public class UnsupportedElementExeption extends CIMSException {

	private static final long serialVersionUID = 1L;

	// -----------------------------------------------

	public UnsupportedElementExeption() {
		super("");
	}

	public UnsupportedElementExeption(Exception e) {
		super(e);
	}

	public UnsupportedElementExeption(String message) {
		super(message);
	}

	public UnsupportedElementExeption(String message, Exception e) {
		super(message, e);
	}

}
