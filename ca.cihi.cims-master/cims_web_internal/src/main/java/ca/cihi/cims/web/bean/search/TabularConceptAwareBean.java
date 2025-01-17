package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.tabular.TabularConceptType;

/**
 * An interface for beans who represent a specific tabular concept type
 * and can provide from/to code ranges
 * @author rshnaper
 *
 */
public interface TabularConceptAwareBean {
	public TabularConceptType getTabularConceptType();
	public String getCodeFrom(TabularConceptType type);
	public String getCodeTo(TabularConceptType type);
}
