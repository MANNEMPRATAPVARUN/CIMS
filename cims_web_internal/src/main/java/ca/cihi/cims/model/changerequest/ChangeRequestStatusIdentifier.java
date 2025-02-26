package ca.cihi.cims.model.changerequest;

/**
 * Change request status interface
 * @author rshnaper
 *
 */
public interface ChangeRequestStatusIdentifier {
	/**
	 * Returns status id
	 * @return
	 */
	public long getStatusId();
	
	/**
	 * Returns the status code
	 * @return
	 */
	public String getStatusCode();
	
	/**
	 * Returns the status description
	 * @return
	 */
	public String getStatusDescription();
	
	/**
	 * Returns the sub-status code
	 * @return
	 */
	public String getSubStatusCode();
	
	/**
	 * Returns the sub-status description
	 * @return
	 */
	public String getSubStatusDescription();
}
