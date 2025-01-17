package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.CCITabularComparativeBean;

/**
 * Converter implementation for transforming {@link Search} objects
 * into {@link CCITabularComparativeBean}
 * @author rshnaper
 *
 */
public class SearchToCCITabularComparativeBeanConverter extends
		SearchToTabularComparativeBeanConverter<CCITabularComparativeBean> {

	@Override
	protected void convert(CCITabularComparativeBean bean,
			SearchValueProvider provider) {
		super.convert(bean, provider);
		bean.setModifiedLanguage(provider.getValue(CriterionModelConstants.MODIFIED_LANGUAGE, String.class));
		
		switch(bean.getHierarchyLevel()) {
			case Block:
				bean.setSectionCode(provider.getValue(CriterionModelConstants.SECTION_CODE, String.class));
				break;
			case Group:
				bean.setCodeFrom(provider.getValue(CriterionModelConstants.GROUP_CODE_FROM, String.class));
				bean.setCodeTo(provider.getValue(CriterionModelConstants.GROUP_CODE_TO, String.class));
				break;
			case Rubric:
				bean.setCodeFrom(provider.getValue(CriterionModelConstants.RUBRIC_CODE_FROM, String.class));
				bean.setCodeTo(provider.getValue(CriterionModelConstants.RUBRIC_CODE_TO, String.class));
				bean.setCodesOnly(provider.getValue(CriterionModelConstants.CODES_ONLY, Boolean.class));
				break;
		}
	}
}
