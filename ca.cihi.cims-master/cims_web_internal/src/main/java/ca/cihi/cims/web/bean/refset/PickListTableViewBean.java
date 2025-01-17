package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;
import java.util.List;

import ca.cihi.cims.model.refset.ColumnModel;

public class PickListTableViewBean implements Serializable {
	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 90207433396L;

	/**
	 * Picklist code.
	 */
	private String code;

	/**
	 * Picklist name.
	 */
	private String name;

	/**
	 * Picklist classification standard.
	 */
	private String classificationStandard;

	/**
	 * Context Id.
	 */
	private Long contextId;

	/**
	 * Picklist element id.
	 */
	private Long picklistElementId;

	/**
	 * Picklist element version id.
	 */
	private Long picklistElementVersionId;

	/**
	 * List of saved column.
	 */
	private List<ColumnModel> listColumn;

	/**
	 * Flag to indicate user is able to change picklist or not.
	 */
	private boolean editable;

	public Long getContextId() {
		return contextId;
	}

	public void setContextId(Long contextId) {
		this.contextId = contextId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassificationStandard() {
		return classificationStandard;
	}

	public void setClassificationStandard(String classificationStandard) {
		this.classificationStandard = classificationStandard;
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

	public List<ColumnModel> getListColumn() {
		return listColumn;
	}

	public void setListColumn(List<ColumnModel> listColumn) {
		this.listColumn = listColumn;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((contextId == null) ? 0 : contextId.hashCode());
		result = (prime * result) + ((picklistElementId == null) ? 0 : picklistElementId.hashCode());
		result = (prime * result) + ((picklistElementVersionId == null) ? 0 : picklistElementVersionId.hashCode());
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
		PickListTableViewBean other = (PickListTableViewBean) obj;
		if (contextId == null) {
			if (other.contextId != null) {
				return false;
			}
		} else if (!contextId.equals(other.contextId)) {
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
		return true;
	}

	@Override
	public String toString() {
		return "PickListTableViewBean [code=" + code + ", name=" + name + ", classificationStandard="
				+ classificationStandard + ", contextId=" + contextId + ", picklistElementId=" + picklistElementId
				+ ", picklistElementVersionId=" + picklistElementVersionId + ", listColumn=" + listColumn
				+ ", editable=" + editable + "]";
	}
}
