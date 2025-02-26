package ca.cihi.cims.service.sgsc;

import ca.cihi.cims.data.mapper.SGSCMapper;
import ca.cihi.cims.model.sgsc.SupplementContentRequest;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.ViewService;

public abstract class SupplementContentGenerator {

	protected static final String APPENDIX_GH_ENG_HEADER = "<table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Reference Number</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Reference Description</th><th style='min-width:67px;width:67px;border: 1px solid black;'>Attribute Code</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Attribute Description</th></tr></thead></table>";
	protected static final String APPENDIX_GH_FRA_HEADER = "<table style='width:auto'><thead><tr><th style='min-width:77px;width:77px;border: 1px solid black;'>Numéro de référence</th><th style='min-width:200px;width:200px;border: 1px solid black;'>Description de référence</th><th style='min-width:67px;width:67px;border: 1px solid black;'>Codes des attributs</th><th style='min-width:300px;width:300px;border: 1px solid black;'>Descriptions des attributs</th></tr></thead></table>";
	protected static final String CCI = "CCI";
	protected static final String ICD10CA = "ICD-10-CA";

	private ClassificationService classificationService;
	private ConceptService conceptService;

	private SGSCMapper sgscMapper;
	private ViewService viewService;

	protected String calculateRowspan(int size) {
		if (size > 1) {
			return "rowspan='" + size + "'";
		} else {
			return "";
		}
	}

	public abstract String generateSupplementContent(SupplementContentRequest request);

	public ClassificationService getClassificationService() {
		return classificationService;
	}

	public ConceptService getConceptService() {
		return conceptService;
	}

	public SGSCMapper getSgscMapper() {
		return sgscMapper;
	}

	public ViewService getViewService() {
		return viewService;
	}

	public void setClassificationService(ClassificationService classificationService) {
		this.classificationService = classificationService;
	}

	public void setConceptService(ConceptService conceptService) {
		this.conceptService = conceptService;
	}

	public void setSgscMapper(SGSCMapper sgscMapper) {
		this.sgscMapper = sgscMapper;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}
}
