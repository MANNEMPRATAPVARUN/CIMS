package ca.cihi.cims;

public enum IndexBookType {
	A("AlphabeticIndex"), D("DrugsAndChemicalsIndex"), E("ExternalInjuryIndex"), N("NeoplasmIndex"), CCI_A("Index");

	private String code;

	private IndexBookType(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
