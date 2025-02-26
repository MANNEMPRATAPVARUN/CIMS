package ca.cihi.cims.web.controller.search.modelvalue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.service.LookupService;

/**
 * Coverage tests for {@link TabularSimpleModelValuesProvider}
 * 
 * @author rshnaper
 * 
 */
public class TabularSimpleModelValuesProviderTest {
	private static final String YEAR_2015 = "2015";

	@Mock
	private TabularSimpleModelValuesProvider provider;

	@Mock
	private ContextProvider contextProvider;

	@Mock
	private LookupService lookupService;

	private Model model;

	@Mock
	private Search search;

	@Mock
	private SearchType searchType;

	@Mock
	private ContextIdentifier contextIdentifier;

	@Mock
	private ContextAccess contextAccess;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		Mockito.doCallRealMethod().when(provider).populate((Model) Mockito.any(), (Search) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setContextProvider((ContextProvider) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setLookupService((LookupService) Mockito.any());
		Mockito.doCallRealMethod().when(provider).getContextIdentifiers(Mockito.anyString(), Mockito.anyBoolean());
		Mockito.doCallRealMethod().when(provider).getCurrentOpenContextIdentifier(Mockito.anyString());
		Mockito.doCallRealMethod().when(provider).sort(Mockito.anyCollection(), (Comparator) Mockito.any());

		Mockito.when(search.getType()).thenReturn(searchType);

		Mockito.when(contextIdentifier.getVersionCode()).thenReturn(YEAR_2015);
		Mockito.when(search.getType().getName()).thenReturn(Mockito.anyString());

		provider.setContextProvider(contextProvider);
		provider.setLookupService(lookupService);

		Mockito.when(contextProvider.findContext((ContextIdentifier) Mockito.any())).thenReturn(contextAccess);

		model = new ExtendedModelMap();
	}

	@Test
	public void testDaggerAsterisk() {
		Mockito.when(search.getType().getName()).thenReturn(SearchTypes.ICDTabularSimple.getTypeName());

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(TabularSimpleModelValuesProvider.CONCEPT_STATUSES));
		MatcherAssert
				.assertThat(model.asMap(), Matchers.hasKey(TabularSimpleModelValuesProvider.DAGGER_ASTERISK_TYPES));
	}

	@Test
	public void testInvasivenessLevelEmpty() {
		Mockito.when(search.getType().getName()).thenReturn(SearchTypes.CCITabularSimple.getTypeName());
		Mockito.when(contextAccess.findAll(CciInvasivenessLevel.class)).thenReturn(Mockito.mock(Iterator.class));

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(TabularSimpleModelValuesProvider.CONCEPT_STATUSES));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(TabularSimpleModelValuesProvider.INVASIVENESS_LEVELS));
	}

	@Ignore
	public void testInvasivenessLevelNonEmpty() {
		Mockito.when(search.getType().getName()).thenReturn(SearchTypes.CCITabularSimple.getTypeName());

		CciInvasivenessLevel invasivenessLevel = Mockito.mock(CciInvasivenessLevel.class);
		Mockito.when(invasivenessLevel.getCode()).thenReturn(Mockito.anyString());

		Mockito.when(contextAccess.findAll(CciInvasivenessLevel.class)).thenReturn(
				Arrays.asList(invasivenessLevel).iterator());

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(TabularSimpleModelValuesProvider.CONCEPT_STATUSES));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(TabularSimpleModelValuesProvider.INVASIVENESS_LEVELS));
	}
}
