package ca.cihi.cims.converter.search.util;

import java.util.ArrayList;
import java.util.Collection;

import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.Search;

/**
 * Implementation of {@link SearchValueProvider}
 * @author rshnaper
 *
 */
public class SearchValueProviderImpl extends CriterionProviderImpl implements SearchValueProvider {
	
	public SearchValueProviderImpl(Search search) {
		super(search);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <V> V getValue(String modelName, Class<V> type) {
		Criterion criterion = getCriterion(modelName);
		return criterion != null ? (V)criterion.getValue() : null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V> Collection<V> getValues(String modelName, Class<V> type) {
		Collection<Criterion> criteria = getCriteria(modelName);
		Collection<V> values = null;
		if(criteria != null) {
			values = new ArrayList<V>();
			for(Criterion criterion : criteria) {
				if(criterion.getValue() != null) {
					values.add((V)criterion.getValue());
				}
			}
		}
		return values;
	}
	
	
}
