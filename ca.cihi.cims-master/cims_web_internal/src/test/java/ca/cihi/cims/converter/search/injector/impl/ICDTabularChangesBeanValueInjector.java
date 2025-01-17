package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.ICDTabularChangesBean;
import ca.cihi.cims.web.bean.search.TabularChangesBean.HierarchyType;

/**
 * Implementation of {@link BeanValueInjector} for {@link ICDTabularChangesBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class ICDTabularChangesBeanValueInjector extends TabularChangesBeanValueInjector<ICDTabularChangesBean> {
	private static final String CATEGORY_CODE_TO = "Z99";
	private static final String CATEGORY_CODE_FROM = "A00";

	@Override
	public void inject(ICDTabularChangesBean bean) {
		super.inject(bean);

		bean.setCodeFrom(CATEGORY_CODE_FROM);
		bean.setCodeTo(CATEGORY_CODE_TO);
		bean.setLevel(HierarchyType.Category);
	}
}
