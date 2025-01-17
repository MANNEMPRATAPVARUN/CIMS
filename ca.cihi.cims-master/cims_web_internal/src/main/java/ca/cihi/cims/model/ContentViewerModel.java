package ca.cihi.cims.model;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

public class ContentViewerModel extends BaseSerializableCloneableObject {

	private static final long serialVersionUID = 538369016981919587L;

	private String conceptCode;
	private String conceptLongDesc;
	private String conceptShortDesc;

	private String conceptUserDesc;

	private String conceptTextDesc;
	private String conceptId;
	private String unitConceptId;
	private String parentConceptId;
	private String parentConceptType;
	private boolean isLeaf;
	private String conceptLevel;
	private String conceptType;
	private String conceptCodeDesc;// convert the concept code to roman numeral if the concept type is CHP
	private Boolean isCanFlag;
	private Integer requestCount;
	private String htmlString;

	private String attributeCode;
	private String attributeDescription;
	private String attributeNote;
	private String attributeRefNote;
	private String attributeRefDesc;
	private String withChildren;
	private String hasChildren;

	private String includeXmlText;

	private String excludeXmlText;

	private String alsoXmlText;

	private String noteXmlText;

	private String supplementDefXmlText;

	private String indexNoteDescXmlText;

	private String indexRefDefXmlText;

	private String daggerAsterisk;

	private String omitCodeXmlText;

	public String getOmitCodeXmlText() {
		return omitCodeXmlText;
	}

	public void setOmitCodeXmlText(String omitCodeXmlText) {
		this.omitCodeXmlText = omitCodeXmlText;
	}

	public String getIndexRefDefXmlText() {
		return this.indexRefDefXmlText;
	}

	public void setIndexRefDefXmlText(String indexRefDefXmlText) {
		this.indexRefDefXmlText = indexRefDefXmlText;
	}

	public String getIndexNoteDescXmlText() {
		return this.indexNoteDescXmlText;
	}

	public void setIndexNoteDescXmlText(String indexNoteDescXmlText) {
		this.indexNoteDescXmlText = indexNoteDescXmlText;
	}

	public String getSupplementDefXmlText() {
		return this.supplementDefXmlText;
	}

	public void setSupplementDefXmlText(String supplementDefXmlText) {
		this.supplementDefXmlText = supplementDefXmlText;
	}

	public String getAttributeCode() {
		return attributeCode;
	}

	public String getHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(String hasChildren) {
		this.hasChildren = hasChildren;
	}

	public String getAttributeDescription() {
		return attributeDescription;
	}

	public String getAttributeNote() {
		return attributeNote;
	}

	public String getAttributeRefDesc() {
		return attributeRefDesc;
	}

	public String getAttributeRefNote() {
		return attributeRefNote;
	}

	public String getConceptCode() {
		return conceptCode;
	}

	public String getConceptCodeDesc() {
		return conceptCodeDesc;
	}

	public String getConceptCodeWithDecimal() {
		return conceptCode;
	}

	public String getConceptId() {
		return conceptId;
	}

	public String getConceptLevel() {
		return conceptLevel;
	}

	public String getConceptLongDesc() {
		return conceptLongDesc;
	}

	public String getConceptShortDesc() {
		return this.conceptShortDesc;
	}

	public String getConceptUserDesc() {
		return this.conceptUserDesc;
	}

	public String getConceptTextDesc() {
		return conceptTextDesc;
	}

	public String getConceptType() {
		return conceptType;
	}

	public String getHtmlString() {
		return htmlString;
	}

	public Boolean getIsCanFlag() {
		return isCanFlag;
	}

	public String getParentConceptId() {
		return parentConceptId;
	}

	public String getParentConceptType() {
		return parentConceptType;
	}

	public Integer getRequestCount() {
		return requestCount;
	}

	public String getTitle() {
		return this.getConceptLongDesc();
	}

	public String getUnitConceptId() {
		return unitConceptId;
	}

	public String getWithChildren() {
		return withChildren;
	}

	public boolean hasRequests() {
		return requestCount != null && requestCount.intValue() > 0;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setAttributeCode(String attributeCode) {
		this.attributeCode = attributeCode;
	}

	public void setAttributeDescription(String attributeDescription) {
		this.attributeDescription = attributeDescription;
	}

	public void setAttributeNote(String attributeNote) {
		this.attributeNote = attributeNote;
	}

	public void setAttributeRefDesc(String attributeRefDesc) {
		this.attributeRefDesc = attributeRefDesc;
	}

	public void setAttributeRefNote(String attributeRefNote) {
		this.attributeRefNote = attributeRefNote;
	}

	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}

	public void setConceptCodeDesc(String conceptCodeDesc) {
		this.conceptCodeDesc = conceptCodeDesc;
	}

	public void setConceptId(String conceptId) {
		this.conceptId = conceptId;
	}

	public void setConceptLevel(String conceptLevel) {
		this.conceptLevel = conceptLevel;
	}

	public void setConceptLongDesc(String conceptLongDesc) {
		this.conceptLongDesc = conceptLongDesc;
	}

	public void setConceptShortDesc(String conceptShortDesc) {
		this.conceptShortDesc = conceptShortDesc;
	}

	public void setConceptUserDesc(String conceptUserDesc) {
		this.conceptUserDesc = conceptUserDesc;
	}

	public void setConceptTextDesc(String conceptTextDesc) {
		this.conceptTextDesc = conceptTextDesc;
	}

	public void setConceptType(String conceptType) {
		this.conceptType = conceptType;
	}

	public void setHtmlString(String htmlString) {
		this.htmlString = htmlString;
	}

	public void setIsCanFlag(Boolean isCanFlag) {
		this.isCanFlag = isCanFlag;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public void setParentConceptId(String parentConceptId) {
		this.parentConceptId = parentConceptId;
	}

	public void setParentConceptType(String parentConceptType) {
		this.parentConceptType = parentConceptType;
	}

	public void setRequestCount(Integer requestCount) {
		this.requestCount = requestCount;
	}

	public void setUnitConceptId(String unitConceptId) {
		this.unitConceptId = unitConceptId;
	}

	public void setWithChildren(String withChildren) {
		this.withChildren = withChildren;
	}

	public String getIncludeXmlText() {
		return this.includeXmlText;
	}

	public void setIncludeXmlText(String includeXmlText) {
		this.includeXmlText = includeXmlText;
	}

	public String getExcludeXmlText() {
		return this.excludeXmlText;
	}

	public void setExcludeXmlText(String excludeXmlText) {
		this.excludeXmlText = excludeXmlText;
	}

	public String getNoteXmlText() {
		return this.noteXmlText;
	}

	public void setNoteXmlText(String notesXmlText) {
		this.noteXmlText = notesXmlText;
	}

	public String getAlsoXmlText() {
		return this.alsoXmlText;
	}

	public void setAlsoXmlText(String alsoXmlText) {
		this.alsoXmlText = alsoXmlText;
	}

	public String getDaggerAsterisk() {
		return this.daggerAsterisk;
	}

	public void setDaggerAsterisk(String daggerAsterisk) {
		this.daggerAsterisk = daggerAsterisk;
	}
}