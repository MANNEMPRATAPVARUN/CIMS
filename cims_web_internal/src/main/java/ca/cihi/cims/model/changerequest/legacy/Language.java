package ca.cihi.cims.model.changerequest.legacy;

import java.io.Serializable;

public class Language implements Serializable {
	private static final long serialVersionUID = 201410211249L;

    private String languageCode;
    private String languageDesc;

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getLanguageDesc() {
		return languageDesc;
	}

	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}


}
