package ca.cihi.cims.refset.util;

import ca.cihi.cims.transformation.util.RomanNumeralUtil;

public class RomanNumberFormatter implements CodeFormatter {

	@Override
	public String format(String origin) {
		return RomanNumeralUtil.int2RomanNumberal(origin);
	}

}
