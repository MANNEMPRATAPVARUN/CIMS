package ca.cihi.cims.bll;

public class ContextDefinition {

	public static ContextDefinition forChangeContext(long changeRequestId) {
		return new ContextDefinition(changeRequestId, null, null, null);
	}

	public static ContextDefinition forChangeContext(Long changeRequestId, String baseClassification,
			Long changeContextId) {
		return new ContextDefinition(changeRequestId, baseClassification, null, changeContextId);
	}

	public static ContextDefinition forChangeContext(String baseClassification, Long changeContextId) {
		return forChangeContext(null, baseClassification, changeContextId);
	}

	public static ContextDefinition forVersion(String baseClassification, String versionCode) {
		return new ContextDefinition(null, baseClassification, versionCode, null);
	}

	// -----------------------------------------------------------------------------------

	private final Long changeRequestId;
	private final String baseClassification;
	private final String versionCode;
	private final Long changeContextId;

	// -----------------------------------------------------------------------------------

	private ContextDefinition(Long changeRequestId, String baseClassification, String versionCode, Long changeContextId) {
		this.changeRequestId = changeRequestId;
		this.baseClassification = baseClassification;
		this.versionCode = versionCode;
		this.changeContextId = changeContextId;
	}

	public String getBaseClassification() {
		return baseClassification;
	}

	public Long getChangeContextd() {
		return changeContextId;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public String getVersionCode() {
		return versionCode;
	}

	@Override
	public String toString() {
		return "Context[bc=" + baseClassification + ","
				+ (versionCode != null ? "vc=" + versionCode : "chgCtxId=" + changeContextId) + "]";
	}

}
