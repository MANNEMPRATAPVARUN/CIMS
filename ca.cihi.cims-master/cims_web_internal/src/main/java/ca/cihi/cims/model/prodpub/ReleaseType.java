package ca.cihi.cims.model.prodpub;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.model.Distribution;

public enum ReleaseType {
	UNKNOWN(0, "Unknown", null), PRELIMINARY_INTERNAL_QA(1, "Preliminary_Internal_QA",
			Distribution.DL_ID_PreliminaryRelease), PRELIMINARY(2, "Preliminary", Distribution.DL_ID_PreliminaryRelease), OFFICIAL_INTERNAL_QA(
			3, "Official_Internal_QA", Distribution.DL_ID_OfficialRelease), OFFICIAL(4, "Official",
			Distribution.DL_ID_OfficialRelease);

	private int releaseTypeId;

	private String releaseTypeCode;
	private Long emailDLId;

	private static final Map<String, ReleaseType> STRING_TO_ENUM = new HashMap<String, ReleaseType>();

	static { // Initialize map from constant name to enum constant
		for (ReleaseType releaseType : values()) {
			STRING_TO_ENUM.put(releaseType.getReleaseTypeCode(), releaseType);
		}
	}

	public static ReleaseType fromString(final String code) {
		ReleaseType releaseType = STRING_TO_ENUM.get(code);
		return releaseType;
	}

	private ReleaseType(int releaseTypeId, String releaseTypeCode, Long emailDLId) {
		this.releaseTypeId = releaseTypeId;
		this.releaseTypeCode = releaseTypeCode;
		this.emailDLId = emailDLId;
	}

	public Long getEmailDLId() {
		return emailDLId;
	}

	public String getReleaseTypeCode() {
		return releaseTypeCode;
	}

	public int getReleaseTypeId() {
		return releaseTypeId;
	}

	public void setEmailDLId(Long emailDLId) {
		this.emailDLId = emailDLId;
	}

	public void setReleaseTypeCode(String releaseTypeCode) {
		this.releaseTypeCode = releaseTypeCode;
	}

	public void setReleaseTypeId(int releaseTypeId) {
		this.releaseTypeId = releaseTypeId;
	}

}
