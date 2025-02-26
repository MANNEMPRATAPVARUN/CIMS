package ca.cihi.cims.model.notification;

public enum NotificationTypeCode {

	PCR("Prelimary Classification Release"), FCR("Final Classification Release"), NCR("New Component Request"), NRVR(
			"New Reference Value Request"), AR("Advice Request"), RR("Review Request"), CRC(
			"Component Request Complete"), RVRC("Reference Value Request Complete"), AP("Advice Provided"), RC(
			"Review Complete"), CRA("Change Request Assignment"), CRP("Change Request Pick-Up"), CRTO(
			"Change Request Ownership"), PUN("Pick-up New"), PUA("Pick-up Acceptance"), PUT("Pick-up Translation"), PUV(
			"Pick-up Validation"), PUR("Pick-up Realization"), PAPPROVAL("Pick-up Approval"), CRI(
			"Change Request Incomplete"), CRACPT("Change Request Accepted"), PUQA("Pick-up QA"), WRPICD(
			"Wrap Up ICD Work"), WRPCCI("Wrap Up CCI Work"), WRPBTH("Wrap Up ICD and CCI Work"), PRPIQA(
			"Preliminary internal QA Package Released"), PROIQA("Official internal QA Package Released"), PRP(
			"Preliminary Package Released"), PRO("Official Package Released"),

	FRZ_ICD("ICD FREEZED"), FRZ_CCI("ICD FREEZED"), FRZ_BTH("ICD and CCI FREEZED");

	private String desc;

	private NotificationTypeCode(final String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

}
