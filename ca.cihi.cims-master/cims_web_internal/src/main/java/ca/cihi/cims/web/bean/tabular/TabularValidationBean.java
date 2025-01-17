package ca.cihi.cims.web.bean.tabular;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.DxType;
import ca.cihi.cims.model.IdCodeDescription;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationDadHoldingModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationGenderModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptValidationSetModel;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.OptimisticLockSource;

public class TabularValidationBean implements OptimisticLockSource {

	private BeanResult result;
	private String errorMessage;
	private String successMessage;
	private boolean editable;

	private long elementId;
	private long dataHoldingId;

	private String parentCode;

	private String code;
	private String englishShortTitle;
	private TabularConceptType type;
	private TabularConceptValidationSetModel model;

	private List<TabularConceptValidationGenderModel> genders;
	private List<TabularConceptValidationDadHoldingModel> dataHoldings;

	private List<IdCodeDescription> cciStatusReferences;
	private List<IdCodeDescription> cciExtentReferences;
	private List<IdCodeDescription> cciLocationReferences;
	private List<IdCodeDescription> cciModeOfDeliveryReferences;

	private DxType icdDxType;
	private List<DxType> icdDxTypes;

	private boolean extendValidationToOtherDataHoldings;
	private List<Long> selectedOtherDataHoldings = new ArrayList<Long>();
	private List<TabularConceptValidationDadHoldingModel> otherDataholdings;

	private long lockTimestamp;

	// ----------------------------------------------------------------------------

	public List<IdCodeDescription> getCciExtentReferences() {
		return cciExtentReferences;
	}

	public List<IdCodeDescription> getCciLocationReferences() {
		return cciLocationReferences;
	}

	public List<IdCodeDescription> getCciModeOfDeliveryReferences() {
		return cciModeOfDeliveryReferences;
	}

	public List<IdCodeDescription> getCciStatusReferences() {
		return cciStatusReferences;
	}

	public Classification getClassification() {
		return type.getClassification();
	}

	public String getCode() {
		return code;
	}

	public long getDataHoldingId() {
		return model == null ? dataHoldingId : model.getElementId();
	}

	public List<TabularConceptValidationDadHoldingModel> getDataHoldings() {
		return dataHoldings;
	}

	public long getElementId() {
		return elementId;
	}

	public String getEnglishShortTitle() {
		return englishShortTitle;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public List<TabularConceptValidationGenderModel> getGenders() {
		return genders;
	}

	public DxType getIcdDxType() {
		return icdDxType;
	}

	public List<DxType> getIcdDxTypes() {
		return icdDxTypes;
	}

	public long getLockTimestamp() {
		return lockTimestamp;
	}

	public TabularConceptValidationSetModel getModel() {
		return model;
	}

	public List<TabularConceptValidationDadHoldingModel> getOtherDataholdings() {
		return otherDataholdings;
	}

	public String getParentCode() {
		return parentCode;
	}

	public BeanResult getResult() {
		return result;
	}

	public List<Long> getSelectedOtherDataHoldings() {
		return selectedOtherDataHoldings;
	}

	public String getSuccessMessage() {
		return successMessage;
	}

	public TabularConceptType getType() {
		return type;
	}

	public boolean isAddable() {
		return editable && model == null;
	}

	public boolean isEditable() {
		return editable && model != null;
	}

	public boolean isExtendValidationToOtherDataHoldings() {
		return extendValidationToOtherDataHoldings;
	}

	public void resetModel() {
		this.model = null;
	}

	public void setCciExtentReferences(List<IdCodeDescription> cciExtentReferences) {
		this.cciExtentReferences = cciExtentReferences;
	}

	public void setCciLocationReferences(List<IdCodeDescription> cciLocationReferences) {
		this.cciLocationReferences = cciLocationReferences;
	}

	public void setCciModeOfDeliveryReferences(List<IdCodeDescription> cciModeOfDeliveryReferences) {
		this.cciModeOfDeliveryReferences = cciModeOfDeliveryReferences;
	}

	public void setCciStatusReferences(List<IdCodeDescription> cciStatusReferences) {
		this.cciStatusReferences = cciStatusReferences;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDataHoldingId(long dataHoldingId) {
		this.dataHoldingId = dataHoldingId;
	}

	public void setDataHoldings(List<TabularConceptValidationDadHoldingModel> dataHoldings) {
		this.dataHoldings = dataHoldings;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setEnglishShortTitle(String englishShortTitle) {
		this.englishShortTitle = englishShortTitle;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public void setExtendValidationToOtherDataHoldings(boolean extendValidationToOtherDataHoldings) {
		this.extendValidationToOtherDataHoldings = extendValidationToOtherDataHoldings;
	}

	public void setGenders(List<TabularConceptValidationGenderModel> genders) {
		this.genders = genders;
	}

	public void setIcdDxType(DxType icdDxType) {
		this.icdDxType = icdDxType;
	}

	public void setIcdDxTypes(List<DxType> icdDxTypes) {
		this.icdDxTypes = icdDxTypes;
	}

	public void setLockTimestamp(long lockTimestamp) {
		this.lockTimestamp = lockTimestamp;
	}

	public void setModel(TabularConceptValidationSetModel model) {
		// FIXME: spring MVC fix
		if (this.model == null) {
			this.model = model;
		}
	}

	public void setOtherDataholdings(List<TabularConceptValidationDadHoldingModel> otherDataholdings) {
		this.otherDataholdings = otherDataholdings;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public void setResult(BeanResult result) {
		this.result = result;
	}

	public void setSelectedOtherDataHoldings(List<Long> selectedOtherDataHoldings) {
		this.selectedOtherDataHoldings = selectedOtherDataHoldings;
	}

	public void setSuccessMessage(String successMessage) {
		this.successMessage = successMessage;
	}

	public void setType(TabularConceptType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "TabularValidationBean [elementId=" + elementId + ", code=" + code + ", conceptType=" + type
				+ ", dataHoldingId=" + dataHoldingId + ", editable=" + editable + ", englishShortTitle="
				+ englishShortTitle + ", model=" + model + ", otherDataholdings=" + otherDataholdings
				+ ", extendValidationToOtherDataHoldings=" + extendValidationToOtherDataHoldings
				+ ", selectedOtherDataHoldings=" + selectedOtherDataHoldings + ", errorMessage=" + errorMessage
				+ ", result=" + result + "]";
	}

}
