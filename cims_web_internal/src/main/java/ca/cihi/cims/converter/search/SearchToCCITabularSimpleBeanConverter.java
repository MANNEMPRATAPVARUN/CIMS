package ca.cihi.cims.converter.search;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.SearchValueProvider;
import ca.cihi.cims.web.bean.search.CCITabularSimpleBean;

public class SearchToCCITabularSimpleBeanConverter extends SearchToTabularSimpleBeanConverter<CCITabularSimpleBean> {

	@Override
	protected void convert(CCITabularSimpleBean bean, SearchValueProvider provider) {
		super.convert(bean, provider);

		switch (bean.getHierarchyLevel()) {
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
			break;
		}

		bean.setInvasivenessLevel(provider.getValue(CriterionModelConstants.INVASIVENESS_LEVEL_CONCEPT_ID, Long.class));
		bean.setRefValueStatusCode(provider.getValue(CriterionModelConstants.REF_VALUE_STATUS_CODE, String.class));
		bean.setRefValueLocationModeCode(provider.getValue(CriterionModelConstants.REF_VALUE_LOCATION_MODE_CODE,
				String.class));
		bean.setRefValueExtentCode(provider.getValue(CriterionModelConstants.REF_VALUE_EXTENT_CODE, String.class));
	}
}
