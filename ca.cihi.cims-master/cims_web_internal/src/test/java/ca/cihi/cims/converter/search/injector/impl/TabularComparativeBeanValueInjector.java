package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.TabularComparativeBean;
import ca.cihi.cims.web.bean.search.TabularComparativeBean.ComparativeType;

/**
 * Implementation of {@link BeanValueInjector} for {@link TabularComparativeBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class TabularComparativeBeanValueInjector<T extends TabularComparativeBean> extends BaseBeanValueInjector<T> {

	@Override
	public void inject(T bean) {
		super.inject(bean);

		bean.setComparativeType(ComparativeType.NewCode);
		bean.setContextId(ValueGenerator.generateValue(Long.class));
		bean.setPriorContextId(ValueGenerator.generateValue(Long.class));
	}
}
