package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.CCITabularSimpleBean;
import ca.cihi.cims.web.bean.search.HierarchyLevel;

/**
 * Implementation of {@link BeanValueInjector} for {@link CCITabularSimpleBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class CCITabularSimpleBeanValueInjector extends TabularSimpleBeanValueInjector<CCITabularSimpleBean> {
	private static final String RUBRIC_CODE_TO = "9.ZZ.99";
	private static final String RUBRIC_CODE_FROM = "1.AA.00";

	@Override
	public void inject(CCITabularSimpleBean bean) {
		super.inject(bean);

		bean.setCodeFrom(RUBRIC_CODE_FROM);
		bean.setCodeTo(RUBRIC_CODE_TO);
		bean.setHierarchyLevel(HierarchyLevel.Rubric);
		bean.setInvasivenessLevel(ValueGenerator.generateValue(Long.class));
	}
}
