package ca.cihi.cims.converter.search.injector.impl;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean.ComparativeType;

/**
 * Implementation of {@link BeanValueInjector} for {@link CCIReferenceValueComparativeBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class CCIReferenceValueComparativeBeanValueInjector extends
		BaseBeanValueInjector<CCIReferenceValueComparativeBean> {

	@Override
	public void inject(CCIReferenceValueComparativeBean bean) {
		super.inject(bean);
		bean.setAttributeTypeId(ValueGenerator.generateValue(Long.class));
		bean.setComparativeType(ComparativeType.NewRefValue);
		bean.setContextId(ValueGenerator.generateValue(Long.class));
		bean.setPriorContextId(ValueGenerator.generateValue(Long.class));
	}
}
