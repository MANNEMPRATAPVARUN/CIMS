package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.ICDTabularComparativeBean;

/**
 * Implementation of {@link BeanValueInjector} for {@link ICDTabularComparativeBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class ICDTabularComparativeBeanValueInjector extends
		TabularComparativeBeanValueInjector<ICDTabularComparativeBean> {

	private static final String CATEGORY_CODE_TO = "Z99";
	private static final String CATEGORY_CODE_FROM = "A00";

	@Override
	public void inject(ICDTabularComparativeBean bean) {
		super.inject(bean);

		bean.setHierarchyLevel(HierarchyLevel.Category);
		bean.setCodeFrom(CATEGORY_CODE_FROM);
		bean.setCodeTo(CATEGORY_CODE_TO);
	}

}
