package ca.cihi.cims.service.search;

/**
 * Index changes query builder implementation
 * @author rshnaper
 *
 */
class IndexChangesQueryBuilder extends AbstractQueryBuilder {

	@Override
	protected String getQueryName() {
		return "IndexCRQuery";
	}
}
