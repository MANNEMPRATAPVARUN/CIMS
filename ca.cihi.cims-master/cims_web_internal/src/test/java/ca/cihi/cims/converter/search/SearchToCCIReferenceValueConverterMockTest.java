package ca.cihi.cims.converter.search;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.converter.search.bean.CCIReferenceValueComparativeBeanToSearchConverter;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean;

/**
 * Additional coverage tests for {@link CCIReferenceValueComparativeBeanToSearchConverter}
 * 
 * @author rshnaper
 * 
 */
public class SearchToCCIReferenceValueConverterMockTest {
	@Mock
	private LookupService lookupService;

	@Mock
	private ContextProvider contextProvider;

	@Mock
	private SearchService searchService;

	@Spy
	private SearchToCCIReferenceValueComparativeBeanConverter converter;

	@Spy
	private Search search;

	@Spy
	private SearchType searchType;

	@Mock
	private CriterionType criterionType;

	@Mock
	private Criterion criterion;

	@Mock
	private ContextAccess contextAccess;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(converter.getLookupService()).thenReturn(lookupService);
		Mockito.when(converter.getContextProvider()).thenReturn(contextProvider);
		Mockito.when(converter.getSearchService()).thenReturn(searchService);

		Mockito.doReturn(new CCIReferenceValueComparativeBean()).when(converter).createBean();

		Mockito.when(lookupService.findCurrentOpenContextByClassification(Mockito.anyString())).thenReturn(1L);
		Mockito.when(lookupService.findContextIdentificationById(Mockito.anyLong())).thenReturn(
				Mockito.mock(ContextIdentifier.class));
		Mockito.when(
				lookupService.findPriorBaseContextIdentifiersByClassificationAndContext(Mockito.anyString(),
						Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(
				Arrays.asList(Mockito.mock(ContextIdentifier.class)));

		Mockito.when(contextProvider.findContext((ContextIdentifier) Mockito.any())).thenReturn(contextAccess);
		Mockito.when(contextAccess.findAll(CciAttributeType.class)).thenReturn(
				Collections.<CciAttributeType> emptyList().iterator());

		Mockito.when(search.getType()).thenReturn(searchType);
		Mockito.when(search.getCriteria()).thenReturn(Arrays.asList(criterion));
		Mockito.when(criterion.getType()).thenReturn(criterionType);

		Mockito.when(searchType.getName()).thenReturn(SearchTypes.CCIReferenceValuesComparative.getTypeName());
	}

	@Test
	public void testEmptyContextIdentifiers() {
		Mockito.when(search.getId()).thenReturn(0L);
		Mockito.when(
				lookupService.findPriorBaseContextIdentifiersByClassificationAndContext(Mockito.anyString(),
						Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(
				Collections.<ContextIdentifier> emptyList());

		CCIReferenceValueComparativeBean bean = converter.convert(search);
		MatcherAssert.assertThat(bean, Matchers.notNullValue());
	}

	@Test
	public void testNullContextIdentifiers() {
		Mockito.when(search.getId()).thenReturn(0L);
		Mockito.when(
				lookupService.findPriorBaseContextIdentifiersByClassificationAndContext(Mockito.anyString(),
						Mockito.anyLong(), Mockito.anyBoolean())).thenReturn(null);

		CCIReferenceValueComparativeBean bean = converter.convert(search);
		MatcherAssert.assertThat(bean, Matchers.notNullValue());
	}

	@Test
	public void testSavedSearchConversion() {
		Mockito.when(search.getId()).thenReturn(1L);
		CCIReferenceValueComparativeBean bean = converter.convert(search);
		MatcherAssert.assertThat(bean, Matchers.notNullValue());
	}

	@Test
	public void testUnsavedSearchConversion() {
		Mockito.when(search.getId()).thenReturn(0L);
		CCIReferenceValueComparativeBean bean = converter.convert(search);
		MatcherAssert.assertThat(bean, Matchers.notNullValue());
	}
}
