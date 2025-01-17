package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.CCITabularChangesBean;
import ca.cihi.cims.web.bean.search.TabularChangesBean.HierarchyType;

/**
 * Implementation of {@link BeanValueInjector} for {@link CCITabularChangesBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class CCITabularChangesBeanValueInjector extends TabularChangesBeanValueInjector<CCITabularChangesBean> {
	private static final String RUBRIC_CODE_TO = "9.ZZ.99";
	private static final String RUBRIC_CODE_FROM = "1.AA.00";

	@Override
	public void inject(CCITabularChangesBean bean) {
		super.inject(bean);

		bean.setCodeFrom(RUBRIC_CODE_FROM);
		bean.setCodeTo(RUBRIC_CODE_TO);
		bean.setLevel(HierarchyType.Rubric);
	}
}
