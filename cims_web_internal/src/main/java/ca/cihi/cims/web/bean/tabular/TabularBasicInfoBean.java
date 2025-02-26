package ca.cihi.cims.web.bean.tabular;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.content.icd.DaggerAsterisk;
import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.OptimisticLockSource;

public class TabularBasicInfoBean implements OptimisticLockSource {

	private BeanResult result;
	private String errorMessage;
	private TabularConceptModel model;

	private boolean editable;
	private boolean edit;

	private boolean codeVisible;
	private boolean statusVisible;
	private boolean englishVisible;
	private boolean frenchVisible;
	private boolean canadianEnhancementVisible;
	private boolean invasivenessLevelVisible;

	private boolean saveVisible;
	private boolean resetVisible;
	private boolean removeVisible;
	private boolean addCodeVisible;
	private boolean addQualifierVisible;

	private TabularConceptType addType;
	private TabularConceptType addBlockType;
	private TabularConceptType addGroupType;
	private TabularConceptType addCategoryType;

	private boolean codeEditable;
	private boolean daggerAsteriskEditable;
	private boolean canadianEnhancementEditable;
	private boolean statusEditable;
	private boolean userTitleEditable;
	private boolean invasivenessLevelEditable;

	private boolean englishEditable;
	private boolean frenchEditable;

	public List<DaggerAsterisk> daggerAsteriskTypes;
	public Map<Long, String> cciInvasivenessLevels;

	private String nodeTitle;
	private boolean daggerAsteriskVisible;

	private boolean referenceLinksVisible;

	private boolean childTableVisible;
	private boolean childTable;

	private long lockTimestamp;

	// -----------------------------------------------------

	public TabularBasicInfoBean() {
	}

	public TabularBasicInfoBean(TabularConceptModel concept) {
		this.model = concept;
	}

	public TabularConceptType getAddBlockType() {
		return addBlockType;
	}

	public TabularConceptType getAddCategoryType() {
		return addCategoryType;
	}

	public TabularConceptType getAddGroupType() {
		return addGroupType;
	}

	public TabularConceptType getAddType() {
		return addType;
	}

	public Map<Long, String> getCciInvasivenessLevels() {
		return cciInvasivenessLevels;
	}

	public Classification getClassification() {
		return model.getClassification();
	}

	public String getCode() {
		return model.getCode();
	}

