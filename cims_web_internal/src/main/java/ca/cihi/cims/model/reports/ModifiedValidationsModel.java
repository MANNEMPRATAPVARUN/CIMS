package ca.cihi.cims.model.reports;

import ca.cihi.cims.content.shared.ValidationXml;

public class ModifiedValidationsModel {

	private String codeValue;
	private String currentYear;
	private String priorYear;
	private String dataHolding;
	private String dataHoldingCode;
	
	private String currentXml;
	private String priorXml;
	private String currentStatus;
	private String priorStatus;
	
	private ValidationXml currentValidation;
	private ValidationXml priorValidation;
	public String getCodeValue() {
		return codeValue;
	}
	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}
	public String getCurrentYear() {
		return currentYear;
	}
	public void setCurrentYear(String currentYear) {
		this.currentYear = currentYear;
	}
	public String getPriorYear() {
		return priorYear;
	}
	public void setPriorYear(String priorYear) {
		this.priorYear = priorYear;
	}
	public String getDataHolding() {
		return dataHolding;
	}
	public void setDataHolding(String dataHolding) {
		this.dataHolding = dataHolding;
	}
	public String getDataHoldingCode() {
		return dataHoldingCode;
	}
	public void setDataHoldingCode(String dataHoldingCode) {
		this.dataHoldingCode = dataHoldingCode;
	}
	public String getCurrentXml() {
		return currentXml;
	}
	public void setCurrentXml(String currentXml) {
		this.currentXml = currentXml;
	}
	public String getPriorXml() {
		return priorXml;
	}
	public void setPriorXml(String priorXml) {
		this.priorXml = priorXml;
	}
	public String getCurrentStatus() {
		return currentStatus;
	}
	public void setCurrentStatus(String currentStatus) {
		this.currentStatus = currentStatus;
	}
	public String getPriorStatus() {
		return priorStatus;
	}
	public void setPriorStatus(String priorStatus) {
		this.priorStatus = priorStatus;
	}
	public ValidationXml getCurrentValidation() {
		return currentValidation;
	}
	public void setCurrentValidation(ValidationXml currentValidation) {
		this.currentValidation = currentValidation;
	}
	public ValidationXml getPriorValidation() {
		return priorValidation;
	}
	public void setPriorValidation(ValidationXml priorValidation) {
		this.priorValidation = priorValidation;
	}
	
}
