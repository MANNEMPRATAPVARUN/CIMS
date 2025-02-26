package ca.cihi.cims.dal;

import ca.cihi.cims.dal.annotations.RequiredForUpdate;

public class GraphicsPropertyVersion extends DataPropertyVersion<byte[]> implements LanguageSpecific {

	@RequiredForUpdate
	private byte[] value;
	
	private String languageCode;

	@Override
	public byte[] getValue() {
		return value;
	}

	@Override
	public void setValue(byte[] value) {
		this.value = value;
	}

	@Override
	public String getLanguageCode() {
		return languageCode;
	}

	@Override
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}
}
