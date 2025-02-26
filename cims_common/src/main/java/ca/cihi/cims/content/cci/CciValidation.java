package ca.cihi.cims.content.cci;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.content.shared.Validation;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("ValidationCCI")
@HGBaseClassification("CCI")
public abstract class CciValidation extends Validation {

	private static final String CCI = "ValidationCCI";

	public static CciValidation create(ContextAccess access, CciTabular concept, long dataHoldingId) {
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, CCI,
				String.valueOf(concept.getElementId()), String.valueOf(dataHoldingId));
		FacilityType ft = access.load(dataHoldingId);
		CciValidation v = access.createWrapper(CciValidation.class, CCI, businessKey);
		v.setFacilityType(ft);
		v.setTabularConcept(concept);
		return v;
	}

	@HGConceptProperty(relationshipClass = "ValidationCCICPV", inverse = false)
	public abstract CciTabular getTabularConcept();

	public abstract void setTabularConcept(CciTabular tabularConcept);

}
