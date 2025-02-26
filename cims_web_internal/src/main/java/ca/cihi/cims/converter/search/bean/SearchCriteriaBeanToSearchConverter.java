package ca.cihi.cims.converter.search.bean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionProviderImpl;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProviderImpl;
import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.ColumnType;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.model.search.SearchType;
import ca.cihi.cims.service.search.SearchService;
import ca.cihi.cims.web.bean.search.SearchCriteriaBean;

/**
 * Base converter from {@link SearchCriteriaBean} to {@link Search}
 * @author rshnaper
 *
 * @param <S>
 */
public abstract class SearchCriteriaBeanToSearchConverter<S extends SearchCriteriaBean> implements Converter<S, Search> {
	@Autowired
	private SearchService service;
	
	@Override
	public Search convert(S bean) {
		Search search = null;
		
		if(bean.getSearchId() > 0) {
			search = service.getSearchById(bean.getSearchId());
		}
		
		if(search == null) {
			SearchType type = new SearchType(bean.getSearchTypeId(), bean.getSearchTypeName());
			search = new Search(bean.getSearchId(),type);
		}
		
		search.setName(bean.getSearchName());
		search.setOwnerId(bean.getOwnerId());
		search.setClassificationName(bean.getClassificationName());
		search.setShared(bean.isShared());
		search.setColumns(getColumns(bean));
		
		CriterionTypeProvider criterionTypeProvider = new CriterionTypeProviderImpl(service, search.getType().getId());
		CriterionProvider criterionProvider = new CriterionProviderImpl(search);
		BeanWrapper wrapper = new BeanWrapperImpl(bean);
		
		convert(wrapper, search, criterionProvider, criterionTypeProvider);
		
		return search;
	}
	
	public abstract void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider, CriterionTypeProvider critionTypeProvider);		
	
	protected Criterion createCriterion(String modelName, CriterionTypeProvider provider) {
		CriterionType type = provider.getCriterionType(modelName);
		if(type != null) {
			return new Criterion(0, type);
		}
		return null;
	}
	
	private Collection<Column> getColumns(SearchCriteriaBean bean) {
		Collection<ColumnType> types = service.getColumnTypes(bean.getSearchTypeId());
		Map<Long, ColumnType> columnTypeMap = new HashMap<Long, ColumnType>();
		if(types != null) {
			for(ColumnType type : types) {
				columnTypeMap.put(type.getId(), type);
			}
		}
		
		Column column;
		Collection<Column> columns = new ArrayList<Column>();
		if(bean.getColumnTypeIds() != null && !bean.getColumnTypeIds().isEmpty()) {
			ColumnType type;
			int order = 0;
			for(Long columnTypeId : bean.getColumnTypeIds()) {
				type = columnTypeMap.get(columnTypeId);
				if(type != null) {
					column = new Column(0, type);
					column.setOrder(order);
					columns.add(column);
				}
				order++;
			}
		}
		else {
			//add default columns
			for(ColumnType type : types) {
				if(type.isDefault()) {
					column = new Column(0, type);
					column.setOrder(type.getOrder());
					columns.add(column);
				}
			}
		}
		
		if(!(columns instanceof List)) {
			columns = new ArrayList<Column>(columns);
		}
		Collections.sort((List<Column>)columns);
		
		return columns;
	}
	
	protected void setValue(BeanWrapper wrapper, String propertyName, String modelName, Search search, CriterionProvider criterionProvider, CriterionTypeProvider typeProvider) {
		Object value = wrapper.getPropertyValue(propertyName);
		setValue(value, modelName, search, criterionProvider, typeProvider);
	}
	
	protected void setValue(Object value, String modelName, Search search, CriterionProvider criterionProvider, CriterionTypeProvider typeProvider) {
		Collection<Criterion> criteria = criterionProvider.getCriteria(modelName);
		if(value instanceof Collection) {
			if(criteria == null) {
				criteria = Collections.emptyList();
			}
			
			Criterion criterion;
			Iterator<Criterion> criterionIterator = criteria.iterator();
			for(Object singleValue : (Collection)value) {
				criterion = criterionIterator.hasNext() ? criterionIterator.next() : null;
				if(criterion == null) {
					criterion = createCriterion(modelName, typeProvider);
					if(criterion != null) {
						search.addCriterion(criterion);
					}
				}
				criterion.setValue(singleValue);
			}
			
			//remove values that no longer used
			while(criterionIterator.hasNext()) {
				search.removeCriterion(criterionIterator.next());
			}
		}
		else {
			if((value == null || StringUtils.isEmpty(value))) {
				if(criteria != null && !criteria.isEmpty()) {
					for(Criterion criterion : criteria) {
						search.removeCriterion(criterion);
					}
				}
			}
			else {
				Criterion criterion = criteria != null && !criteria.isEmpty() ? criteria.iterator().next() : null;
				if(criterion == null) {
					criterion = createCriterion(modelName, typeProvider);
					if(criterion != null) {
						search.addCriterion(criterion);
					}
				}
				if(criterion != null) {
					criterion.setValue(value);
				}
			}
		}
	}
}
