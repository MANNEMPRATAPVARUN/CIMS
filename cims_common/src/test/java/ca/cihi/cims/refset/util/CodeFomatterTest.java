package ca.cihi.cims.refset.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CodeFomatterTest {

	@Test
	public void cciCodeFormatterTest() {
		CCICodeFormatter formatter = new CCICodeFormatter();
		assertTrue(formatter.format(null).equals(""));
		assertTrue(formatter.format("").equals(""));
		assertTrue(formatter.format("1.AA.35.^^").equals("1AA35"));
	}

	@Test
	public void icdCodeFormatterTest() {
		ICD10CACodeFomatter formatter = new ICD10CACodeFomatter();
		assertTrue(formatter.format(null).equals(""));
		assertTrue(formatter.format("").equals(""));
		assertTrue(formatter.format("8/1000").equals("81000"));
		assertTrue(formatter.format("A35.98").equals("A3598"));
	}
}
