package ca.cihi.cims.framework.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:22:23 AM
 */
public enum ElementStatus {

	ACTIVE("ACTIVE"), DISABLED("DISABLED"), REMOVED("REMOVED");

	private static final Map<String, ElementStatus> STRING_TO_ENUM = new HashMap<String, ElementStatus>();

	static {
		for (ElementStatus s : values()) {
			STRING_TO_ENUM.put(s.getStatus(), s);
		}
	}

	private String status;

	private ElementStatus(final String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
}
