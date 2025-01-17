package ca.cihi.cims.model;

import java.io.Serializable;

public class IdCodeDescription implements Serializable {
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private long id;
	private String code;
	private String description;

	// -------------------------------

	public String getCode() {
		return code;
	}

	public String getCodeDescription() {
		return code + " - " + description;
	}

	public String getDescription() {
		return description;
	}

	public long getId() {
		return id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "IdCodeDescription [id=" + id + ", code=" + code + ", description=" + description + "]";
	}

}
