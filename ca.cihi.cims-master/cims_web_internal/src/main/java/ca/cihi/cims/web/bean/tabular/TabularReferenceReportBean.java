package ca.cihi.cims.web.bean.tabular;

import java.util.List;

import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.IndexBookReferencedLink;
import ca.cihi.cims.model.TabularReferencedLink;

public class TabularReferenceReportBean {

	private String codeValue;
	private List<TabularReferencedLink> tabularReferencedLinks;
	private List<IndexBookReferencedLink> indexReferencedLinks;

	private List<CodeDescription> supplementReferencedLinks;

	// ---------------------------------------------------------------

	public String getCode() {
		return codeValue;
	}

	public String getCodeValue() {
		return codeValue;
	}

	public List<IndexBookReferencedLink> getIndexReferencedLinks() {
		return indexReferencedLinks;
	}

	public List<CodeDescription> getSupplementReferencedLinks() {
		return supplementReferencedLinks;
	}

	public List<TabularReferencedLink> getTabularReferencedLinks() {
		return tabularReferencedLinks;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	public void setIndexReferencedLinks(List<IndexBookReferencedLink> indexReferencedLinks) {
		this.indexReferencedLinks = indexReferencedLinks;
	}

	public void setSupplementReferencedLinks(List<CodeDescription> supplementReferencedLinks) {
		this.supplementReferencedLinks = supplementReferencedLinks;
	}

	public void setTabularReferencedLinks(List<TabularReferencedLink> tabularReferencedLinks) {
		this.tabularReferencedLinks = tabularReferencedLinks;
	}

}
