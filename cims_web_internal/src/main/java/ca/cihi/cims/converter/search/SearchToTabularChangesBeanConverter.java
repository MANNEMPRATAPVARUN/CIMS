package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.TabularChangesBean;

/**
 * Converter implementation for transforming {@link Search} objects into {@link TabularChangesBean}
 * 
 * @author rshnaper
 * 
 */
public class SearchToTabularChangesBeanConverter<T extends TabularChangesBean> extends
		SearchToChangeRequestPropertiesBeanConverter<T> {

	@Override
	public void convert(T bean, SearchValueProvider provider) {
		super.convert(bean, provider);
		if (bean.getSearchId() == 0) {
			// CSRE-939 by default select all change types
			bean.setNewCodeValues(true);
			bean.setDisabledCodeValues(true);
			bean.setModifiedProperties(true);
			bean.setValidations(true);
			bean.setConceptMovement(false);
			bean.setModifiedLanguage("ENG");
		} else {
			bean.setNewCodeValues(provider.getValue(CriterionModelConstants.SEARCH_IN_NEW_CODES, Boolean.class));
			bean.setDisabledCodeValues(provider.getValue(CriterionModelConstants.SEARCH_IN_DISABLED_CODES,
					Boolean.class));
			bean.setModifiedProperties(provider.getValue(CriterionModelConstants.SEARCH_IN_MODIFIED_PROPERTIES,
					Boolean.class));
			bean.setValidations(provider.getValue(CriterionModelConstants.SEARCH_IN_VALIDATIONS, Boolean.class));
			bean.setConceptMovement(provider.getValue(CriterionModelConstants.SEARCH_IN_CONCEPT_MOVEMENT,
					Boolean.class));
			bean.setModifiedLanguage(provider.getValue(CriterionModelConstants.MODIFIED_LANGUAGE, String.class));
		}
	}
}
