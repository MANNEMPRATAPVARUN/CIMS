package ca.cihi.cims.transformation.util;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class of UrlFormatUtils
 * 
 * @author wxing
 * 
 */
public class UrlFormatUtilsTest {

	@Test
	public void testFormatPopupUrl() {
		Assert
				.assertTrue("<a href=\"conceptDetailPopup.htm?refid=A10.0&language=ENG&classification=ICD-10-CA&fiscalYear=2001\"><b>A10.0&#134;</b></a>"
						.equals(UrlFormatUtils
								.formatPopupUrl("<a href=\"conceptDetailPopup.htm?refid=A10.0&language=ENG&classification=ICD-10-CA&fiscalYear=2001\"><b>A10.0&amp;#134;</b></a>")));
	}

	@Test
	public void testFormatUrl() {
		String input = "<a href=\"#A10.0\">A10.0</a>";
		// String classification = "CCI";
		// Long contextId = 1L;
		// String language = "ENG";
		Map<String, String> map = new HashMap<String, String>();
		String result = "<a href=\"javascript:navigateFromDynaTree";
		Assert.assertFalse(UrlFormatUtils.formatUrl(input, map).contains(result));
	}

	@Test
	public void testReplace() {
		Map<String, String> replacemap = new HashMap<String, String>();

		Assert.assertNull(UrlFormatUtils.replace(null, replacemap));
		Assert.assertTrue("".equals(UrlFormatUtils.replace("", replacemap)));
		Assert.assertTrue("test2".equals(UrlFormatUtils.replace("test2", null)));
		Assert.assertTrue("test".equals(UrlFormatUtils.replace("test", replacemap)));

		replacemap.put("#A39.8\">", "17#A39.8\">");

		Assert.assertTrue("This is a test <a href=\"17#A39.8\">A39.8</a>".equals(UrlFormatUtils.replace(
				"This is a test <a href=\"#A39.8\">A39.8</a>", replacemap)));

	}

}
