package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.CCITabularSimpleBean;
import ca.cihi.cims.web.bean.search.HierarchyLevel;

public class CCITabularSimpleBeanToSearchConverter extends TabularSimpleBeanToSearchConverter<CCITabularSimpleBean> {

	@Override
	public void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider critionTypeProvider) {
		super.convert(wrapper, search, criterionProvider, critionTypeProvider);

		HierarchyLevel level = ((CCITabularSimpleBean) wrapper.getWrappedInstance()).getHierarchyLevel();
		switch (level) {
		case Block:
			setValue(wrapper, "sectionCode", CriterionModelConstants.SECTION_CODE, search, criterionProvider,
					critionTypeProvider);
			break;
		case Group:
			setValue(wrapper, "codeFrom", CriterionModelConstants.GROUP_CODE_FROM, search, criterionProvider,
					critionTypeProvider);
			setValue(wrapper, "codeTo", CriterionModelConstants.GROUP_CODE_TO, search, criterionProvider,
					critionTypeProvider);
			break;
		case Rubric:
			setValue(wrapper, "codeFrom", CriterionModelConstants.RUBRIC_CODE_FROM, search, criterionProvider,
					critionTypeProvider);
			setValue(wrapper, "codeTo", CriterionModelConstants.RUBRIC_CODE_TO, search, criterionProvider,
					critionTypeProvider);
			break;
		default:
			setValue(null, CriterionModelConstants.RUBRIC_CODE_FROM, search, criterionProvider, critionTypeProvider);
			setValue(null, CriterionModelConstants.RUBRIC_CODE_TO, search, criterionProvider, critionTypeProvider);
			setValue(null, CriterionModelConstants.GROUP_CODE_FROM, search, criterionProvider, critionTypeProvider);
			setValue(null, CriterionModelConstants.GROUP_CODE_TO, search, criterionProvider, critionTypeProvider);
			setValue(null, CriterionModelConstants.SECTION_CODE, search, criterionProvider, critionTypeProvider);
			break;
		}

		setValue(wrapper, "invasivenessLevel", CriterionModelConstants.INVASIVENESS_LEVEL_CONCEPT_ID, search,
				criterionProvider, critionTypeProvider);
		setValue(wrapper, "refValueStatusCode", CriterionModelConstants.REF_VALUE_STATUS_CODE, search,
				criterionProvider, critionTypeProvider);
		setValue(wrapper, "refValueLocationModeCode", CriterionModelConstants.REF_VALUE_LOCATION_MODE_CODE, search,
				criterionProvider, critionTypeProvider);
		setValue(wrapper, "refValueExtentCode", CriterionModelConstants.REF_VALUE_EXTENT_CODE, search,
				criterionProvider, critionTypeProvider);
	}
}
