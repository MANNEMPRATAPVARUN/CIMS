package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

public class ChangeRequestLanguageDTO implements Serializable {
	private static final long serialVersionUID = 201501141846L;

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
