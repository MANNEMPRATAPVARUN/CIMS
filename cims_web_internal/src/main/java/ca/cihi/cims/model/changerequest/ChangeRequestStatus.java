package ca.cihi.cims.model.changerequest;

import static ca.cihi.cims.util.CollectionUtils.asSet;

import java.util.Set;

public enum ChangeRequestStatus {

	UNKNOWN(0, "Unknown"), NEW_INIT_NO_OWNER(1, "New"), NEW_INITSUBMIT_NO_OWNER(2, "New"), NEW_WITH_OWNER(3, "New"), VALID_READY(
			4, "Valid"), VALID_INCOMPLETE(5, "Valid Incomplete"), DEFERRED(6, "Closed-Deferred"), REJECTED(7,
			"Rejected"), DELETED(8, "Deleted"), VALID_DRAFT(9, "Valid"), ACCEPTED_DRAFT(10, "Accepted"), ACCEPTED_READY_FOR_REALIZATION(
			11, "Accepted"), ACCEPTED_READY_FOR_TRANSLATION(12, "Accepted"), ACCEPTED_INCOMPLETE(13,
			"Accepted Incomplete"), TRANSLATION_DONE(14, "Translation Done"), VALIDATION_DONE(15, "Validation Done"), REALIZED(
			16, "Realized"), ITERATIVE_QA_DONE(17, "Iterative QA Done"), CLOSED_APPROVED(18, "Closed-Approved"), PUBLISHED(
			19, "Published");

	public final static Set<ChangeRequestStatus> VALID_STATUSES = asSet(ChangeRequestStatus.VALID_READY,
			ChangeRequestStatus.VALID_INCOMPLETE, ChangeRequestStatus.VALID_DRAFT, ChangeRequestStatus.ACCEPTED_DRAFT,
			ChangeRequestStatus.ACCEPTED_READY_FOR_REALIZATION, ChangeRequestStatus.ACCEPTED_READY_FOR_TRANSLATION,
			ChangeRequestStatus.ACCEPTED_INCOMPLETE, ChangeRequestStatus.TRANSLATION_DONE);

	// -----------------------------------------------------------------------------------

	private int statusId;
	private String statusCode;

	// -----------------------------------------------------------------------------------

	private ChangeRequestStatus(int statusId, String statusCode) {
		this.statusId = statusId;
		this.statusCode = statusCode;
	}

	public String getName() {
		return name();
	}

	public String getStatusCode() {
		return statusCode;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

}
