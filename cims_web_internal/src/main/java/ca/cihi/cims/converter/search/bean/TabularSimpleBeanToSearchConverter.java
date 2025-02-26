package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.TabularSimpleBean;

public class TabularSimpleBeanToSearchConverter<T extends TabularSimpleBean> extends
		SearchCriteriaBeanToSearchConverter<T> {

	@Override
	@SuppressWarnings("unchecked")
	public void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider critionTypeProvider) {
		setValue(wrapper, "contextIds", CriterionModelConstants.YEAR, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "statusCode", CriterionModelConstants.STATUS_CODE, search, criterionProvider,
				critionTypeProvider);

		HierarchyLevel hierarchyLevel = ((T) wrapper.getWrappedInstance()).getHierarchyLevel();
		if (hierarchyLevel != null) {
			setValue(Long.valueOf(hierarchyLevel.getCode()), CriterionModelConstants.HIERARCHY_LEVEL, search,
					criterionProvider, critionTypeProvider);
		}

		setValue(wrapper, "searchText", CriterionModelConstants.SEARCH_TEXT, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isEnglishShort", CriterionModelConstants.SEARCH_IN_ENG_SHORT, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isEnglishLong", CriterionModelConstants.SEARCH_IN_ENG_LONG, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isEnglishUser", CriterionModelConstants.SEARCH_IN_ENG_USER, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isFrenchShort", CriterionModelConstants.SEARCH_IN_FR_SHORT, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isFrenchLong", CriterionModelConstants.SEARCH_IN_FR_LONG, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isFrenchUser", CriterionModelConstants.SEARCH_IN_FR_USER, search, criterionProvider,
				critionTypeProvider);

		setValue(wrapper, "isEnglishViewerContent", CriterionModelConstants.SEARCH_IN_ENG_VIEWER_CONTENT, search,
				criterionProvider, critionTypeProvider);

		setValue(wrapper, "isFrenchViewerContent", CriterionModelConstants.SEARCH_IN_FR_VIEWER_CONTENT, search,
				criterionProvider, critionTypeProvider);

	}
}
