package ca.cihi.cims.content.cci;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.builder.CompareToBuilder;

import ca.cihi.cims.content.shared.DomainEnum;
import ca.cihi.cims.hg.mapper.annotations.HGBaseClassification;
import ca.cihi.cims.hg.mapper.annotations.HGConceptProperty;
import ca.cihi.cims.hg.mapper.annotations.HGWrapper;

@HGWrapper("AgentGroup")
@HGBaseClassification("CCI")
public abstract class CciAgentGroup extends DomainEnum implements Comparable<CciAgentGroup> {

	@HGConceptProperty(relationshipClass = "AgentGroupIndicator", inverse = true)
	public abstract Collection<CciDeviceAgentComponent> getDeviceAgentComponents();

	public SortedSet<CciDeviceAgentComponent> getSortedDeviceAgentComponents() {
		return new TreeSet<CciDeviceAgentComponent>(getDeviceAgentComponents());
	}

	@Override
	public int compareTo(CciAgentGroup other) {
		return new CompareToBuilder().append(getCode(), other.getCode()).append(getElementId(), other.getElementId())
						.toComparison();
	}
}
