package ca.cihi.cims.model.tabular.validation;

public class TabularConceptValidationDadHoldingModel {

	private long elementId;
	private String title;
	private String code;

	// ------------------------------

	public String getCode() {
		return code;
	}

	public long getElementId() {
		return elementId;
	}

	public String getTitle() {
		return title;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "TabularConceptValidationDadHoldingModel [elementId=" + elementId + ", title=" + title + "]";
	}

}
