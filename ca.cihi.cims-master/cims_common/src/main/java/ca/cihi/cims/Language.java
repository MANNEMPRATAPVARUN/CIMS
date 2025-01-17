package ca.cihi.cims;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.cihi.cims.util.CollectionUtils;

/**
 * @author szhang
 */
public enum Language {

	ENGLISH("ENG"), FRENCH("FRA");

	// ---------------------------------------------------

	private static final Log LOGGER = LogFactory.getLog(Language.class);
	private static final Map<String, Language> STRING_TO_ENUM = new HashMap<String, Language>();
	public static final Set<Language> NONE = Collections.emptySet();
	public static final Set<Language> ALL = Collections.unmodifiableSet(new HashSet<Language>(Arrays.asList(Language
			.values())));

	// ---------------------------------------------------

	static {
		// Initialize map from constant name to enum constant
		for (Language l : values()) {
			STRING_TO_ENUM.put(l.getCode(), l);
		}
	}

	public static Set<Language> asSet(String code) {
		if (code.equalsIgnoreCase("ALL")) {
			return ALL;
		} else {
			return CollectionUtils.asSet(fromString(code));
		}
	}

	public static Language fromString(String code) {
		Language l = STRING_TO_ENUM.get(code);
		if (l == null) {
			LOGGER.warn("Failed converting [" + code + "] to an Language.");
		}
		return l;
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
