package ca.cihi.cims.dal;

import org.apache.commons.lang.builder.ToStringBuilder;

public class StructureElement {

	private long elementId;
	private long elementVersionId;
	private long structureId;

	public long getElementId() {
		return elementId;
	}

	public void setEntityId(long elementId) {
		this.elementId = elementId;
	}

	public long getElementVersionId() {
		return elementVersionId;
	}

	public void setElementVersionId(long elementVersionId) {
		this.elementVersionId = elementVersionId;
	}

	public long getStructureId() {
		return structureId;
	}

	public void setStructureId(long structureId) {
		this.structureId = structureId;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
