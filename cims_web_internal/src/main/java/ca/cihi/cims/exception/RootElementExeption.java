package ca.cihi.cims.exception;

import ca.cihi.cims.CIMSException;

public class RootElementExeption extends CIMSException {

	private static final long serialVersionUID = 1L;
	
	//-----------------------------------------------

	public RootElementExeption(Exception e) {
		super(e);
	}

	public RootElementExeption(String message, Exception e) {
		super(message, e);
	}

	public RootElementExeption(String message) {
		super(message);
	}

}
