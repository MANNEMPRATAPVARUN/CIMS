package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.validator.ValidRange;

/**
 * CCI reference values comparative bean implementation
 * 
 * @author rshnaper
 * 
 */
@ValidRange(fromProperty = "priorContextId", toProperty = "contextId")
public class CCIReferenceValueComparativeBean extends SearchCriteriaBean {
	public static enum ComparativeType {
		NewRefValue("ref.value.new"), DisabledRefValue("ref.value.disabled"), ModifiedMandatoryInd(
				"mandatory.ind.modified"), ModifiedInContextDesc("context.desc.modified");

		public static ComparativeType forCode(String code) {
			for (ComparativeType cType : ComparativeType.values()) {
				if (cType.getCode().equals(code)) {
					return cType;
				}
			}
			return null;
		}

		private final String code;

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
	private ComparativeType comparativeType;
	private Long attributeTypeId;

	public CCIReferenceValueComparativeBean() {
		// defaults
		setComparativeType(ComparativeType.NewRefValue);
	}

	public Long getAttributeTypeId() {
		return attributeTypeId;
	}

	public ComparativeType getComparativeType() {
		return comparativeType;
	}

	public Long getContextId() {
		return contextId;
	}

	public Long getPriorContextId() {
		return priorContextId;
	}

	public void setAttributeTypeId(Long attributeTypeId) {
		this.attributeTypeId = attributeTypeId;
	}

	public void setComparativeType(ComparativeType comparativeType) {
		this.comparativeType = comparativeType;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public void setPriorContextId(Long priorContextId) {
		this.priorContextId = priorContextId;
	}

}
