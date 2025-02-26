package ca.cihi.cims.model.tabular;

public class TabularConceptDetails {

	private long elementId;
	private long classId;
	private String className;
	private String code;

	private String userTitleEnglish;
	private String userTitleFrench;
	private String shortTitleEnglish;
	private String shortTitleFrench;
	private String longTitleEnglish;
	private String longTitleFrench;

	private String diagramFileNameEnglish;
	private String diagramFileNameFrench;
	private byte[] diagramEnglishBytes;
	private byte[] diagramFrenchBytes;

	// FIXME: Fail to convert to internal representation
	private String canadianEnhancement;
	private String tablePresentationEnglish;
	private String tablePresentationFrench;
	private Long daggerAsteriscId;

	private String status;
	private int nestingLevel;
	private String chapterCode;

	private String versionCode;
	private String contextVersionCode;
	private Long invasivenessLevelId;

	private long parentId;
	private String parentCode;
	private int parentNestingLevel;

	// ------------------------------------------------

	public String getCanadianEnhancement() {
		return canadianEnhancement;
	}

	public String getChapterCode() {
		return chapterCode;
	}

	public long getClassId() {
		return classId;
	}

	public String getClassName() {
		return className;
	}

	public String getCode() {
		return code;
	}

	public String getContextVersionCode() {
		return contextVersionCode;
	}

	public Long getDaggerAsteriscId() {
		return daggerAsteriscId;
	}

	public byte[] getDiagramEnglishBytes() {
		return diagramEnglishBytes;
	}

	public String getDiagramFileNameEnglish() {
		return diagramFileNameEnglish;
	}

	public String getDiagramFileNameFrench() {
		return diagramFileNameFrench;
	}

	public byte[] getDiagramFrenchBytes() {
		return diagramFrenchBytes;
	}

	public long getElementId() {
		return elementId;
	}

	public Long getInvasivenessLevelId() {
		return invasivenessLevelId;
	}

	public String getLongTitleEnglish() {
		return longTitleEnglish;
	}

	public String getLongTitleFrench() {
		return longTitleFrench;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public String getParentCode() {
		return parentCode;
	}

	public long getParentId() {
		return parentId;
	}

	public int getParentNestingLevel() {
		return parentNestingLevel;
	}

	public String getShortTitleEnglish() {
		return shortTitleEnglish;
	}

	public String getShortTitleFrench() {
		return shortTitleFrench;
	}

	public String getStatus() {
		return status;
	}

	public String getTablePresentationEnglish() {
		return tablePresentationEnglish;
	}

	public String getTablePresentationFrench() {
		return tablePresentationFrench;
	}

	public String getUserTitleEnglish() {
		return userTitleEnglish;
	}

	public String getUserTitleFrench() {
		return userTitleFrench;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public boolean isCanadianEnhancementBool() {
		return "Y".equalsIgnoreCase(canadianEnhancement);
	}

	public void setCanadianEnhancement(String canadianEnhancement) {
		this.canadianEnhancement = canadianEnhancement;
	}

	public void setChapterCode(String chapterCode) {
		this.chapterCode = chapterCode;
	}

	public void setClassId(long classId) {
		this.classId = classId;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setContextVersionCode(String contextVersionCode) {
		this.contextVersionCode = contextVersionCode;
	}

	public void setDaggerAsteriscId(Long daggerAsteriscId) {
		this.daggerAsteriscId = daggerAsteriscId;
	}

	public void setDiagramEnglishBytes(byte[] diagramEnglishBytes) {
		this.diagramEnglishBytes = diagramEnglishBytes;
	}

	public void setDiagramFileNameEnglish(String diagramFileNameEnglish) {
		this.diagramFileNameEnglish = diagramFileNameEnglish;
	}

	public void setDiagramFileNameFrench(String diagramFileNameFrench) {
		this.diagramFileNameFrench = diagramFileNameFrench;
	}

	public void setDiagramFrenchBytes(byte[] diagramFrenchBytes) {
		this.diagramFrenchBytes = diagramFrenchBytes;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setInvasivenessLevelId(Long invasivenessLevelId) {
		this.invasivenessLevelId = invasivenessLevelId;
	}

	public void setLongTitleEnglish(String longTitleEnglish) {
		this.longTitleEnglish = longTitleEnglish;
	}

	public void setLongTitleFrench(String longTitleFrench) {
		this.longTitleFrench = longTitleFrench;
	}

	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public void setParentNestingLevel(int parentNestingLevel) {
		this.parentNestingLevel = parentNestingLevel;
	}

	public void setShortTitleEnglish(String shortTitleEnglish) {
		this.shortTitleEnglish = shortTitleEnglish;
	}

	public void setShortTitleFrench(String shortTitleFrench) {
		this.shortTitleFrench = shortTitleFrench;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTablePresentationEnglish(String tablePresentationEnglish) {
		this.tablePresentationEnglish = tablePresentationEnglish;
	}

	public void setTablePresentationFrench(String tablePresentationFrench) {
		this.tablePresentationFrench = tablePresentationFrench;
	}

	public void setUserTitleEnglish(String userTitleEnglish) {
		this.userTitleEnglish = userTitleEnglish;
	}

	public void setUserTitleFrench(String userTitleFrench) {
		this.userTitleFrench = userTitleFrench;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

}
