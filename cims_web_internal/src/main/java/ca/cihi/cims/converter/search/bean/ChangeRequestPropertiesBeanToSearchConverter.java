package ca.cihi.cims.converter.search.bean;

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.BeanWrapper;
import org.springframework.util.StringUtils;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Criterion;
import ca.cihi.cims.model.search.CriterionType;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchDateTypes;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchTextTypes;
import ca.cihi.cims.web.bean.search.ChangeRequestPropetiesBean.SearchUserTypes;

/**
 * Converts {@link ChangeRequestPropetiesBean} into {@link Search} object
 * 
 * @author rshnaper
 * 
 */
public class ChangeRequestPropertiesBeanToSearchConverter<T extends ChangeRequestPropetiesBean> extends
		SearchCriteriaBeanToSearchConverter<T> {

	@Override
	public void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		setValue(wrapper, "contextIds", CriterionModelConstants.YEAR, search, criterionProvider, typeProvider);
		setValue(wrapper, "language", CriterionModelConstants.LANGUAGE, search, criterionProvider, typeProvider);
		setValue(wrapper, "requestCategory", CriterionModelConstants.REQUEST_CATEGORY, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "changeTypeId", CriterionModelConstants.CHANGE_TYPE_ID, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "changeNatureId", CriterionModelConstants.CHANGE_NATURE_ID, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "requestorId", CriterionModelConstants.REQUESTOR_ID, search, criterionProvider, typeProvider);
		setValue(wrapper, "evolutionRequired", CriterionModelConstants.EVOLUTION_REQUIRED, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "patternChange", CriterionModelConstants.PATTERN_CHANGE, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "patternTopic", CriterionModelConstants.PATTERN_TOPIC, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "indexRequired", CriterionModelConstants.INDEX_REQUIRED, search, criterionProvider,
				typeProvider);
		setValue(wrapper, "evolutionLanguage", CriterionModelConstants.EVOLUTION_LANGUAGE, search, criterionProvider, typeProvider);

		// status ids
		setStatusValue(wrapper, search, criterionProvider, typeProvider);

		// text search
		setTextSearchValue(wrapper, search, criterionProvider, typeProvider);

		// user search
		setUserSearchValue(wrapper, search, criterionProvider, typeProvider);

		// date search
		setDateSearchValue(wrapper, search, criterionProvider, typeProvider);
	}

	private void setDateSearchValue(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		SearchDateTypes searchDateType = ((ChangeRequestPropetiesBean) wrapper.getWrappedInstance())
				.getSearchDateType();
		Date dateFrom = ((ChangeRequestPropetiesBean) wrapper.getWrappedInstance()).getDateFrom();
		Date dateTo = ((ChangeRequestPropetiesBean) wrapper.getWrappedInstance()).getDateTo();

		Collection<CriterionType> types = typeProvider
				.getCriterionTypes(searchDateType == SearchDateTypes.Created ? CriterionModelConstants.CREATION_DATE
						: CriterionModelConstants.MODIFIED_DATE);
		if (types != null) {
			Collection<Criterion> criteria;
			for (CriterionType type : types) {
				criteria = criterionProvider.getCriteria(type);
				if (criteria != null && !criteria.isEmpty()) {
					for (Criterion criterion : criteria) {
						setDateValueToCriterion(criterion, dateFrom, dateTo);
						if (criterion.getValue() == null) {
							search.removeCriterion(criterion);
						}
					}
				} else {
					Criterion criterion = new Criterion(0, type);
					setDateValueToCriterion(criterion, dateFrom, dateTo);
					if (criterion.getValue() != null) {
						search.addCriterion(criterion);
					}
				}
			}
		}

		// clear previous values
		if (searchDateType == SearchDateTypes.Created) {
			setValue(null, CriterionModelConstants.MODIFIED_DATE, search, criterionProvider, typeProvider);
		} else {
			setValue(null, CriterionModelConstants.CREATION_DATE, search, criterionProvider, typeProvider);
		}
	}

	private void setDateValueToCriterion(Criterion criterion, Date dateFrom, Date dateTo) {
		criterion.setValue(criterion.getType().getDisplayName().toLowerCase().contains("from") ? dateFrom : dateTo);
	}

	private void setStatusValue(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		Collection<Long> values = (Collection<Long>) wrapper.getPropertyValue("statusIds");
		if (values != null && values.size() == 1 && values.iterator().next() == 0) {
			setValue(null, CriterionModelConstants.STATUS, search, criterionProvider, typeProvider);
		} else {
			setValue(values, CriterionModelConstants.STATUS, search, criterionProvider, typeProvider);
		}
	}

	private void setTextSearchValue(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		SearchTextTypes searchTextType = ((ChangeRequestPropetiesBean) wrapper.getWrappedInstance())
				.getSearchTextType();
		if (searchTextType == SearchTextTypes.RequestName) {
			setValue(wrapper, "searchText", CriterionModelConstants.SEARCH_REQUEST_NAME, search, criterionProvider,
					typeProvider);
			setValue(null, CriterionModelConstants.SEARCH_RATIONALE, search, criterionProvider, typeProvider);
		} else {
			setValue(wrapper, "searchText", CriterionModelConstants.SEARCH_RATIONALE, search, criterionProvider,
					typeProvider);
			setValue(null, CriterionModelConstants.SEARCH_REQUEST_NAME, search, criterionProvider, typeProvider);
		}
	}

	private void setUserSearchValue(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		SearchUserTypes searchUserType = ((ChangeRequestPropetiesBean) wrapper.getWrappedInstance())
				.getSearchUserType();
		Long userId = null;
		String searchUserId = (String) wrapper.getPropertyValue("searchUserId");
		boolean isDistributionList = false;
		if (searchUserId != null && !StringUtils.isEmpty(searchUserId)) {
			if (searchUserId.startsWith(ChangeRequestPropetiesBean.USER_ID_PREFIX_DL)) {
				userId = Long.valueOf(searchUserId.substring(ChangeRequestPropetiesBean.USER_ID_PREFIX_DL.length()));
				isDistributionList = true;
			} else {
				userId = Long.valueOf(searchUserId.substring(ChangeRequestPropetiesBean.USER_ID_PREFIX_USER.length()));
			}
		}

		if (searchUserType == SearchUserTypes.Owner) {
			setValue(userId, CriterionModelConstants.OWNER_ID, search, criterionProvider, typeProvider);
			setValue(null, CriterionModelConstants.ASSIGNEE_DL_ID, search, criterionProvider, typeProvider);
			setValue(null, CriterionModelConstants.ASSIGNEE_USER_PROFILE_ID, search, criterionProvider, typeProvider);
		} else {
			if (isDistributionList) {
				setValue(userId, CriterionModelConstants.ASSIGNEE_DL_ID, search, criterionProvider, typeProvider);
				setValue(null, CriterionModelConstants.OWNER_ID, search, criterionProvider, typeProvider);
				setValue(null, CriterionModelConstants.ASSIGNEE_USER_PROFILE_ID, search, criterionProvider,
						typeProvider);
			} else {
				setValue(userId, CriterionModelConstants.ASSIGNEE_USER_PROFILE_ID, search, criterionProvider,
						typeProvider);
				setValue(null, CriterionModelConstants.OWNER_ID, search, criterionProvider, typeProvider);
				setValue(null, CriterionModelConstants.ASSIGNEE_DL_ID, search, criterionProvider, typeProvider);
			}
		}
	}
}
