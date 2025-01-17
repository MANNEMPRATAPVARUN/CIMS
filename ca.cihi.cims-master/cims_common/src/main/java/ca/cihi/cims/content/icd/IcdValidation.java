package ca.cihi.cims.content.icd;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.content.shared.FacilityType;
import ca.cihi.cims.content.shared.Validation;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("ValidationICD")
@HGBaseClassification("ICD-10-CA")
public abstract class IcdValidation extends Validation {

	private static final String ICD = "ValidationICD";

	public static IcdValidation create(ContextAccess access, IcdTabular concept, long dataHoldingId) {
		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, ICD,
				String.valueOf(concept.getElementId()), String.valueOf(dataHoldingId));
		FacilityType ft = access.load(dataHoldingId);
		IcdValidation v = access.createWrapper(IcdValidation.class, ICD, businessKey);
		v.setFacilityType(ft);
		v.setTabularConcept(concept);
		return v;
	}

	@HGConceptProperty(relationshipClass = "ValidationICDCPV", inverse = false)
	public abstract IcdTabular getTabularConcept();

	public abstract void setTabularConcept(IcdTabular tabularConcept);

	@Override
	public String toString() {
		return "IcdValidation [getElementId()=" + getElementId() + ", getStatus()=" + getStatus() + "]";
	}

}
