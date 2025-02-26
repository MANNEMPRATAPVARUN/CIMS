package ca.cihi.cims.hg.mapper.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class WrapperConfig extends Type {
	private WrapperConfig superType;

	private Class wrapperClass;

	private Collection<String> classNames = new ArrayList<String>();

	private Map<String, PropertyConfig> propertiesByName = new HashMap<String, PropertyConfig>();

	public WrapperConfig(Class wrapperClass) {
		this.wrapperClass = wrapperClass;
	}

	public Class getWrapperClass() {
		return wrapperClass;
	}

	public void addClassName(String className) {
		classNames.add(className);
	}

	public WrapperConfig getSuperType() {
		return superType;
	}

	public void setSuperType(WrapperConfig superType) {
		this.superType = superType;
	}

	public void addProperty(PropertyConfig propertyConfig) {

		if (propertiesByName.containsKey(propertyConfig.getPropertyName()))

			throw new IllegalStateException(
							"Currently, this system can only hold one property by a given class name per entity. "
											+ "Duplicate definition of property class "
											+ propertyConfig.getPropertyName());
		propertiesByName.put(propertyConfig.getPropertyName(), propertyConfig);
	}

	public Map<String, PropertyConfig> getProperties() {
		return Collections.unmodifiableMap(propertiesByName);
	}

	public PropertyConfig getProperty(String propertyName) {
		return propertiesByName.get(propertyName);
	}

	public PropertyConfig getPropertyConfig(Method method) {
		for (PropertyConfig config : propertiesByName.values())
			if (config.isForMethod(method))
				return config;
		return null;
	}

	public Collection<String> getClassNames() {
		return Collections.unmodifiableCollection(classNames);
	}

	public String toString() {
		return "WrapperConfig(" + wrapperClass.getSimpleName() + ")";
	}

}

// To complete this work, I need to associate a method adapter with each method.
// The association/adapter needs to know:
// The method, obviously
// The class name
// The return type - sometimes the return type is a collection of that type, as
// in the case of collections
// It doesn't need to know the property name, it seems - that's just to
// associate multiple methods from the same class
