package ca.cihi.cims.hg.mapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * This annotation indicates that a Java class the class of should normally
 * appear at a package level, which will cause it to apply to all classes. It
 * can also be specified at a class level to override the default.
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HGWrapper {
	public static final String UNSPECIFIED = "";

	/**
	 * The name of the HG 'class'.
	 */
	public String value() default UNSPECIFIED;
}
