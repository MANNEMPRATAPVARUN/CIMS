package ca.cihi.cims.model.changerequest;

public class ConflictProposedIndexChange extends ProposedChange {
	public final static String ActionCode_Keep = "keep";
	public final static String ActionCode_Discard = "discard";
	public final static String ChangeType_Index = "Index";
	public final static String ChangeType_IndexRef = "IndexRef";

	private static final long serialVersionUID = 1L;
	private Long elementId; // this is concept element id from ConceptModification
	private String changeType;
	private String indexTerm;
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

	public String getIndexTerm() {
		return indexTerm;
	}

	public String getResolveActionCode() {
		return resolveActionCode;
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

	public void setIndexTerm(String indexTerm) {
		this.indexTerm = indexTerm;
	}

	public void setResolveActionCode(String resolveActionCode) {
		this.resolveActionCode = resolveActionCode;
	}

}
