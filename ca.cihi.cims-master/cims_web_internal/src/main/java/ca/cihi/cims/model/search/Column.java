package ca.cihi.cims.model.search;

import java.util.Objects;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

public class Column extends BaseSerializableCloneableObject implements Comparable<Column>{

	private static final long serialVersionUID = 1L;
	private long id;
	private ColumnType type;
	private int order;
	
	public Column(long id, ColumnType type) {
		this.id = id;
		this.type = type;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public ColumnType getType() {
		return type;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), getType().getId());
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Column
				&& ((Column)obj).getId() == getId()
				&& ((Column)obj).getType() != null && getType() != null
				&& ((Column)obj).getType().getId() == getType().getId()
				&& ((Column)obj).getOrder() == getOrder();
	}

	@Override
	public String toString() {
		return String.format("Column[id: %d, displayName: %s,order: %d, typeId: %d]",getId(),getType().getDisplayName(),getOrder(),getType().getId());
	}

	@Override
	public int compareTo(Column other) {
		return getOrder() - other.getOrder();
	}
	
	
}
