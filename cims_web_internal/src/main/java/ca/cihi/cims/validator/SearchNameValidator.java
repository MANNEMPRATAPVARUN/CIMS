package ca.cihi.cims.validator;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.web.bean.search.SearchCriteriaBean;

/**
 * Standalone search name validator
 * @author rshnaper
 *
 */
public class SearchNameValidator implements Validator {
	private final static int MIN_SIZE = 1;
	private final static int MAX_SIZE = 50;
	@Override
	public boolean supports(Class<?> clazz) {
		return SearchCriteriaBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		SearchCriteriaBean bean = (SearchCriteriaBean)object;
		boolean valid = false;
		if(bean.getSearchName() != null) {
			int length = bean.getSearchName().trim().length();
			valid = length <= MAX_SIZE && length >= MIN_SIZE;
		}
		
		if(!valid) {
			errors.rejectValue("searchName", "Size", new Object[]{bean.getSearchName(),MAX_SIZE,MIN_SIZE}, "Invalid length");
		}
	}

}
