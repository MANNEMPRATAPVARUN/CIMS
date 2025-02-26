package ca.cihi.cims.converter.search;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchDateTypes;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchTextTypes;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchUserTypes;

/**
 * Converts specific properties of {@link ChangeRequestPropetiesBean} to {@link Search} object
 * 
 * @author rshnaper
 * 
 */
public class SearchToChangeRequestPropertiesBeanConverter<T extends ChangeRequestPropetiesBean> extends
		SearchToSearchCriteriaBeanConverter<T> {
	private @Autowired
	LookupService lookupService;

	@Override
	public void convert(T bean, SearchValueProvider provider) {
		String requestCategory = provider.getValue(CriterionModelConstants.REQUEST_CATEGORY, String.class);
		if (requestCategory != null) {
			bean.setRequestCategory(requestCategory);
		}

		bean.setLanguage(provider.getValue(CriterionModelConstants.LANGUAGE, String.class));

		if (bean.getSearchId() == 0) {
			Long currentOpenContextId = lookupService.findCurrentOpenContextByClassification(bean
					.getClassificationName());
			if (currentOpenContextId != null && currentOpenContextId > 0) {
				bean.setContextIds(Arrays.asList(currentOpenContextId));
			}
		} else {
			bean.setContextIds(provider.getValues(CriterionModelConstants.YEAR, Long.class));
		}

		bean.setStatusIds(provider.getValues(CriterionModelConstants.STATUS, Long.class));
		bean.setChangeTypeId(provider.getValue(CriterionModelConstants.CHANGE_TYPE_ID, Long.class));
		bean.setChangeNatureId(provider.getValue(CriterionModelConstants.CHANGE_NATURE_ID, Long.class));
		bean.setRequestorId(provider.getValue(CriterionModelConstants.REQUESTOR_ID, Long.class));
		bean.setPatternChange(provider.getValue(CriterionModelConstants.PATTERN_CHANGE, Boolean.class));
		bean.setEvolutionRequired(provider.getValue(CriterionModelConstants.EVOLUTION_REQUIRED, Boolean.class));
		bean.setPatternTopic(provider.getValue(CriterionModelConstants.PATTERN_TOPIC, String.class));
		bean.setIndexRequired(provider.getValue(CriterionModelConstants.INDEX_REQUIRED, Boolean.class));
		bean.setEvolutionLanguage(provider.getValue(CriterionModelConstants.EVOLUTION_LANGUAGE, String.class));
		String searchText = provider.getValue(CriterionModelConstants.SEARCH_REQUEST_NAME, String.class);
		if (searchText != null) {
			bean.setSearchTextType(SearchTextTypes.RequestName);
		} else {
			searchText = provider.getValue(CriterionModelConstants.SEARCH_RATIONALE, String.class);
			if (searchText != null) {
				bean.setSearchTextType(SearchTextTypes.RationaleChange);
			}
		}
		bean.setSearchText(searchText);

		// owner-assignee search
		Long distributionListId = provider.getValue(CriterionModelConstants.ASSIGNEE_DL_ID, Long.class);
		if (distributionListId != null) {
			bean.setSearchUserType(SearchUserTypes.Assignee);
			bean.setSearchUserId(ChangeRequestPropetiesBean.USER_ID_PREFIX_DL + distributionListId);
		} else {
			Long userId = provider.getValue(CriterionModelConstants.OWNER_ID, Long.class);
			if (userId != null) {
				bean.setSearchUserType(SearchUserTypes.Owner);
			} else {
				userId = provider.getValue(CriterionModelConstants.ASSIGNEE_USER_PROFILE_ID, Long.class);
				if (userId != null) {
					bean.setSearchUserType(SearchUserTypes.Assignee);
				}
			}
			bean.setSearchUserId(ChangeRequestPropetiesBean.USER_ID_PREFIX_USER + userId);
		}

		Collection<Date> dates = provider.getValues(CriterionModelConstants.CREATION_DATE, Date.class);
		if (dates != null) {

			bean.setSearchDateType(SearchDateTypes.Created);
		} else {
			dates = provider.getValues(CriterionModelConstants.MODIFIED_DATE, Date.class);
			if (dates != null) {
				bean.setSearchDateType(SearchDateTypes.Modified);
			}
		}

		if (dates != null) {
			Iterator<Date> dateIterator = dates.iterator();
			if (dateIterator.hasNext()) {
				bean.setDateFrom(dateIterator.next());
			}
			if (dateIterator.hasNext()) {
				bean.setDateTo(dateIterator.next());
			}
		}
	}
}
