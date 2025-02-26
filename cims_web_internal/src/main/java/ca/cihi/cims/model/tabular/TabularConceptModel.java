package ca.cihi.cims.model.tabular;

import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_BLOCK;
import static org.apache.commons.lang.StringUtils.trim;

import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.model.Classification;

public class TabularConceptModel {

	private Long elementId;
	private String code;
	private ConceptStatus status;
	private boolean validCode;
	private Long daggerAsteriskId;
	private boolean canadianEnhancement;
	@Size(max = 60, message = "short title should be less then 60 characters")
	private String shortTitleEng, shortTitleFra;
	@Size(max = 255, message = "long title should be less then 255 characters")
	private String longTitleEng, longTitleFra;
	@Size(max = 255, message = "user title should be less then 255 characters")
	private String userTitleEng, userTitleFra;
	private TabularConceptModel parent;
	private String parentCode;
	private int nestingLevel;
	private boolean morphology;

	private TabularConceptDiagramModel diagramEng = new TabularConceptDiagramModel();
	private TabularConceptDiagramModel diagramFra = new TabularConceptDiagramModel();

	private TabularConceptType type;

	private Long invasivenessLevel;
	private String versionCode;
	private String contextVersionCode;

	private boolean childTable;

	// ----------------------------------------------------------------------

	public Classification getClassification() {
		return type.getClassification();
	}

	public String getCode() {
		return code;
	}

	public String getContextVersionCode() {
		return contextVersionCode;
	}

	public Long getDaggerAsteriskId() {
		return daggerAsteriskId;
	}

	public TabularConceptDiagramModel getDiagramEng() {
		return diagramEng;
	}

	public TabularConceptDiagramModel getDiagramFra() {
		return diagramFra;
	}

	public Long getElementId() {
		return elementId;
	}

	public Long getInvasivenessLevel() {
		return invasivenessLevel == null || invasivenessLevel == -1 ? null : invasivenessLevel;
	}

	public String getLongTitleEng() {
		return longTitleEng;
	}

	public String getLongTitleFra() {
		return longTitleFra;
	}

	public int getNestingLevel() {
		return nestingLevel;
	}

	public TabularConceptModel getParent() {
		return parent;
	}

	public String getParentCode() {
		return parentCode;
	}

	public String getShortTitleEng() {
		return shortTitleEng;
	}

	public String getShortTitleFra() {
		return shortTitleFra;
	}

	public ConceptStatus getStatus() {
		return status;
	}

	public TabularConceptType getType() {
		return type;
	}

	public String getTypeCode() {
		return type.getCode();
	}

	public String getTypeLabel() {
		if (type == CCI_BLOCK || type == ICD_BLOCK) {
			return type.getLabel() + " " + nestingLevel;
		} else {
			return type.getLabel();
		}
	}

	public String getUserTitleEng() {
		return userTitleEng;
	}

	public String getUserTitleFra() {
		return userTitleFra;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public boolean isAddedInCurrentVersionYear() {
		return versionCode == null || StringUtils.equals(contextVersionCode, versionCode);
	}

	public boolean isAddedInPreviouseVersionYear() {
		return !isAddedInCurrentVersionYear();
	}

	public boolean isCanadianEnhancement() {
		return canadianEnhancement;
	}

	public boolean isCci() {
		return Classification.CCI.equals(type.getClassification());
	}

	public boolean isCciBlock() {
		return TabularConceptType.CCI_BLOCK.equals(type);
	}

	public boolean isCciCode() {
		return TabularConceptType.CCI_CCICODE.equals(type);
	}

	public boolean isCciGroup() {
		return TabularConceptType.CCI_GROUP.equals(type);
	}

	public boolean isCciRubric() {
		return TabularConceptType.CCI_RUBRIC.equals(type);
	}

	public boolean isCciSection() {
		return TabularConceptType.CCI_SECTION.equals(type);
	}

	public boolean isChildTable() {
		return childTable;
	}

	public boolean isIcdBlock() {
		return TabularConceptType.ICD_BLOCK.equals(type);
	}

	public boolean isIcdCategory() {
		return TabularConceptType.ICD_CATEGORY.equals(type);
	}

	public boolean isIcdChapter() {
		return TabularConceptType.ICD_CHAPTER.equals(type);
	}

	public boolean isIcdCode() {
		return TabularConceptType.ICD_CODE.equals(type);
	}

	public boolean isMorphology() {
		return morphology;
	}

	public boolean isValidCode() {
		return validCode;
	}

	public void setCanadianEnhancement(boolean canadianEnhancement) {
		this.canadianEnhancement = canadianEnhancement;
	}

	public void setChildTable(boolean childTable) {
		this.childTable = childTable;
	}

	public void setCode(String code) {
		this.code = code == null ? null : code.trim().toUpperCase();
	}

	public void setContextVersionCode(String contextVersionCode) {
		this.contextVersionCode = contextVersionCode;
	}

	public void setDaggerAsteriskId(Long daggerAsteriskId) {
		this.daggerAsteriskId = daggerAsteriskId;
	}

	public void setDiagramEng(TabularConceptDiagramModel diagramEng) {
		this.diagramEng = diagramEng;
	}

	public void setDiagramFra(TabularConceptDiagramModel diagramFra) {
		this.diagramFra = diagramFra;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setInvasivenessLevel(Long invasivenessLevel) {
		this.invasivenessLevel = invasivenessLevel;
	}

	public void setLongTitleEng(String longTitleEng) {
		this.longTitleEng = longTitleEng;
	}

	public void setLongTitleFra(String longTitleFra) {
		this.longTitleFra = longTitleFra;
	}

	public void setMorphology(boolean morphology) {
		this.morphology = morphology;
	}

	public void setNestingLevel(int nestingLevel) {
		this.nestingLevel = nestingLevel;
	}

	public void setParent(TabularConceptModel parent) {
		this.parent = parent;
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}

	public void setShortTitleEng(String shortTitleEng) {
		this.shortTitleEng = shortTitleEng;
	}

	public void setShortTitleFra(String shortTitleFra) {
		this.shortTitleFra = shortTitleFra;
	}

	public void setStatus(ConceptStatus status) {
		this.status = status;
	}

	public void setType(TabularConceptType type) {
		this.type = type;
	}

	public void setUserTitleEng(String userTitleEng) {
		this.userTitleEng = userTitleEng;
	}

	public void setUserTitleFra(String userTitleFra) {
		this.userTitleFra = userTitleFra;
	}

	public void setValidCode(boolean validCode) {
		this.validCode = validCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public void trimSpaces() {
		code = trim(code);
		shortTitleEng = trim(shortTitleEng);
		shortTitleFra = trim(shortTitleFra);
		longTitleEng = trim(longTitleEng);
		longTitleFra = trim(longTitleFra);
		userTitleEng = trim(userTitleEng);
		userTitleFra = trim(userTitleFra);
	}

}
