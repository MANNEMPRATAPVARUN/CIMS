package ca.cihi.cims.model.changerequest;

import java.io.Serializable;

public class ChangeRequestRealization implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long realizationId;
	private Long changeRequestId;
	private ChangeRequestRealizationStatus realizationStatus;
	private ChangeRequestRealizationStep processStep;

	private String failedReason;

	private boolean newCreated;

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public ChangeRequestRealizationStep getProcessStep() {
		return processStep;
	}

	public Long getRealizationId() {
		return realizationId;
	}

	public ChangeRequestRealizationStatus getRealizationStatus() {
		return realizationStatus;
	}

	public boolean isNewCreated() {
		return newCreated;
	}

	public void setChangeRequestId(final Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public void setFailedReason(final String failedReason) {
		this.failedReason = failedReason;
	}

	public void setNewCreated(boolean newCreated) {
		this.newCreated = newCreated;
	}

	public void setProcessStep(ChangeRequestRealizationStep processStep) {
		this.processStep = processStep;
	}

	public void setRealizationId(final Long realizationId) {
		this.realizationId = realizationId;
	}

	public void setRealizationStatus(ChangeRequestRealizationStatus realizationStatus) {
		this.realizationStatus = realizationStatus;
	}

}
