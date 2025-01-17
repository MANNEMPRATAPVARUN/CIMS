package ca.cihi.cims.framework;

import java.io.Serializable;

/**
 * @author tyang
 * @version 1.0
 * @created 03-Jun-2016 10:19:07 AM
 */
public class ElementIdentifier implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 2434320033833828344L;
	private Long elementId;
	private Long elementVersionId;

	public ElementIdentifier() {

	}

	public ElementIdentifier(Long elementId, Long elementVersionId) {
		this.elementId = elementId;
		this.elementVersionId = elementVersionId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ElementIdentifier other = (ElementIdentifier) obj;
		if (elementId == null) {
			if (other.elementId != null) {
				return false;
			}
		} else if (!elementId.equals(other.elementId)) {
			return false;
		}
		if (elementVersionId == null) {
			if (other.elementVersionId != null) {
				return false;
			}
		} else if (!elementVersionId.equals(other.elementVersionId)) {
			return false;
		}
		return true;
	}

	public Long getElementId() {
		return elementId;
	}

	public Long getElementVersionId() {
		return elementVersionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((elementId == null) ? 0 : elementId.hashCode());
		result = (prime * result) + ((elementVersionId == null) ? 0 : elementVersionId.hashCode());
		return result;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setElementVersionId(Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

}