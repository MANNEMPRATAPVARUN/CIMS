package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.CCITabularChangesBean;
import ca.cihi.cims.web.bean.search.TabularChangesBean.HierarchyType;

/**
 * Converter implementation for transforming {@link Search} objects
 * into {@link CCITabularChangesBean}
 * @author rshnaper
 *
 */
public class SearchToCCITabularChangesBeanConverter extends
		SearchToTabularChangesBeanConverter<CCITabularChangesBean> {

	@Override
	public void convert(CCITabularChangesBean bean, SearchValueProvider provider) {
		super.convert(bean, provider);
		
		String codeFrom = provider.getValue(CriterionModelConstants.GROUP_CODE_FROM, String.class);
		String codeTo = provider.getValue(CriterionModelConstants.GROUP_CODE_TO, String.class);
		Boolean codesOnly = provider.getValue(CriterionModelConstants.CODES_ONLY, Boolean.class);
		String modifiedLanguage = provider.getValue(CriterionModelConstants.MODIFIED_LANGUAGE, String.class);
		HierarchyType levelType = HierarchyType.Group;
		
		if(codeFrom == null) {
			codeFrom = provider.getValue(CriterionModelConstants.RUBRIC_CODE_FROM, String.class);
			codeTo = provider.getValue(CriterionModelConstants.RUBRIC_CODE_TO, String.class);
			
			if(codeFrom != null || codeTo != null) {
				levelType = HierarchyType.Rubric;
			}
		}
		
		bean.setCodeFrom(codeFrom);
		bean.setCodeTo(codeTo);
		bean.setLevel(levelType);
		bean.setCodesOnly(codesOnly);
		bean.setModifiedLanguage(modifiedLanguage);
	}
}
