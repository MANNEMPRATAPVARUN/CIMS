package ca.cihi.cims.hg.mapper.config;

import java.lang.reflect.Method;

public class PropertyConfig {

    /**
     * TODO: Remove me
     */
    //@Deprecated
    //private BaseClassification baseClassification;

    private PropertyMethods propertyMethods;

    public PropertyConfig(/*BaseClassification baseClassification, */PropertyMethods propertyMethods) {
        //this.baseClassification = baseClassification;
        this.propertyMethods = propertyMethods;
    }

    /**
     * This is not safe to call because there are some wrapper classes that are shared between classification systems.
     * Property adapters should instead get the base classification out of the context. TOOD: Remove this method
     * entirely and everything that populates the underlying field.
     */
	// @Deprecated
	// public BaseClassification getBaseClassification() {
	// return baseClassification;
	// }

    public String getPropertyName() {
        return propertyMethods.getPropertyName();
    }

    public PropertyMethods getPropertyMethods() {
        return propertyMethods;
    }

    public boolean isForMethod(Method method) {
        return propertyMethods.hasMethod(method);
    }

}