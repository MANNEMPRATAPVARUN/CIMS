package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

import ca.cihi.cims.dal.ContextIdentifier;

public class ProposedChange implements Serializable {
	private static final long serialVersionUID = 5566405804422872264L;

	private Long elementVersionId;

	private Long validationId; // validationId used in resolve conflict screen to find the ProposedValidationChanges
	private String tableName;
	private String fieldName;
	private String oldValue;
	private String proposedValue;
	private String conflictValue;
	// private Long conflictValueRealizedByCR;
	private ContextIdentifier conflictRealizedByContext;

	public ContextIdentifier getConflictRealizedByContext() {
		return conflictRealizedByContext;
	}

	public String getConflictValue() {
		return conflictValue;
	}

	public Long getElementVersionId() {
		return elementVersionId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getOldValue() {
		return oldValue;
	}

	public String getProposedValue() {
		return proposedValue;
	}

	public String getTableName() {
		return tableName;
	}

	public Long getValidationId() {
		return validationId;
	}

	public void setConflictRealizedByContext(ContextIdentifier conflictRealizedByContext) {
		this.conflictRealizedByContext = conflictRealizedByContext;
	}

	public void setConflictValue(final String conflictValue) {
		this.conflictValue = conflictValue;
	}

	public void setElementVersionId(final Long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

	public void setFieldName(final String fieldName) {
		this.fieldName = fieldName;
	}

	public void setOldValue(final String oldValue) {
		this.oldValue = oldValue;
	}

	public void setProposedValue(final String proposedValue) {
		this.proposedValue = proposedValue;
	}

	public void setTableName(final String tableName) {
		this.tableName = tableName;
	}

	public void setValidationId(final Long validationId) {
		this.validationId = validationId;
	}

}
