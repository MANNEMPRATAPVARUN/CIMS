package ca.cihi.cims.util;

import org.apache.commons.lang.StringUtils;

public class SpecialCharactersUtils {

	public static final String ACUTE_ACCENT = new String("\u00b4");
	public static final String SINGLE_QUOTATION_MARK = new String("'");
	public static final String INVERTED_QUESTION_MARK = new String("\u00bf");

	// ----------------------------------------------------------

	public static String replace(String xml) {
		if (xml != null) {
			xml = StringUtils.replace(xml, INVERTED_QUESTION_MARK, ACUTE_ACCENT);
		}
		return xml;
	}

}
