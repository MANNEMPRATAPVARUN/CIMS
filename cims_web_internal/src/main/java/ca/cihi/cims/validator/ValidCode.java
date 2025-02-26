package ca.cihi.cims.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Validates CCI/ICD codes
 * @author rshnaper
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ValidCodeValidator.class)
@Documented
public @interface ValidCode {
	public String message() default "";
	Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
