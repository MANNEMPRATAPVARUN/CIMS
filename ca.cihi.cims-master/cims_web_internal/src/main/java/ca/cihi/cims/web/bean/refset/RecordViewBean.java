package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;
import java.util.List;

public class RecordViewBean extends RefsetBaseBean implements Serializable {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((containerElementId == null) ? 0 : containerElementId.hashCode());
		result = (prime * result) + ((containerElementVersionId == null) ? 0 : containerElementVersionId.hashCode());
		result = (prime * result) + (containerSublist ? 1231 : 1237);
		result = (prime * result) + ((recordElementId == null) ? 0 : recordElementId.hashCode());
		result = (prime * result) + ((recordElementVersionId == null) ? 0 : recordElementVersionId.hashCode());
		result = (prime * result) + ((values == null) ? 0 : values.hashCode());
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
		RecordViewBean other = (RecordViewBean) obj;
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
		if (recordElementId == null) {
			if (other.recordElementId != null) {
				return false;
			}
		} else if (!recordElementId.equals(other.recordElementId)) {
			return false;
		}
		if (recordElementVersionId == null) {
			if (other.recordElementVersionId != null) {
				return false;
			}
		} else if (!recordElementVersionId.equals(other.recordElementVersionId)) {
			return false;
		}
		if (values == null) {
			if (other.values != null) {
				return false;
			}
		} else if (!values.equals(other.values)) {
			return false;
		}
		return true;
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 7337262894878301402L;
	private Long containerElementId;
	private Long containerElementVersionId;
	private Long recordElementId;
	private Long recordElementVersionId;
	private boolean containerSublist;

	public boolean isContainerSublist() {
		return containerSublist;
	}

	public void setContainerSublist(boolean containerSublist) {
		this.containerSublist = containerSublist;
	}

	private List<ValueViewBean> values;

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

	public List<ValueViewBean> getValues() {
		return values;
	}

	public void setValues(List<ValueViewBean> values) {
		this.values = values;
	}

	public Long getRecordElementId() {
		return recordElementId;
	}

	public void setRecordElementId(Long recordElementId) {
		this.recordElementId = recordElementId;
	}

	public Long getRecordElementVersionId() {
		return recordElementVersionId;
	}

	public void setRecordElementVersionId(Long recordElementVersionId) {
		this.recordElementVersionId = recordElementVersionId;
	}

	@Override
	public String toString() {
		return "RecordViewBean [containerElementId=" + containerElementId + ", containerElementVersionId="
				+ containerElementVersionId + ", recordElementId=" + recordElementId + ", recordElementVersionId="
				+ recordElementVersionId + ", containerSublist=" + containerSublist + ", values=" + values
				+ ", getContextId()=" + getContextId() + "]";
	}

}
