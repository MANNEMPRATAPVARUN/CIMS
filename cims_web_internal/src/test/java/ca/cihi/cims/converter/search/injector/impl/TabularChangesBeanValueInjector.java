package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.TabularChangesBean;

/**
 * Implementation of {@link BeanValueInjector} for {@link TabularChangesBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
class TabularChangesBeanValueInjector<T extends TabularChangesBean> extends CRBeanValueInjector<T> {

	@Override
	public void inject(T bean) {
		super.inject(bean);

		bean.setDisabledCodeValues(ValueGenerator.generateValue(Boolean.class));
		bean.setModifiedProperties(ValueGenerator.generateValue(Boolean.class));
		bean.setNewCodeValues(ValueGenerator.generateValue(Boolean.class));
		bean.setValidations(ValueGenerator.generateValue(Boolean.class));
		bean.setConceptMovement(ValueGenerator.generateValue(Boolean.class));
	}
}
