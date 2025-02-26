package ca.cihi.cims.model.changerequest;

public enum ChangeRequestCategory {
	T("Tabular List"), I("Index"), S("Supplements");

	private String code;

	// --------------------------------------------

	private ChangeRequestCategory(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public String getSubject() {
		switch (this) {
		case I:
			return "Index";
		case S:
			return "Supplement";
		case T:
			return "Tabular";
		default:
			throw new RuntimeException("Unknown category: " + this);
		}
	}

}
