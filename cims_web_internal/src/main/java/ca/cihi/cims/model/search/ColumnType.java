package ca.cihi.cims.model.search;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;

public class ColumnType extends BaseSerializableCloneableObject implements Comparable<ColumnType>{

	private static final long serialVersionUID = 1L;

	private long id;
	private String modelName, displayName;
	private boolean isDefault;
	private int order;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getModelName() {
		return modelName;
	}
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public boolean isDefault() {
		return isDefault;
	}
	public void setDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}
	public int getOrder() {
		return order;
	}
	public void setOrder(int order) {
		this.order = order;
	}
	@Override
	public int compareTo(ColumnType other) {
		return getOrder() - other.getOrder();
	}
}
