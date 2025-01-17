package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.tabular.TabularConceptType;

/**
 * ICD specific tabular comparative search bean
 * 
 * @author rshnaper
 * 
 */
public class ICDTabularComparativeBean extends TabularComparativeBean {
	private static final long serialVersionUID = 1L;
	private String chapterCode;
	private Boolean codesOnly;
	private String modifiedLanguage;
	
	public ICDTabularComparativeBean() {
		// defaults
		setHierarchyLevel(HierarchyLevel.Category);
	}

	public String getChapterCode() {
		return chapterCode;
	}
	
	public Boolean getCodesOnly() {
		return codesOnly;
	}
	
	public String getModifiedLanguage() {
		return modifiedLanguage;
	}

	@Override
	public String getCodeFrom(TabularConceptType type) {
		if (type == TabularConceptType.ICD_CHAPTER) {
			return getChapterCode();
		}
		return getCodeFrom();
	}

	@Override
	public String getCodeTo(TabularConceptType type) {
		if (type == TabularConceptType.ICD_CATEGORY) {
			return getCodeTo();
		}
		return null;
	}

	@Override
	public TabularConceptType getTabularConceptType() {
		TabularConceptType type = null;
		switch (getHierarchyLevel()) {
		case Block:
			type = TabularConceptType.ICD_CHAPTER;
			break;
		case Category:
			type = TabularConceptType.ICD_CATEGORY;
			break;
		}
		return type;
	}

	public void setChapterCode(String chapterCode) {
		this.chapterCode = chapterCode;
	}
	
	public void setCodesOnly(Boolean codesOnly) {
		this.codesOnly = codesOnly;
	}
	
	public void setModifiedLanguage(String modifiedLanguage) {
		this.modifiedLanguage = modifiedLanguage;
	}
}
