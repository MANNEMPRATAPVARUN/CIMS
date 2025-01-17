package ca.cihi.cims.service.reports;

import static ca.cihi.cims.WebConstants.MARK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.content.cci.CciValidationXml;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.ModifiedValidationsModel;
import ca.cihi.cims.util.XmlUtils;
import ca.cihi.cims.web.bean.report.ReportViewBean;

/**
 * {@link ReportGenerator} of CCI Modified Validations Report. The report contains two rows for each Rubric/CCICode with
 * modified validation rules, one for current year and one for prior year.
 * 
 * @author TYang
 * 
 */
public class CCIModifiedValidations extends ReportGenerator {
	private static final String STATUSREF = "statusRef";
	private static final String LOCATIONREF = "locationRef";
	private static final String EXTENTREF = "extentRef";
	private static final Logger logger = LogManager.getLogger(CCIModifiedValidations.class);

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
		if (logger.isDebugEnabled()) {
			logger.debug("Start generating CCI Modified Validations Report.");
		}
		Map<String, Object> reportData = new HashMap<String, Object>();
		String classification = reportViewBean.getClassification();
		String currentYear = reportViewBean.getCurrentYear();
		String priorYear = reportViewBean.getPriorYear();

		reportData.put("currentYear", currentYear);
		reportData.put("priorYear", priorYear);
		reportData.put("classification", classification);

		List<Map<String, Object>> detailDataList = new ArrayList<Map<String, Object>>();
		reportData.put("detail1", detailDataList);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentYear", currentYear);
		params.put("priorYear", priorYear);
		ContextIdentifier currentContext = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, currentYear);
		params.put("currentContextId", currentContext.getContextId());
		ContextIdentifier priorContext = getLookupService().findBaseContextIdentifierByClassificationAndYear(
				classification, priorYear);
		params.put("priorContextId", priorContext.getContextId());
		params.put("validationCPVClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationCCICPV"));
		params.put("validationClassId", getConceptService()
				.getCCIClassID(WebConstants.CONCEPT_VERSION, "ValidationCCI"));
		params.put("catRubricClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params.put("validationFacilityClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId",
				getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", getConceptService()
				.getCCIClassID("TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId", getConceptService().getCCIClassID("TextPropertyVersion", "Code"));
		params.put("cciCodeClassId", getConceptService().getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params.put("narrowClassId", getConceptService()
				.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		List<ModifiedValidationsModel> modifiedValidations = getReportMapper().findCCIModifiedValidations(params);

		for (ModifiedValidationsModel model : modifiedValidations) {
			processOnePair(model, detailDataList);
		}

		reportData.put("codeCount", detailDataList.size() / 2 + "");

		return reportData;
	}

	private void markStatusChanged(String currentStatus, Map<String, Object> current, Map<String, Object> prior,
			CciValidationXml currentValidation, CciValidationXml priorValidation) {
		if ("DISABLED".equals(currentStatus)) {
			current.put(GENDER, MARK);
			current.put(MAXAGE, MARK);
			current.put(MINAGE, MARK);
			current.put(STATUSREF, MARK);
			current.put(LOCATIONREF, MARK);
			current.put(EXTENTREF, MARK);

			prior.put(GENDER, appendMark(priorValidation.getGenderDescriptionEng()));
			prior.put(MAXAGE, appendMark(priorValidation.getAgeMax()));
			prior.put(MINAGE, appendMark(priorValidation.getAgeMin()));
			prior.put(STATUSREF, appendMark(priorValidation.getStatusReferenceCode()));
			prior.put(LOCATIONREF, appendMark(priorValidation.getLocationReferenceCode()));
			prior.put(EXTENTREF, appendMark(priorValidation.getExtentReferenceCode()));
		} else {
			prior.put(GENDER, MARK);
			prior.put(MAXAGE, MARK);
			prior.put(MINAGE, MARK);
			prior.put(STATUSREF, MARK);
			prior.put(LOCATIONREF, MARK);
			prior.put(EXTENTREF, MARK);

			current.put(GENDER, appendMark(currentValidation.getGenderDescriptionEng()));
			current.put(MAXAGE, appendMark(currentValidation.getAgeMax()));
			current.put(MINAGE, appendMark(currentValidation.getAgeMin()));
			current.put(STATUSREF, appendMark(currentValidation.getStatusReferenceCode()));
			current.put(LOCATIONREF, appendMark(currentValidation.getLocationReferenceCode()));
			current.put(EXTENTREF, appendMark(currentValidation.getExtentReferenceCode()));
		}
	}

	private void processOnePair(ModifiedValidationsModel model, List<Map<String, Object>> detailDataList) {
		Map<String, Object> current = new HashMap<String, Object>();
		Map<String, Object> prior = new HashMap<String, Object>();
		String dhcode = model.getDataHoldingCode();
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("dvdClassId", getConceptService().getCCIClassID("TextPropertyVersion", "DomainValueDescription"));
		params1.put("dvcClassId", getConceptService().getCCIClassID("TextPropertyVersion", "DomainValueCode"));
		params1.put("facilityTypeClassId", getConceptService().getCCIClassID("ConceptVersion", "FacilityType"));
		params1.put("language", "ENG");
		params1.put("dhCode", dhcode);
		String dataHolding = getReportMapper().getDataHoldingByDHCode(params1);

		current.put("codeValue", model.getCodeValue());
		current.put("year", model.getCurrentYear());
		current.put("dataHolding", dataHolding);

		prior.put("codeValue", model.getCodeValue());
		prior.put("year", model.getPriorYear());
		prior.put("dataHolding", dataHolding);

		String currentXml = model.getCurrentXml();
		String priorXml = model.getPriorXml();
		CciValidationXml currentValidation = XmlUtils.deserialize(CciValidationXml.class, currentXml);
		CciValidationXml priorValidation = XmlUtils.deserialize(CciValidationXml.class, priorXml);
		if (currentValidation == null || priorValidation == null) {
			return;
		}
		detailDataList.add(prior);
		detailDataList.add(current);
		if (!model.getCurrentStatus().equals(model.getPriorStatus())) {
			markStatusChanged(model.getCurrentStatus(), current, prior, currentValidation, priorValidation);
		} else {
			markData(current, prior, currentValidation.getGenderDescriptionEng(),
					priorValidation.getGenderDescriptionEng(), GENDER);
			markData(current, prior, currentValidation.getAgeMax(), priorValidation.getAgeMax(), MAXAGE);
			markData(current, prior, currentValidation.getAgeMin(), priorValidation.getAgeMin(), MINAGE);
			markData(current, prior, currentValidation.getStatusReferenceCode(),
					priorValidation.getStatusReferenceCode(), STATUSREF);
			markData(current, prior, currentValidation.getLocationReferenceCode(),
					priorValidation.getLocationReferenceCode(), LOCATIONREF);
			markData(current, prior, currentValidation.getExtentReferenceCode(),
					priorValidation.getExtentReferenceCode(), EXTENTREF);
		}

	}
}
