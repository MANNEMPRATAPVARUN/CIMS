package ca.cihi.cims.converter.search.injector.impl;

import java.util.Arrays;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.TabularSimpleBean;

/**
 * Implementation of {@link BeanValueInjector} for {@link TabularSimpleBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class TabularSimpleBeanValueInjector<T extends TabularSimpleBean> extends BaseBeanValueInjector<T> {

	@Override
	public void inject(T bean) {
		super.inject(bean);

		bean.setContextIds(Arrays.asList(ValueGenerator.generateValue(Long.class)));
		bean.setIsEnglishLong(ValueGenerator.generateValue(Boolean.class));
		bean.setIsEnglishShort(ValueGenerator.generateValue(Boolean.class));
		bean.setIsEnglishUser(ValueGenerator.generateValue(Boolean.class));
		bean.setIsEnglishViewerContent(ValueGenerator.generateValue(Boolean.class));
		bean.setIsFrenchLong(ValueGenerator.generateValue(Boolean.class));
		bean.setIsFrenchShort(ValueGenerator.generateValue(Boolean.class));
		bean.setIsFrenchUser(ValueGenerator.generateValue(Boolean.class));
		bean.setIsFrenchViewerContent(ValueGenerator.generateValue(Boolean.class));
		bean.setSearchText(ValueGenerator.generateValue(String.class));
	}
}
