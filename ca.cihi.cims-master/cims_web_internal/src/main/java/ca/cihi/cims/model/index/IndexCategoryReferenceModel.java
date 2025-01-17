package ca.cihi.cims.model.index;

public class IndexCategoryReferenceModel {

	private long mainElementId;
	private String mainCustomDescription;
	private String mainDaggerAsterisk;

	private long pairedElementId;
	private String pairedCustomDescription;
	private String pairedDaggerAsterisk;

	@Deprecated
	private transient String mainCode;
	@Deprecated
	private transient String pairedCode;

	// ------------------------------------------

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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

	@Deprecated
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
		return "IndexCategoryReferenceModel [mainElementId=" + mainElementId + ", mainCustomDescription="
				+ mainCustomDescription + ", mainDaggerAsterisk=" + mainDaggerAsterisk + ", pairedElementId="
				+ pairedElementId + ", pairedCustomDescription=" + pairedCustomDescription + ", pairedDaggerAsterisk="
				+ pairedDaggerAsterisk + "]";
	}

}
