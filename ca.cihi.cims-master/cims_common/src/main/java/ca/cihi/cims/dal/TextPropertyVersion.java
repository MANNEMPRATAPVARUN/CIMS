package ca.cihi.cims.dal;

public class TextPropertyVersion extends StringPropertyVersion implements LanguageSpecific {

	private String languageCode;

	@Override
	public String getLanguageCode() {
		return languageCode;
	}

	@Override
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

}
