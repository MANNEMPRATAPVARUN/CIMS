package ca.cihi.cims.model.refset;

import java.io.Serializable;

import ca.cihi.cims.framework.ElementIdentifier;

public class SupplementViewEntity implements Serializable {
	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 9087762L;

	/**
	 * Element Identifier.
	 */
	private ElementIdentifier elementIdentifier;

	/**
	 * Supplement Code.
	 */
	private String code;

	/**
	 * Supplement Name.
	 */
	private String name;

	/**
	 * Supplement Filename.
	 */
	private String filename;

	public ElementIdentifier getElementIdentifier() {
		return elementIdentifier;
	}

	public void setElementIdentifier(ElementIdentifier elementIdentifier) {
		this.elementIdentifier = elementIdentifier;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((elementIdentifier == null) ? 0 : elementIdentifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SupplementViewEntity other = (SupplementViewEntity) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (elementIdentifier == null) {
			if (other.elementIdentifier != null)
				return false;
		} else if (!elementIdentifier.equals(other.elementIdentifier))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SupplementViewBean [elementIdentifier=" + elementIdentifier + ", code=" + code + ", name=" + name
		        + ", filename=" + filename + "]";
	}
}
