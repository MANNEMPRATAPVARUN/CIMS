package ca.cihi.cims.web.bean;

import java.io.Serializable;
import java.util.List;

import ca.cihi.cims.model.TransformationError;

public class BasicInfoBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8145371019381945364L;

	private String classification;
	private String fiscalYear;
	private List<TransformationError> errorList;

	public String getClassification() {
		return classification;
	}

	public List<TransformationError> getErrorList() {
		return errorList;
	}

	public String getFiscalYear() {
		return fiscalYear;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public void setErrorList(List<TransformationError> errorList) {
		this.errorList = errorList;
	}

	public void setFiscalYear(String fiscalYear) {
		this.fiscalYear = fiscalYear;
	}
}
