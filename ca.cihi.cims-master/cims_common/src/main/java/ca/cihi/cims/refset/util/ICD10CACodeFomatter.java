package ca.cihi.cims.refset.util;

import org.springframework.util.StringUtils;

public class ICD10CACodeFomatter implements CodeFormatter {

	@Override
	public String format(String origin) {
		if (StringUtils.isEmpty(origin)) {
			return "";
		}
		return origin.replaceAll("\\.|/", "");
	}

}
