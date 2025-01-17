package ca.cihi.cims.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

public class ErrorBuilder {

	private final Errors errors;
	private final String model;

	// --------------------------------------------

	public ErrorBuilder(Errors errors) {
		this(null, errors);
	}

	public ErrorBuilder(String model, Errors errors) {
		this.model = model;
		this.errors = errors;
	}

	public Errors getErrors() {
		return errors;
	}

	public FieldError getFieldError(String property) {
		return errors.getFieldError(getModelProperty(property));
	}

	private String getModelProperty(String property) {
		return model == null ? property : model + "." + property;
	}

	public boolean hasErrors() {
		return errors.hasErrors();
	}

	public void rejectValue(String property, String message) {
		errors.rejectValue(getModelProperty(property), message, message);
	}

}
