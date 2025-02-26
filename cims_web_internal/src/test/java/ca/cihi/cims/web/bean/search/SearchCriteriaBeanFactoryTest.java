package ca.cihi.cims.web.bean.search;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Test cases for {@link SearchCriteriaBeanFactory}
 * 
 * @author rshnaper
 * 
 */
public class SearchCriteriaBeanFactoryTest {
	private SearchCriteriaBeanFactory beanFactory;

	@Before
	public void init() {
		beanFactory = new SearchCriteriaBeanFactoryImpl();
	}

	@Test
	public void testCreateCCIReferenceValuesComparative() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.CCIReferenceValuesComparative);
		assertThat(bean, instanceOf(CCIReferenceValueComparativeBean.class));
	}

	@Test
	public void testCreateCCITabularComparative() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.CCITabularComparative);
		assertThat(bean, instanceOf(CCITabularComparativeBean.class));
	}

	@Test
	public void testCreateCCITabularSimple() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.CCITabularSimple);
		assertThat(bean, instanceOf(CCITabularSimpleBean.class));
	}

	@Test
	public void testCreateChangeRequestCCITabular() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.ChangeRequestCCITabular);
		assertThat(bean, instanceOf(CCITabularChangesBean.class));
	}

	@Test
	public void testCreateChangeRequestICDTabular() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.ChangeRequestICDTabular);
		assertThat(bean, instanceOf(ICDTabularChangesBean.class));
	}

	@Test
	public void testCreateChangeRequestIndex() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.ChangeRequestIndex);
		assertThat(bean, instanceOf(IndexChangesBean.class));
	}

	@Test
	public void testCreateChangeRequestProperties() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.ChangeRequestProperties);
		assertThat(bean, instanceOf(ChangeRequestPropetiesBean.class));
	}

	@Test
	public void testCreateICDTabularComparative() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.ICDTabularComparative);
		assertThat(bean, instanceOf(ICDTabularComparativeBean.class));
	}

	@Test
	public void testCreateICDTabularSimple() {
		SearchCriteriaBean bean = beanFactory.createBean(SearchTypes.ICDTabularSimple);
		assertThat(bean, instanceOf(ICDTabularSimpleBean.class));
	}

	@Test
	public void testGetBeanClassCCIReferenceValuesComparative() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory
				.getBeanClass(SearchTypes.CCIReferenceValuesComparative);
		assertEquals(beanClass, CCIReferenceValueComparativeBean.class);
	}

	@Test
	public void testGetBeanClassCCITabularComparative() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.CCITabularComparative);
		assertEquals(beanClass, CCITabularComparativeBean.class);
	}

	@Test
	public void testGetBeanClassCCITabularSimple() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.CCITabularSimple);
		assertEquals(beanClass, CCITabularSimpleBean.class);
	}

	@Test
	public void testGetBeanClassChangeRequestCCITabular() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.ChangeRequestCCITabular);
		assertEquals(beanClass, CCITabularChangesBean.class);
	}

	@Test
	public void testGetBeanClassChangeRequestICDTabular() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.ChangeRequestICDTabular);
		assertEquals(beanClass, ICDTabularChangesBean.class);
	}

	@Test
	public void testGetBeanClassChangeRequestIndex() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.ChangeRequestIndex);
		assertEquals(beanClass, IndexChangesBean.class);
	}

	@Test
	public void testGetBeanClassChangeRequestProperties() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.ChangeRequestProperties);
		assertEquals(beanClass, ChangeRequestPropetiesBean.class);
	}

	@Test
	public void testGetBeanClassICDTabularComparative() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.ICDTabularComparative);
		assertEquals(beanClass, ICDTabularComparativeBean.class);
	}

	@Test
	public void testGetBeanClassICDTabularSimple() {
		Class<? extends SearchCriteriaBean> beanClass = beanFactory.getBeanClass(SearchTypes.ICDTabularSimple);
		assertEquals(beanClass, ICDTabularSimpleBean.class);
	}
}
