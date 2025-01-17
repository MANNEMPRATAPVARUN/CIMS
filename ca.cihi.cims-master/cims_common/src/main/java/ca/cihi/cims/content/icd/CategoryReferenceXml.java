package ca.cihi.cims.content.icd;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

@XmlRootElement(name = "CATEGORY_REFERENCE")
@XmlAccessorType(XmlAccessType.FIELD)
public class CategoryReferenceXml {

	@XmlElement(name = "MAIN_CODE_PRESENTATION")
	private String mainCodePresentation;
	@XmlElement(name = "MAIN_CONTAINER_CONCEPT_ID")
	private String mainContainerConceptIdPath;
	@XmlElement(name = "MAIN_CODE")
	private String mainCode;
	@XmlElement(name = "MAIN_DAGGER_ASTERISK")
	private String mainCodeDaggerAsteriskCode;

	@XmlElement(name = "PAIRED_FLAG")
	private String pairedFlag;
	@XmlElement(name = "SORT_STRING")
	private String sortString;

	@XmlElement(name = "PAIRED_CODE_PRESENTATION")
	private String pairedCodePresentation;
	@XmlElement(name = "PAIRED_CONTAINER_CONCEPT_ID")
	private String pairedContainerConceptIdPath;
	@XmlElement(name = "PAIRED_CODE")
	private String pairedCode;
	@XmlElement(name = "PAIRED_DAGGER_ASTERISK")
	private String pairedCodeDaggerAsteriskCode;

	// -------------------------------------------------

	public String getMainCode() {
		return mainCode;
	}

	public String getMainCodeDaggerAsteriskCode() {
		return mainCodeDaggerAsteriskCode;
	}

	public String getMainCodePresentation() {
		return mainCodePresentation;
	}

	public String getMainContainerConceptIdPath() {
		return mainContainerConceptIdPath;
	}

	public long getMainElementId() {
		if (StringUtils.isEmpty(mainContainerConceptIdPath)) {
			return -1;
		} else {
			return Long
					.parseLong(mainContainerConceptIdPath.substring(mainContainerConceptIdPath.lastIndexOf("/") + 1));
		}
	}

	public String getPairedCode() {
		return pairedCode;
	}

	public String getPairedCodeDaggerAsteriskCode() {
		return pairedCodeDaggerAsteriskCode;
	}

	public String getPairedCodePresentation() {
		return pairedCodePresentation;
	}

	public String getPairedContainerConceptIdPath() {
		return pairedContainerConceptIdPath;
	}

	public long getPairedElementId() {
		if (StringUtils.isEmpty(pairedContainerConceptIdPath)) {
			return -1;
		} else {
			return Long.parseLong(pairedContainerConceptIdPath
					.substring(pairedContainerConceptIdPath.lastIndexOf("/") + 1));
		}
	}

	public String getPairedFlag() {
		return pairedFlag;
	}

	public String getSortString() {
		return sortString;
	}

	public void setMainCode(String mainCode) {
		this.mainCode = mainCode;
	}

	public void setMainCodeDaggerAsteriskCode(String mainCodeDaggerAsteriskCode) {
		this.mainCodeDaggerAsteriskCode = mainCodeDaggerAsteriskCode;
	}

	public void setMainCodePresentation(String mainCodePresentation) {
		this.mainCodePresentation = mainCodePresentation;
	}

	public void setMainContainerConceptIdPath(String mainContainerConceptIdPath) {
		this.mainContainerConceptIdPath = mainContainerConceptIdPath;
	}

	public void setPairedCode(String pairedCode) {
		this.pairedCode = pairedCode;
	}

	public void setPairedCodeDaggerAsteriskCode(String pairedCodeDaggerAsteriskCode) {
		this.pairedCodeDaggerAsteriskCode = pairedCodeDaggerAsteriskCode;
	}

	public void setPairedCodePresentation(String pairedCodePresentation) {
		this.pairedCodePresentation = pairedCodePresentation;
	}

	public void setPairedContainerConceptIdPath(String pairedContainerConceptIdPath) {
		this.pairedContainerConceptIdPath = pairedContainerConceptIdPath;
	}

	public void setPairedFlag(String pairedFlag) {
		this.pairedFlag = pairedFlag;
	}

	public void setSortString(String sortString) {
		this.sortString = sortString;
	}

	@Override
	public String toString() {
		return "CategoryReferenceXml [mainCode=" + mainCode + ", mainCodeDaggerAsteriskCode="
				+ mainCodeDaggerAsteriskCode + ", mainCodePresentation=" + mainCodePresentation
				+ ", mainContainerConceptIdPath=" + mainContainerConceptIdPath + ", pairedCode=" + pairedCode
				+ ", pairedCodeDaggerAsteriskCode=" + pairedCodeDaggerAsteriskCode + ", pairedCodePresentation="
				+ pairedCodePresentation + ", pairedContainerConceptIdPath=" + pairedContainerConceptIdPath
				+ ", pairedFlag=" + pairedFlag + ", sortString=" + sortString + "]";
	}

}
