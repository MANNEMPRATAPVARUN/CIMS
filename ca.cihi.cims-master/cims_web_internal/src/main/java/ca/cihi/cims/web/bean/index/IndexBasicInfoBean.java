package ca.cihi.cims.web.bean.index;

import java.util.List;

import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.model.index.SiteIndicatorModel;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.OptimisticLockSource;

public class IndexBasicInfoBean implements OptimisticLockSource {

	private BeanResult result;
	private String errorMessage;
	private IndexModel model;

	private String nodeTitle;
	private boolean editable;
	private boolean edit;

	private boolean statusEditable;
	private boolean statusReadonly;

	private boolean saveVisible;
	private boolean resetVisible;
	private boolean removeVisible;
	private boolean addVisible;

	private List<SiteIndicatorModel> siteIndicators;

	private long lockTimestamp;
	private String breadCrumbs;
	private IndexType addType;

	// ------------------------------------------

	public IndexBasicInfoBean() {
	}

	public IndexBasicInfoBean(IndexModel model) {
		this.model = model;
	}

	public IndexType getAddType() {
		return addType;
	}

	public String getBreadCrumbs() {
		return breadCrumbs;
	}

	public Classification getClassification() {
		return model.getClassification();
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

	public String getParentCode() {
		return null;
	}

	public BeanResult getResult() {
		return result;
	}

	public List<SiteIndicatorModel> getSiteIndicators() {
		return siteIndicators;
	}

	public IndexType getType() {
		return model.getType();
	}

	public boolean isAdd() {
		return !edit;
	}

	public boolean isAddVisible() {
		return addVisible;
	}

	public boolean isEdit() {
		return edit;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isRemoveVisible() {
		return removeVisible;
	}

	public boolean isResetVisible() {
		return resetVisible;
	}

	public boolean isSaveVisible() {
		return saveVisible;
	}

	public boolean isStatusEditable() {
		return statusEditable;
	}

	public boolean isStatusReadonly() {
		return statusReadonly;
	}

	public void setAddType(IndexType addType) {
		this.addType = addType;
	}

	public void setAddVisible(boolean addVisible) {
		this.addVisible = addVisible;
	}

	public void setBreadCrumbs(String breadCrumbs) {
		this.breadCrumbs = breadCrumbs;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
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

	public void setRemoveVisible(boolean removeVisible) {
		this.removeVisible = removeVisible;
	}

	public void setResetVisible(boolean resetVisible) {
		this.resetVisible = resetVisible;
	}

	public void setResult(BeanResult result) {
		this.result = result;
	}

	public void setSaveVisible(boolean saveVisible) {
		this.saveVisible = saveVisible;
	}

	public void setSiteIndicators(List<SiteIndicatorModel> siteIndicators) {
		this.siteIndicators = siteIndicators;
	}

	public void setStatusEditable(boolean statusEditable) {
		this.statusEditable = statusEditable;
	}

	public void setStatusReadonly(boolean statusReadonly) {
		this.statusReadonly = statusReadonly;
	}

}
