package ca.cihi.cims.model.changerequest;

/**
 * Implementation of {@link ChangeRequestStatusIdentifier}
 * @author rshnaper
 *
 */
public class ChangeRequestStatusIdentifierImpl implements
		ChangeRequestStatusIdentifier {
	private long statusId;
	private String statusCode, statusDescription, subStatusCode, subStatusDescription;
	
	public long getStatusId() {
		return statusId;
	}
	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getStatusDescription() {
		return statusDescription;
	}
	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	public String getSubStatusCode() {
		return subStatusCode;
	}
	public void setSubStatusCode(String subStatusCode) {
		this.subStatusCode = subStatusCode;
	}
	public String getSubStatusDescription() {
		return subStatusDescription;
	}
	public void setSubStatusDescription(String subStatusDescription) {
		this.subStatusDescription = subStatusDescription;
	}
}
