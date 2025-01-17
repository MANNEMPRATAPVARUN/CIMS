package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.ICDTabularComparativeBean;

/**
 * Converter implementation for transforming {@link ICDTabularComparativeBean} objects
 * into {@link Search}
 * @author rshnaper
 *
 */
public class ICDTabularComparativeBeanToSearchConverter extends
		TabularComparativeBeanToSearchConverter<ICDTabularComparativeBean> {

	@Override
	public void convert(BeanWrapper wrapper, Search search,
			CriterionProvider criterionProvider,
			CriterionTypeProvider critionTypeProvider) {
		super.convert(wrapper, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "codeFrom", CriterionModelConstants.CATEGORY_CODE_FROM, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "codeTo", CriterionModelConstants.CATEGORY_CODE_TO, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "chapterCode", CriterionModelConstants.CHAPTER_CODE, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "codesOnly", CriterionModelConstants.CODES_ONLY, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "modifiedLanguage", CriterionModelConstants.MODIFIED_LANGUAGE, search, criterionProvider, critionTypeProvider);
	}
}
