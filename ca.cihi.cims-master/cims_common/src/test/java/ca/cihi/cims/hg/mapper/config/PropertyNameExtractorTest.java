package ca.cihi.cims.hg.mapper.config;

import org.junit.Assert;
import org.junit.Test;

public class PropertyNameExtractorTest {

	@Test
	public void testPropertyNamesCorrectlyExtracted() {
		Assert.assertEquals("chocolate", getPropName("getChocolate"));
		Assert.assertEquals("bold", getPropName("isBold"));
		Assert.assertEquals("set", getPropName("setSet"));
		Assert.assertEquals("camelCaps", getPropName("isCamelCaps"));

	}

	private String getPropName(String methodName) {
		return new PropertyNameExtractor().extractPropertyName(methodName);
	}
}
