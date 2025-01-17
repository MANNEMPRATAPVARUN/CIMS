package ca.cihi.cims.model.tabular.validation;

public class TabularConceptValidationGenderModel {

	public static String CODE_MALE_FEMALE_OTHER = "A";

	private long elementId;
	private String code;
	private String title;

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
		return "TabularConceptValidationGenderModel [elementId=" + elementId + ", title=" + title + "]";
	}

}
