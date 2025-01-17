package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.Language;

public enum Languages {
	Both("ALL","English & French"),
	English(Language.ENGLISH.getCode(),"English"),
	French(Language.FRENCH.getCode(), "French");
	
	private String code;
	private String label;
	
	private Languages(String code, String label) {
		this.code = code;
		this.label = label;
	}

	public String getCode() {
		return code;
	}
	public String getLabel() {
		return label;
	}
	public String toString() {
		return code;
	}
}