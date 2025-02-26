package ca.cihi.cims;

public class DuplicateConceptException extends CIMSException {

	private static final long serialVersionUID = 4887251430232377425L;

	public DuplicateConceptException(String message) {
		super(message);
	}

	public DuplicateConceptException(String message, Exception e) {
		super(message, e);
	}

	public DuplicateConceptException(Exception e) {
		super(e);
	}

}
