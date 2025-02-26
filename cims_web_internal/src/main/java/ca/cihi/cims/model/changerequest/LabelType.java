package ca.cihi.cims.model.changerequest;

public enum LabelType {
	Language("Language"), Classification("Classification"), Year("Year"), RequestCategory("Request Category"), RequestName(
			"Request Name"), NatureOfChange("Nature of Change"), TypeOfChange("Type of Change"), RequestStatus(
			"Request Status"), Requestor("Requestor"), ReviewGroups("Review Groups"), IndexRequired("Index Required"), EvolutionRequired(
			"Evolution Required"), ConversionRequired("Conversion Required"), PatternChange("Pattern Change"), PatternTopic(
			"Pattern Topic"), EvolutionCodes("Evolution Codes", true), EvolutionEngComments(
			"Evolution English Comments", true), EvolutionFraComments("Evolution French Comments", true), RationalForChange(
			"Rationale for Change ", true), RationalForValid("Rationale for 'Valid' Status ", true), RationalForRejected(
			"Rationale for 'Rejected' Status ", true), RationalForDeferred("Rationale for 'Closed-Deferred' Status ",
			true), RationalForIncomp("Send Back Reason", true), QFRNum("Question Number"), QFRReviewer(
			"Question Reviewer"), QFRContent("Question Content", true), CodingQuestions("Coding Questions"), UrcAttachments(
			"URC Attachments"), UrcLinks("URC Links"), OtherAttachments("Other Attachments"), OtherLinks("Other Links "), Owner(
			"Owner"), Assignee("Assignee"), Assignor("Assignor"), CreationTime("Creation Date/Time"), AssignTime(
			"Assign Date/Time"), EvolutionLanguage("Evolution Language");

	private String label;
	private boolean isLink;

	private LabelType(String label) {
		this.label = label;
		isLink = false;
	}

	private LabelType(String label, boolean isLink) {
		this.label = label;
		this.isLink = isLink;
	}

	public boolean getIsLink() {
		return isLink;
	}

	public String getLabel() {
		return label;
	}

	public boolean isLink() {
		return isLink;
	}

	public void setIsLink(boolean isLink) {
		this.isLink = isLink;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
