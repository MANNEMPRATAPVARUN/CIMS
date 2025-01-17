package ca.cihi.cims;

public enum FreezingStatus {
	// BLK -- for block unfreeze btn, during close year , the user can't unfreeze the classification or generate file
	UNKNOWN(0, "Unknown"), TAB(1, "TAB"), ALL(2, "ALL"), BLK(3, "BLK");

	private int freezingStatusId;
	private String freezingStatusCode;

	private FreezingStatus(int freezingStatusId, String freezingStatusCode) {
		this.freezingStatusId = freezingStatusId;
		this.freezingStatusCode = freezingStatusCode;
	}

	public String getFreezingStatusCode() {
		return freezingStatusCode;
	}

	public int getFreezingStatusId() {
		return freezingStatusId;
	}

	public void setFreezingStatusCode(String freezingStatusCode) {
		this.freezingStatusCode = freezingStatusCode;
	}

	public void setFreezingStatusId(int freezingStatusId) {
		this.freezingStatusId = freezingStatusId;
	}
}
