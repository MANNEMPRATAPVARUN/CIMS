package ca.cihi.cims.converter.search;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.TabularComparativeBean;
import ca.cihi.cims.web.bean.search.TabularComparativeBean.ComparativeType;

/**
 * Base converter from {@link Search} to {@link TabularComparativeBean}
 * 
 * @author rshnaper
 * 
 * @param <T>
 */
public class SearchToTabularComparativeBeanConverter<T extends TabularComparativeBean> extends
		SearchToSearchCriteriaBeanConverter<T> {
	private @Autowired
	LookupService lookupService;

	@Override
	protected void convert(T bean, SearchValueProvider provider) {

		if (bean.getSearchId() == 0) {
			Long currentOpenContextId = lookupService.findCurrentOpenContextByClassification(bean
					.getClassificationName());
			if (currentOpenContextId != null && currentOpenContextId > 0) {
				bean.setContextId(currentOpenContextId);
			}

			List<ContextIdentifier> contexts = lookupService.findPriorBaseContextIdentifiersByClassificationAndContext(
					bean.getClassificationName(), currentOpenContextId, false);
			if (contexts != null && !contexts.isEmpty()) {
				bean.setPriorContextId(contexts.get(0).getContextId());
			} else {
				bean.setPriorContextId(provider.getValue(CriterionModelConstants.PRIOR_YEAR, Long.class));
			}
		} else {
			bean.setContextId(provider.getValue(CriterionModelConstants.YEAR, Long.class));
			bean.setPriorContextId(provider.getValue(CriterionModelConstants.PRIOR_YEAR, Long.class));
		}

		Long hierarchyLevel = provider.getValue(CriterionModelConstants.HIERARCHY_LEVEL, Long.class);
		if (hierarchyLevel != null) {
			bean.setHierarchyLevel(HierarchyLevel.forCode(hierarchyLevel.intValue()));
		}

		String comparativeType = provider.getValue(CriterionModelConstants.COMPARATIVE_TYPE, String.class);
		if (comparativeType != null) {
			bean.setComparativeType(ComparativeType.forCode(comparativeType));
		}
	}
}
