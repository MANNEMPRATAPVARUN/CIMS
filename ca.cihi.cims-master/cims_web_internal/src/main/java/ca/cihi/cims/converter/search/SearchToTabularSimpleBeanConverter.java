package ca.cihi.cims.converter.search;

import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.search.HierarchyLevel;
import ca.cihi.cims.web.bean.search.TabularSimpleBean;

public class SearchToTabularSimpleBeanConverter<T extends TabularSimpleBean> extends
		SearchToSearchCriteriaBeanConverter<T> {

	protected static final Logger logger = LogManager.getLogger(SearchToTabularSimpleBeanConverter.class);

	private @Autowired
	LookupService lookupService;

	@Override
	protected void convert(T bean, SearchValueProvider provider) {

		if (bean.getSearchId() == 0) {
			// Set default value to contextIds: current open year
			Long currentOpenContextId = lookupService.findCurrentOpenContextByClassification(bean
					.getClassificationName());
			if (currentOpenContextId != null && currentOpenContextId > 0) {
				bean.setContextIds(Arrays.asList(currentOpenContextId));
			}

			// set default value to statusCode: Active
			bean.setStatusCode(ConceptStatus.ACTIVE.name().intern());
		} else {
			bean.setContextIds(provider.getValues(CriterionModelConstants.YEAR, Long.class));
			bean.setStatusCode(provider.getValue(CriterionModelConstants.STATUS_CODE, String.class));
		}

		Long hierarchyLevelCode = provider.getValue(CriterionModelConstants.HIERARCHY_LEVEL, Long.class);
		if (hierarchyLevelCode != null) {
			bean.setHierarchyLevel(HierarchyLevel.forCode(hierarchyLevelCode.intValue()));
		}

		// set searchText and default values to all search checkboxes
		String searchText = provider.getValue(CriterionModelConstants.SEARCH_TEXT, String.class);
		bean.setSearchText(searchText);
		if (StringUtils.isEmpty(searchText)) {
			bean.setIsEnglishShort(true);
			bean.setIsEnglishLong(true);
			bean.setIsEnglishUser(true);
			bean.setIsEnglishViewerContent(true);
			bean.setIsFrenchShort(true);
			bean.setIsFrenchLong(true);
			bean.setIsFrenchUser(true);
			bean.setIsFrenchViewerContent(true);
		} else {
			bean.setIsEnglishShort(provider.getValue(CriterionModelConstants.SEARCH_IN_ENG_SHORT, Boolean.class));
			bean.setIsEnglishLong(provider.getValue(CriterionModelConstants.SEARCH_IN_ENG_LONG, Boolean.class));
			bean.setIsEnglishUser(provider.getValue(CriterionModelConstants.SEARCH_IN_ENG_USER, Boolean.class));
			bean.setIsEnglishViewerContent(provider.getValue(CriterionModelConstants.SEARCH_IN_ENG_VIEWER_CONTENT,
					Boolean.class));
			bean.setIsFrenchShort(provider.getValue(CriterionModelConstants.SEARCH_IN_FR_SHORT, Boolean.class));
			bean.setIsFrenchLong(provider.getValue(CriterionModelConstants.SEARCH_IN_FR_LONG, Boolean.class));
			bean.setIsFrenchUser(provider.getValue(CriterionModelConstants.SEARCH_IN_FR_USER, Boolean.class));
			bean.setIsFrenchViewerContent(provider.getValue(CriterionModelConstants.SEARCH_IN_FR_VIEWER_CONTENT,
					Boolean.class));
		}
	}

}
