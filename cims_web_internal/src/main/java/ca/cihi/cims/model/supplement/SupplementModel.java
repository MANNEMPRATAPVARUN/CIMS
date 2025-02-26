package ca.cihi.cims.model.supplement;

import static org.apache.commons.lang.StringUtils.trim;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.content.shared.Supplement;

public class SupplementModel {

	private long elementId;
	@NotNull
	private ConceptStatus status;
	@Size(min = 1, max = 200, message = "description should be not empty and less then 200 characters")
	private String description;

	private int sortOrder;
	private String markup;
	private SupplementMatter matter;

	private int level;

	private Supplement entity;

	@Deprecated
	private String code;

	private SupplementModel parent;

	// -------------------------------------

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public long getElementId() {
		return elementId;
	}

	public Supplement getEntity() {
		return entity;
	}

	public int getLevel() {
		return level;
	}

	public String getMarkup() {
		return markup;
	}

	public SupplementMatter getMatter() {
		return matter;
	}

	public SupplementModel getParent() {
		return parent;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public ConceptStatus getStatus() {
		return status;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setElementId(long elementId) {
		this.elementId = elementId;
	}

	public void setEntity(Supplement entity) {
		this.entity = entity;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setMarkup(String markup) {
		this.markup = markup;
	}

	public void setMatter(SupplementMatter matter) {
		this.matter = matter;
	}

	public void setParent(SupplementModel parent) {
		this.parent = parent;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public void setStatus(ConceptStatus status) {
		this.status = status;
	}

	public void trimSpaces() {
		description = trim(description);
	}

}
