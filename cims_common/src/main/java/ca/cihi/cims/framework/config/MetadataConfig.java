package ca.cihi.cims.framework.config;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.framework.domain.Concept;

public class MetadataConfig {

	private Map<Class<? extends Concept>, ConceptMetadata> config = new HashMap<>();

	public void define(Class<? extends Concept> clazz, ConceptMetadata metadata) {
		config.put(clazz, metadata);
	}

	public ConceptMetadata getMetadata(Class<? extends Concept> clazz) {
		return config.get(clazz);
	}

	public boolean isDefined(Class<? extends Concept> clazz) {
		return config.containsKey(clazz);
	}
}
