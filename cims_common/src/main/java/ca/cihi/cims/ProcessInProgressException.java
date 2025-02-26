package ca.cihi.cims;


public class ProcessInProgressException extends CIMSException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3721819147832265157L;

	public ProcessInProgressException(Exception e) {
		super(e);
	}

	public ProcessInProgressException(String message) {
		super(message);
	}

	public ProcessInProgressException(String message, Exception e) {
		super(message, e);
	}

}
