package ca.cihi.cims.model.changerequest;

public class IncompleteProperty {

	private String codeValue;
	private String incompleteRatoinale;
	private String breadCrumbs;

	public String getBreadCrumbs() {
		return breadCrumbs;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public String getIncompleteRatoinale() {
		return incompleteRatoinale;
	}

	public void setBreadCrumbs(String breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public void setIncompleteRatoinale(String incompleteRatoinale) {
		this.incompleteRatoinale = incompleteRatoinale;
	}

}
