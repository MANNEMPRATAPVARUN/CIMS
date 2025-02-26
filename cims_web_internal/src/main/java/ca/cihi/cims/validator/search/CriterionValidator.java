package ca.cihi.cims.validator.search;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.model.search.Criterion;

public class CriterionValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Criterion.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		Criterion criterion = (Criterion)object;
		if(!isValidType(criterion, criterion.getValue())) {
			errors.reject("criterion.invalid.value", 
					new Object[]{criterion.getId(), criterion.getType().getModelName(), criterion.getType().getClassName(), criterion.getValue().getClass().getName()}, 
					String.format("Invalid value type for criterion id: %d, model name: %s. Expecting %s but got %s",
							criterion.getId(), criterion.getType().getModelName(), criterion.getType().getClassName(), criterion.getValue().getClass().getName()));
		}
	}
	
	protected boolean isValidType(Criterion criterion, Object value) {
		boolean valid = false;
		if(value == null) {
			valid = true;
		}
		else {
			Class expectedClass = getClassForName(criterion.getType().getClassName());
			if(expectedClass != null) {
				valid = expectedClass.isAssignableFrom(value.getClass());
			}
		}
		return valid;
	}
	
	private Class getClassForName(String className) {
		try {
			return Class.forName(className);
		}
		catch(Exception e) {
			//do nothing
		}
		return null;
	}

}
