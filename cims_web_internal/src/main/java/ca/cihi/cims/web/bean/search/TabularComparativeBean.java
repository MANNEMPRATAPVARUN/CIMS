package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.validator.ValidCode;
import ca.cihi.cims.validator.ValidRange;

/**
 * Base tabular comparative search bean
 * 
 * @author rshnaper
 * 
 */
@ValidRange.List({ @ValidRange(fromProperty = "priorContextId", toProperty = "contextId"),
		@ValidRange(fromProperty = "codeFrom", toProperty = "codeTo", allowNulls = true) })
@ValidCode
public abstract class TabularComparativeBean extends SearchCriteriaBean implements TabularConceptAwareBean {

	public static enum ComparativeType {
		NewCode("code.new"), DisabledCode("code.disabled"), ModifiedCodeTitle("code.title.modified"), ModifiedViewerContent(
				"viewer.content.modified");

		public static ComparativeType forCode(String code) {
			for (ComparativeType cType : ComparativeType.values()) {
				if (cType.getCode().equals(code)) {
					return cType;
				}
			}
			return null;
		}

		private String code;

		ComparativeType(String code) {
			this.code = code;
		}

		public String getCode() {
			return code;
		}

		@Override
		public String toString() {
			return getCode();
		}
	}

	private static final long serialVersionUID = 1L;

	private Long contextId;
	private Long priorContextId;
	private HierarchyLevel hierarchyLevel;
	private ComparativeType comparativeType;
	private String codeFrom, codeTo;
	private String modifiedLanguage;

	public TabularComparativeBean() {
		// defaults
		setComparativeType(ComparativeType.NewCode);
	}

	public String getCodeFrom() {
		return codeFrom;
	}

	public String getCodeTo() {
		return codeTo;
	}

	public ComparativeType getComparativeType() {
		return comparativeType;
	}

	public Long getContextId() {
		return contextId;
	}

	public HierarchyLevel getHierarchyLevel() {
		return hierarchyLevel;
	}

	public Long getPriorContextId() {
		return priorContextId;
	}
	
	public String getModifiedLanguage() {
		return modifiedLanguage;
	}

	public void setCodeFrom(String codeFrom) {
		this.codeFrom = codeFrom;
	}

	public void setCodeTo(String codeTo) {
		this.codeTo = codeTo;
	}

	public void setComparativeType(ComparativeType comparativeType) {
		this.comparativeType = comparativeType;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public void setHierarchyLevel(HierarchyLevel hierarchyType) {
		this.hierarchyLevel = hierarchyType;
	}

	public void setPriorContextId(Long priorContextId) {
		this.priorContextId = priorContextId;
	}
	
	public void setModifiedLanguage(String modifiedLanguage) {
		this.modifiedLanguage = modifiedLanguage;
	}
}
