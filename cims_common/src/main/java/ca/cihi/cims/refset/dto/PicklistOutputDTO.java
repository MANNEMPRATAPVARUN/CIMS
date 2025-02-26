package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class PicklistOutputDTO implements Serializable {
	/**
	 * Default Serial Version UID.
	 */
	private static final long serialVersionUID = 18902378L;

	/**
	 * Picklist Output Id.
	 */
	private Integer picklistOutputId;

	/**
	 * Refset Context Id.
	 */
	private Long refsetContextId;

	/**
	 * Picklist Element Id.
	 */
	private Long picklistId;

	/**
	 * Output Name.
	 */
	private String name;

	/**
	 * Language Code.
	 */
	private String languageCode;

	/**
	 * Tab Name in Excel Output File.
	 */
	private String tabName;

	/**
	 * Table Name & Description in Excel Output File.
	 */
	private String tableName;

	/**
	 * Picklist Output Code.
	 */
	private String outputCode;

	/**
	 * ASOT Release Ind code
	 */
	private String asotReleaseIndCode;

	public String getAsotReleaseIndCode() {
		return asotReleaseIndCode;
	}

	public void setAsotReleaseIndCode(String asotReleaseIndCode) {
		this.asotReleaseIndCode = asotReleaseIndCode;
	}

	public Integer getPicklistOutputId() {
		return picklistOutputId;
	}

	public Long getRefsetContextId() {
		return refsetContextId;
	}

	public void setRefsetContextId(Long refsetContextId) {
		this.refsetContextId = refsetContextId;
	}

	public Long getPicklistId() {
		return picklistId;
	}

	public String getName() {
		return name;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public String getTabName() {
		return tabName;
	}

	public String getTableName() {
		return tableName != null ? tableName.trim().replaceAll("[\\t\\n\\r]", " ") : null;
	}

	public void setPicklistOutputId(Integer picklistOutputId) {
		this.picklistOutputId = picklistOutputId;
	}

	public void setPicklistId(Long picklistId) {
		this.picklistId = picklistId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public void setTabName(String tabName) {
		this.tabName = tabName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getOutputCode() {
		return outputCode;
	}

	public void setOutputCode(String outputCode) {
		this.outputCode = outputCode;
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
		PicklistOutputDTO other = (PicklistOutputDTO) obj;
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
		return "PicklistOutputDTO [picklistOutputId=" + picklistOutputId + ", refsetContextId=" + refsetContextId
		        + ", picklistId=" + picklistId + ", name=" + name + ", languageCode=" + languageCode + ", tabName="
		        + tabName + ", tableName=" + tableName + ", outputCode=" + outputCode + "]";
	}
}
