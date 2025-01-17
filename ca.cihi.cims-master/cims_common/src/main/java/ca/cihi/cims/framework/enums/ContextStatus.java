package ca.cihi.cims.framework.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:22:23 AM
 */
public enum ContextStatus {

CLOSED("CLOSED"), DELETED("DELETED"), OPEN("OPEN");

private static final Map<String, ContextStatus> STRING_TO_ENUM = new HashMap<String, ContextStatus>();

static {
	for (ContextStatus s : values()) {
		STRING_TO_ENUM.put(s.getStatus(), s);
	}
}

private String status;

private ContextStatus(final String status) {
	this.status = status;
}

public String getStatus() {
	return status;
}
}
