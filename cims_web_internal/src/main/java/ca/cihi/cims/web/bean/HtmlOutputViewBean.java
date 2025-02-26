package ca.cihi.cims.web.bean;

import java.io.Serializable;

public class HtmlOutputViewBean implements Serializable {
	private static final long serialVersionUID = -3981588858416607863L;
	private String baseClassification;
	private String fiscalYear;
	private String language;

	public String getBaseClassification() {
		return baseClassification;
	}

	public void setBaseClassification(String baseClassification) {
		this.baseClassification = baseClassification;
	}

	public String getFiscalYear() {
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
