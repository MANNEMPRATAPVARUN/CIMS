package ca.cihi.cims.web.bean.index;

import org.apache.commons.lang.StringUtils;

public class IndexCategoryReferenceBean {

	private boolean deleted;

	private long mainElementId;
	private String mainCode;
	private String mainCustomDescription;
	private String mainDaggerAsterisk;

	private long pairedElementId;
	private String pairedCode;
	private String pairedCustomDescription;
	private String pairedDaggerAsterisk;

	// -----------------------------------------

	public String getMainCode() {
		return mainCode;
	}

	public String getMainCustomDescription() {
		return mainCustomDescription;
	}

	public String getMainDaggerAsterisk() {
		return mainDaggerAsterisk;
	}

	public long getMainElementId() {
		return mainElementId;
	}

	public String getPairedCode() {
		return pairedCode;
	}

	public String getPairedCustomDescription() {
		return pairedCustomDescription;
	}

	public String getPairedDaggerAsterisk() {
		return pairedDaggerAsterisk;
	}

	public long getPairedElementId() {
		return pairedElementId;
	}

	public boolean isBlank() {
		return getMainElementId() == 0 && getPairedElementId() == 0 //
				&& StringUtils.isBlank(getMainCode()) && StringUtils.isBlank(getPairedCode()) //
				&& StringUtils.isBlank(getMainCustomDescription()) && StringUtils.isBlank(getPairedCustomDescription());

	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public void setMainCode(String mainCode) {
		this.mainCode = mainCode;
	}

	public void setMainCustomDescription(String mainCustomDescription) {
		this.mainCustomDescription = mainCustomDescription;
	}

	public void setMainDaggerAsterisk(String mainDaggerAsterisk) {
		this.mainDaggerAsterisk = mainDaggerAsterisk;
	}

	public void setMainElementId(long mainElementId) {
		this.mainElementId = mainElementId;
	}

	public void setPairedCode(String pairedCode) {
		this.pairedCode = pairedCode;
	}

	public void setPairedCustomDescription(String pairedCustomDescription) {
		this.pairedCustomDescription = pairedCustomDescription;
	}

	public void setPairedDaggerAsterisk(String pairedDaggerAsterisk) {
		this.pairedDaggerAsterisk = pairedDaggerAsterisk;
	}

	public void setPairedElementId(long pairedElementId) {
		this.pairedElementId = pairedElementId;
	}

	@Override
	public String toString() {
		return "IndexCategoryReferenceBean [deleted=" + deleted + ", mainCode=" + mainCode + ", mainCustomDescription="
				+ mainCustomDescription + ", mainDaggerAsterisk=" + mainDaggerAsterisk + ", mainElementId="
				+ mainElementId + ", pairedCode=" + pairedCode + ", pairedCustomDescription=" + pairedCustomDescription
				+ ", pairedDaggerAsterisk=" + pairedDaggerAsterisk + ", pairedElementId=" + pairedElementId + "]";
	}

}
