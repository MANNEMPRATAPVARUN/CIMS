package ca.cihi.cims.validator.search;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.Search;

/**
 * Performs various validations on {@link Search} objects
 * 
 * @author rshnaper
 * 
 */
public class SearchValidator implements Validator {
	private @Autowired
	CriterionValidator criterionValidator;
	private @Autowired
	SearchResultCountValidator resultCountValidator;

	protected CriterionValidator getCriterionValidator() {
		return criterionValidator;
	}

	protected SearchResultCountValidator getResultCountValidator() {
		return resultCountValidator;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Search.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		Search search = (Search) object;

		Collection<Criterion> criteria = search.getCriteria();
		if (criteria != null && !criteria.isEmpty()) {
			for (Criterion criterion : criteria) {
				getCriterionValidator().validate(criterion, errors);
			}
		}

		if (!errors.hasErrors()) {
			getResultCountValidator().validate(object, errors);
		}
	}

}
