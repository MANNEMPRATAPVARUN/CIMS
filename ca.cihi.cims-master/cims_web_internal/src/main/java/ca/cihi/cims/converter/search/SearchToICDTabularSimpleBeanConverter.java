package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.web.bean.search.ICDTabularSimpleBean;

public class SearchToICDTabularSimpleBeanConverter extends SearchToTabularSimpleBeanConverter<ICDTabularSimpleBean> {

	@Override
	protected void convert(ICDTabularSimpleBean bean, SearchValueProvider provider) {
		super.convert(bean, provider);

		bean.setCodeFrom(provider.getValue(CriterionModelConstants.CATEGORY_CODE_FROM, String.class));
		bean.setCodeTo(provider.getValue(CriterionModelConstants.CATEGORY_CODE_TO, String.class));
		bean.setChapterCode(provider.getValue(CriterionModelConstants.CHAPTER_CODE, String.class));
		bean.setDaggerAsteriskId(provider.getValue(CriterionModelConstants.DAGGER_ASTERISK_CONCEPT_ID, Long.class));
		bean.setCanEnhancementFlag(provider.getValue(CriterionModelConstants.CAN_ENHANCEMENT_IND, Boolean.class));
	}
}
