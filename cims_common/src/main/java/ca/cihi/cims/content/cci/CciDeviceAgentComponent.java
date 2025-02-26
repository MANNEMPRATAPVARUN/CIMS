package ca.cihi.cims.content.cci;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.TextPropertyVersion;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGLang;
import ca.cihi.cims.hg.mapper.annotations.HGProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("DeviceAgent")
@HGBaseClassification("CCI")
public abstract class CciDeviceAgentComponent extends CciComponent implements Comparable<CciDeviceAgentComponent> {

	@HGProperty(className = "AgentTypeDescription", elementClass = TextPropertyVersion.class)
	public abstract String getAgentTypeDescription(@HGLang String language);

	public abstract void setAgentTypeDescription(@HGLang String language, String agentTypeDescription);

	@HGProperty(className = "AgentExample", elementClass = TextPropertyVersion.class)
	public abstract String getAgentExample(@HGLang String language);

	public abstract void setAgentExample(@HGLang String language, String agentExample);

	@HGProperty(className = "AgentATCCode", elementClass = TextPropertyVersion.class)
	public abstract String getAgentATCCode();

	public abstract void setAgentATCCode(String agentATCCode);

	@HGConceptProperty(relationshipClass = "AgentGroupIndicator")
	public abstract CciAgentGroup getAgentGroup();

	public abstract void setAgentGroup(CciAgentGroup value);

	@HGConceptProperty(relationshipClass = "DeviceAgentToSectionCPV")
	public abstract CciTabular getSectionAssociatedWith();

	public abstract void setSectionAssociatedWith(CciTabular tabularConcept);

	// This wont work. See ORA-01795: maximum number of expressions in a list is 1000
	// @HGConceptProperty(relationshipClass = "DeviceAgentCPV", inverse = true)
	
	@Override
	public int compareTo(CciDeviceAgentComponent other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
						.toComparison();
	}

	public static CciDeviceAgentComponent create(ContextAccess access, String code, CciTabular cciTabular) {

		// Optionally add some checks to ensure code is valid
		// For Device/Agent Qualifier 2, the component code can be 2 alpha characters, 2 alpha numeric characters or 2
		// numeric alpha characters.

		String businessKey = BusinessKeyGenerator.generateConceptBusinesskey(access, "DeviceAgent", code,
						cciTabular.getCode());

		CciDeviceAgentComponent wrapper = access.createWrapper(CciDeviceAgentComponent.class, "DeviceAgent",
						businessKey);
		wrapper.setCode(code);
		wrapper.setSectionAssociatedWith(cciTabular);

		return wrapper;
	}
}
