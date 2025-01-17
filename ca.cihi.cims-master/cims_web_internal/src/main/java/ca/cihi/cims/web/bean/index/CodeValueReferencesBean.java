package ca.cihi.cims.web.bean.index;

import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.OptimisticLockSource;

public abstract class CodeValueReferencesBean implements OptimisticLockSource {

	private BeanResult result;
	private String errorMessage;
	private IndexModel model;
	private boolean editable;
	private long lockTimestamp;

	private String nodeTitle;
	private String breadCrumbs;

	// ------------------------------------------

	public String getBreadCrumbs() {
		return breadCrumbs;
	}

	public Long getElementId() {
		if (model == null) {
			return null;
		} else {
			return model.getElementId();
		}
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public long getLockTimestamp() {
		return lockTimestamp;
	}

	public IndexModel getModel() {
		return model;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public BeanResult getResult() {
		return result;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setBreadCrumbs(String breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setLockTimestamp(long lockTimestamp) {
		this.lockTimestamp = lockTimestamp;
	}

	public void setModel(IndexModel model) {
		this.model = model;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public void setResult(BeanResult result) {
		this.result = result;
	}

}
