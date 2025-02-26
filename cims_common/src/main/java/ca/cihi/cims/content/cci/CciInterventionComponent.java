package ca.cihi.cims.content.cci;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.XmlPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("Intervention")
@HGBaseClassification("CCI")
public abstract class CciInterventionComponent extends CciComponent implements Comparable<CciInterventionComponent> {

	@HGProperty(className = "ComponentDefinitionTitle", elementClass = XmlPropertyVersion.class)
	public abstract String getDefinitionTitle(@HGLang String language);

	public abstract void setDefinitionTitle(@HGLang String language, String definitionTitle);

	@HGConceptProperty(relationshipClass = "InterventionToSectionCPV")
	public abstract CciTabular getSectionAssociatedWith();

	public abstract void setSectionAssociatedWith(CciTabular tabularConcept);

	// This wont work. See ORA-01795: maximum number of expressions in a list is 1000
	// @HGConceptProperty(relationshipClass = "InterventionCPV", inverse = true)

	@Override
	public int compareTo(CciInterventionComponent other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
						.toComparison();
	}

	public static CciInterventionComponent create(ContextAccess access, String code, CciTabular cciTabular) {

		// Optionally add some checks to ensure code is valid
		// For Intervention, the component code is 2 numeric characters.

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "Intervention", code,
						cciTabular.getCode());

		CciInterventionComponent wrapper = access.createWrapper(CciInterventionComponent.class, "Intervention",
						businessKey);
		wrapper.setCode(code);
		wrapper.setSectionAssociatedWith(cciTabular);

		return wrapper;
	}
}
