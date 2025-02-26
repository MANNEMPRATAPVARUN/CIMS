package ca.cihi.cims.transformation.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ca.cihi.cims.bll.ContextAccess;

/**
 * Provides utility methods for working with Href cross link.
 * 
 * @author szhang
 */

public final class UrlFormatUtils {

	private static final Log LOGGER = LogFactory.getLog(UrlFormatUtils.class);

	public static final String PATTERN = "href=\"#";

	public static Map<String, String> findAndTranformAnchorCodes(final String input, final ContextAccess contextAccess) {

		final Pattern pattern = Pattern.compile(PATTERN);
		final Matcher matcher = pattern.matcher(input);
		final List<Integer> startPositions = new ArrayList<Integer>();
		while (matcher.find()) {
			startPositions.add(matcher.start());
		}
		final Map<String, String> replaceMap = new HashMap<String, String>();
		String conceptCode;
		for (Integer beginIndex : startPositions) {
			final Integer endIndex = input.indexOf("\">", beginIndex);

			if (endIndex != -1) {
				conceptCode = input.substring(beginIndex + PATTERN.length(), endIndex);

				// idPath is full path like '32/37/57'
				final String idPath = contextAccess.determineContainingIdPath(conceptCode);
				final String javascriptFunction = "javascript:navigateFromDynaTree('" + idPath + "');\">";
				final String anchor = "#" + conceptCode + "\">";

				replaceMap.put(anchor, javascriptFunction);
			}
		}
		return replaceMap;
	}

	public static String formatPopupUrl(final String input) {
		LOGGER.info("< formatPopupUrl ");

		final String result = input.replace("&amp;", "&");

		LOGGER.info("> formatPopupUrl");

		return result;
	}

	public static String formatUrl(final String input, final Map<String, String> map) {
		LOGGER.info("< formatUrl");

		final String result = replace(input, map);
		LOGGER.info("> formatUrl");
		return result;
	}

	public static String replace(final String input, final Map<String, String> replacemap) {
		String result;

		if (input == null || "".equals(input) || replacemap == null || replacemap.isEmpty()) {
			result = input;
		} else {
			final StringBuilder regexBuilder = new StringBuilder();
			final Iterator<String> iterator = replacemap.keySet().iterator();
			regexBuilder.append(Pattern.quote(iterator.next()));
			while (iterator.hasNext()) {
				regexBuilder.append('|').append(Pattern.quote(iterator.next()));
			}
			final Matcher matcher = Pattern.compile(regexBuilder.toString()).matcher(input);
			final StringBuffer out = new StringBuffer(input.length() + (input.length() / 10));
			while (matcher.find()) {
				matcher.appendReplacement(out, replacemap.get(matcher.group()));
			}
			matcher.appendTail(out);
			result = out.toString();
		}

		return result;
	}

	private UrlFormatUtils() {
		super();
	}
	/*
	 * public static String formatUrl( final String input, final String classification, final String fiscalYear, final
	 * String language, final Map<String, String> map) { LOGGER.info("< formatUrl");
	 * 
	 * final String path = "href=\"" + "contents.htm?classification=" + classification + "&fiscalYear=" + fiscalYear +
	 * "&language=" + language + "&conceptId=#";
	 * 
	 * final String inputUrlWithPath = input.replace(PATTERN, path);
	 * 
	 * final String result = replace(inputUrlWithPath, map); LOGGER.info("> formatUrl"); return result; }
	 */
}
