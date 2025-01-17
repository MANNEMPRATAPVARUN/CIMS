package ca.cihi.cims.model.index;

public class SiteIndicatorModel {

	private String code;
	private String description;

	// --------------------------------------

	public SiteIndicatorModel() {
	}

	public SiteIndicatorModel(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "SiteIndicatorModel [code=" + code + ", description=" + description + "]";
	}

}
