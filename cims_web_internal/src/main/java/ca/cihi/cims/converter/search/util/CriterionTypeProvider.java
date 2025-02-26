package ca.cihi.cims.converter.search.util;

import java.util.Collection;

import ca.cihi.cims.model.search.CriterionType;

/**
 * Criterion type provider from {@link Search} objects
 * @author rshnaper
 *
 */
public interface CriterionTypeProvider {
	public CriterionType getCriterionType(String modelName);
	public Collection<CriterionType> getCriterionTypes(String modelName);
}
