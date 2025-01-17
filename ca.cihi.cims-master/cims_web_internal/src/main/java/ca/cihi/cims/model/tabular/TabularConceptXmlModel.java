package ca.cihi.cims.model.tabular;

import ca.cihi.cims.content.shared.TabularConcept;

public class TabularConceptXmlModel {

	private long elementId;
	private String code;
	private TabularConceptXmlType type;

	private String parentCode;

	private String englishXml;
	private String frenchXml;

	/** for internal use only */
	private TabularConcept rawConcept;
	private TabularConceptType conceptType;

	// ----------------------------------------

	public String getCode() {
		return code;
	}

	public TabularConceptType getConceptType() {
		return conceptType;
	}

	public long getElementId() {
		return elementId;
	}

	public String getEnglishXml() {
		return englishXml;
	}

	public String getFrenchXml() {
		return frenchXml;
	}

	public String getParentCode() {
		return parentCode;
	}

	public TabularConcept getRawConcept() {
		return rawConcept;
	}

	public TabularConceptXmlType getType() {
		return type;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setConceptType(TabularConceptType conceptType) {
		this.conceptType = conceptType;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setEnglishXml(String englishXml) {
		this.englishXml = "".equals(englishXml) ? null : englishXml;
	}

	public void setFrenchXml(String frenchXml) {
		this.frenchXml = "".equals(frenchXml) ? null : frenchXml;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public void setRawConcept(TabularConcept rawConcept) {
		this.rawConcept = rawConcept;
	}

	public void setType(TabularConceptXmlType type) {
		this.type = type;
	}

}
