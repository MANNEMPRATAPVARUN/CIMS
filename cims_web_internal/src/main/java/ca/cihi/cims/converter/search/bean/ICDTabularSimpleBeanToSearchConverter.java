package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.ICDTabularSimpleBean;

public class ICDTabularSimpleBeanToSearchConverter extends TabularSimpleBeanToSearchConverter<ICDTabularSimpleBean> {

	@Override
	public void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider critionTypeProvider) {
		super.convert(wrapper, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "codeFrom", CriterionModelConstants.CATEGORY_CODE_FROM, search, criterionProvider,
				critionTypeProvider);
		setValue(wrapper, "codeTo", CriterionModelConstants.CATEGORY_CODE_TO, search, criterionProvider,
				critionTypeProvider);
		setValue(wrapper, "chapterCode", CriterionModelConstants.CHAPTER_CODE, search, criterionProvider,
				critionTypeProvider);
		setValue(wrapper, "daggerAsteriskId", CriterionModelConstants.DAGGER_ASTERISK_CONCEPT_ID, search,
				criterionProvider, critionTypeProvider);
		setValue(wrapper, "canEnhancementFlag", CriterionModelConstants.CAN_ENHANCEMENT_IND, search, criterionProvider,
				critionTypeProvider);
	}
}
