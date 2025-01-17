package ca.cihi.cims.validator;

import java.beans.PropertyDescriptor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

/**
 * Constraint validator implementation for {@link ValidRange}
 * 
 * @author rshnaper
 * 
 */
@SuppressWarnings("rawtypes")
public class RangeValidator implements ConstraintValidator<ValidRange, Object> {
	private String fromProperty, toProperty;
	private boolean allowNulls;

	private void checkProperty(BeanWrapper wrapper, String propertyName) throws IllegalArgumentException {
		PropertyDescriptor descriptor = wrapper.getPropertyDescriptor(propertyName);
		if (descriptor == null) {
			throw new IllegalArgumentException(String.format("Illegal argument. Property '%s' does not exist",
					propertyName));
		} else if (!Comparable.class.isAssignableFrom(descriptor.getPropertyType())) {
			throw new IllegalArgumentException(String.format("Illegal argument. Property '%s' must be of type %s",
					propertyName, Comparable.class.getName()));
		}
	}

	@Override
	public void initialize(ValidRange annotation) {
		fromProperty = annotation.fromProperty();
		toProperty = annotation.toProperty();
		allowNulls = annotation.allowNulls();
	}

	private boolean isNull(Comparable value) {
		if (value instanceof CharSequence) {
			return StringUtils.isEmpty(value);
		}
		return value == null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		BeanWrapper wrapper = new BeanWrapperImpl(object);

		checkProperty(wrapper, fromProperty);
		checkProperty(wrapper, toProperty);

		Comparable fromValue = (Comparable) wrapper.getPropertyValue(fromProperty);
		Comparable toValue = (Comparable) wrapper.getPropertyValue(toProperty);

		String defaultValidationMessage = null;
		String violatedProperty = null;

		//2020-12-03 - Remove validation for context id in comparative searches
		if (!isNull(toValue) && isNull(fromValue) && !allowNulls) {
			defaultValidationMessage = "Value of ''from'' cannot be null if ''to'' is specified";
			violatedProperty = fromProperty;
		} else if (!isNull(fromValue) && !isNull(toValue) && fromValue.compareTo(toValue) > 0 && !toProperty.equals("contextId")) {
			defaultValidationMessage = "Value of ''to'' cannot be before ''from''";
			violatedProperty = toProperty;
		}

		if (violatedProperty != null) {
			context.disableDefaultConstraintViolation();

			String userProvidedMessage = context.getDefaultConstraintMessageTemplate();
			ConstraintViolationBuilder constraintBuilder;
			String message = userProvidedMessage != null && !StringUtils.isEmpty(userProvidedMessage) ? userProvidedMessage
					: defaultValidationMessage;
			constraintBuilder = context.buildConstraintViolationWithTemplate(message);

			constraintBuilder.addNode(violatedProperty);
			constraintBuilder.addConstraintViolation();
		}

		return violatedProperty == null;
	}
}
