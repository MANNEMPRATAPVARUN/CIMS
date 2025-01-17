package ca.cihi.cims.model.changerequest;

public enum AttachmentFormat {
	EXCEL_FILE("EXCEL"), PDF_FILE("PDF"), TXT_FILE("TXT"), LINK("LINK"), ZIP("ZIP");

	private String code;

	private AttachmentFormat(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
