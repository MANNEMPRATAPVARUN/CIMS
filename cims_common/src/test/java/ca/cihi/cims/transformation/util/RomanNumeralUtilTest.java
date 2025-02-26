package ca.cihi.cims.transformation.util;

import org.junit.Test;

public class RomanNumeralUtilTest {

	@Test
	public void testInt2RomanNumberal() {
		junit.framework.Assert.assertEquals("I", RomanNumeralUtil.int2RomanNumberal(1));
		junit.framework.Assert.assertEquals("II", RomanNumeralUtil.int2RomanNumberal(2));
		junit.framework.Assert.assertEquals("III", RomanNumeralUtil.int2RomanNumberal(3));
		junit.framework.Assert.assertEquals("IV", RomanNumeralUtil.int2RomanNumberal(4));
		junit.framework.Assert.assertEquals("V", RomanNumeralUtil.int2RomanNumberal(5));
		junit.framework.Assert.assertEquals("VI", RomanNumeralUtil.int2RomanNumberal(6));
		junit.framework.Assert.assertEquals("VII", RomanNumeralUtil.int2RomanNumberal(7));
		junit.framework.Assert.assertEquals("VIII", RomanNumeralUtil.int2RomanNumberal(8));
		junit.framework.Assert.assertEquals("IX", RomanNumeralUtil.int2RomanNumberal(9));
		junit.framework.Assert.assertEquals("X", RomanNumeralUtil.int2RomanNumberal(10));
		junit.framework.Assert.assertEquals("XI", RomanNumeralUtil.int2RomanNumberal(11));
		junit.framework.Assert.assertEquals("XII", RomanNumeralUtil.int2RomanNumberal(12));
		junit.framework.Assert.assertEquals("XIII", RomanNumeralUtil.int2RomanNumberal(13));
		junit.framework.Assert.assertEquals("XIV", RomanNumeralUtil.int2RomanNumberal(14));
		junit.framework.Assert.assertEquals("XV", RomanNumeralUtil.int2RomanNumberal(15));
		junit.framework.Assert.assertEquals("XVI", RomanNumeralUtil.int2RomanNumberal(16));
		junit.framework.Assert.assertEquals("XVII", RomanNumeralUtil.int2RomanNumberal(17));
		junit.framework.Assert.assertEquals("XVIII", RomanNumeralUtil.int2RomanNumberal(18));
		junit.framework.Assert.assertEquals("XIX", RomanNumeralUtil.int2RomanNumberal(19));
		junit.framework.Assert.assertEquals("XX", RomanNumeralUtil.int2RomanNumberal(20));
		junit.framework.Assert.assertEquals("XXI", RomanNumeralUtil.int2RomanNumberal(21));
		junit.framework.Assert.assertEquals("XXII", RomanNumeralUtil.int2RomanNumberal(22));
		junit.framework.Assert.assertEquals("XXIII", RomanNumeralUtil.int2RomanNumberal(23));
		junit.framework.Assert.assertEquals("XXIV", RomanNumeralUtil.int2RomanNumberal(24));
		junit.framework.Assert.assertEquals("XXV", RomanNumeralUtil.int2RomanNumberal(25));
		junit.framework.Assert.assertEquals("XXVI", RomanNumeralUtil.int2RomanNumberal(26));
		junit.framework.Assert.assertEquals("XXVII", RomanNumeralUtil.int2RomanNumberal(27));
		junit.framework.Assert.assertEquals("XXVIII", RomanNumeralUtil.int2RomanNumberal(28));
		junit.framework.Assert.assertEquals("XXIX", RomanNumeralUtil.int2RomanNumberal(29));
		junit.framework.Assert.assertEquals("XXX", RomanNumeralUtil.int2RomanNumberal(30));

		junit.framework.Assert.assertEquals("invalid interger format:30a", RomanNumeralUtil.int2RomanNumberal("30a"));
		junit.framework.Assert.assertEquals("Not in valid range:-1", RomanNumeralUtil.int2RomanNumberal("-1"));
		junit.framework.Assert.assertEquals("Not in valid range:0", RomanNumeralUtil.int2RomanNumberal("0"));
	}
}
