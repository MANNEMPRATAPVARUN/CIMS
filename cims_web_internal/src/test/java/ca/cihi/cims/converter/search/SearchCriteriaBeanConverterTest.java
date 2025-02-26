package ca.cihi.cims.converter.search;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.cihi.cims.converter.search.injector.SearchCriteriaBeanValueInjector;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchTypes;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;
import ca.cihi.cims.web.bean.search.SearchCriteriaBeanFactory;

/**
 * Tests for search bean converters. Each test performs assertions on valid conversions of specific
 * {@link SearchCriteriaBean} to and from {@link Search} model object
 * 
 * @author rshnaper
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class SearchCriteriaBeanConverterTest {

	private final Logger logger = LogManager.getLogger(SearchCriteriaBeanConverterTest.class);

	@Autowired
	private SearchCriteriaBeanFactory searchCriteriaBeanFactory;

	@Autowired
	private ConversionService conversionService;

	@Autowired
	private SearchCriteriaBeanValueInjector injector;

	private <T extends SearchCriteriaBean> T convert(Search search, Class<T> clazz) {
		return conversionService.convert(search, clazz);
	}

	private <T extends SearchCriteriaBean> Search convert(T bean) {
		return conversionService.convert(bean, Search.class);
	}

	private <T extends SearchCriteriaBean> T create(SearchTypes type) {
		T bean = searchCriteriaBeanFactory.createBean(type);
		if (bean != null) {
			bean.setSearchId(-1);
			bean.setSearchTypeName(type.getTypeName());
		}
		return bean;
	}

	private <T extends SearchCriteriaBean> void populate(T bean) {
		injector.injectValues(bean);
	}

	@Test
	public void testCCIReferenceValueComparativeConversion() throws Exception {
		testConversion(SearchTypes.CCIReferenceValuesComparative);
	}

	/*@Test
	public void testCCITabularChangesConversion() throws Exception {
		testConversion(SearchTypes.ChangeRequestCCITabular);
	}*/

	@Test
	public void testCCITabularComparativeConversion() throws Exception {
		testConversion(SearchTypes.CCITabularComparative);
	}

	@Test
	public void testCCITabularSimpleConversion() throws Exception {
		testConversion(SearchTypes.CCITabularSimple);
	}

	@Test
	public void testChangeRequestConversion() throws Exception {
		testConversion(SearchTypes.ChangeRequestProperties);
	}

	@SuppressWarnings("unchecked")
	private <T extends SearchCriteriaBean> void testConversion(SearchTypes type) throws Exception {
		T originalBean = create(type);
		populate(originalBean);

		// convert the SearchCriteriaBean to Search object
		Search search = convert(originalBean);

		// convert the Search object back to SearchCriteriaBean
		T convertedBean = (T) convert(search, originalBean.getClass());

		/**
		 * For debugging purposes un-comment the below code to see why the objects aren't equal
		 */
		 ObjectMapper mapper = new ObjectMapper();
		 String beanJson = mapper.writeValueAsString(originalBean);
		 String bean2Json = mapper.writeValueAsString(convertedBean);
		 System.out.println(beanJson);
		 System.out.println(bean2Json);
		 //Assert.assertEquals(beanJson, bean2Json);

		// compare the two beans
		Assert.assertTrue(EqualsBuilder.reflectionEquals(originalBean, convertedBean));
	}

	/*@Test
	public void testICDTabularChangesConversion() throws Exception {
		testConversion(SearchTypes.ChangeRequestICDTabular);
	}*/

	@Test
	public void testICDTabularComparativeConversion() throws Exception {
		testConversion(SearchTypes.ICDTabularComparative);
	}

	@Test
	public void testICDTabularSimpleConversion() throws Exception {
		testConversion(SearchTypes.ICDTabularSimple);
	}

	@Test
	public void testIndexChangesConversion() throws Exception {
		testConversion(SearchTypes.ChangeRequestIndex);
	}
}
