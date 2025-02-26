package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.ICDTabularSimpleBean;

/**
 * Implementation of {@link BeanValueInjector} for {@link ICDTabularSimpleBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class ICDTabularSimpleBeanValueInjector extends TabularSimpleBeanValueInjector<ICDTabularSimpleBean> {
	private static final String CATEGORY_CODE_TO = "Z99";
	private static final String CATEGORY_CODE_FROM = "A00";

	@Override
	public void inject(ICDTabularSimpleBean bean) {
		super.inject(bean);

		bean.setCodeFrom(CATEGORY_CODE_FROM);
		bean.setCodeTo(CATEGORY_CODE_TO);
		bean.setHierarchyLevel(HierarchyLevel.Category);
		bean.setCanEnhancementFlag(ValueGenerator.generateValue(Boolean.class));
	}
}
