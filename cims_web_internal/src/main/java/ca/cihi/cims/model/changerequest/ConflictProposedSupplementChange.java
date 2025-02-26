package ca.cihi.cims.model.changerequest;

public class ConflictProposedSupplementChange extends ProposedChange {
	public final static String ACTIONCODE_KEEP = "keep";
	public final static String ACTIONCODE_DISCARD = "discard";
	public final static String CHANGETYPE_SUPPLEMENT = "Supplement";

	private static final long serialVersionUID = 1L;
	private Long elementId; // this is concept element id from ConceptModification
	private String changeType;
	private String supplement;
	private String hierarchicalPath;

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

	public Long getElementId() {
		return elementId;
	}

	public String getHierarchicalPath() {
		return hierarchicalPath;
	}

	public String getResolveActionCode() {
		return resolveActionCode;
	}

	public String getSupplement() {
		return supplement;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setHierarchicalPath(String hierarchicalPath) {
		this.hierarchicalPath = hierarchicalPath;
	}

	public void setResolveActionCode(String resolveActionCode) {
		this.resolveActionCode = resolveActionCode;
	}

	public void setSupplement(String supplement) {
		this.supplement = supplement;
	}

}
