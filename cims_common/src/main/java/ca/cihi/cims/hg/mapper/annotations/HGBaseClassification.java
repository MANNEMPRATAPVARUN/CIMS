package ca.cihi.cims.hg.mapper.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be added to each concrete class in a content model.
 */
@Target({ ElementType.TYPE })
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface HGBaseClassification {
	/**
	 * The name of the base classification, e.g. ICD-10-CA or CCI.
	 */
	public String value();
}
