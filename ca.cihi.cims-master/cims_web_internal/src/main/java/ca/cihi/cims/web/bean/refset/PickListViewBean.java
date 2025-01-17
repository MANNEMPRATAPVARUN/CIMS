package ca.cihi.cims.web.bean.refset;

public class PickListViewBean extends RefsetBaseBean {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((classificationStandard == null) ? 0 : classificationStandard.hashCode());
		result = (prime * result) + ((code == null) ? 0 : code.hashCode());
		result = (prime * result) + ((name == null) ? 0 : name.hashCode());
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
		PickListViewBean other = (PickListViewBean) obj;

		if (classificationStandard == null) {
			if (other.classificationStandard != null) {
				return false;
			}
		} else if (!classificationStandard.equals(other.classificationStandard)) {
			return false;
		}
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
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

	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 7839651611707433396L;
	private String code;
	private String name;
	private String classificationStandard;

	private Long picklistElementId;
	private Long picklistElementVersionId;

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

	@Override
	public String toString() {
		return "PickListViewBean [code=" + code + ", name=" + name + ", classificationStandard="
				+ classificationStandard + ", picklistElementId=" + picklistElementId + ", picklistElementVersionId="
				+ picklistElementVersionId + "]";
	}
}
