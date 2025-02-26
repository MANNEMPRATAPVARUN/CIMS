package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.TabularComparativeBean;
import ca.cihi.cims.web.bean.search.TabularComparativeBean.ComparativeType;

/**
 * Converter implementation for transforming {@link TabularComparativeBean} objects into {@link Search}
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class TabularComparativeBeanToSearchConverter<T extends TabularComparativeBean> extends
		SearchCriteriaBeanToSearchConverter<T> {

	@SuppressWarnings("unchecked")
	@Override
	public void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider critionTypeProvider) {
		setValue(wrapper, "contextId", CriterionModelConstants.YEAR, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "priorContextId", CriterionModelConstants.PRIOR_YEAR, search, criterionProvider,
				critionTypeProvider);

		HierarchyLevel hierarchyLevel = ((T) wrapper.getWrappedInstance()).getHierarchyLevel();
		if (hierarchyLevel != null) {
			setValue(Long.valueOf(hierarchyLevel.getCode()), CriterionModelConstants.HIERARCHY_LEVEL, search,
					criterionProvider, critionTypeProvider);
		}

		ComparativeType comparativeType = ((T) wrapper.getWrappedInstance()).getComparativeType();
		if (comparativeType != null) {
			setValue(comparativeType.getCode(), CriterionModelConstants.COMPARATIVE_TYPE, search, criterionProvider,
					critionTypeProvider);
		}
	}
}
