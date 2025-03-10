package ca.cihi.cims.model;

import java.io.Serializable;

public class SimpleMap implements Serializable {

	private static final long serialVersionUID = 1L;
	private String key;
	private String value;

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
