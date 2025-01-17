package ca.cihi.cims.transformation.util;

import junit.framework.Assert;

import org.junit.Test;

public class SpecialXmlCharactersUtilsTest {

	@Test
	public void testDecode() {
		Assert.assertEquals(
				SpecialXmlCharactersUtils.decodeSpecialSymbols("&amp;#134;&amp;#134;&amp;diams;&amp;le;&amp;ge;"),
				"&#134;&#134;&diams;&le;&ge;");

		Assert.assertEquals(SpecialXmlCharactersUtils.decodeSpecialSymbols("&amp;alpha;"), "&alpha;");

		Assert.assertEquals(SpecialXmlCharactersUtils.decodeSpecialSymbols("&amp;beta;"), "&beta;");

		Assert.assertEquals(SpecialXmlCharactersUtils.decodeSpecialSymbols("&amp;gamma;"), "&gamma;");
	}

	@Test
	public void testEncode() {
		Assert.assertEquals(SpecialXmlCharactersUtils.encodeSpecialSymbols("&#134;&dagger;&diams;&le;&ge;"),
				"&amp;#134;&amp;dagger;&amp;diams;&amp;le;&amp;ge;");

		Assert.assertEquals(SpecialXmlCharactersUtils.encodeSpecialSymbols("[agr ]"), "&amp;alpha;");

		Assert.assertEquals(SpecialXmlCharactersUtils.encodeSpecialSymbols("[bgr  ]"), "&amp;beta;");

		Assert.assertEquals(SpecialXmlCharactersUtils.encodeSpecialSymbols("[ggr   ]"), "&amp;gamma;");
	}

	@Test
	public void testReplace() {
		Assert.assertEquals(SpecialXmlCharactersUtils.replace("‘Single quotation test’"),
				"&#145;Single quotation test&#146;");
	}

}