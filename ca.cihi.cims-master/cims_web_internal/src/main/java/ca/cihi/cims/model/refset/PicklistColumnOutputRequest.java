package ca.cihi.cims.model.refset;

import java.io.Serializable;
import java.util.List;

public class PicklistColumnOutputRequest implements Serializable {
	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 890367L;

	/**
	 * Refset Context Id.
	 */
	private Long refsetContextId;

	/**
	 * Picklist Column Output Id.
	 */
	private Integer picklistOutputId;

	/**
	 * List of Picklist Column Output.
	 */
	private List<PicklistColumnOutput> picklistColumnOutputList;

	/*
	 * Tab Name in Excel Output File.
	 */
	private String outputTabName;

	/**
	 * Data Table Name and Description.
	 */
	private String dataTableDescription;

	/**
	 * Indicator for release to ASOT
	 */
	private String asotReleaseIndCode;

	public String getAsotReleaseIndCode() {
		return asotReleaseIndCode;
	}

	public void setAsotReleaseIndCode(String asotReleaseIndCode) {
		this.asotReleaseIndCode = asotReleaseIndCode;
	}

	public Long getRefsetContextId() {
		return refsetContextId;
	}

	public void setRefsetContextId(Long refsetContextId) {
		this.refsetContextId = refsetContextId;
	}

	public Integer getPicklistOutputId() {
		return picklistOutputId;
	}

	public void setPicklistOutputId(Integer picklistOutputId) {
		this.picklistOutputId = picklistOutputId;
	}

	public List<PicklistColumnOutput> getPicklistColumnOutputList() {
		return picklistColumnOutputList;
	}

	public void setPicklistColumnOutputList(List<PicklistColumnOutput> picklistColumnOutputList) {
		this.picklistColumnOutputList = picklistColumnOutputList;
	}

	public String getOutputTabName() {
		return outputTabName;
	}

	public String getDataTableDescription() {
		return dataTableDescription;
	}

	public void setOutputTabName(String outputTabName) {
		this.outputTabName = outputTabName;
	}

	public void setDataTableDescription(String dataTableDescription) {
		this.dataTableDescription = dataTableDescription;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((picklistOutputId == null) ? 0 : picklistOutputId.hashCode());
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
		PicklistColumnOutputRequest other = (PicklistColumnOutputRequest) obj;
		if (picklistOutputId == null) {
			if (other.picklistOutputId != null) {
				return false;
			}
		} else if (!picklistOutputId.equals(other.picklistOutputId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "PicklistColumnOutputRequest [refsetContextId=" + refsetContextId + ", picklistOutputId="
				+ picklistOutputId + ", picklistColumnOutputList=" + picklistColumnOutputList + ", outputTabName="
				+ outputTabName + ", dataTableDescription=" + dataTableDescription + "]";
	}
}
