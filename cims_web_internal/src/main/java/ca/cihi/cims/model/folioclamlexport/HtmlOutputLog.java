package ca.cihi.cims.model.folioclamlexport;

import java.io.Serializable;
import java.util.Date;

public class HtmlOutputLog implements Serializable {

	private static final long serialVersionUID = -2206918703839032170L;
	private Long htmlOutputLogId;
	private String fiscalYear;
	private String classificationCode;
	private String languageCode;
	private Long createdByUserId;
	private Date creationDate;
	private String statusCode;
	private String zipFileName;

	public Long getHtmlOutputLogId() {
		return htmlOutputLogId;
	}

	public void setHtmlOutputLogId(Long htmlOutputLogId) {
		this.htmlOutputLogId = htmlOutputLogId;
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
