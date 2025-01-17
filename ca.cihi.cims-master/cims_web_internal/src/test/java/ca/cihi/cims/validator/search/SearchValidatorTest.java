package ca.cihi.cims.validator.search;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;

import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;

/**
 * Test coverage for {@link SearchValidator}
 * 
 * @author rshnaper
 * 
 */
public class SearchValidatorTest {
	@Mock
	private Search search;

	@Mock
	private SearchValidator validator;

	@Mock
	private CriterionValidator criterionValidator;

	@Mock
	private SearchResultCountValidator searchResultValidator;

	@Mock
	private SearchType searchType;

	private Errors errors;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		errors = new MapBindingResult(new HashMap(), "map");

		Mockito.doCallRealMethod().when(validator).supports(Search.class);
		Mockito.doCallRealMethod().when(validator).validate(Mockito.any(), (Errors) Mockito.any());
		Mockito.when(validator.getCriterionValidator()).thenReturn(criterionValidator);
		Mockito.when(validator.getResultCountValidator()).thenReturn(searchResultValidator);

		Mockito.when(search.getType()).thenReturn(searchType);
		Mockito.when(searchType.getName()).thenReturn(SearchTypes.ChangeRequestProperties.getTypeName());
	}

	@Test
	public void testEmptyCriteria() {
		Mockito.when(search.getCriteria()).thenReturn(Collections.<Criterion> emptyList());

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testNonEmptyCriteria() {
		Mockito.when(search.getCriteria()).thenReturn(Arrays.asList(Mockito.mock(Criterion.class)));

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testNullCriteria() {
		Mockito.when(search.getCriteria()).thenReturn(null);

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testSearchValidatorSupport() {
		MatcherAssert.assertThat(validator.supports(Search.class), Matchers.is(true));
	}

}
