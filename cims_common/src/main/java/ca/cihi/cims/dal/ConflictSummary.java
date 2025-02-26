package ca.cihi.cims.dal;

import java.util.HashMap;
import java.util.Map;

public class ConflictSummary {

	private Map<Long, Map<ElementVersion, ElementVersion>> summaryMap = new HashMap<Long, Map<ElementVersion, ElementVersion>>();
	private ContextIdentifier contextId;

	public ConflictSummary(ContextIdentifier contextId) {
		this.contextId = contextId;
	}

	public void add(ElementVersion changeRequestElementVersion, ElementVersion baseElementVersion) {

		long domainElementId;

		if (changeRequestElementVersion instanceof PropertyVersion) {
			domainElementId = ((PropertyVersion) changeRequestElementVersion).getDomainElementId();
		} else {
			// This is a ConceptVersion
			domainElementId = changeRequestElementVersion.getElementId();
		}

		if (summaryMap.containsKey(domainElementId)) {
			// Exists
			Map<ElementVersion, ElementVersion> evPair = summaryMap.get(domainElementId);
			evPair.put(changeRequestElementVersion, baseElementVersion);
			summaryMap.put(domainElementId, evPair);
		} else {
			// New
			Map<ElementVersion, ElementVersion> evPair = new HashMap<ElementVersion, ElementVersion>();
			evPair.put(changeRequestElementVersion, baseElementVersion);
			summaryMap.put(domainElementId, evPair);
		}

	}

	public Map<Long, Map<ElementVersion, ElementVersion>> report() {
		return summaryMap;
	}

	public void flush() {
		summaryMap.clear();
	}

	public ContextIdentifier getContextId() {
		return contextId;
	}

}
