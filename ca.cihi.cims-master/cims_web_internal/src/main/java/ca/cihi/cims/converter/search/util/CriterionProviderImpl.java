package ca.cihi.cims.converter.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;

/**
 * Implementation of {@link CriterionProvider}
 * @author rshnaper
 *
 */
public class CriterionProviderImpl implements CriterionProvider {
	private Map<String, Collection<Criterion>> criterionMap;
	private Map<CriterionType, Collection<Criterion>> criterionTypeMap;
	
	public CriterionProviderImpl(Search search) {
		initalizeMaps(search);
	}
	
	@Override
	public Criterion getCriterion(String modelName) {
		Collection<Criterion> criteria = getCriteria(modelName);
		return criteria != null && !criteria.isEmpty() ? criteria.iterator().next() : null;
	}
	
	@Override
	public Collection<Criterion> getCriteria(String modelName) {
		return criterionMap.get(modelName);
	}
	
	@Override
	public Collection<Criterion> getCriteria(CriterionType type) {
		return criterionTypeMap.get(type);
	}

	private void initalizeMaps(Search search) {
		criterionMap = new HashMap<String, Collection<Criterion>>();
		criterionTypeMap = new HashMap<CriterionType, Collection<Criterion>>();
		
		if(search.getCriteria() != null) {
			for(Criterion criterion : search.getCriteria()) {
				mapByModelName(criterionMap, criterion.getType().getModelName(), criterion);
				mapByType(criterionTypeMap, criterion.getType(), criterion);
			}
		}
	}
	
	private void mapByModelName(Map<String,Collection<Criterion>> map, String modelName, Criterion criterion) {
		Collection<Criterion> criteria = map.get(modelName);
		if(criteria == null) {
			criteria = new ArrayList<Criterion>();
			map.put(modelName, criteria);
		}
		criteria.add(criterion);
	}
	
	private void mapByType(Map<CriterionType,Collection<Criterion>> map, CriterionType type, Criterion criterion) {
		Collection<Criterion> criteria = map.get(type);
		if(criteria == null) {
			criteria = new ArrayList<Criterion>();
			map.put(type, criteria);
		}
		criteria.add(criterion);
	}
}
