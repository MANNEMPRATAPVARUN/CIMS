package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.tabular.TabularConceptType;

public class CCITabularSimpleBean extends TabularSimpleBean {

	private static final long serialVersionUID = 1L;

	private String sectionCode;
	private Long invasivenessLevel;
	private String refValueStatusCode;
	private String refValueLocationModeCode;
	private String refValueExtentCode;

	public CCITabularSimpleBean() {
		// defaults
		setHierarchyLevel(HierarchyLevel.Rubric);
	}

	@Override
	public String getCodeFrom(TabularConceptType type) {
		if (type == TabularConceptType.CCI_SECTION) {
			return getSectionCode();
		} else if (type == TabularConceptType.CCI_GROUP || type == TabularConceptType.CCI_RUBRIC) {
			return getCodeFrom();
		}
		return null;
	}

	@Override
	public String getCodeTo(TabularConceptType type) {
		if (type == TabularConceptType.CCI_GROUP || type == TabularConceptType.CCI_RUBRIC) {
			return getCodeTo();
		}
		return null;
	}

	public Long getInvasivenessLevel() {
		return invasivenessLevel;
	}

	public String getRefValueExtentCode() {
		return refValueExtentCode;
	}

	public String getRefValueLocationModeCode() {
		return refValueLocationModeCode;
	}

	public String getRefValueStatusCode() {
		return refValueStatusCode;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	@Override
	public TabularConceptType getTabularConceptType() {
		TabularConceptType type = null;
		switch (getHierarchyLevel()) {
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

	public void setInvasivenessLevel(Long invasivenessLevel) {
		this.invasivenessLevel = invasivenessLevel;
	}

	public void setRefValueExtentCode(String refValueExtentCode) {
		this.refValueExtentCode = refValueExtentCode;
	}

	public void setRefValueLocationModeCode(String refValueLocationModeCode) {
		this.refValueLocationModeCode = refValueLocationModeCode;
	}

	public void setRefValueStatusCode(String refValueStatusCode) {
		this.refValueStatusCode = refValueStatusCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

}
