package ca.cihi.cims.content.cci;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("Tissue")
@HGBaseClassification("CCI")
public abstract class CciTissueComponent extends CciComponent implements Comparable<CciTissueComponent> {

	@HGConceptProperty(relationshipClass = "TissueToSectionCPV")
	public abstract CciTabular getSectionAssociatedWith();

	public abstract void setSectionAssociatedWith(CciTabular tabularConcept);

	// This wont work. See ORA-01795: maximum number of expressions in a list is 1000
	// @HGConceptProperty(relationshipClass = "TissueCPV", inverse = true)
	// public abstract Collection<CciTabular> getConceptsAssociatedWith();

	@Override
	public int compareTo(CciTissueComponent other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
						.toComparison();
	}

	public static CciTissueComponent create(ContextAccess access, String code, CciTabular cciTabular) {

		// Optionally add some checks to ensure code is valid
		// For Tissue Qualifier 3, the component code is 1 alpha numeric character.

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "Tissue", code,
						cciTabular.getCode());

		CciTissueComponent wrapper = access.createWrapper(CciTissueComponent.class, "Tissue", businessKey);
		wrapper.setCode(code);
		wrapper.setSectionAssociatedWith(cciTabular);

		return wrapper;
	}
}
