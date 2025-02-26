package ca.cihi.cims.service.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import ca.cihi.cims.model.search.Column;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.Search;

/**
 * Abstract query builder that provides access to common methods
 * and the underlying MyBatis mapper instance
 * @author rshnaper
 *
 */
abstract class AbstractQueryBuilder implements QueryBuilder {
	private SqlSession sqlSession;

	public SqlSession getSQLSession() {
		return sqlSession;
	}

	public void setSQLSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}
	
	@Override
	public long getCount(Search search) {
		long count = 0;
		Map<String,Object> parameters = prepareParameters(search);
		parameters.put("$COUNT_ONLY", true);
		
		Collection<Map<String,Object>> result = executeQuery(sqlSession, parameters);
		if(result != null && !result.isEmpty()) {
			Number resultCount = (Number)result.iterator().next().get("COUNT");
			if(resultCount != null) {
				count = resultCount.longValue();
			}
		}
		return count;
	}

	@Override
	public Collection<Map<String,Object>> executeSearch(Search search, Collection<Column> columns, Collection<Column> order) {
		Map<String,Object> parameters = prepareParameters(search);
		parameters.put("$COLUMNS", columns);
		parameters.put("$ORDER", order);
		
		return executeQuery(sqlSession, parameters);
	}
	
	/**
	 * Executes the constructed query using the sqlSession and parameter map
	 * @param sqlSession
	 * @param parameters
	 * @return
	 */
	protected Collection<Map<String,Object>> executeQuery(SqlSession sqlSession, Map<String,Object> parameters) {
		String query = String.format("%s.%s",QueryBuilder.class.getName(), getQueryName());
		return sqlSession.selectList(query, parameters);
	}
	
	/**
	 * Returns the name of the query that needs to be executed
	 * @return
	 */
	protected abstract String getQueryName();
	
	/**
	 * Prepares the search criteria by combining the same criteria types
	 * into a single collection of values.
	 * @param search
	 * @return java.util.Map - map of values with key as the criterion model name
	 */
	@SuppressWarnings("unchecked")
	protected Map<String,Object> prepareParameters(Search search) {
		Map<String,Object> criteriaMap = new HashMap<String, Object>();
		if(search != null) {
			String key;
			for(Criterion criterion : search.getCriteria()) {
				key = criterion.getType().getModelName();
				if(criteriaMap.containsKey(key)) {
					Object existingValue = criteriaMap.get(key);
					if(existingValue instanceof List) {
						((List)existingValue).add(criterion.getValue());
					}
					else {
						List<Object> values = new ArrayList<Object>();
						values.add(existingValue);
						values.add(criterion.getValue());
						criteriaMap.put(key, values);
					}
				}
				else {
					criteriaMap.put(key, criterion.getValue());
				}
			}			
		}
		return criteriaMap;
	}
}
