package ca.cihi.cims.content.icd;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "REFERENCE_LIST")
@XmlAccessorType(XmlAccessType.FIELD)
public class ReferenceListXml {

	@XmlElement(name = "INDEX_REF")
	@XmlElementWrapper(name = "INDEX_REF_LIST")
	private List<IndexReferenceXml> indexReferenceList = new ArrayList<IndexReferenceXml>();

	@XmlElement(name = "CATEGORY_REFERENCE")
	@XmlElementWrapper(name = "CATEGORY_REFERENCE_LIST")
	private List<CategoryReferenceXml> categoryReferenceList = new ArrayList<CategoryReferenceXml>();

	// ---------------------------------------------------

	public List<CategoryReferenceXml> getCategoryReferenceList() {
		return categoryReferenceList;
	}

	public List<IndexReferenceXml> getIndexReferenceList() {
		return indexReferenceList;
	}

	public void setCategoryReferenceList(List<CategoryReferenceXml> categoryReferenceList) {
		this.categoryReferenceList = categoryReferenceList;
	}

	public void setIndexReferenceList(List<IndexReferenceXml> indexReferenceList) {
		this.indexReferenceList = indexReferenceList;
	}

}
