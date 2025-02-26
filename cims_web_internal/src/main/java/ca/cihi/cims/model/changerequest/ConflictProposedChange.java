package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

public class ConflictProposedChange extends ProposedChange implements Serializable {
	public final static String ActionCode_Keep = "keep";
	public final static String ActionCode_Discard = "discard";
	public final static String ChangeType_Tabular = "Tabular";
	public final static String ChangeType_Validation = "Validation";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long elementId; // this is concept element id from ConceptModification
	// private Long validationId; // from ConceptModification
	private String code;
	private String changeType;
	private String resolveActionCode;

	public void copyFrom(ProposedChange pc) {
		setElementVersionId(pc.getElementVersionId());
		setTableName(pc.getTableName());
		setFieldName(pc.getFieldName());
		setOldValue(pc.getOldValue());
		setProposedValue(pc.getProposedValue());
		setConflictValue(pc.getConflictValue());
		setValidationId(pc.getValidationId());

		setConflictRealizedByContext(pc.getConflictRealizedByContext());

	}

	public String getChangeType() {
		return changeType;
	}

	public String getCode() {
		return code;
	}

	public Long getElementId() {
		return elementId;
	}

	public String getResolveActionCode() {
		return resolveActionCode;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setResolveActionCode(String resolveActionCode) {
		this.resolveActionCode = resolveActionCode;
	}

}
