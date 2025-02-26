package ca.cihi.cims.content.shared;

import static ca.cihi.cims.Language.ENGLISH;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import ca.cihi.cims.ClassificationLanguage;
import ca.cihi.cims.Language;

@XmlTransient
public abstract class ValidationXml implements ClassificationLanguage {

	@XmlAttribute(name = "classification")
	private String classification;

	@XmlAttribute(name = "language")
	private String language;

	@XmlElement(name = "ELEMENT_ID")
	private long elementId;

	@XmlElement(name = "GENDER_CODE")
	private String genderCode;

	@XmlElement(name = "GENDER_DESC_ENG")
	private String genderDescriptionEng;

	@XmlElement(name = "GENDER_DESC_FRA")
	private String genderDescriptionFra;

	@XmlElement(name = "AGE_RANGE")
	private String ageRange;

	// -----------------------------------------------

	@Override
	public boolean equals(Object object) {
		boolean isEqual = false;
		if (object instanceof ValidationXml) {
			ValidationXml validationXml = (ValidationXml) object;
			isEqual = this.getClassification().equals(validationXml.getClassification())
					&& this.getGenderCode().equals(validationXml.getGenderCode())
					&& this.getAgeRange().equalsIgnoreCase(validationXml.getAgeRange());
		}
		return isEqual;
	}

	public int getAgeMax() {
		return Integer.parseInt(ageRange.substring(ageRange.indexOf('-') + 1));
	}

	public int getAgeMin() {
		return Integer.parseInt(ageRange.substring(0, ageRange.indexOf('-')));
	}

	public String getAgeRange() {
		return ageRange;
	}

	@Override
	public String getClassification() {
		return classification;
	}

	public long getElementId() {
		return elementId;
	}

	public String getGenderCode() {
		return genderCode;
	}

	public String getGenderDescription(Language lang) {
		return lang == ENGLISH ? genderDescriptionEng : genderDescriptionFra;
	}

	public String getGenderDescriptionEng() {
		return genderDescriptionEng;
	}

	public String getGenderDescriptionFra() {
		return genderDescriptionFra;
	}

	@Override
	public String getLanguage() {
		return language;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (classification == null ? 0 : classification.hashCode());
		result = prime * result + (ageRange == null ? 0 : ageRange.hashCode());
		result = prime * result + (genderCode == null ? 0 : genderCode.hashCode());
		return result;
	}

	public void setAgeRange(String ageRange) {
		this.ageRange = ageRange;
	}

	@Override
	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setGenderCode(String genderCode) {
		this.genderCode = genderCode;
	}

	public void setGenderDescriptionEng(String genderDescriptionEng) {
		this.genderDescriptionEng = genderDescriptionEng;
	}

	public void setGenderDescriptionFra(String genderDescriptionFra) {
		this.genderDescriptionFra = genderDescriptionFra;
	}

	@Override
	public void setLanguage(String language) {
		this.language = language;
	}

}
