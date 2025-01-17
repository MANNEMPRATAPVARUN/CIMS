package ca.cihi.cims.model.changerequest;

import java.util.HashMap;
import java.util.Map;

public enum ChangeRequestLanguage {
	ENG("ENG"), FRA("FRA"), ALL("ALL");

	private String code;

	private static final Map<String, ChangeRequestLanguage> STRING_TO_ENUM = new HashMap<String, ChangeRequestLanguage>();

	static { // Initialize map from constant name to enum constant
		for (ChangeRequestLanguage lang : values()) {
			STRING_TO_ENUM.put(lang.getCode(), lang);
		}
	}

	private ChangeRequestLanguage(String code) {
		this.code = code;
	}

	public static ChangeRequestLanguage fromString(final String code) {
		ChangeRequestLanguage lang = STRING_TO_ENUM.get(code);
		return lang;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
