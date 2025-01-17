package ca.cihi.cims.service.reports;

import static ca.cihi.cims.WebConstants.MARK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.content.icd.IcdValidationXml;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.reports.ModifiedValidationsModel;
import ca.cihi.cims.util.XmlUtils;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class ICDModifiedValidations extends ReportGenerator {

	private static final String MRDX = "mrdx";
	private static final String DXTYPE1 = "dxType1";
	private static final String DXTYPE2 = "dxType2";
	private static final String DXTYPE3 = "dxType3";
	private static final String DXTYPE4 = "dxType4";
	private static final String DXTYPE6 = "dxType6";
	private static final String DXTYPE9 = "dxType9";
	private static final String DXTYPEW = "dxTypeW";
	private static final String DXTYPEX = "dxTypeX";
	private static final String DXTYPEY = "dxTypeY";
	private static final String NEWBORN = "newBorn";

	@Override
	public Map<String, Object> generatReportData(ReportViewBean reportViewBean) {
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
				getConceptService().getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationICDCPV"));
		params.put("validationClassId", getConceptService()
				.getICDClassID(WebConstants.CONCEPT_VERSION, "ValidationICD"));
		params.put("catRubricClassId", getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("validationFacilityClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId",
				getConceptService().getICDClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", getConceptService()
				.getICDClassID("TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId", getConceptService().getICDClassID("TextPropertyVersion", "Code"));
		params.put("narrowClassId", getConceptService()
				.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		List<ModifiedValidationsModel> modifiedValidations = getReportMapper().findICDModifiedValidations(params);

		for (ModifiedValidationsModel model : modifiedValidations) {
			processOnePair(model, detailDataList);
		}

		reportData.put("codeCount", detailDataList.size() / 2 + "");

		return reportData;
	}

	private void markStatusChanged(String currentStatus, Map<String, Object> current, Map<String, Object> prior,
			IcdValidationXml currentValidation, IcdValidationXml priorValidation) {
		if ("DISABLED".equals(currentStatus)) {
			current.put(GENDER, MARK);
			current.put(MAXAGE, MARK);
			current.put(MINAGE, MARK);
			current.put(MRDX, MARK);
			current.put(DXTYPE1, MARK);
			current.put(DXTYPE2, MARK);
			current.put(DXTYPE3, MARK);
			current.put(DXTYPE4, MARK);
			current.put(DXTYPE6, MARK);
			current.put(DXTYPE9, MARK);
			current.put(DXTYPEW, MARK);
			current.put(DXTYPEY, MARK);
			current.put(DXTYPEX, MARK);
			current.put(NEWBORN, MARK);

			prior.put(GENDER, appendMark(priorValidation.getGenderDescriptionEng()));
			prior.put(MAXAGE, appendMark(priorValidation.getAgeMax()));
			prior.put(MINAGE, appendMark(priorValidation.getAgeMin()));
			prior.put(MRDX, appendMark(priorValidation.getMRDxMain()));
			prior.put(DXTYPE1, appendMark(priorValidation.getDxType1()));
			prior.put(DXTYPE2, appendMark(priorValidation.getDxType2()));
			prior.put(DXTYPE3, appendMark(priorValidation.getDxType3()));
			prior.put(DXTYPE4, appendMark(priorValidation.getDxType4()));
			prior.put(DXTYPE6, appendMark(priorValidation.getDxType6()));
			prior.put(DXTYPE9, appendMark(priorValidation.getDxType9()));
			prior.put(DXTYPEW, appendMark(priorValidation.getDxTypeW()));
			prior.put(DXTYPEY, appendMark(priorValidation.getDxTypeY()));
			prior.put(DXTYPEX, appendMark(priorValidation.getDxTypeX()));
			prior.put(NEWBORN, appendMark(priorValidation.getNewBorn()));

		} else {
			prior.put(GENDER, MARK);
			prior.put(MAXAGE, MARK);
			prior.put(MINAGE, MARK);
			prior.put(MRDX, MARK);
			prior.put(DXTYPE1, MARK);
			prior.put(DXTYPE2, MARK);
			prior.put(DXTYPE3, MARK);
			prior.put(DXTYPE4, MARK);
			prior.put(DXTYPE6, MARK);
			prior.put(DXTYPE9, MARK);
			prior.put(DXTYPEW, MARK);
			prior.put(DXTYPEY, MARK);
			prior.put(DXTYPEX, MARK);
			prior.put(NEWBORN, MARK);

			current.put(GENDER, appendMark(currentValidation.getGenderDescriptionEng()));
			current.put(MAXAGE, appendMark(currentValidation.getAgeMax()));
			current.put(MINAGE, appendMark(currentValidation.getAgeMin()));
			current.put(MRDX, appendMark(currentValidation.getMRDxMain()));
			current.put(DXTYPE1, appendMark(currentValidation.getDxType1()));
			current.put(DXTYPE2, appendMark(currentValidation.getDxType2()));
			current.put(DXTYPE3, appendMark(currentValidation.getDxType3()));
			current.put(DXTYPE4, appendMark(currentValidation.getDxType4()));
			current.put(DXTYPE6, appendMark(currentValidation.getDxType6()));
			current.put(DXTYPE9, appendMark(currentValidation.getDxType9()));
			current.put(DXTYPEW, appendMark(currentValidation.getDxTypeW()));
			current.put(DXTYPEY, appendMark(currentValidation.getDxTypeY()));
			current.put(DXTYPEX, appendMark(currentValidation.getDxTypeX()));
			current.put(NEWBORN, appendMark(currentValidation.getNewBorn()));
		}
	}

	private void processOnePair(ModifiedValidationsModel model, List<Map<String, Object>> detailDataList) {
		Map<String, Object> current = new HashMap<String, Object>();
		Map<String, Object> prior = new HashMap<String, Object>();
		String dhcode = model.getDataHoldingCode();
		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("dvdClassId", getConceptService().getICDClassID("TextPropertyVersion", "DomainValueDescription"));
		params1.put("dvcClassId", getConceptService().getICDClassID("TextPropertyVersion", "DomainValueCode"));
		params1.put("facilityTypeClassId", getConceptService().getICDClassID("ConceptVersion", "FacilityType"));
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
		IcdValidationXml currentValidation = XmlUtils.deserialize(IcdValidationXml.class, currentXml);
		IcdValidationXml priorValidation = XmlUtils.deserialize(IcdValidationXml.class, priorXml);

		if (currentValidation == null || priorValidation == null) {
			return;
		}

		if (currentValidation != null && priorValidation != null) {
			if (!model.getCurrentStatus().equals(model.getPriorStatus())) {
				markStatusChanged(model.getCurrentStatus(), current, prior, currentValidation, priorValidation);
			} else {
				markData(current, prior, currentValidation.getGenderDescriptionEng(),
						priorValidation.getGenderDescriptionEng(), GENDER);
				markData(current, prior, currentValidation.getAgeMax(), priorValidation.getAgeMax(), MAXAGE);
				markData(current, prior, currentValidation.getAgeMin(), priorValidation.getAgeMin(), MINAGE);
				markData(current, prior, currentValidation.getMRDxMain(), priorValidation.getMRDxMain(), MRDX);
				markData(current, prior, currentValidation.getDxType1(), priorValidation.getDxType1(), DXTYPE1);
				markData(current, prior, currentValidation.getDxType2(), priorValidation.getDxType2(), DXTYPE2);
				markData(current, prior, currentValidation.getDxType3(), priorValidation.getDxType3(), DXTYPE3);
				markData(current, prior, currentValidation.getDxType4(), priorValidation.getDxType4(), DXTYPE4);
				markData(current, prior, currentValidation.getDxType6(), priorValidation.getDxType6(), DXTYPE6);
				markData(current, prior, currentValidation.getDxType9(), priorValidation.getDxType9(), DXTYPE9);
				markData(current, prior, currentValidation.getDxTypeW(), priorValidation.getDxTypeW(), DXTYPEW);
				markData(current, prior, currentValidation.getDxTypeX(), priorValidation.getDxTypeX(), DXTYPEX);
				markData(current, prior, currentValidation.getDxTypeY(), priorValidation.getDxTypeY(), DXTYPEY);
				markData(current, prior, currentValidation.getNewBorn(), priorValidation.getNewBorn(), NEWBORN);
			}
		}
		detailDataList.add(prior);
		detailDataList.add(current);
	}
}
