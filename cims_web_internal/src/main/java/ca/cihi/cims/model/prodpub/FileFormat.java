package ca.cihi.cims.model.prodpub;

public enum FileFormat {
	TAB("Tab Delimited"), FIX("Fixed-Width");

	private String fileFormatDescription;

	private FileFormat(String fileFormatDescription) {
		this.fileFormatDescription = fileFormatDescription;
	}

	public String getFileFormatDescription() {
		return fileFormatDescription;
	}

	public void setFileFormatDescription(String fileFormatDescription) {
		this.fileFormatDescription = fileFormatDescription;
	}
}
