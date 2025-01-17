package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.List;

/*
 * this class is used for keeping use selected keep or discard action in resolve conflict screen
 */
public class ResolveConflict implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long changeRequestId;
	private Long currentContextId;

	private List<ConflictProposedChange> conflictChanges;

	private List<ConflictProposedIndexChange> conflictIndexChanges;

	private List<ConflictProposedSupplementChange> conflictSupplementChanges;

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public List<ConflictProposedChange> getConflictChanges() {
		return conflictChanges;
	}

	public List<ConflictProposedIndexChange> getConflictIndexChanges() {
		return conflictIndexChanges;
	}

	public List<ConflictProposedSupplementChange> getConflictSupplementChanges() {
		return conflictSupplementChanges;
	}

	public Long getCurrentContextId() {
		return currentContextId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setConflictChanges(List<ConflictProposedChange> conflictChanges) {
		this.conflictChanges = conflictChanges;
	}

	public void setConflictIndexChanges(List<ConflictProposedIndexChange> conflictIndexChanges) {
		this.conflictIndexChanges = conflictIndexChanges;
	}

	public void setConflictSupplementChanges(List<ConflictProposedSupplementChange> conflictSupplementChanges) {
		this.conflictSupplementChanges = conflictSupplementChanges;
	}

	public void setCurrentContextId(Long currentContextId) {
		this.currentContextId = currentContextId;
	}

}
