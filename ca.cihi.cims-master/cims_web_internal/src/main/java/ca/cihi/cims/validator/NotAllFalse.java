package ca.cihi.cims.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates whether the values of specified properties are all resolve to 'false' or not. Optionally user can specify
 * an SPEL condition that needs to be evaluated to true in order for validation to be executed
 * 
 * @author rshnaper
 * 
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotAllFalseValidator.class)
@Documented
public @interface NotAllFalse {
	public String condition() default "";

	Class<?>[] groups() default {};

	public String message() default "";

	Class<? extends Payload>[] payload() default {};

	public String[] properties();
}
