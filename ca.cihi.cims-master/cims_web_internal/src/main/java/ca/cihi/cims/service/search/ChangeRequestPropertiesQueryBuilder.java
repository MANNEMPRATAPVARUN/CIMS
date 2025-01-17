package ca.cihi.cims.service.search;


/**
 * Query builder implementation for change request properties searches
 * @author rshnaper
 *
 */
class ChangeRequestPropertiesQueryBuilder extends AbstractQueryBuilder {

	@Override
	protected String getQueryName() {
		return "BasicCRQuery";
	}
}