	public List<DaggerAsterisk> getDaggerAsteriskTypes() {
		return daggerAsteriskTypes;
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

	public TabularConceptModel getModel() {
		return model;
	}

	public String getNodeTitle() {
		return nodeTitle;
	}

	public String getParentCode() {
		return model.getParentCode();
	}

	public BeanResult getResult() {
		return result;
	}

	public TabularConceptType getType() {
		return model.getType();
	}

	public String getUserTitleEng() {
		return model.getUserTitleEng();
	}

	public String getUserTitleFra() {
		return model.getUserTitleFra();
	}

	public boolean isAdd() {
		return !edit;
	}

	public boolean isAddBlockVisible() {
		return addBlockType != null;
	}

	public boolean isAddCategoryVisible() {
		return addCategoryType != null;
	}

	public boolean isAddCodeVisible() {
		return addCodeVisible;
	}

	public boolean isAddGroupVisible() {
		return addGroupType != null;
	}

	public boolean isAddQualifierVisible() {
		return addQualifierVisible;
	}

	public boolean isCanadianEnhancementEditable() {
		return canadianEnhancementEditable && editable;
	}

	public boolean isCanadianEnhancementVisible() {
		return canadianEnhancementVisible;
	}

	public boolean isChildTable() {
		return childTable;
	}

	public boolean isChildTableVisible() {
		return childTableVisible;
	}

	public boolean isCodeEditable() {
		return codeEditable && editable;
	}

	public boolean isCodeVisible() {
		return codeVisible;
	}

	public boolean isDaggerAsteriskEditable() {
		return daggerAsteriskEditable && editable;
	}

	public boolean isDaggerAsteriskVisible() {
		return daggerAsteriskVisible;
	}

	public boolean isEdit() {
		return edit;
	}

	public boolean isEditable() {
		return editable;
	}

	public boolean isEnglishEditable() {
		return englishEditable && editable;
	}

	public boolean isEnglishVisible() {
		return englishVisible;
	}

	public boolean isFrenchEditable() {
		return frenchEditable && editable;
	}

	public boolean isFrenchVisible() {
		return frenchVisible;
	}

	public boolean isInvasivenessLevelEditable() {
		return invasivenessLevelEditable && editable;
	}

	public boolean isInvasivenessLevelVisible() {
		return invasivenessLevelVisible;
	}

	public boolean isLanguagesVisible() {
		return englishVisible || frenchVisible;
	}

	public boolean isReferenceLinksVisible() {
		return referenceLinksVisible;
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
		return statusEditable && editable;
	}

	public boolean isStatusVisible() {
		return statusVisible;
	}

	public boolean isUserTitleEditable() {
		return userTitleEditable && editable;
	}

	public void setAddBlockType(TabularConceptType addBlockType) {
		this.addBlockType = addBlockType;
	}

	public void setAddCategoryType(TabularConceptType addCategoryType) {
		this.addCategoryType = addCategoryType;
	}

	public void setAddCodeVisible(boolean addCodeVisible) {
		this.addCodeVisible = addCodeVisible;
	}

	public void setAddGroupType(TabularConceptType addGroupType) {
		this.addGroupType = addGroupType;
	}

	public void setAddQualifierVisible(boolean addQualifierVisible) {
		this.addQualifierVisible = addQualifierVisible;
	}

	public void setAddType(TabularConceptType addType) {
		this.addType = addType;
	}

	public void setCanadianEnhancementEditable(boolean canadianEnhancementEditable) {
		this.canadianEnhancementEditable = canadianEnhancementEditable;
	}

	public void setCanadianEnhancementVisible(boolean visible) {
		this.canadianEnhancementVisible = visible;
	}

	public void setCciInvasivenessLevels(Map<Long, String> cciInvasivenessLevels) {
		this.cciInvasivenessLevels = cciInvasivenessLevels;
	}

	public void setChildTable(boolean childTable) {
		this.childTable = childTable;
	}

	public void setChildTableVisible(boolean childTableVisible) {
		this.childTableVisible = childTableVisible;
	}

	public void setCode(String code) {
		model.setCode(code);
	}

	public void setCodeEditable(boolean codeEditable) {
		this.codeEditable = codeEditable;
	}

	public void setCodeVisible(boolean codeVisible) {
		this.codeVisible = codeVisible;
	}

	public void setDaggerAsteriskEditable(boolean daggerAsteriskEditable) {
		this.daggerAsteriskEditable = daggerAsteriskEditable;
	}

	public void setDaggerAsteriskTypes(List<DaggerAsterisk> daggerAsteriskTypes) {
		this.daggerAsteriskTypes = daggerAsteriskTypes;
	}

	public void setDaggerAsteriskVisible(boolean daggerAsteriskVisible) {
		this.daggerAsteriskVisible = daggerAsteriskVisible;
	}

	public void setEdit(boolean edit) {
		this.edit = edit;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
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

	public void setInvasivenessLevelEditable(boolean invasivenessLevelEditable) {
		this.invasivenessLevelEditable = invasivenessLevelEditable;
	}

	public void setInvasivenessLevelVisible(boolean invasivenessLevelVisible) {
		this.invasivenessLevelVisible = invasivenessLevelVisible;
	}

	public void setLockTimestamp(long lockTimestamp) {
		this.lockTimestamp = lockTimestamp;
	}

	public void setModel(TabularConceptModel model) {
		this.model = model;
	}

	public void setNodeTitle(String nodeTitle) {
		this.nodeTitle = nodeTitle;
	}

	public void setReferenceLinksVisible(boolean referenceLinksVisible) {
		this.referenceLinksVisible = referenceLinksVisible;
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

	public void setStatusVisible(boolean statusVisible) {
		this.statusVisible = statusVisible;
	}

	public void setUserTitleEditable(boolean userTitleEditable) {
		this.userTitleEditable = userTitleEditable;
	}

	public void setUserTitleEng(String userTitleEng) {
		model.setUserTitleEng(userTitleEng);
	}

	public void setUserTitleFra(String userTitleFra) {
		model.setUserTitleFra(userTitleFra);
	}

}
