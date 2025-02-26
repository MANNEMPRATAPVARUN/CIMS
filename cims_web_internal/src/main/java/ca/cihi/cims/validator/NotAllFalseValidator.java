package ca.cihi.cims.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.StringUtils;

/**
 * Implementation of {@link NotAllFalse} validation annotation
 * 
 * @author rshnaper
 * 
 */
public class NotAllFalseValidator implements ConstraintValidator<NotAllFalse, Object> {
	private String[] properties;
	private String condition;

	private boolean checkCondition(String expression, Object object) {
		SpelParserConfiguration configuration = new SpelParserConfiguration(false, false);
		ExpressionParser parser = new SpelExpressionParser(configuration);
		Expression exp = parser.parseExpression(expression);
		Boolean expressionValue = exp.getValue(object, Boolean.class);
		return expressionValue != null && expressionValue.booleanValue();
	}

	@Override
	public void initialize(NotAllFalse annotation) {
		this.properties = annotation.properties();
		this.condition = !StringUtils.isEmpty(annotation.condition()) ? annotation.condition() : null;
	}

	private boolean isConditionMet(Object object) {
		return condition == null || checkCondition(condition, object);
	}

	@Override
	public boolean isValid(Object object, ConstraintValidatorContext context) {
		if (isConditionMet(object)) {
			BeanWrapper wrapper = new BeanWrapperImpl(object);
			boolean allFalse = true;

			if (properties != null) {
				Boolean value = null;
				for (int i = 0; allFalse && i < properties.length; i++) {
					value = (Boolean) wrapper.getPropertyValue(properties[i]);
					if (value == null) {
						value = Boolean.FALSE;
					}
					allFalse &= !value;
				}
			}

			if (allFalse) {
				context.disableDefaultConstraintViolation();

				String userProvidedMessage = context.getDefaultConstraintMessageTemplate();
				ConstraintViolationBuilder constraintBuilder;
				String message = userProvidedMessage != null && !StringUtils.isEmpty(userProvidedMessage) ? userProvidedMessage
						: "All values are false";
				constraintBuilder = context.buildConstraintViolationWithTemplate(message);

				constraintBuilder.addConstraintViolation();
			}

			return !allFalse;
		}

		return true;
	}

}
