package ca.cihi.cims.converter.search.util;

import java.util.Collection;

import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;

/**
 * Criterion provider from {@link Search} objects
 * @author rshnaper
 *
 */
public interface CriterionProvider {
	public Criterion getCriterion(String modelName);
	public Collection<Criterion> getCriteria(String modelName);
	public Collection<Criterion> getCriteria(CriterionType type);
}