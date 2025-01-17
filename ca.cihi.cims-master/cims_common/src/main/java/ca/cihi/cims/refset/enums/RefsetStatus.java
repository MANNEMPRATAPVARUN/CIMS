package ca.cihi.cims.refset.enums;

/**
 *
 * @author lzhu
 * @version 1.0
 * @created 12-Jun-2016 2:29:30 PM
 *
 */
public enum RefsetStatus {

	ACTIVE("ACTIVE"), DISABLED("DISABLED");

	private String status;

	private RefsetStatus(final String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

}
