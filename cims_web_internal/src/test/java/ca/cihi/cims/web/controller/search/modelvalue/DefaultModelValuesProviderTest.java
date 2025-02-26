package ca.cihi.cims.web.controller.search.modelvalue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.LookupService;

/**
 * Coverage tests for {@link DefaultModelValuesProvider}
 * 
 * @author rshnaper
 * 
 */
public class DefaultModelValuesProviderTest {
	@Mock
	private DefaultModelValuesProvider provider;

	@Mock
	private ContextProvider contextProvider;

	@Mock
	private LookupService lookupService;

	private Model model;

	@Mock
	private Search search;

	@Mock
	private ContextIdentifier contextIdentifier;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		Mockito.doCallRealMethod().when(provider).populate((Model) Mockito.any(), (Search) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setContextProvider((ContextProvider) Mockito.any());
		Mockito.doCallRealMethod().when(provider).setLookupService((LookupService) Mockito.any());
		Mockito.doCallRealMethod().when(provider).getContextIdentifiers(Mockito.anyString(), Mockito.anyBoolean());
		Mockito.doCallRealMethod().when(provider).getCurrentOpenContextIdentifier(Mockito.anyString());
		Mockito.doCallRealMethod().when(provider).sort(Mockito.anyCollection(), (Comparator) Mockito.any());

		Mockito.when(contextIdentifier.getVersionCode()).thenReturn(Mockito.anyString());

		provider.setContextProvider(contextProvider);
		provider.setLookupService(lookupService);

		model = new ExtendedModelMap();
	}

	@Test
	public void testEmpty() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Collections.<ContextIdentifier> emptyList());
		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS));
	}

	@Test
	public void testNonEmpty() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS));
	}

	@Test
	public void testNonEmptyVesrionYear() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS));
	}

	@Test
	public void testNonEmptyVesrionYearContextYear() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(
				Arrays.asList(contextIdentifier, contextIdentifier));
		Mockito.when(provider.isVersionYearOnly()).thenReturn(true);
		Mockito.when(contextIdentifier.isVersionYear()).thenReturn(true);

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS));
	}

	@Test
	public void testNull() {
		Mockito.when(contextProvider.findBaseContextIdentifiers(Mockito.anyString())).thenReturn(null);
		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(DefaultModelValuesProvider.CONTEXT_IDS));
	}
}
