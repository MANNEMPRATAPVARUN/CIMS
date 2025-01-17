package ca.cihi.cims.framework.domain;

/**
 * @author tyang
 * @version 1.0
 * @created 13-Jun-2016 10:47:14 AM
 */
public class PropertyValue {

	private Object value;

	/**
	 * Do we need this as a class ??
	 *
	 * Note. Must match PropertyDTO.value type e.g. for ConceptProperty the value is of elementIdentifier type
	 */
	public PropertyValue() {
	}

	public PropertyValue(Object value) {
		setValue(value);
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
		PropertyValue other = (PropertyValue) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	public Object getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}