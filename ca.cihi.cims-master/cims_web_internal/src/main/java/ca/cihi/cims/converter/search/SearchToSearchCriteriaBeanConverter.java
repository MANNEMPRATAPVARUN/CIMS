package ca.cihi.cims.converter.search;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;

import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.converter.search.util.SearchValueProviderImpl;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;

/**
 * Base converter from {@link Search} to {@link SearchCriteriaBean}
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public abstract class SearchToSearchCriteriaBeanConverter<T extends SearchCriteriaBean> implements Converter<Search, T> {
	protected static final Logger logger = LogManager.getLogger(SearchToSearchCriteriaBeanConverter.class);

	private @Autowired
	SearchService searchService;

	@Override
	public final T convert(Search search) {
		T bean = createBean();

		bean.setSearchId(search.getId());
		bean.setSearchTypeId(search.getType().getId());
		bean.setSearchName(search.getName());
		bean.setClassificationName(search.getClassificationName());
		bean.setSearchTypeName(search.getType().getName());
		bean.setOwnerId(search.getOwnerId());
		bean.setShared(search.isShared());

		// load search columns or add default ones
		Collection<Column> columns = getColumns(search);
		Collection<Long> columnTypeIds = new ArrayList<Long>();
		if (columns != null) {
			for (Column column : columns) {
				columnTypeIds.add(column.getType().getId());
			}
		}
		bean.setColumnTypeIds(columnTypeIds);

		convert(bean, new SearchValueProviderImpl(search));

		return bean;
	}

	protected abstract void convert(T bean, SearchValueProvider provider);

	@SuppressWarnings("unchecked")
	protected T createBean() {
		T bean = null;
		Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		Class<T> beanClass = null;
		if (type instanceof TypeVariable) {
			beanClass = (Class<T>) ((TypeVariable) type).getBounds()[0];
		} else {
			beanClass = (Class<T>) type;
		}
		try {
			bean = beanClass.newInstance();
		} catch (Exception e) {
			logger.error(String.format("Unable to create bean of type %s", beanClass), e);
		}
		return bean;
	}

	private Collection<Column> getColumns(Search search) {
		Collection<Column> columns = search.getColumns();
		if (columns == null || columns.isEmpty()) {
			columns = new ArrayList<Column>();
			Collection<ColumnType> types = getSearchService().getColumnTypes(search.getType().getId());
			if (types != null) {
				Column column;
				for (ColumnType type : types) {
					if (type.isDefault()) {
						column = new Column(0, type);
						column.setOrder(type.getOrder());
						columns.add(column);
					}
				}
			}
			Collections.sort((List<Column>) columns);
		}
		return columns;
	}

	protected SearchService getSearchService() {
		return searchService;
	}

}
