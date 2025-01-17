package ca.cihi.cims.model;

import java.io.Serializable;

public class CciComponentRefLink implements Serializable {

	private static final long serialVersionUID = 1L;
	private String componentCode;
	private String code;
	private String shortDescriptionEng;
	private String shortDescriptionFra;

	public String getCode() {
		return code;
	}

	public String getComponentCode() {
		return componentCode;
	}

	public String getShortDescriptionEng() {
		return shortDescriptionEng;
	}

	public String getShortDescriptionFra() {
		return shortDescriptionFra;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setComponentCode(String componentCode) {
		this.componentCode = componentCode;
	}

	public void setShortDescriptionEng(String shortDescriptionEng) {
		this.shortDescriptionEng = shortDescriptionEng;
	}

	public void setShortDescriptionFra(String shortDescriptionFra) {
		this.shortDescriptionFra = shortDescriptionFra;
	}
}
