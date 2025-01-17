package ca.cihi.cims.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum ContextStatus {

	OPEN("OPEN"), CLOSED("CLOSED"), DELETED("DELETED");

	private static final Map<String, ContextStatus> STRING_TO_ENUM = new HashMap<String, ContextStatus>();
	static { // Initialize map from constant name to enum constant
		for (ContextStatus contextStatus : values()) {
			STRING_TO_ENUM.put(contextStatus.getCode(), contextStatus);
		}
	}

	private static final Log LOGGER = LogFactory.getLog(ContextStatus.class);

	public static ContextStatus fromString(final String code) {
		final ContextStatus contextStatus = STRING_TO_ENUM.get(code);

		if (contextStatus == null) {
			LOGGER.warn(new StringBuilder().append("Failed converting [").append(code).append("] to an Status."));
		}
		return contextStatus;
	}

	private String code;

	private ContextStatus(final String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
