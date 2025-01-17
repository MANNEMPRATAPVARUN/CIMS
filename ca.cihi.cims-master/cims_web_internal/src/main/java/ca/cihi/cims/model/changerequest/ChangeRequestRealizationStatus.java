package ca.cihi.cims.model.changerequest;

public enum ChangeRequestRealizationStatus {
	PRE_PROCESS("PRE_PROCESS"),
	PRE_PROCESS_FAILED("PRE_PROCESS_FAILED"),
	PROCESS_BEGINS("PROCESS_BEGINS"),
	PROCESS_FAILED("PROCESS_FAILED"),
	PROCESS_ENDS("PROCESS_ENDS_SUCCEED");

	private String statusCode;

	private ChangeRequestRealizationStatus(String statusCode){
		this.statusCode= statusCode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
