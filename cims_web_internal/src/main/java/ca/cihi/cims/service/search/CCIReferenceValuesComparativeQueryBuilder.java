package ca.cihi.cims.service.search;

/**
 * Implementation of {@link AbstractQueryBuilder} for CCI reference value comparative search
 * 
 * @author rshnaper
 * 
 */
public class CCIReferenceValuesComparativeQueryBuilder extends AbstractQueryBuilder {

	private static final String CCI_REFERENCE_VALUES_COMPARATIVE_QUERY = "CCIReferenceValuesComparativeQuery";

	@Override
	protected String getQueryName() {
		return CCI_REFERENCE_VALUES_COMPARATIVE_QUERY;
	}

}
