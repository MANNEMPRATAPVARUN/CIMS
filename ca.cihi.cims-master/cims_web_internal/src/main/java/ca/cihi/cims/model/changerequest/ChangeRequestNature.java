package ca.cihi.cims.model.changerequest;

public enum ChangeRequestNature {
	M("Major Tabular"),
	N("Minor Tabular"),
	F("Front Matter"),
	O("Alphabetical Index"),
	P("External Cause Index"),
	Q("Table of Drugs and Chemicals Index"),
	R("Neoplasm Index"),
	X("Back Matter"),
	Z("Classification Tables"),
	Y("Attribute Only");

	private final String label;

	// --------------------------------------------

	private ChangeRequestNature(String code) {
		this.label = code;
	}

	public String getLabel() {
		return label;
	}
}
