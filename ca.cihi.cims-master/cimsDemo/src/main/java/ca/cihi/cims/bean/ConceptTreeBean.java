package ca.cihi.cims.bean;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.model.Concept;

/**
 * @author szhang
 */

public class ConceptTreeBean extends BaseTreeBean {

	private static final long serialVersionUID = -3991565776255262604L;

	private List<Concept> conceptList;
	private List<ConceptTreeBean> children = new ArrayList<ConceptTreeBean>();

	public List<Concept> getConceptList() {
		return conceptList;
	}

	public void setConceptList(List<Concept> conceptList) {
		this.conceptList = conceptList;
	}

	public void addChild(ConceptTreeBean child) {
		this.children.add(child);
	}

	public List<ConceptTreeBean> getChildren() {
		return children;
	}

	public void setChildren(List<ConceptTreeBean> children) {
		this.children = children;
	}
}
