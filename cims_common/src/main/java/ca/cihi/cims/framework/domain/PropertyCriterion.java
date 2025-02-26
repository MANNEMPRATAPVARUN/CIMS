package ca.cihi.cims.framework.domain;

import java.io.Serializable;

public class PropertyCriterion implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1929356884038236488L;
	private Long classsId;
	private Object value;
	private String languageCode;
	private String propertyType;
	private String operator;
	private String relationshipDirection;

	public Long getClasssId() {
		return classsId;
	}

	public void setClasssId(Long classsId) {
		this.classsId = classsId;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public void setPropertyType(String propertyType) {
		this.propertyType = propertyType;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public String getRelationshipDirection() {
		return relationshipDirection;
	}

	public void setRelationshipDirection(String relationshipDirection) {
		this.relationshipDirection = relationshipDirection;
	}
}
