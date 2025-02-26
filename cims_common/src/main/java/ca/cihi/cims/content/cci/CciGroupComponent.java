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

@HGWrapper("GroupComp")
@HGBaseClassification("CCI")
public abstract class CciGroupComponent extends CciComponent implements Comparable<CciGroupComponent> {

	@HGProperty(className = "ComponentDefinitionTitle", elementClass = XmlPropertyVersion.class)
	public abstract String getDefinitionTitle(@HGLang String language);

	public abstract void setDefinitionTitle(@HGLang String language, String definitionTitle);

	@HGConceptProperty(relationshipClass = "GroupCompToSectionCPV")
	public abstract CciTabular getSectionAssociatedWith();

	public abstract void setSectionAssociatedWith(CciTabular tabularConcept);

	// This wont work. See ORA-01795: maximum number of expressions in a list is 1000
	// @HGConceptProperty(relationshipClass = "GroupCompCPV", inverse = true)

	@Override
	public int compareTo(CciGroupComponent other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
						.toComparison();
	}

	public static CciGroupComponent create(ContextAccess access, String code, CciTabular cciTabular) {

		// Optionally add some checks to ensure code is valid
		// For Group, the component code can be 1 alpha character or 2 alpha characters.

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "GroupComp", code,
						cciTabular.getCode());

		CciGroupComponent wrapper = access.createWrapper(CciGroupComponent.class, "GroupComp", businessKey);
		wrapper.setCode(code);
		wrapper.setSectionAssociatedWith(cciTabular);

		return wrapper;
	}

}
