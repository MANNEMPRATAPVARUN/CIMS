package ca.cihi.cims.model;

public class ContentToSynchronize {

	private long elementId;
	private String type;

	// ------------------------------------------

	public long getElementId() {
		return elementId;
	}

	public String getType() {
		return type;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " [elementId=" + elementId + ", type=" + type + "]";
	}

}
