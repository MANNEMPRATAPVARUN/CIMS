package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.ICDTabularComparativeBean;

/**
 * Converter implementation for transforming {@link Search} objects
 * into {@link ICDTabularComparativeBean}
 * @author rshnaper
 *
 */
public class SearchToICDTabularComparativeBeanConverter extends
		SearchToTabularComparativeBeanConverter<ICDTabularComparativeBean> {

	@Override
	protected void convert(ICDTabularComparativeBean bean,
			SearchValueProvider provider) {
		super.convert(bean, provider);
		bean.setCodeFrom(provider.getValue(CriterionModelConstants.CATEGORY_CODE_FROM, String.class));
		bean.setCodeTo(provider.getValue(CriterionModelConstants.CATEGORY_CODE_TO, String.class));
		bean.setChapterCode(provider.getValue(CriterionModelConstants.CHAPTER_CODE, String.class));
		bean.setCodesOnly(provider.getValue(CriterionModelConstants.CODES_ONLY, Boolean.class));
		bean.setModifiedLanguage(provider.getValue(CriterionModelConstants.MODIFIED_LANGUAGE, String.class));
	}
}
