package ca.cihi.cims.model.folioclamlexport;

import java.io.Serializable;
import java.util.Date;

public class ClamlOutputLog implements Serializable {

	private Long clamlOutputLogId;
	private String fiscalYear;
	private String classificationCode;
	private String languageCode;
	private Long createdByUserId;
	private Date creationDate;
	private String statusCode;
	private String zipFileName;

	public Long getClamlOutputLogId() {
		return clamlOutputLogId;
	}

	public void setHtmlOutputLogId(Long clamlOutputLogId) {
		this.clamlOutputLogId = clamlOutputLogId;
	}

	public String getFiscalYear() {
		return fiscalYear;
	}

	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public String getClassificationCode() {
		return classificationCode;
	}

	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}

	public String getLanguageCode() {
		return languageCode;
	}

	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public Long getCreatedByUserId() {
		return createdByUserId;
	}

	public void setCreatedByUserId(Long createdByUserId) {
		this.createdByUserId = createdByUserId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getZipFileName() {
		return zipFileName;
	}

	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

}
