package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.validator.NotAllFalse;
import ca.cihi.cims.validator.ValidCode;
import ca.cihi.cims.validator.ValidRange;

/**
 * Base tabular changes bean
 * 
 * @author rshnaper
 * 
 */
@ValidRange(fromProperty = "codeFrom", toProperty = "codeTo", allowNulls = true)
@ValidCode
@NotAllFalse(properties = { "newCodeValues", "disabledCodeValues", "modifiedProperties", "validations", "conceptMovement" }, message = "{NotAllFalse.search.changeType}")
public class TabularChangesBean extends ChangeRequestPropetiesBean implements TabularConceptAwareBean {
	public static enum HierarchyType {
		Category, Group, Rubric
	}

	private static final long serialVersionUID = 1L;;

	private Boolean newCodeValues, disabledCodeValues, modifiedProperties, validations, conceptMovement;

	private HierarchyType level;
	private String codeFrom;
	private String codeTo;
	private Boolean codesOnly;
	private String modifiedLanguage;
	private String evolutionLanguage;
	
	public TabularChangesBean() {
		setRequestCategory(ChangeRequestCategory.T.name());
	}

	public String getCodeFrom() {
		return codeFrom;
	}

	@Override
	public String getCodeFrom(TabularConceptType type) {
		return getCodeFrom();
	}

	public String getCodeTo() {
		return codeTo;
	}

	@Override
	public String getCodeTo(TabularConceptType type) {
		return getCodeTo();
	}

	public Boolean getDisabledCodeValues() {
		return disabledCodeValues;
	}

	public HierarchyType getLevel() {
		return level;
	}

	public Boolean getModifiedProperties() {
		return modifiedProperties;
	}

	public Boolean getNewCodeValues() {
		return newCodeValues;
	}

	public Boolean getConceptMovement() {
		return conceptMovement;
	}
	
	@Override
	public TabularConceptType getTabularConceptType() {
		TabularConceptType conceptType = null;
		switch (getLevel()) {
		case Category:
			conceptType = TabularConceptType.ICD_CATEGORY;
			break;
		case Group:
			conceptType = TabularConceptType.CCI_GROUP;
			break;
		case Rubric:
			conceptType = TabularConceptType.CCI_RUBRIC;
			break;
		}
		return conceptType;
	}

	public Boolean getValidations() {
		return validations;
	}
	
	public Boolean getCodesOnly() {
		return codesOnly;
	}
	
	public String getModifiedLanguage() {
		return modifiedLanguage;
	}
	
	public String getEvolutionLanguage() {
		return evolutionLanguage;
	}	
	
	public void setCodeFrom(String codeFrom) {
		this.codeFrom = codeFrom;
	}

	public void setCodeTo(String codeTo) {
		this.codeTo = codeTo;
	}

	public void setDisabledCodeValues(Boolean isDisabledCodeValues) {
		this.disabledCodeValues = isDisabledCodeValues;
	}

	public void setLevel(HierarchyType level) {
		this.level = level;
	}

	public void setModifiedProperties(Boolean isModifiedProperties) {
		this.modifiedProperties = isModifiedProperties;
	}

	public void setNewCodeValues(Boolean isNewCodeValues) {
		this.newCodeValues = isNewCodeValues;
	}

	public void setValidations(Boolean isValidations) {
		this.validations = isValidations;
	}
	
	public void setConceptMovement(Boolean isConceptMovement) {
		this.conceptMovement = isConceptMovement;
	}
	
	public void setCodesOnly(Boolean codesOnly) {
		this.codesOnly = codesOnly;
	}
	
	public void setModifiedLanguage(String modifiedLanguage) {
		this.modifiedLanguage = modifiedLanguage;
	}
	
	public void setEvolutionLanguage(String evolutionLanguage) {
		this.evolutionLanguage = evolutionLanguage;
	}
}
