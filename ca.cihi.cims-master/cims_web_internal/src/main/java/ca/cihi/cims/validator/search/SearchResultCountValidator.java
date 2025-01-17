package ca.cihi.cims.validator.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.search.QueryBuilder;
import ca.cihi.cims.service.search.QueryBuilderFactory;

/**
 * Search result count validator that reports an error if the search result count exceeded a predefined maximum
 * 
 * @author rshnaper
 * 
 */
public class SearchResultCountValidator implements Validator {
	public final static int MAX_SEARCH_RESULT_COUNT = 1000;

	private @Autowired
	QueryBuilderFactory queryBuilderFactory;

	protected QueryBuilderFactory getQueryBuilderFactory() {
		return queryBuilderFactory;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return Search.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object object, Errors errors) {
		Search search = (Search) object;
		QueryBuilder queryBuilder = getQueryBuilderFactory().getInstanceFor(
				SearchTypes.forName(search.getType().getName()));
		if (queryBuilder != null) {
			long resultsCount = queryBuilder.getCount(search);
			if (resultsCount > MAX_SEARCH_RESULT_COUNT) {
				errors.reject("search.results.count.max", new Object[] { resultsCount, MAX_SEARCH_RESULT_COUNT },
						"Search result count exceeded maximum");
			}
		}
	}

}
