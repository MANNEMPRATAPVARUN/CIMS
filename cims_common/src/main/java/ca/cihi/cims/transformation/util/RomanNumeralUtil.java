package ca.cihi.cims.transformation.util;

import java.util.LinkedHashMap;
import java.util.Map;

public class RomanNumeralUtil {

	public static String int2RomanNumberal(final int intInput) {
		int inputNumber = intInput;

		final LinkedHashMap<String, Integer> roman_numerals = new LinkedHashMap<String, Integer>();
		roman_numerals.put("M", 1000);
		roman_numerals.put("CM", 900);
		roman_numerals.put("D", 500);
		roman_numerals.put("CD", 400);
		roman_numerals.put("C", 100);
		roman_numerals.put("XC", 90);
		roman_numerals.put("L", 50);
		roman_numerals.put("XL", 40);
		roman_numerals.put("X", 10);
		roman_numerals.put("IX", 9);
		roman_numerals.put("V", 5);
		roman_numerals.put("IV", 4);
		roman_numerals.put("I", 1);

		final StringBuffer res = new StringBuffer();

		if (intInput < 1) {
			res.append("Not in valid range:" + intInput);
		} else {
			for (Map.Entry<String, Integer> entry : roman_numerals.entrySet()) {
				final int matches = inputNumber / entry.getValue();
				res.append(repeat(entry.getKey(), matches));
				inputNumber = inputNumber % entry.getValue();
			}
		}
		return res.toString();
	}

	public static String int2RomanNumberal(final String intStr) {
		String romanNumberal;
		try {
			romanNumberal = int2RomanNumberal(Integer.parseInt(intStr));
		} catch (NumberFormatException ex) {
			romanNumberal = "invalid interger format:" + intStr;
		}

		return romanNumberal;
	}

	private static String repeat(final String aString, final int aNumber) {
		if (aString == null) {
			return null;
		}
		final StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < aNumber; i++) {
			stringBuilder.append(aString);
		}
		return stringBuilder.toString();
	}
}
