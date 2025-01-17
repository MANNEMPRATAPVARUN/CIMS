package ca.cihi.cims;

public enum ConceptType {
	T("Tabular"), I("Index"), S("Supplements"), C("CciComponent");

	private String code;

	private ConceptType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
