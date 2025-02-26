package ca.cihi.cims.web.bean.tabular;

import java.util.List;

import ca.cihi.cims.model.Classification;
import ca.cihi.cims.model.tabular.validation.TabularConceptCciValidationSetReportModel;
import ca.cihi.cims.model.tabular.validation.TabularConceptIcdValidationSetReportModel;

public class TabularValidationSetReportBean {

	private String code;
	private Classification classification;

	private List<TabularConceptCciValidationSetReportModel> cciValidationSets;
	private List<TabularConceptIcdValidationSetReportModel> icdValidationSets;

	// ---------------------------------------------------------------

	public List<TabularConceptCciValidationSetReportModel> getCciValidationSets() {
		return cciValidationSets;
	}

	public Classification getClassification() {
		return classification;
	}

	public String getCode() {
		return code;
	}

	public List<TabularConceptIcdValidationSetReportModel> getIcdValidationSets() {
		return icdValidationSets;
	}

	public void setCciValidationSets(List<TabularConceptCciValidationSetReportModel> cciValidationSets) {
		this.cciValidationSets = cciValidationSets;
	}

	public void setClassification(Classification classification) {
		this.classification = classification;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setIcdValidationSets(List<TabularConceptIcdValidationSetReportModel> icdValidationSets) {
		this.icdValidationSets = icdValidationSets;
	}

}
