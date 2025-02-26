package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

public class RealizedChange implements Serializable {

	private static final long serialVersionUID = -3250388435682561604L;

	private Long elementVersionId;
	private String tableName;
	private String fieldName;
	private String oldValue;
	private String newValue;

	public Long getElementVersionId() {
		return elementVersionId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getNewValue() {
		return newValue;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getTableName() {
		return tableName;
	}

	public void setElementVersionId(final Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}

	public void setOldValue(final String oldValue) {
		this.oldValue = oldValue;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

}
