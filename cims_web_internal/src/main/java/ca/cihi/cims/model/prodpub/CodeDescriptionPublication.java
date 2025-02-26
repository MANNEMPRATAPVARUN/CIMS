package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

/*
 * can be used for BLK, Code, Desc
 */
public class CodeDescriptionPublication implements Serializable, Comparable<CodeDescriptionPublication> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String code;
	private String shortTitle;
	private String longTitle;

	@Override
	public int compareTo(CodeDescriptionPublication other) {

		return this.code.compareTo(other.getCode());
	}

	public String getCode() {
		return code;
	}

	public String getLongTitle() {
		return longTitle;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setLongTitle(String longTitle) {
		this.longTitle = longTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

}
