package ca.cihi.cims.framework.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.framework.enums.PropertyType;

@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {
	/**
	 * What is the classsName of the property element?
	 */
	public String classsName();

	/**
	 * What are the degrees which load this property?
	 *
	 * @return
	 */
	public ConceptLoadDegree[] degrees();

	/**
	 * What are the supported languages of the property element?
	 *
	 * @return
	 */
	public Language[] languages() default Language.NOLANGUAGE;

	/**
	 * What is the propertyType of the property element? (table name field in class table)
	 */
	public PropertyType propertyType();
}
