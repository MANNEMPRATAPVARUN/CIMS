package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

public class GenerateReleaseTablesCriteria implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CLASSIFICATION_TYPE_CCI = "CCI";
	public static final String CLASSIFICATION_TYPE_ICD = "ICD-10-CA";
	public static final String CLASSIFICATION_TYPE_BOTH = "ICD-CCI";
	// public static final String FILE_FORMAT_TAB_DELIMITED = "TAB_DELIMITLED";
	// public static final String FILE_FORMAT_FIXED_WIDTH = "FIXED_WIDTH";
	public static final String RELEASE_TYPE_PRELIMINARY_INTERNAL_QA = "Preliminary_Internal_QA";
	public static final String RELEASE_TYPE_PRELIMINARY = "Preliminary";
	public static final String RELEASE_TYPE_OFFICIAL_INTERNAL_QA = "Official_Internal_QA";
	public static final String RELEASE_TYPE_OFFICIAL = "Official";

	@NotNull(message = "Please select the classification.")
	private String classification;

	private Long currentOpenYear;
	// private String asciiFileFormat;
	private FileFormat fileFormat;

	private String note;

	private String releaseType;

	public String getClassification() {
		return classification;
	}

	public Long getCurrentOpenYear() {
		return currentOpenYear;
	}

	public FileFormat getFileFormat() {
		return fileFormat;
	}

	public String getNote() {
		return note;
	}

	public String getReleaseType() {
		return releaseType;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setCurrentOpenYear(Long currentOpenYear) {
		this.currentOpenYear = currentOpenYear;
	}

	public void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setReleaseType(String releaseType) {
		this.releaseType = releaseType;
	}

}
