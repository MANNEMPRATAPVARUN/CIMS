package ca.cihi.cims.web.bean.refset;

public class PickListColumnBean extends RefsetBaseBean {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((columnElementId == null) ? 0 : columnElementId.hashCode());
		result = (prime * result) + ((columnElementVersionId == null) ? 0 : columnElementVersionId.hashCode());
		result = (prime * result) + ((columnOrder == null) ? 0 : columnOrder.hashCode());
		result = (prime * result) + ((columnType == null) ? 0 : columnType.hashCode());
		result = (prime * result) + ((containerElementId == null) ? 0 : containerElementId.hashCode());
		result = (prime * result) + ((containerElementVersionId == null) ? 0 : containerElementVersionId.hashCode());
		result = (prime * result) + (containerSublist ? 1231 : 1237);
		result = (prime * result) + ((picklistElementId == null) ? 0 : picklistElementId.hashCode());
		result = (prime * result) + ((picklistElementVersionId == null) ? 0 : picklistElementVersionId.hashCode());
		result = (prime * result) + ((revisedColumnName == null) ? 0 : revisedColumnName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		PickListColumnBean other = (PickListColumnBean) obj;
		if (columnElementId == null) {
			if (other.columnElementId != null) {
				return false;
			}
		} else if (!columnElementId.equals(other.columnElementId)) {
			return false;
		}
		if (columnElementVersionId == null) {
			if (other.columnElementVersionId != null) {
				return false;
			}
		} else if (!columnElementVersionId.equals(other.columnElementVersionId)) {
			return false;
		}
		if (columnOrder == null) {
			if (other.columnOrder != null) {
				return false;
			}
		} else if (!columnOrder.equals(other.columnOrder)) {
			return false;
		}
		if (columnType == null) {
			if (other.columnType != null) {
				return false;
			}
		} else if (!columnType.equals(other.columnType)) {
			return false;
		}
		if (containerElementId == null) {
			if (other.containerElementId != null) {
				return false;
			}
		} else if (!containerElementId.equals(other.containerElementId)) {
			return false;
		}
		if (containerElementVersionId == null) {
			if (other.containerElementVersionId != null) {
				return false;
			}
		} else if (!containerElementVersionId.equals(other.containerElementVersionId)) {
			return false;
		}
		if (containerSublist != other.containerSublist) {
			return false;
		}
		if (picklistElementId == null) {
			if (other.picklistElementId != null) {
				return false;
			}
		} else if (!picklistElementId.equals(other.picklistElementId)) {
			return false;
		}
		if (picklistElementVersionId == null) {
			if (other.picklistElementVersionId != null) {
				return false;
			}
		} else if (!picklistElementVersionId.equals(other.picklistElementVersionId)) {
			return false;
		}
		if (revisedColumnName == null) {
			if (other.revisedColumnName != null) {
				return false;
			}
		} else if (!revisedColumnName.equals(other.revisedColumnName)) {
			return false;
		}
		return true;
	}

	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 78901287L;

	/**
	 * Picklist Column Order.
	 */
	private Integer columnOrder;

	/**
	 * Picklist Column Type.
	 */
	private String columnType;

	/**
	 * Picklist Revised Column Name.
	 */
	private String revisedColumnName;

	private boolean containerSublist;

	public boolean isContainerSublist() {
		return containerSublist;
	}

	public void setContainerSublist(boolean containerSublist) {
		this.containerSublist = containerSublist;
	}

	private Long picklistElementId;
	private Long picklistElementVersionId;

	private Long containerElementId;
	private Long containerElementVersionId;

	private Long columnElementId;
	private Long columnElementVersionId;

	public Long getColumnElementId() {
		return columnElementId;
	}

	public void setColumnElementId(Long columnElementId) {
		this.columnElementId = columnElementId;
	}

	public Long getColumnElementVersionId() {
		return columnElementVersionId;
	}

	public void setColumnElementVersionId(Long columnElementVersionId) {
		this.columnElementVersionId = columnElementVersionId;
	}

	public PickListColumnBean() {
	}

	public Integer getColumnOrder() {
		return columnOrder;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnOrder(Integer columnOrder) {
		this.columnOrder = columnOrder;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Long getContainerElementId() {
		return containerElementId;
	}

	public void setContainerElementId(Long containerElementId) {
		this.containerElementId = containerElementId;
	}

	public Long getContainerElementVersionId() {
		return containerElementVersionId;
	}

	public void setContainerElementVersionId(Long containerElementVersionId) {
		this.containerElementVersionId = containerElementVersionId;
	}

	public String getRevisedColumnName() {
		return revisedColumnName;
	}

	public void setRevisedColumnName(String revisedColumnName) {
		this.revisedColumnName = revisedColumnName;
	}

	public Long getPicklistElementId() {
		return picklistElementId;
	}

	public void setPicklistElementId(Long picklistElementId) {
		this.picklistElementId = picklistElementId;
	}

	public Long getPicklistElementVersionId() {
		return picklistElementVersionId;
	}

	public void setPicklistElementVersionId(Long picklistElementVersionId) {
		this.picklistElementVersionId = picklistElementVersionId;
	}

	@Override
	public String toString() {
		return "PickListColumnBean [columnOrder=" + columnOrder + ", columnType=" + columnType + ", revisedColumnName="
				+ revisedColumnName + ", containerSublist=" + containerSublist + ", containerElementId="
				+ containerElementId + ", containerElementVersionId=" + containerElementVersionId + ", columnElementId="
				+ columnElementId + ", columnElementVersionId=" + columnElementVersionId + "]";
	}

}
