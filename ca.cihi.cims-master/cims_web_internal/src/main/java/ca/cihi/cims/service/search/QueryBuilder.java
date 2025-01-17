package ca.cihi.cims.service.search;

import java.util.Collection;
import java.util.Map;

import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.Search;

/**
 * Query builder interface for performing searches
 * @author rshnaper
 *
 */
public interface QueryBuilder {
	/**
	 * Returns the count of results for the specified search
	 * @param search
	 * @return
	 */
	public long getCount(Search search);
	
	/**
	 * Executes the search and returns the results
	 * @param search
	 * @param columns
	 * @param order
	 * @return
	 */
	public Collection<Map<String,Object>> executeSearch(Search search, Collection<Column> columns, Collection<Column> order);
}
