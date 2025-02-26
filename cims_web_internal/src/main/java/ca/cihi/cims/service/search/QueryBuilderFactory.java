package ca.cihi.cims.service.search;

import ca.cihi.cims.model.search.SearchTypes;


/**
 * A factory interface for creating {@link QueryBuilder} instances
 * @author rshnaper
 * <p>(c)2015 Canadian Institute for Health Information</p> 
 */
public interface QueryBuilderFactory {
	/**
	 * Creates an instance of {@link QueryBuilder} for the specified query type
	 * @param type
	 * @return
	 */
	public QueryBuilder getInstanceFor(SearchTypes type);
}
