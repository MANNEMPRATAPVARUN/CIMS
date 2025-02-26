package ca.cihi.cims.hg.mapper.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyNameExtractor {
	private static final Pattern PROPERTY_PATTERN = Pattern
			.compile("((get)|(is)|(set))(.)(.*)");

	public String extractPropertyName(String methodName) {

		Matcher matcher = PROPERTY_PATTERN.matcher(methodName);
		if (!matcher.matches())
			throw new IllegalArgumentException(
					"Problem: cannot extract a property name from method named "
							+ methodName + ".");

		return matcher.group(5).toLowerCase() + matcher.group(6);
	}
}
