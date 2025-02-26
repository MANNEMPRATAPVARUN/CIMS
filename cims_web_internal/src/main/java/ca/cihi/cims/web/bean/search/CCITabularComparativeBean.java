package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.tabular.TabularConceptType;

/**
 * CCI specific tabular comparative search bean
 * @author rshnaper
 *
 */
public class CCITabularComparativeBean extends TabularComparativeBean {
	private static final long serialVersionUID = 1L;
	
	private String sectionCode;
	private Boolean codesOnly;
	private String modifiedLanguage;
	
	public CCITabularComparativeBean() {
		//defaults
		setHierarchyLevel(HierarchyLevel.Section);
	}
	
	public String getSectionCode() {
		return sectionCode;
	}
	public Boolean getCodesOnly() {
		return codesOnly;
	}
	public String getModifiedLanguage() {
		return modifiedLanguage;
	}
	
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public void setCodesOnly(Boolean codesOnly) {
		this.codesOnly = codesOnly;
	}
	public void setModifiedLanguage(String modifiedLanguage) {
		this.modifiedLanguage = modifiedLanguage;
	}
	
	@Override
	public TabularConceptType getTabularConceptType() {
		TabularConceptType type = null;
		switch(getHierarchyLevel()) {
			case Block:
				type = TabularConceptType.CCI_SECTION;
				break;
			case Group:
				type = TabularConceptType.CCI_GROUP;
				break;
			case Rubric:
				type = TabularConceptType.CCI_RUBRIC;
				break;
		}
		return type;
	}
	@Override
	public String getCodeFrom(TabularConceptType type) {
		if(type == TabularConceptType.CCI_SECTION) {
			return getSectionCode();
		}
		else if(type == TabularConceptType.CCI_GROUP || type == TabularConceptType.CCI_RUBRIC) {
			return getCodeFrom();
		}
		return null;
	}
	@Override
	public String getCodeTo(TabularConceptType type) {
		if(type == TabularConceptType.CCI_GROUP || type == TabularConceptType.CCI_RUBRIC) {
			return getCodeTo();
		}
		return null;
	}
	
	public Boolean getCodesOnly(TabularConceptType type) {
		if(type == TabularConceptType.CCI_SECTION || type == TabularConceptType.CCI_RUBRIC || 
		   type == TabularConceptType.CCI_GROUP || type == TabularConceptType.CCI_CCICODE) {
			return codesOnly;
		}
		return null;
	}
	
	
	public String getModifiedLanguage(TabularConceptType type) {
		if(type == TabularConceptType.CCI_SECTION || type == TabularConceptType.CCI_RUBRIC || 
		   type == TabularConceptType.CCI_GROUP || type == TabularConceptType.CCI_CCICODE) {
			return modifiedLanguage;
		}
		return null;
	}
	
}
