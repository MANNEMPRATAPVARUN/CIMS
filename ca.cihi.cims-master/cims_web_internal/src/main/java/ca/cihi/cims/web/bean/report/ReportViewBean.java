package ca.cihi.cims.web.bean.report;

import java.io.Serializable;
import java.util.Date;

public class ReportViewBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8715953481274807218L;
	private String classification;
	private String requestCategory;
	private String indexBook;
	private Long indexElementId;
	private String codeFrom;
	private String codeTo;
	private String leadTerm;
	private Long leadTermElementId;
	private String reportType;
	private String year;
	private String dataHoldingCode;
	private String dataHolding;
	private String ccp_bc;
	private String ccp_cid;
	private String ccp_on;
	private String currentYear;
	private String priorYear;
	private String owner;
	private Date fromDate;
	private Date toDate;
	private String fromStatus;
	private String language;

	public String getCcp_bc() {
		return ccp_bc;
	}

	public String getCcp_cid() {
		return ccp_cid;
	}

	public String getCcp_on() {
		return ccp_on;
	}

	public String getClassification() {
		return classification;
	}

	public String getCodeFrom() {
		return codeFrom;
	}

	public String getCodeTo() {
		return codeTo;
	}

	public String getCurrentYear() {
		return currentYear;
	}

	public String getDataHolding() {
		return dataHolding;
	}

	public String getDataHoldingCode() {
		return dataHoldingCode;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public String getFromStatus() {
		return fromStatus;
	}

	public String getIndexBook() {
		return indexBook;
	}

	public Long getIndexElementId() {
		return indexElementId;
	}

	public String getLanguage() {
		return language;
	}

	public String getLeadTerm() {
		return leadTerm;
	}

	public Long getLeadTermElementId() {
		return leadTermElementId;
	}

	public String getOwner() {
		return owner;
	}

	public String getPriorYear() {
		return priorYear;
	}

	public String getReportType() {
		return reportType;
	}

	public String getRequestCategory() {
		return requestCategory;
	}

	public Date getToDate() {
		return toDate;
	}

	public String getYear() {
		return year;
	}

	public void setCcp_bc(String ccp_bc) {
		this.ccp_bc = ccp_bc;
	}

	public void setCcp_cid(String ccp_cid) {
		this.ccp_cid = ccp_cid;
	}

	public void setCcp_on(String ccp_on) {
		this.ccp_on = ccp_on;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setCodeFrom(String codeFrom) {
		this.codeFrom = codeFrom;
	}

	public void setCodeTo(String codeTo) {
		this.codeTo = codeTo;
	}

	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}

	public void setDataHolding(String dataHolding) {
		this.dataHolding = dataHolding;
	}

	public void setDataHoldingCode(String dataHoldingCode) {
		this.dataHoldingCode = dataHoldingCode;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public void setFromStatus(String fromStatus) {
		this.fromStatus = fromStatus;
	}

	public void setIndexBook(String indexBook) {
		this.indexBook = indexBook;
	}

	public void setIndexElementId(Long indexElementId) {
		this.indexElementId = indexElementId;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setLeadTerm(String leadTerm) {
		this.leadTerm = leadTerm;
	}

	public void setLeadTermElementId(Long leadTermElementId) {
		this.leadTermElementId = leadTermElementId;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public void setPriorYear(String priorYear) {
		this.priorYear = priorYear;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public void setRequestCategory(String requestCategory) {
		this.requestCategory = requestCategory;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
