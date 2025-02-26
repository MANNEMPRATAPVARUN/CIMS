package ca.cihi.cims.web.controller.search.modelvalue;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Test cases for {@link ModelValuesProviderFactory}
 * 
 * @author rshnaper
 * 
 */
public class ModelValuesProviderFactoryTest {

	private ModelValuesProviderFactoryImpl modelValueFactory;

	@Mock
	private ApplicationContext context;

	@Mock
	private ChangeRequestModelValuesProvider crModelValueProvider;

	@Mock
	private DefaultModelValuesProvider defaultModelValueProvider;

	@Mock
	private TabularSimpleModelValuesProvider tabularSimpleModelValueProvider;

	@Mock
	private ReferenceValuesModelValuesProvider referenceValueModelValueProvider;

	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);

		Mockito.when(context.getBean(ChangeRequestModelValuesProvider.class)).thenReturn(crModelValueProvider);
		Mockito.when(context.getBean(TabularSimpleModelValuesProvider.class)).thenReturn(
				tabularSimpleModelValueProvider);
		Mockito.when(context.getBean(ReferenceValuesModelValuesProvider.class)).thenReturn(
				referenceValueModelValueProvider);
		Mockito.when(context.getBean("defaultModelValueProvider", DefaultModelValuesProvider.class)).thenReturn(
				defaultModelValueProvider);

		modelValueFactory = new ModelValuesProviderFactoryImpl();
		modelValueFactory.setApplicationContext(context);
	}

	@Test
	public void testCCIReferenceValuesComparativeValueProvider() {
		ModelValuesProvider provider = modelValueFactory
				.getModelValuesProviderFor(SearchTypes.CCIReferenceValuesComparative);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(ReferenceValuesModelValuesProvider.class));
	}

	@Test
	public void testCCITabularComparativeValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.CCITabularComparative);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(DefaultModelValuesProvider.class));
	}

	@Test
	public void testCCITabularSimpleValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.CCITabularSimple);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(TabularSimpleModelValuesProvider.class));
	}

	@Test
	public void testChangeRequestCCITabularValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.ChangeRequestCCITabular);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(ChangeRequestModelValuesProvider.class));
	}

	@Test
	public void testChangeRequestICDTabularValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.ChangeRequestICDTabular);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(ChangeRequestModelValuesProvider.class));
	}

	@Test
	public void testChangeRequestIndexValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.ChangeRequestIndex);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(ChangeRequestModelValuesProvider.class));
	}

	@Test
	public void testChangeRequestPropertiesValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.ChangeRequestProperties);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(ChangeRequestModelValuesProvider.class));
	}

	@Test
	public void testICDTabularComparativeValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.ICDTabularComparative);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(DefaultModelValuesProvider.class));
	}

	@Test
	public void testICDTabularSimpleValueProvider() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(SearchTypes.ICDTabularSimple);
		MatcherAssert.assertThat(provider, Matchers.instanceOf(TabularSimpleModelValuesProvider.class));
	}

	@Test
	public void testNullType() {
		ModelValuesProvider provider = modelValueFactory.getModelValuesProviderFor(null);
		MatcherAssert.assertThat(provider, Matchers.nullValue());
	}
}
