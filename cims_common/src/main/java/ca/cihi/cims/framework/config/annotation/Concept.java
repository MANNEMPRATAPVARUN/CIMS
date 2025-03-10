package ca.cihi.cims.framework.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Concept {

	/**
	 * The classsName of the concept
	 *
	 * @return
	 */
	public String classsName();
}
