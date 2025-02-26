package ca.cihi.cims.transformation.util;

import org.springframework.util.StringUtils;

public class HtmlStringUtils {

	private static final String XMLNS = "xmlns:ora=\"http://www.oracle.com/XSL/Transform/java\"";
	private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	public static String removeLineBreaks(String oriString) {
		String resultString = oriString;
		// Remove line breaks from the html
		resultString = resultString.trim().replaceAll("[\n\r\t]", "");
		resultString = resultString.replaceAll("[\\xA0\\x0A\\x0D]", "");
		return resultString;
	}

	public static String removeXmlnsAndVersion(String oriString) {
		String resultString = oriString;
		// Remove the xmlns and xmlVersion strings for reduce string size
		resultString = StringUtils.replace(resultString, XMLNS, "");
		resultString = StringUtils.replace(resultString, XML_VERSION, "");

		return resultString;
	}
}
