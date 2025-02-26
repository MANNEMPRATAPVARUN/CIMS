package ca.cihi.cims.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public enum SecurityRole {
ROLE_INITIATOR("ROLE_INITIATOR"), 
ROLE_ENG_CONTENT_DEVELOPER("ROLE_ENG_CONTENT_DEVELOPER"), 
ROLE_FRA_CONTENT_DEVELOPER("ROLE_FRA_CONTENT_DEVELOPER"), 
ROLE_REVIEWER("ROLE_REVIEWER"), 
ROLE_ADMINISTRATOR("ROLE_ADMINISTRATOR"), 
ROLE_RELEASE_OPERATOR("ROLE_RELEASE_OPERATOR"), 
ROLE_READ_ONLY("ROLE_READ_ONLY"), 
ROLE_IT_ADMINISTRATOR("ROLE_IT_ADMINISTRATOR"), 
ROLE_REFSET_DEVELOPER("ROLE_REFSET_DEVELOPER");
private String role;

private SecurityRole(String role) {
	this.role = role;
}

private static final Log LOGGER = LogFactory.getLog(SecurityRole.class);
private static final Map<String, SecurityRole> STRING_TO_ENUM = new HashMap<String, SecurityRole>();

static { // Initialize map from constant name to enum constant
	for (SecurityRole sr : values()) {
		STRING_TO_ENUM.put(sr.getRole(), sr);
	}
}

public static SecurityRole fromString(final String role) {
	final SecurityRole sr = STRING_TO_ENUM.get(role);
	if (sr == null) {
		LOGGER.warn(
				new StringBuilder().append("Failed converting [").append(role).append("] to an SecurityRole type."));
	}
	return sr;
}

public String getRole() {
	return role;
}

public void setRole(String role) {
	this.role = role;
}

}
