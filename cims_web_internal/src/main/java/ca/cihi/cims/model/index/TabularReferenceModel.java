package ca.cihi.cims.model.index;

public class TabularReferenceModel {

	private long elementId;
	private String customDescription;

	// ---------------------------------------

	public String getCustomDescription() {
		return customDescription;
	}

	public long getElementId() {
		return elementId;
	}

	public void setCustomDescription(String customDescription) {
		this.customDescription = customDescription;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	@Override
	public String toString() {
		return "TabularReferenceModel [customDescription=" + customDescription + ", elementId=" + elementId + "]";
	}

}
