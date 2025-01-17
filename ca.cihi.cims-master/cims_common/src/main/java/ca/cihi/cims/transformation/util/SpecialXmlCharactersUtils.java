package ca.cihi.cims.transformation.util;

import org.apache.commons.lang.StringUtils;

public class SpecialXmlCharactersUtils {

	public static final String RIGHT_SINGLE_QUOT = "’";
	public static final String REPLACEMENT_RIGHT_SINGLE_QUOT = "&#146;";
	public static final String LEFT_SINGLE_QUOT = "‘";
	public static final String REPLACEMENT_LEFT_SINGLE_QUOT = "&#145;";

	public static final String PATTERN_ALPHA = "(\\[)( )*agr( )+(\\])";
	public static final String ENCODED_ALPHA = "&amp;alpha;";
	public static final String PATTERN_BETA = "(\\[)( )*bgr( )+(\\])";
	public static final String ENCODED_BETA = "&amp;beta;";
	public static final String PATTERN_GAMMA = "(\\[)( )*ggr( )+(\\])";
	public static final String ENCODED_GAMMA = "&amp;gamma;";

	public static final String AMPERSAND = "&";
	public static final String ENCODED_AMPERSAND = "&amp;";

	public static String decodeSpecialSymbols(final String aString) {

		String resultString = aString;
		if (aString != null && !aString.isEmpty()) {
			resultString = StringUtils.replace(resultString, ENCODED_AMPERSAND, AMPERSAND);
		}

		return resultString;
	}

	public static String encodeSpecialSymbols(final String aString) {

		String resultString = aString;
		if (aString != null && !aString.isEmpty()) {
			resultString = StringUtils.replace(resultString, AMPERSAND, ENCODED_AMPERSAND);
			resultString = resultString.replaceAll(PATTERN_ALPHA, ENCODED_ALPHA);
			resultString = resultString.replaceAll(PATTERN_BETA, ENCODED_BETA);
			resultString = resultString.replaceAll(PATTERN_GAMMA, ENCODED_GAMMA);
		}

		return resultString;
	}

	public static String replace(String oriString) {

		if (oriString != null) {
			// replace the left single quotation mark and right single quotation mark with ASCII code
			oriString = oriString.replaceAll(LEFT_SINGLE_QUOT, REPLACEMENT_LEFT_SINGLE_QUOT);
			oriString = oriString.replaceAll(RIGHT_SINGLE_QUOT, REPLACEMENT_RIGHT_SINGLE_QUOT);
		}

		return oriString;

	}

}
