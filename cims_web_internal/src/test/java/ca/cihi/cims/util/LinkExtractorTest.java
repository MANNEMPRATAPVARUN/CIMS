package ca.cihi.cims.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import ca.cihi.cims.service.folioclamlexport.LinkExtractor;

public class LinkExtractorTest {

	@Test
	public void testExtractContent() {
		String html = "<a href=\"javascript:popupConceptDetail ('conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA');\">Test</a>";
		String expected = "javascript:popupConceptDetail ('conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA');";
		assertEquals(expected, LinkExtractor.extractContent(html, LinkExtractor.URL_PATTERN));
	}

	@Test
	public void testExtractLink() {
		String html = "<a href=\"javascript:popupConceptDetail ('conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA');\">Test</a>";
		List<String> links = LinkExtractor.extractLinks(html);
		String linkExpected = "javascript:popupConceptDetail ('conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA');";

		assertNotNull(links);
		assertEquals(links.size(), 1);

		assertEquals(linkExpected, links.get(0));
	}

	@Test
	public void testExtractPrefix() {
		String url = "javascript:popupConceptDetail ('conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA');";
		String prefix = LinkExtractor.extractPrefix(url);

		String prefixExpected = "javascript:popupConceptDetail";

		assertEquals(prefixExpected, prefix);
	}

	@Test
	public void testExtractURLContent() {
		String url = "javascript:popupConceptDetail ('conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA');";
		String content = LinkExtractor.extractURLContent(url);

		String contentExpected = "conceptDetailPopup.htm?refid=O08.20&language=ENG&classification=ICD-10-CA";

		assertEquals(contentExpected, content);
	}

	@Test
	public void testReplaceGraphicTag() {
		String html = "<div class=\"graphicDiv\" src=\"E_figure1icd.gif\" align=\"center\" style=\"height:150%;\"/>";
		assertNotNull(LinkExtractor.replaceGraphicTag(html));
	}
}