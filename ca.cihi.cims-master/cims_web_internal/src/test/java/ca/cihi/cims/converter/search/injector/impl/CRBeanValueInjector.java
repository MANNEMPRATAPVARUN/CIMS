package ca.cihi.cims.converter.search.injector.impl;

import java.util.Arrays;
import java.util.Date;

import ca.cihi.cims.Language;
import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchDateTypes;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchTextTypes;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchUserTypes;

/**
 * Implementation of {@link BeanValueInjector} for {@link ChangeRequestPropetiesBean} type
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
class CRBeanValueInjector<T extends ChangeRequestPropetiesBean> extends BaseBeanValueInjector<T> {

	@Override
	public void inject(T bean) {
		super.inject(bean);
		bean.setChangeNatureId(ValueGenerator.generateValue(Long.class));
		bean.setChangeTypeId(ValueGenerator.generateValue(Long.class));
		bean.setContextIds(Arrays.asList(ValueGenerator.generateValue(Long.class),
				ValueGenerator.generateValue(Long.class)));
		bean.setDateFrom(ValueGenerator.generateValue(Date.class));
		bean.setEvolutionRequired(ValueGenerator.generateValue(Boolean.class));
		bean.setIndexRequired(ValueGenerator.generateValue(Boolean.class));
		bean.setLanguage(Language.ENGLISH.getCode());
		bean.setPatternChange(ValueGenerator.generateValue(Boolean.class));
		bean.setPatternTopic(ValueGenerator.generateValue(String.class));
		bean.setRequestCategory(ValueGenerator.generateValue(String.class));
		bean.setRequestorId(ValueGenerator.generateValue(Long.class));
		bean.setSearchDateType(SearchDateTypes.Created);
		bean.setSearchText(ValueGenerator.generateValue(String.class));
		bean.setSearchTextType(SearchTextTypes.RequestName);
		bean.setStatusIds(Arrays.asList(ValueGenerator.generateValue(Long.class),
				ValueGenerator.generateValue(Long.class)));
		bean.setSearchUserId(ChangeRequestPropetiesBean.USER_ID_PREFIX_USER + ValueGenerator.generateValue(Long.class));
		bean.setSearchUserType(SearchUserTypes.Owner);
	}

}