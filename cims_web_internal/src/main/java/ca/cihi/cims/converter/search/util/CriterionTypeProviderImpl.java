package ca.cihi.cims.converter.search.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.service.search.SearchService;

/**
 * Implementation of {@link CriterionTypeProvider}
 * @author rshnaper
 *
 */
public class CriterionTypeProviderImpl implements CriterionTypeProvider {
	private Map<String,Collection<CriterionType>> criterionTypes;
	
	public CriterionTypeProviderImpl(SearchService service, long searchTypeId) {
		this.criterionTypes = createCriterionMap(service, searchTypeId);
	}
	
	@Override
	public CriterionType getCriterionType(String modelName) {
		Collection<CriterionType> types = getCriterionTypes(modelName);
		return types != null ? types.iterator().next() : null;
	}
	
	@Override
	public Collection<CriterionType> getCriterionTypes(String modelName) {
		return criterionTypes.get(modelName);
	}
	
	private Map<String,Collection<CriterionType>> createCriterionMap(SearchService service, long searchTypeId) {
		Map<String,Collection<CriterionType>> criterionTypes = new HashMap<String, Collection<CriterionType>>();
		Collection<CriterionType> types = service.getCriterionTypes(searchTypeId);
		if(types != null) {
			Collection<CriterionType> typeList;
			for(CriterionType type : types) {
				typeList = criterionTypes.get(type.getModelName());
				if(typeList == null) {
					typeList = new ArrayList<CriterionType>();
					criterionTypes.put(type.getModelName(), typeList);
				}
				typeList.add(type);
			}
		}
		return criterionTypes;
	}
}
