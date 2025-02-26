package ca.cihi.cims.exception;

/**
 * Exception that is raised when a search with the same name already exists
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p>
 */
public class DuplicateSearchNameException extends Exception {

	public DuplicateSearchNameException(String message) {
		super(message);
	}
	
	public DuplicateSearchNameException(String message, Throwable cause) {
		super(message, cause);
	}
}
