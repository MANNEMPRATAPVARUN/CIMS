package ca.cihi.cims.web.bean.tabular;

import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.model.tabular.TabularConceptXmlModel;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.OptimisticLockSource;

public class TabularXmlBean implements OptimisticLockSource {

	private TabularConceptXmlModel model;

	private boolean frenchEditable;
	private boolean englishEditable;

	private boolean frenchVisible;
	private boolean englishVisible;

	private boolean saveVisible;

	private BeanResult result;
	private String errorMessage;

	private long lockTimestamp;

	// ---------------------------------------------------

	public Classification getClassification() {
		return getType().getClassification();
	}

	public String getCode() {
		return model.getCode();
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

	public TabularConceptXmlModel getModel() {
		return model;
	}

	public String getParentCode() {
		return model.getParentCode();
	}

	public BeanResult getResult() {
		return result;
	}

	public TabularConceptType getType() {
		return model.getConceptType();
	}

	public boolean isEnglishEditable() {
		return englishEditable;
	}

	public boolean isEnglishVisible() {
		return englishVisible;
	}

	public boolean isFrenchEditable() {
		return frenchEditable;
	}

	public boolean isFrenchVisible() {
		return frenchVisible;
	}

	public boolean isSaveVisible() {
		return saveVisible;
	}

	public void setEnglishEditable(boolean englishEditable) {
		this.englishEditable = englishEditable;
	}

	public void setEnglishVisible(boolean englishVisible) {
		this.englishVisible = englishVisible;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setFrenchEditable(boolean frenchEditable) {
		this.frenchEditable = frenchEditable;
	}

	public void setFrenchVisible(boolean frenchVisible) {
		this.frenchVisible = frenchVisible;
	}

	public void setLockTimestamp(long lockTimestamp) {
		this.lockTimestamp = lockTimestamp;
	}

	public void setModel(TabularConceptXmlModel model) {
		this.model = model;
	}

	public void setResult(BeanResult result) {
		this.result = result;
	}

	public void setSaveVisible(boolean saveVisible) {
		this.saveVisible = saveVisible;
	}

}
