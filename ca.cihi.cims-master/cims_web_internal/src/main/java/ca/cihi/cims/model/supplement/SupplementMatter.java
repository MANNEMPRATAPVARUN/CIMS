package ca.cihi.cims.model.supplement;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.CIMSException;

public enum SupplementMatter {
	FRONT("F"), BACK("B");

	public static SupplementMatter fromCode(String code) {
		if (StringUtils.equals(FRONT.getCode(), code)) {
			return FRONT;
		} else if (StringUtils.equals(BACK.getCode(), code)) {
			return BACK;
		} else {
			throw new CIMSException("Unknown code:" + code);
		}

	}

	// --------------------------------------------

	private String code;

	// --------------------------------------------

	private SupplementMatter(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

}
