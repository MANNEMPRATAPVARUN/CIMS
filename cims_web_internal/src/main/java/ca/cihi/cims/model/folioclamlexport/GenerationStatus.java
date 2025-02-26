package ca.cihi.cims.model.folioclamlexport;

import java.io.Serializable;
import java.util.Date;

public class GenerationStatus implements Serializable {
	private static final long serialVersionUID = -2335579947062422518L;
	private Long htmlOutputLogId;
	private Date generatedDate;
	private String classification;
	private String year;
	private String language;
	private String status;
	private String downloadUrl;
	private Boolean hasDetailedLog;
	private Boolean lastGeneration;

	public Long getHtmlOutputLogId() {
		return htmlOutputLogId;
	}

	public void setHtmlOutputLogId(Long htmlOutputLogId) {
		this.htmlOutputLogId = htmlOutputLogId;
	}

	public Date getGeneratedDate() {
		return generatedDate;
	}

	public void setGeneratedDate(Date generatedDate) {
		this.generatedDate = generatedDate;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Boolean getHasDetailedLog() {
		return hasDetailedLog;
	}

	public void setHasDetailedLog(Boolean hasDetailedLog) {
		this.hasDetailedLog = hasDetailedLog;
	}

	public Boolean getLastGeneration() {
		return lastGeneration;
	}

	public void setLastGeneration(Boolean lastGeneration) {
		this.lastGeneration = lastGeneration;
	}
	

}
