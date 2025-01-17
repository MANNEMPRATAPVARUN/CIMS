package ca.cihi.cims.converter.search.injector.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.converter.search.injector.BeanValueInjector;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;

/**
 * Abstract implementation of {@link BeanValueInjector}
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
abstract class BaseBeanValueInjector<T extends SearchCriteriaBean> implements BeanValueInjector<T> {
	@Autowired
	private SearchService searchService;

	@Override
	public void inject(T bean) {
		bean.setOwnerId(ValueGenerator.generateValue(Long.class));
		bean.setSearchName(ValueGenerator.generateValue(String.class));
		bean.setShared(ValueGenerator.generateValue(Boolean.class));

		SearchType type = searchService.getSearchTypeByName(bean.getSearchTypeName());
		if (type != null) {
			bean.setSearchTypeId(type.getId());
		}

		Collection<ColumnType> types = searchService.getColumnTypes(bean.getSearchTypeId());
		if (types != null) {
			Collection<Long> columnTypeIds = new ArrayList<Long>();
			for (ColumnType columnType : types) {
				columnTypeIds.add(columnType.getId());
			}
			bean.setColumnTypeIds(columnTypeIds);
		}
	}
}