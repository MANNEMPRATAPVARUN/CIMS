package ca.cihi.cims.refset.exception;

/**
 * This exception will be thrown when a code or name already used in the system
 *
 * @author TYang
 *
 */
public class DuplicateCodeNameException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -7119936813262686234L;

	public DuplicateCodeNameException(String message) {
		super(message);
	}
}
