package ca.cihi.cims.model;


public enum Classification {

	ICD, CCI;

	public static Classification fromBaseClassification(String baseClassification) {
		return valueOf(baseClassification.substring(0, 3));
	}

}
