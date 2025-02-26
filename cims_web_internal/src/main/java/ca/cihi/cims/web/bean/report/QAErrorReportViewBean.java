package ca.cihi.cims.web.bean.report;

import java.io.Serializable;
import java.util.Date;

public class QAErrorReportViewBean extends ReportViewBean {

	/**
	 *
	 */

	private String language;
	private String languageDesc;
	private String owner;
	private String ownerUserName;
	private Date dateFrom;
	private Date dateTo;
	private String statusFrom;
	private String statusFromCode;


	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLanguageDesc() {
		return languageDesc;
	}
	public void setLanguageDesc(String languageDesc) {
		this.languageDesc = languageDesc;
	}

	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerUserName() {
		return ownerUserName;
	}
	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	public String getStatusFrom() {
		return statusFrom;
	}
	public void setStatusFrom(String statusFrom) {
		this.statusFrom = statusFrom;
	}

	public String getStatusFromCode() {
		return statusFromCode;
	}
	public void setStatusFromCode(String statusFromCode) {
		this.statusFromCode = statusFromCode;
	}

	public Date getDateFrom() {
		return dateFrom;
	}
	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}
	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

}
