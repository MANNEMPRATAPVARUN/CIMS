package ca.cihi.cims.model.folioclamlexport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HierarchyModel implements Serializable {
	private static final long serialVersionUID = 8279096532275848395L;

	private String contentUrl;
	private String itemLabel;
	private QueryCriteria queryCriteria; // query criteria to get its children
	private List<HierarchyModel> children = new ArrayList<>();

	public QueryCriteria getQueryCriteria() {
		return queryCriteria;
	}

	public void setQueryCriteria(QueryCriteria queryCriteria) {
		this.queryCriteria = queryCriteria;
	}

	public String getContentUrl() {
		return contentUrl;
	}

	public void setContentUrl(String contentUrl) {
		this.contentUrl = contentUrl;
	}

	public String getItemLabel() {
		return itemLabel;
	}

	public void setItemLabel(String itemLabel) {
		this.itemLabel = itemLabel;
	}

	public List<HierarchyModel> getChildren() {
		return children;
	}

	public void setChildren(List<HierarchyModel> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return this.itemLabel;
	}

}
