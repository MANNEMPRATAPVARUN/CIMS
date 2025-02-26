package ca.cihi.cims.dal;

import org.apache.commons.lang.ObjectUtils;

public abstract class DataPropertyVersion<T> extends PropertyVersion {

	public static final String VALUE_FIELD_PROPERTY_NAME = "value";

	public abstract T getValue();

	public boolean isSameValue(T newValue) {
		return ObjectUtils.equals(getValue(), newValue);
	}

	public abstract void setValue(T value);

}
