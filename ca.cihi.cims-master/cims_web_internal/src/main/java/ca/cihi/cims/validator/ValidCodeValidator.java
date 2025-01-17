package ca.cihi.cims.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;

import org.springframework.util.StringUtils;

import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.web.bean.search.TabularConceptAwareBean;

/**
 * ICD/CCI code validator
 * 
 * @author rshnaper
 * 
 */
public class ValidCodeValidator implements ConstraintValidator<ValidCode, TabularConceptAwareBean> {
	private static String INVALID_CODE_MESSAGE = "Invalid code ''%s''  %s";

	private void buildErrorMessage(ConstraintValidatorContext context, String property, String defaultMessage) {
		String userProvidedMessage = context.getDefaultConstraintMessageTemplate();
		String message = userProvidedMessage != null && !StringUtils.isEmpty(userProvidedMessage) ? userProvidedMessage
				: defaultMessage;
		ConstraintViolationBuilder constraintBuilder = context.buildConstraintViolationWithTemplate(message);
		constraintBuilder.addNode(property);
		constraintBuilder.addConstraintViolation();
	}

	@Override
	public void initialize(ValidCode annotation) {
	}

	@Override
	public boolean isValid(TabularConceptAwareBean object, ConstraintValidatorContext context) {
		CodeValidator validator = new CodeValidator();
		TabularConceptType conceptType = object.getTabularConceptType();
		String codeFromMessage = validate(object.getCodeFrom(conceptType), conceptType, validator);
		String codeToMessage = validate(object.getCodeTo(conceptType), conceptType, validator);

		if (codeFromMessage != null || codeToMessage != null) {
			context.disableDefaultConstraintViolation();

			if (codeFromMessage != null) {
				buildErrorMessage(context, "codeFrom", codeFromMessage);
			}
			if (codeToMessage != null) {
				buildErrorMessage(context, "codeTo", codeToMessage);
			}
		}
		return codeFromMessage == null && codeToMessage == null;
	}

	private String validate(String code, TabularConceptType conceptType, CodeValidator validator) {
		if (code == null || StringUtils.isEmpty(code)) {
			return null;
		}
		String message = validator.validate(conceptType, code, null, 1, false, null);
		if (message != null
				&& (conceptType == TabularConceptType.ICD_CATEGORY || conceptType == TabularConceptType.ICD_BLOCK)) {
			// this is a bit iffy but ICD has two different formats NNNN/N and CNN that we need to validate
			// check the special codes NNNN/N types for ICD if initial CNN validation failed
			String message2 = validator.validate(conceptType, code, null, 1, true, null);
			if (message2 == null) {
				// code is valid
				message = null;
			} else {
				message += ", " + message2;
			}
		}
		return message != null ? String.format(INVALID_CODE_MESSAGE, code, message) : null;
	}
}
