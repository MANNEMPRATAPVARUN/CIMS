package ca.cihi.cims.model.changerequest;

import java.util.List;

public class ChangeSummary {

	private List<ConceptModification> conceptModifications;
	private boolean noChange = false;
	private boolean failedRealization = false;

	public List<ConceptModification> getConceptModifications() {
		return conceptModifications;
	}

	public boolean isFailedRealization() {
		return failedRealization;
	}

	public boolean isNoChange() {
		return noChange;
	}

	public void setConceptModifications(List<ConceptModification> conceptModifications) {
		this.conceptModifications = conceptModifications;
	}

	public void setFailedRealization(boolean failedRealization) {
		this.failedRealization = failedRealization;
	}

	public void setNoChange(boolean noChange) {
		this.noChange = noChange;
	}

}
