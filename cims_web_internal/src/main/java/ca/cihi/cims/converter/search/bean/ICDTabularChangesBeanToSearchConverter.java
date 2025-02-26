package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.ICDTabularChangesBean;

/**
 * Converter implementation for transforming {@link ICDTabularChangesBean} objects
 * into {@link Search}
 * @author rshnaper
 *
 */
public class ICDTabularChangesBeanToSearchConverter extends
		TabularChangesBeanToSearchConverter<ICDTabularChangesBean> {

	@Override
	public void convert(BeanWrapper wrapper, Search search,
			CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		super.convert(wrapper, search, criterionProvider, typeProvider);
		
		setValue(wrapper, "codeFrom", CriterionModelConstants.CATEGORY_CODE_FROM, search, criterionProvider, typeProvider);
		setValue(wrapper, "codeTo", CriterionModelConstants.CATEGORY_CODE_TO, search, criterionProvider, typeProvider);
	}
}
