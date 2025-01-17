package ca.cihi.cims.model.index;

import static org.apache.commons.lang.StringUtils.trim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang.StringUtils;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.content.shared.index.Index;
import ca.cihi.cims.model.Classification;

public class IndexModel {

	private Long elementId;
	@Deprecated
	private String code;
	@NotNull
	private ConceptStatus status;
	private IndexType type;
	private String note;
	@Size(min = 1, max = 200, message = "description should be not empty and less then 200 characters")
	private String description;

	private IndexModel parent;
	private String siteIndicatorCode;
	private int level;

	private Index entity;

	private List<IndexTermReferenceModel> indexReferences = new ArrayList<IndexTermReferenceModel>(0);
	private List<IndexCategoryReferenceModel> categoryReferences = new ArrayList<IndexCategoryReferenceModel>(0);

	private boolean seeAlso;

	private long bookElementId;
	private int section;

	private Map<DrugDetailType, TabularReferenceModel> drugsDetails = new HashMap<DrugDetailType, TabularReferenceModel>();
	private Map<NeoplasmDetailType, TabularReferenceModel> neoplasmDetails = new HashMap<NeoplasmDetailType, TabularReferenceModel>();

	// ----------------------------------------------------------------------

	public void clearDetails() {
		drugsDetails.clear();
		neoplasmDetails.clear();
	}

	public long getBookElementId() {
		return bookElementId;
	}

	public List<IndexCategoryReferenceModel> getCategoryReferences() {
		return categoryReferences;
	}

	public Classification getClassification() {
		return type.getClassification();
	}

	@Deprecated
	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public Map<DrugDetailType, TabularReferenceModel> getDrugsDetails() {
		return drugsDetails;
	}

	public Long getElementId() {
		return elementId;
	}

	public Index getEntity() {
		return entity;
	}

	public List<IndexTermReferenceModel> getIndexReferences() {
		return indexReferences;
	}

	public int getLevel() {
		return level;
	}

	public Map<NeoplasmDetailType, TabularReferenceModel> getNeoplasmDetails() {
		return neoplasmDetails;
	}

	public String getNote() {
		return note;
	}

	public IndexModel getParent() {
		return parent;
	}

	public int getSection() {
		return section;
	}

	public String getSiteIndicatorCode() {
		return siteIndicatorCode;
	}

	public ConceptStatus getStatus() {
		return status;
	}

	public IndexType getType() {
		return type;
	}

	public boolean isIcd() {
		return type.getClassification() == Classification.ICD;
	}

	public boolean isIcd1() {
		return type.getClassification() == Classification.ICD && getSection() == 1;
	}

	public boolean isIcd1Or2OrCci() {
		Classification classification = type.getClassification();
		return classification == Classification.CCI //
				|| classification == Classification.ICD && getSection() == 1 //
				|| classification == Classification.ICD && getSection() == 2;
	}

	public boolean isIcd3() {
		return isIcd() && section == 3;
	}

	public boolean isIcd3Or4() {
		return isIcd() && section == 3 || section == 4;
	}

	public boolean isIcdSection4() {
		return type == IndexType.ICD_NEOPLASM_INDEX;
	}

	public boolean isSeeAlso() {
		return seeAlso;
	}

	public void setBookElementId(long bookElementId) {
		this.bookElementId = bookElementId;
	}

	public void setCategoryReferences(List<IndexCategoryReferenceModel> categoryReferences) {
		this.categoryReferences = categoryReferences;
	}

	@Deprecated
	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setDrugsDetails(Map<DrugDetailType, TabularReferenceModel> drugsDetails) {
		this.drugsDetails = drugsDetails;
	}

	public void setElementId(Long elementId) {
		this.elementId = elementId;
	}

	public void setEntity(Index entity) {
		this.entity = entity;
	}

	public void setIndexReferences(List<IndexTermReferenceModel> indexReferences) {
		this.indexReferences = indexReferences;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setNeoplasmDetails(Map<NeoplasmDetailType, TabularReferenceModel> neoplasmDetails) {
		this.neoplasmDetails = neoplasmDetails;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setParent(IndexModel parent) {
		this.parent = parent;
	}

	public void setSection(int section) {
		this.section = section;
	}

	public void setSection(String section) {
		if (StringUtils.startsWith(section, "Section IV")) {
			this.section = 4;
		} else if (StringUtils.startsWith(section, "Section III")) {
			this.section = 3;
		} else if (StringUtils.startsWith(section, "Section II")) {
			this.section = 2;
		} else if (StringUtils.startsWith(section, "Section I")) {
			this.section = 1;
		}
	}

	public void setSeeAlso(boolean seeAlso) {
		this.seeAlso = seeAlso;
	}

	public void setSiteIndicatorCode(String siteIndicatorCode) {
		this.siteIndicatorCode = siteIndicatorCode;
	}

	public void setStatus(ConceptStatus status) {
		this.status = status;
	}

	public void setType(IndexType type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "IndexModel [elementId=" + elementId + ", bookElementId=" + bookElementId + ", code=" + code
				+ ", description=" + description + ", entity=" + entity + ", level=" + level + ", note=" + note
				+ ", parent=" + parent + ", seeAlso=" + seeAlso + ", siteIndicatorCode=" + siteIndicatorCode
				+ ", status=" + status + ", type=" + type + ", indexReferences=" + indexReferences
				+ ", categoryReferences=" + categoryReferences + "]";
	}

	public void trimSpaces() {
		code = trim(code);
		note = trim(note);
		description = trim(description);
	}

}
