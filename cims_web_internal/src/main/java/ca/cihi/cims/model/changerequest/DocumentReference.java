package ca.cihi.cims.model.changerequest;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class DocumentReference implements Serializable, Comparable<DocumentReference> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long documentReferenceId;
	private DocumentReferenceType referenceType;
	private Long changeRequestId;
	private String fileName;
	private String url;
	private String eQueryId;
	private Date lastUpdatedTime;

	public Long getDocumentReferenceId() {
		return documentReferenceId;
	}

	public void setDocumentReferenceId(Long documentReferenceId) {
		this.documentReferenceId = documentReferenceId;
	}

	public DocumentReferenceType getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(DocumentReferenceType referenceType) {
		this.referenceType = referenceType;
	}

	public Long getChangeRequestId() {
		return changeRequestId;
	}

	public void setChangeRequestId(Long changeRequestId) {
		this.changeRequestId = changeRequestId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String geteQueryId() {
		return eQueryId;
	}

	public void seteQueryId(String eQueryId) {
		this.eQueryId = eQueryId;
	}

	public Date getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(Date lastUpdatedTime) {
		this.lastUpdatedTime = lastUpdatedTime;
	}

	@Override
	public int compareTo(DocumentReference other) {
		int value= 0;
		if (this.referenceType == other.getReferenceType()){
			if (this.referenceType == DocumentReferenceType.CODING_QUESTION){   // compare eQueryId and url
				if (!StringUtils.equalsIgnoreCase(this.eQueryId, other.geteQueryId()) || !StringUtils.equalsIgnoreCase(this.url, other.getUrl())){
					value = -1;
				}
			}
			if (this.referenceType==DocumentReferenceType.URC_LINK){   // compare the url
				if (!StringUtils.equalsIgnoreCase(this.url, other.getUrl())){
					value = -1;
				}
			}
			if (this.referenceType == DocumentReferenceType.URC_FILE) {  // compare the filename
				if (!StringUtils.equalsIgnoreCase(this.fileName, other.getFileName())){
					value = -1;
				}
			}
			if (this.referenceType == DocumentReferenceType.OTHER_LINK) {  // compare the url
				if (!StringUtils.equalsIgnoreCase(this.url, other.getUrl())){
					value = -1;
				}
			}
			if (this.referenceType == DocumentReferenceType.OTHER_FILE) {  // compare the fileName
				if (!StringUtils.equalsIgnoreCase(this.fileName, other.getFileName())){
					value = -1;
				}
			}


		}else{
			value =-1 ;
		}
		return value;
	}

}
