package ca.cihi.cims.service.search;

/**
 * ICD Tabular query builder implementation
 * @author rshnaper
 *
 */
class ICDTabularChangesQueryBuilder extends AbstractQueryBuilder {

	@Override
	protected String getQueryName() {
		return "ICDTabularCRQuery";
	}

}
