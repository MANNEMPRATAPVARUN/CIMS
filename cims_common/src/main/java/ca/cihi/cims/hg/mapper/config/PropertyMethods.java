package ca.cihi.cims.hg.mapper.config;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * All the methods associated with a single logical property. There may be
 * multiple getter and setter methods with different signatures.
 */
public class PropertyMethods {
	private String propertyName;

	private List<Method> getterMethods = new ArrayList<Method>();
	private List<Method> setterMethods = new ArrayList<Method>();

	public PropertyMethods(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void addGetter(Method getter) {
		getterMethods.add(getter);
	}

	public void addSetter(Method setter) {
		setterMethods.add(setter);
	}

	public boolean hasMethod(Method method) {
		if (getterMethods.contains(method))
			return true;
		if (setterMethods.contains(method))
			return true;
		return false;
	}

	public List<Method> getGetterMethods() {
		return Collections.unmodifiableList(getterMethods);
	}

	public List<Method> getSetterMethods() {
		return Collections.unmodifiableList(setterMethods);
	}

}
