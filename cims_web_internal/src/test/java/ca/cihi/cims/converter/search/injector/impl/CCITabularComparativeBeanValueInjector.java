package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.CCITabularComparativeBean;
import ca.cihi.cims.web.bean.search.HierarchyLevel;

/**
 * Implementation of {@link BeanValueInjector} for {@link CCITabularComparativeBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class CCITabularComparativeBeanValueInjector extends
		TabularComparativeBeanValueInjector<CCITabularComparativeBean> {

	@Override
	public void inject(CCITabularComparativeBean bean) {
		super.inject(bean);

		bean.setHierarchyLevel(HierarchyLevel.Block);
		bean.setSectionCode("01");
	}

}
