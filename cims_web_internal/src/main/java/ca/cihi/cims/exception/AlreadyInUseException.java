package ca.cihi.cims.exception;


public class AlreadyInUseException extends Exception {
	
	private static final long serialVersionUID = -2594638892778415044L;

	public AlreadyInUseException() {
		super();
	}

	/**
	 * @param message
	 */
	public AlreadyInUseException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public AlreadyInUseException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public AlreadyInUseException(String message, Throwable cause) {
		super(message, cause);
	}
}
