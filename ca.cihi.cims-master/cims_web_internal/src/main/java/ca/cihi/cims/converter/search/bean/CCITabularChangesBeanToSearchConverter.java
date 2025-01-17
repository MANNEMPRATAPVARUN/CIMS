package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.CCITabularChangesBean;
import ca.cihi.cims.web.bean.search.TabularChangesBean;
import ca.cihi.cims.web.bean.search.TabularChangesBean.HierarchyType;

/**
 * Converter implementation for transforming {@link CCITabularChangesBean} objects
 * into {@link Search}
 * @author rshnaper
 *
 */
public class CCITabularChangesBeanToSearchConverter extends
		TabularChangesBeanToSearchConverter<CCITabularChangesBean> {
	
	@Override
	public void convert(BeanWrapper wrapper, Search search,
			CriterionProvider criterionProvider,
			CriterionTypeProvider typeProvider) {
		super.convert(wrapper, search, criterionProvider, typeProvider);
		
		HierarchyType levelType = ((TabularChangesBean)wrapper.getWrappedInstance()).getLevel();
		
		String codeFromModel = null, codeToModel = null;
		
		if(levelType == HierarchyType.Group) {
			codeFromModel = CriterionModelConstants.GROUP_CODE_FROM;
			codeToModel = CriterionModelConstants.GROUP_CODE_TO;
		}
		else if(levelType == HierarchyType.Rubric) {
			codeFromModel = CriterionModelConstants.RUBRIC_CODE_FROM;
			codeToModel = CriterionModelConstants.RUBRIC_CODE_TO;
		}
		setValue(wrapper, "codeFrom", codeFromModel, search, criterionProvider, typeProvider);
		setValue(wrapper, "codeTo", codeToModel, search, criterionProvider, typeProvider);
		setValue(wrapper, "modifiedLanguage", CriterionModelConstants.MODIFIED_LANGUAGE, search, criterionProvider, typeProvider);
	}
}
