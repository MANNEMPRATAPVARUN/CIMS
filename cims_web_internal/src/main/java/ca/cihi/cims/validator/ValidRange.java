package ca.cihi.cims.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validation annotation for ranges
 * 
 * @author rshnaper
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = RangeValidator.class)
@Documented
public @interface ValidRange {
	/**
	 * Defines several {@code @ValidRange} annotations on the same element.
	 */
	@Target({ ElementType.TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	public @interface List {
		ValidRange[] value();
	}

	public boolean allowNulls() default false;

	public String fromProperty();

	Class<?>[] groups() default {};

	public String message() default "";

	Class<? extends Payload>[] payload() default {};

	public String toProperty();
}
