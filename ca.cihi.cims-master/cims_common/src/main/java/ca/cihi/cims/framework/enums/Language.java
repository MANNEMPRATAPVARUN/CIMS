package ca.cihi.cims.framework.enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:22:23 AM
 */
public enum Language {
ENG("ENG"), FRA("FRA"), NOLANGUAGE("NOLANGUAGE");

private static final Map<String, Language> STRING_TO_ENUM = new HashMap<String, Language>();

// ---------------------------------------------------

static {
	// Initialize map from constant name to enum constant
	for (Language l : values()) {
		STRING_TO_ENUM.put(l.getCode(), l);
	}
}

// ---------------------------------------------------

private String code;

// ---------------------------------------------------

private Language(final String code) {
	this.code = code;
}

public boolean equalsIgnoreCase(String language) {
	return code.equalsIgnoreCase(language);
}

public String getCode() {
	return code;
}

@Override
public String toString() {
	return code;
}
}
