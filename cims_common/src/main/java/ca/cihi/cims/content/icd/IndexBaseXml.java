package ca.cihi.cims.content.icd;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import ca.cihi.cims.ClassificationLanguage;

@XmlAccessorType(XmlAccessType.FIELD)
public abstract class IndexBaseXml implements ClassificationLanguage {

	@XmlAttribute(name = "language")
	private String language;

	@XmlAttribute(name = "classification")
	private String classification;

	@XmlElement(name = "BOOK_INDEX_TYPE")
	private String bookIndexType;

	@XmlElement(name = "ELEMENT_ID")
	private long elementId;

	@XmlElement(name = "INDEX_TYPE")
	private String indexType;

	@XmlElement(name = "LEVEL_NUM")
	private long levelNum;

	@XmlElement(name = "INDEX_TERM_DESC")
	private String indexTermDesc;

	@XmlElement(name = "SEE_ALSO_FLAG")
	private String seeAlsoFlag;

	@XmlElement(name = "SITE_INDICATOR")
	private String siteIndicator;

	@XmlElement(name = "REFERENCE_LIST")
	private ReferenceListXml referenceList;

	// ----------------------------------------

	public String getBookIndexType() {
		return bookIndexType;
	}

	public List<CategoryReferenceXml> getCategoryReferenceList() {
		List<CategoryReferenceXml> list = referenceList == null ? null : referenceList.getCategoryReferenceList();
		return list == null ? Collections.<CategoryReferenceXml> emptyList() : list;
	}

	public String getClassification() {
		return classification;
	}

	public long getElementId() {
		return elementId;
	}

	public List<IndexReferenceXml> getIndexReferenceList() {
		List<IndexReferenceXml> list = referenceList == null ? null : referenceList.getIndexReferenceList();
		return list == null ? Collections.<IndexReferenceXml> emptyList() : list;
	}

	public String getIndexTermDesc() {
		return indexTermDesc;
	}

	public String getIndexType() {
		return indexType;
	}

	public String getLanguage() {
		return language;
	}

	public long getLevelNum() {
		return levelNum;
	}

	public ReferenceListXml getReferenceList() {
		return referenceList;
	}

	public String getSeeAlsoFlag() {
		return seeAlsoFlag;
	}

	public String getSiteIndicator() {
		return siteIndicator;
	}

	public void setBookIndexType(String bookIndexType) {
		this.bookIndexType = bookIndexType;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setIndexTermDesc(String indexTermDesc) {
		this.indexTermDesc = indexTermDesc;
	}

	public void setIndexType(String indexType) {
		this.indexType = indexType;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLevelNum(long levelNum) {
		this.levelNum = levelNum;
	}

	public void setReferenceList(ReferenceListXml referenceList) {
		this.referenceList = referenceList;
	}

	public void setSeeAlsoFlag(String seeAlsoFlag) {
		this.seeAlsoFlag = seeAlsoFlag;
	}

	public void setSiteIndicator(String siteIndicator) {
		this.siteIndicator = siteIndicator;
	}

}
