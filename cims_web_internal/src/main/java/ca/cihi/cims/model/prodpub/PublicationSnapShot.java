package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.util.Date;

import ca.cihi.cims.dal.ContextIdentifier;

public class PublicationSnapShot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CLASSIFICATION_CCI = "CCI";
	public static final String CLASSIFICATION_ICD = "ICD";

	private Long snapShotId;
	private Long structureId;
	private Integer snapShotSeqNumber;
	private String snapShotNote;
	private String snapShotQANote;
	private Long createdByUserId;
	private Date createdDate;
	private GenerateFileStatus status;
	private String failedReason;
	private FileFormat fileFormat;

	private ContextIdentifier contextIdentifier;

	// private String classification;

	public String getClassification() {
		String classification = null;
		if (contextIdentifier != null) {
			if (CLASSIFICATION_CCI.equalsIgnoreCase(contextIdentifier.getBaseClassification())) {
				classification = CLASSIFICATION_CCI;
			} else {
				classification = CLASSIFICATION_ICD;
			}
		}

		return classification;
	}

	public ContextIdentifier getContextIdentifier() {
		return contextIdentifier;
	}

	public Long getCreatedByUserId() {
		return createdByUserId;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public FileFormat getFileFormat() {
		return fileFormat;
	}

	public Long getSnapShotId() {
		return snapShotId;
	}

	public String getSnapShotNote() {
		return snapShotNote;
	}

	public String getSnapShotQANote() {
		return snapShotQANote;
	}

	public Integer getSnapShotSeqNumber() {
		return snapShotSeqNumber;
	}

	public GenerateFileStatus getStatus() {
		return status;
	}

	public Long getStructureId() {
		return structureId;
	}

	public void setContextIdentifier(ContextIdentifier contextIdentifier) {
		this.contextIdentifier = contextIdentifier;
	}

	public void setCreatedByUserId(Long createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public void setFileFormat(FileFormat fileFormat) {
		this.fileFormat = fileFormat;
	}

	public void setSnapShotId(Long snapShotId) {
		this.snapShotId = snapShotId;
	}

	public void setSnapShotNote(String snapShotNote) {
		this.snapShotNote = snapShotNote;
	}

	public void setSnapShotQANote(String snapShotQANote) {
		this.snapShotQANote = snapShotQANote;
	}

	public void setSnapShotSeqNumber(Integer snapShotSeqNumber) {
		this.snapShotSeqNumber = snapShotSeqNumber;
	}

	public void setStatus(GenerateFileStatus status) {
		this.status = status;
	}

	public void setStructureId(Long structureId) {
		this.structureId = structureId;
	}

}
