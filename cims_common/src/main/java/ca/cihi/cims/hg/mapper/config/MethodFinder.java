package ca.cihi.cims.hg.mapper.config;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class MethodFinder {
	private Map<String, PropertyMethods> bundles = new HashMap<String, PropertyMethods>();

	public MethodFinder(Class clazz) {

		for (Method method : clazz.getMethods()) {

			if (method.getDeclaringClass().equals(Object.class)) {
				continue;
			}

			if (isGetter(method)) {
				contributeGetter(method);
			} else if (isSetter(method)) {
				contributeSetter(method);
			}
		}

	}

	private boolean isSetter(Method method) {
		if (!method.getReturnType().equals(void.class))
			return false;

		return method.getName().startsWith("set");
	}

	private boolean isGetter(Method method) {
		if (method.getReturnType().equals(Void.class)) {
			return false;
		}

		return method.getName().startsWith("is")
				|| method.getName().startsWith("get");
	}

	private void contributeSetter(Method method) {
		findOrCreateBundle(extractPropertyName(method)).addSetter(method);
	}

	private void contributeGetter(Method method) {
		findOrCreateBundle(extractPropertyName(method)).addGetter(method);
	}

	private String extractPropertyName(Method method) {
		return new PropertyNameExtractor()
				.extractPropertyName(method.getName());
	}

	private PropertyMethods findOrCreateBundle(String propertyName) {
		if (!bundles.containsKey(propertyName)) {
			bundles.put(propertyName, new PropertyMethods(propertyName));
		}

		return bundles.get(propertyName);
	}

	public Collection<PropertyMethods> getProperties() {
		return bundles.values();
	}

	public PropertyMethods getProperty(String propertyName) {
		return bundles.get(propertyName);
	}
}
