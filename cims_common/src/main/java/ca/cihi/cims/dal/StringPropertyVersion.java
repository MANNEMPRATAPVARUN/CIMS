package ca.cihi.cims.dal;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.dal.annotations.RequiredForUpdate;

public class StringPropertyVersion extends DataPropertyVersion<String> {

	@RequiredForUpdate
	private String value;

	private String getNormalizedValue(String str) {
		return StringUtils.remove(str, '\r');
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public final boolean isSameValue(String newValue) {
		return ObjectUtils.equals(getNormalizedValue(newValue), value);
	}

	@Override
	public final void setValue(String value) {
		this.value = getNormalizedValue(value);
	}

}
