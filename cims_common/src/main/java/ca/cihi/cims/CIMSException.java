package ca.cihi.cims;

public class CIMSException extends RuntimeException {

	private static final long serialVersionUID = 2413211887346099567L;

	public CIMSException(String message) {
		super(message);
	}
	
	public CIMSException(String message, Exception e) {
		super(message, e);
	}
	
	public CIMSException(Exception e) {
		super(e);
	}	
}
