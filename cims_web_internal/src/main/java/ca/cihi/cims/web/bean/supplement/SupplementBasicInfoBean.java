package ca.cihi.cims.web.bean.supplement;

import ca.cihi.cims.model.supplement.SupplementMatter;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.OptimisticLockSource;

public class SupplementBasicInfoBean implements OptimisticLockSource {

	private BeanResult result;
	private String errorMessage;
	private SupplementModel model;

	private int originalSortOrder;
	private SupplementMatter originalMatter;

	private String nodeTitle;
	private boolean editable;
	private boolean edit;

	private boolean statusEditable;
	private boolean statusReadonly;

	private boolean saveVisible;
	private boolean resetVisible;
	private boolean removeVisible;
	private boolean addVisible;

	private long lockTimestamp;
	private String breadCrumbs;

	private int year;

	// ------------------------------------------

	public SupplementBasicInfoBean() {
	}

	public SupplementBasicInfoBean(SupplementModel model) {
		this.model = model;
		this.originalSortOrder = model.getSortOrder();
		this.originalMatter = model.getMatter();
	}

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

	public SupplementModel getModel() {
		return model;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public SupplementMatter getOriginalMatter() {
		return originalMatter;
	}

	public int getOriginalSortOrder() {
		return originalSortOrder;
	}

	public String getParentCode() {
		return null;
	}

	public BeanResult getResult() {
		return result;
	}

	public int getYear() {
		return year;
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

	public boolean isSortingChanged() {
		return model.getSortOrder() != originalSortOrder || model.getMatter() != originalMatter;
	}

	public boolean isStatusEditable() {
		return statusEditable;
	}

	public boolean isStatusReadonly() {
		return statusReadonly;
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

	public void setModel(SupplementModel model) {
		this.model = model;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public void setOriginalMatter(SupplementMatter originalMatter) {
		this.originalMatter = originalMatter;
	}

	public void setOriginalSortOrder(int originalSortOrder) {
		this.originalSortOrder = originalSortOrder;
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

	public void setStatusEditable(boolean statusEditable) {
		this.statusEditable = statusEditable;
	}

	public void setStatusReadonly(boolean statusReadonly) {
		this.statusReadonly = statusReadonly;
	}

	public void setYear(int year) {
		this.year = year;
	}

}
