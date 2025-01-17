package ca.cihi.cims.model.changerequest;

public enum DocumentReferenceType {
	CODING_QUESTION("CODING_QUESTION"), URC_LINK("URC_LINK"), URC_FILE("URC_FILE"), OTHER_LINK("OTHER_LINK"), OTHER_FILE(
			"OTHER_FILE");
	private String code;

	private DocumentReferenceType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
