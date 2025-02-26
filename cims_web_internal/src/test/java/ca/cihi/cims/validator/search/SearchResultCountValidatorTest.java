package ca.cihi.cims.validator.search;

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

import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.search.QueryBuilder;
import ca.cihi.cims.service.search.QueryBuilderFactory;

/**
 * Test coverage for {@link SearchResultCountValidator}
 * 
 * @author rshnaper
 * 
 */
public class SearchResultCountValidatorTest {
	@Mock
	private Search search;

	@Mock
	private SearchResultCountValidator validator;

	@Mock
	private QueryBuilderFactory queryBuilderFactory;

	@Mock
	private QueryBuilder queryBuilder;

	@Mock
	private SearchType searchType;

	private Errors errors;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		errors = new MapBindingResult(new HashMap(), "map");

		Mockito.doCallRealMethod().when(validator).supports(Search.class);
		Mockito.doCallRealMethod().when(validator).validate(Mockito.any(), (Errors) Mockito.any());
		Mockito.when(validator.getQueryBuilderFactory()).thenReturn(queryBuilderFactory);

		Mockito.when(queryBuilderFactory.getInstanceFor(Mockito.any(SearchTypes.class))).thenReturn(queryBuilder);

		Mockito.when(search.getType()).thenReturn(searchType);
		Mockito.when(searchType.getName()).thenReturn(SearchTypes.ChangeRequestProperties.getTypeName());
	}

	@Test
	public void testMaxCountValidation() {
		Mockito.when(queryBuilder.getCount(search)).thenReturn(
				Long.valueOf(SearchResultCountValidator.MAX_SEARCH_RESULT_COUNT));

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testNonZeroCountValidation() {
		Mockito.when(queryBuilder.getCount(search)).thenReturn(10L);

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testNullQueryBuilder() {
		Mockito.when(queryBuilderFactory.getInstanceFor(Mockito.any(SearchTypes.class))).thenReturn(null);

		Mockito.when(queryBuilder.getCount(search)).thenReturn(0L);

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}

	@Test
	public void testOverMaxCountValidation() {
		Mockito.when(queryBuilder.getCount(search)).thenReturn(
				Long.valueOf(SearchResultCountValidator.MAX_SEARCH_RESULT_COUNT + 1));

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(Matchers.not(0))));
	}

	@Test
	public void testQueryBuilderFactory() {
		Mockito.when(validator.getQueryBuilderFactory()).thenCallRealMethod();
		MatcherAssert.assertThat(validator.getQueryBuilderFactory(), Matchers.nullValue());
	}

	@Test
	public void testSearchResultCountValidatorSupport() {
		MatcherAssert.assertThat(validator.supports(Search.class), Matchers.is(true));
	}

	@Test
	public void testZeroCountValidation() {
		Mockito.when(queryBuilder.getCount(search)).thenReturn(0L);

		validator.validate(search, errors);

		MatcherAssert.assertThat(errors, Matchers.hasProperty("errorCount", Matchers.is(0)));
	}
}
