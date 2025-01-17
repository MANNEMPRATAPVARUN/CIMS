package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Implementation of {@link SearchCriteriaBeanFactory}
 * 
 * @author rshnaper
 * 
 */
public class SearchCriteriaBeanFactoryImpl implements SearchCriteriaBeanFactory {

	@SuppressWarnings("unchecked")
	@Override
	public <T extends SearchCriteriaBean> T createBean(SearchTypes type) {
		T bean = null;
		switch (type) {
		case ChangeRequestProperties:
			bean = (T) new ChangeRequestPropetiesBean();
			break;
		case ChangeRequestCCITabular:
			bean = (T) new CCITabularChangesBean();
			break;
		case ChangeRequestICDTabular:
			bean = (T) new ICDTabularChangesBean();
			break;
		case ChangeRequestIndex:
			bean = (T) new IndexChangesBean();
			break;
		case ICDTabularComparative:
			bean = (T) new ICDTabularComparativeBean();
			break;
		case CCITabularComparative:
			bean = (T) new CCITabularComparativeBean();
			break;
		case ICDTabularSimple:
			bean = (T) new ICDTabularSimpleBean();
			break;
		case CCITabularSimple:
			bean = (T) new CCITabularSimpleBean();
			break;
		case CCIReferenceValuesComparative:
			bean = (T) new CCIReferenceValueComparativeBean();
			break;

		}
		return bean;
	}

	@Override
	public Class<? extends SearchCriteriaBean> getBeanClass(SearchTypes type) {
		Class<? extends SearchCriteriaBean> beanClass = null;
		switch (type) {
		case ChangeRequestProperties:
			beanClass = ChangeRequestPropetiesBean.class;
			break;
		case ChangeRequestCCITabular:
			beanClass = CCITabularChangesBean.class;
			break;
		case ChangeRequestICDTabular:
			beanClass = ICDTabularChangesBean.class;
			break;
		case ChangeRequestIndex:
			beanClass = IndexChangesBean.class;
			break;
		case ICDTabularComparative:
			beanClass = ICDTabularComparativeBean.class;
			break;
		case CCITabularComparative:
			beanClass = CCITabularComparativeBean.class;
			break;
		case ICDTabularSimple:
			beanClass = ICDTabularSimpleBean.class;
			break;
		case CCITabularSimple:
			beanClass = CCITabularSimpleBean.class;
			break;
		case CCIReferenceValuesComparative:
			beanClass = CCIReferenceValueComparativeBean.class;
			break;
		}
		return beanClass;
	}

}
