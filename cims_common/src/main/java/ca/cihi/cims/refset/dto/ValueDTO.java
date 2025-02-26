package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class ValueDTO implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2122402609398094808L;
	private Long idValue;
	private String textValue;
	/**
	 * only used when refresh cims changed values, not used in picklist textvalue saving
	 */
	private String languageCode;

	public Long getIdValue() {
		return idValue;
	}

	public void setIdValue(Long idValue) {
		this.idValue = idValue;
	}

	public String getTextValue() {
		return textValue;
	}

	public void setTextValue(String textValue) {
		this.textValue = textValue;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
}
