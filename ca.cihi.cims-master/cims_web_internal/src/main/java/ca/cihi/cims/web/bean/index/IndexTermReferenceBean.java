package ca.cihi.cims.web.bean.index;

import ca.cihi.cims.model.index.IndexTermReferenceModel;

public class IndexTermReferenceBean extends IndexTermReferenceModel {

	private long elementId;
	private String breadCrumbs;
	private boolean deleted;

	// -------------------------------

	public String getBreadCrumbs() {
		return breadCrumbs;
	}

	@Override
	public long getElementId() {
		return elementId;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setBreadCrumbs(String breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

}
