package ca.cihi.cims.hg.mapper.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Contains configuration information for a set of dynamic wrappers.
 */
@SuppressWarnings("rawtypes")
public class MappingConfig {
	private Map<Class, WrapperConfig> entities = new HashMap<Class, WrapperConfig>();

	public boolean isDefined(Class clazz) {
		return entities.containsKey(clazz);
	}

	public void define(Class clazz, WrapperConfig entity) {
		this.entities.put(clazz, entity);
	}

	public WrapperConfig getEntity(Class clazz) {
		return entities.get(clazz);
	}

	public WrapperConfig forElementClassName(String baseClassification, String elementClassName) {

		for (WrapperConfig wc : entities.values()) {

			if (isBaseClassificationCompatible(baseClassification, wc)) {
				if (wc.getClassNames().contains(elementClassName)) {
					return wc;
				}
			}
		}

		return null;
	}

	private boolean isBaseClassificationCompatible(String baseClassification, WrapperConfig wc) {
		if (wc.getBaseClassification() == null) {
			return true;
		} else
			return wc.getBaseClassification().getName().equals(baseClassification);
	}
}
