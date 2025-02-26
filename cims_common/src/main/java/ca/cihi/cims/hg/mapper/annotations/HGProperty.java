package ca.cihi.cims.hg.mapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply to getter methods to indicate the use of a property.
 */
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface HGProperty {
	/**
	 * What is the class name of the data property element?
	 */
	public String className();

	/**
	 * Which field to return in the *PropertyVersion table. Defaults to value.
	 */
	public String field() default "value";

	/**
	 * Which PropertyVersion class is this associated with?
	 */
	public Class elementClass();
}
