package ca.cihi.cims.service.folioclamlexport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkExtractor {

	public static final String EMPTY_STRING = "";
	public static final String GRAPHIC_ALIGN_PATTERN = "align=\"(.*?)\"";
	public static final String GRAPHIC_SRC_PATTERN = "src=\"(.*?)\"";
	public static final String GRAPHIC_STYLE_PATTERN = "style=\"(.*?)\"";
	public static final String URL_PATTERN = "href=\"(.*?)\"";

	public static String extractContent(String html, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(html);
		if (m.find()) {
			return m.group(1);
		} else {
			return EMPTY_STRING;
		}
	}

	public static List<String> extractLinks(String html) {
		Pattern p = Pattern.compile(URL_PATTERN);
		Matcher m = p.matcher(html);
		List<String> links = new ArrayList<>();
		while (m.find()) {
			if (m.group(1).startsWith("javascript")) {
				links.add(m.group(1));
			}
		}
		return links;
	}

	public static String extractPrefix(String url) {
		if (url == null) {
			return EMPTY_STRING;
		}
		if (url.indexOf("(") == -1) {
			return EMPTY_STRING;
		}
		return url.substring(0, url.indexOf("(")).trim();
	}

	/**
	 * The url should be "javascript:...('content')" format and this method return the content part
	 *
	 * @param url
	 * @return
	 */
	public static String extractURLContent(String url) {
		if ((url == null) || !url.startsWith("javascript")) {
			return EMPTY_STRING;
		}
		return url.substring(url.indexOf("'") + 1, url.lastIndexOf("'"));
	}

	public static void main(String[] args) {
		String html = "<div class=\"graphicDiv\" src=\"E_figure1icd.gif\" align=\"center\" style=\"height:150%;\"/>";
		Pattern p = Pattern.compile(GRAPHIC_SRC_PATTERN);
		Matcher m = p.matcher(html);
		while (m.find()) {
			System.out.println(m.group(1));
		}
	}

	public static String replaceGraphicTag(String htmlString) {
		int start = -1;
		while (true) {
			start = htmlString.indexOf("<div class=\"graphicDiv\"", start + 1);
			if (start < 0) {
				break;
			}

			int end = htmlString.indexOf("/>", start);
			String graphicString = htmlString.substring(start, end + 2);
			String graphicStringReserved = htmlString.substring(start, end);
			String src = LinkExtractor.extractContent(graphicString, LinkExtractor.GRAPHIC_SRC_PATTERN);
			String style = LinkExtractor.extractContent(graphicString, LinkExtractor.GRAPHIC_STYLE_PATTERN);
			String imgString = "<img id=\"" + src + "\" style=\"" + style + "\" src=\"" + src + "\"></>";
			htmlString = htmlString.replace(graphicString, graphicStringReserved + ">" + imgString + "</div>");
		}
		return htmlString;
	}
}
