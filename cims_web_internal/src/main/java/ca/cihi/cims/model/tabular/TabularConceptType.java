package ca.cihi.cims.model.tabular;

import org.apache.commons.lang.WordUtils;

import ca.cihi.cims.model.Classification;

public enum TabularConceptType {

	ICD_CHAPTER(Classification.ICD), //
	ICD_BLOCK(Classification.ICD), //
	ICD_CATEGORY(Classification.ICD), //
	ICD_CODE(Classification.ICD), //

	CCI_SECTION(Classification.CCI), //
	CCI_BLOCK(Classification.CCI), //
	CCI_GROUP(Classification.CCI), //
	CCI_RUBRIC(Classification.CCI), //
	// TODO: rename into CCI_CODE
	// TODO: see [ClassificationService.toSubType]
	CCI_CCICODE(Classification.CCI),

	ICD_ALPHABETICINDEX(Classification.ICD); //

	private final Classification classification;
	private final String code;
	private final String label;

	// ----------------------------------------------------------

	private TabularConceptType(Classification classification) {
		this.classification = classification;
		this.code = name().substring(4, 5) + name().substring(5).toLowerCase();
		this.label = WordUtils.capitalize(code.toLowerCase().replace("cci", ""));
	}

	public Classification getClassification() {
		return classification;
	}

	public String getCode() {
		return code;
	}

	public String getLabel() {
		return label;
	}

}
