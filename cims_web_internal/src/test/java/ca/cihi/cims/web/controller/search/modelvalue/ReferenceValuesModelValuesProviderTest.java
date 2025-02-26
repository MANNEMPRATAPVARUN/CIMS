package ca.cihi.cims.web.controller.search.modelvalue;

import java.util.Arrays;
import java.util.Collections;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.LookupService;

/**
 * Coverage tests for {@link ReferenceValuesModelValuesProvider}
 * 
 * @author rshnaper
 * 
 */
public class ReferenceValuesModelValuesProviderTest {

	private ReferenceValuesModelValuesProvider provider;

	@Mock
	private ContextProvider contextProvider;

	@Mock
	private LookupService lookupService;

	@Mock
	private ContextAccess contextAccess;

	private Model model;

	@Mock
	private Search search;

	@Mock
	private CciAttributeType attributeType;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		provider = new ReferenceValuesModelValuesProvider();
		provider.setContextProvider(contextProvider);
		provider.setLookupService(lookupService);

		model = new ExtendedModelMap();

		Mockito.when(contextProvider.findContext((ContextIdentifier) Mockito.any())).thenReturn(contextAccess);
		Mockito.when(contextAccess.findAll(CciAttributeType.class)).thenReturn(
				Collections.<CciAttributeType> emptyList().iterator());
	}

	@Test
	public void testEmpty() {
		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(ReferenceValuesModelValuesProvider.ATTRIBUTE_TYPES));
	}

	@Test
	public void testNonEmpty() {
		Mockito.when(contextAccess.findAll(CciAttributeType.class)).thenReturn(
				Arrays.<CciAttributeType> asList(attributeType).iterator());

		provider.populate(model, search);
		MatcherAssert.assertThat(model.asMap().keySet(), Matchers.hasSize(Matchers.greaterThan(0)));
		MatcherAssert.assertThat(model.asMap(), Matchers.hasKey(ReferenceValuesModelValuesProvider.ATTRIBUTE_TYPES));
	}
}
