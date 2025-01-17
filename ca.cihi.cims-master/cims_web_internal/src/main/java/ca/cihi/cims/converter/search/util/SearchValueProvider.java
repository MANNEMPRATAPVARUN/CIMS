package ca.cihi.cims.converter.search.util;

import java.util.Collection;

import ca.cihi.cims.model.search.Search;

/**
 * Interface for value provider from {@link Search} objects
 * @author rshnaper
 *
 */
public interface SearchValueProvider {
	public <V> V getValue(String modelName, Class<V> type);
	public <V> Collection<V> getValues(String modelName, Class<V> type);
}
