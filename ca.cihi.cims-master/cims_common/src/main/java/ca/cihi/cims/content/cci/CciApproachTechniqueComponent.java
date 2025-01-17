package ca.cihi.cims.content.cci;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("ApproachTechnique")
@HGBaseClassification("CCI")
public abstract class CciApproachTechniqueComponent extends CciComponent implements
				Comparable<CciApproachTechniqueComponent> {

	@HGConceptProperty(relationshipClass = "ApproachTechniqueToSectionCPV")
	public abstract CciTabular getSectionAssociatedWith();

	public abstract void setSectionAssociatedWith(CciTabular tabularConcept);

	// This wont work. See ORA-01795: maximum number of expressions in a list is 1000
	// @HGConceptProperty(relationshipClass = "ApproachTechniqueCPV", inverse = true)
	
	@Override
	public int compareTo(CciApproachTechniqueComponent other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
						.toComparison();
	}

	public static CciApproachTechniqueComponent create(ContextAccess access, String code, CciTabular cciTabular) {

		// Optionally add some checks to ensure code is valid
		// For Approach/Technique Qualifier 1, the component code is 2 alpha numeric characters.

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "ApproachTechnique", code,
						cciTabular.getCode());

		CciApproachTechniqueComponent wrapper = access.createWrapper(CciApproachTechniqueComponent.class,
						"ApproachTechnique", businessKey);
		wrapper.setCode(code);
		wrapper.setSectionAssociatedWith(cciTabular);

		return wrapper;
	}
}
