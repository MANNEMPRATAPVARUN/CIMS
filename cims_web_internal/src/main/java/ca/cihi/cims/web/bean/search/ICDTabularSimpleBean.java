package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.tabular.TabularConceptType;

public class ICDTabularSimpleBean extends TabularSimpleBean {

	private static final long serialVersionUID = 1L;
	private String chapterCode;
	private Long daggerAsteriskId;
	private Boolean canEnhancementFlag;

	public ICDTabularSimpleBean() {
		// defaults
		setHierarchyLevel(HierarchyLevel.Category);
	}

	public Boolean getCanEnhancementFlag() {
		return canEnhancementFlag;
	}

	public String getChapterCode() {
		return chapterCode;
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

	public Long getDaggerAsteriskId() {
		return daggerAsteriskId;
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

	public void setCanEnhancementFlag(Boolean canEnhancementFlag) {
		this.canEnhancementFlag = canEnhancementFlag;
	}

	public void setChapterCode(String chapterCode) {
		this.chapterCode = chapterCode;
	}

	public void setDaggerAsteriskId(Long daggerAsteriskId) {
		this.daggerAsteriskId = daggerAsteriskId;
	}

}
