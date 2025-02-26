package ca.cihi.cims.service.search;

import org.mybatis.spring.support.SqlSessionDaoSupport;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Implementation of {@link QueryBuilderFactory} which returns a specialized query builder depending on the search type
 * 
 * @author rshnaper
 * 
 */
public class QueryBuilderFactoryImpl extends SqlSessionDaoSupport implements QueryBuilderFactory {

	@Override
	public QueryBuilder getInstanceFor(SearchTypes type) {
		AbstractQueryBuilder queryBuilder = null;

		if (type != null) {
			switch (type) {
			case ChangeRequestProperties:
				queryBuilder = new ChangeRequestPropertiesQueryBuilder();
				break;
			case ChangeRequestICDTabular:
				queryBuilder = new ICDTabularChangesQueryBuilder();
				break;
			case ChangeRequestCCITabular:
				queryBuilder = new CCITabularChangesQueryBuilder();
				break;
			case ChangeRequestIndex:
				queryBuilder = new IndexChangesQueryBuilder();
				break;
			case ICDTabularComparative:
				queryBuilder = new ICDTabularComparativeQueryBuilder();
				break;
			case CCITabularComparative:
				queryBuilder = new CCITabularComparativeQueryBuilder();
				break;
			case ICDTabularSimple:
				queryBuilder = new ICDTabularSimpleQueryBuilder();
				break;
			case CCITabularSimple:
				queryBuilder = new CCITabularSimpleQueryBuilder();
				break;
			case CCIReferenceValuesComparative:
				queryBuilder = new CCIReferenceValuesComparativeQueryBuilder();
				break;
			}
		}

		if (queryBuilder != null) {
			queryBuilder.setSQLSession(getSqlSession());
		}

		return queryBuilder;
	}

}
