package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.TabularChangesBean;

/**
 * Converter implementation for transforming {@link TabularChangesBean} objects
 * into {@link Search}
 * @author rshnaper
 *
 */
public class TabularChangesBeanToSearchConverter<T extends TabularChangesBean> extends
		ChangeRequestPropertiesBeanToSearchConverter<T> {

	@Override
	public void convert(BeanWrapper wrapper, Search search,
			CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		super.convert(wrapper, search, criterionProvider, typeProvider);
		
		setValue(wrapper, "newCodeValues", CriterionModelConstants.SEARCH_IN_NEW_CODES, search, criterionProvider, typeProvider);
		setValue(wrapper, "disabledCodeValues", CriterionModelConstants.SEARCH_IN_DISABLED_CODES, search, criterionProvider, typeProvider);
		setValue(wrapper, "modifiedProperties", CriterionModelConstants.SEARCH_IN_MODIFIED_PROPERTIES, search, criterionProvider, typeProvider);
		setValue(wrapper, "validations", CriterionModelConstants.SEARCH_IN_VALIDATIONS, search, criterionProvider, typeProvider);
		setValue(wrapper, "conceptMovement", CriterionModelConstants.SEARCH_IN_CONCEPT_MOVEMENT, search, criterionProvider, typeProvider);
	}
}
