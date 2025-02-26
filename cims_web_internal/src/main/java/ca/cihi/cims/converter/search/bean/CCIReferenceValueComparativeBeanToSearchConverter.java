package ca.cihi.cims.converter.search.bean;

import org.springframework.beans.BeanWrapper;

import ca.cihi.cims.converter.search.util.CriterionModelConstants;
import ca.cihi.cims.converter.search.util.CriterionProvider;
import ca.cihi.cims.converter.search.util.CriterionTypeProvider;
import ca.cihi.cims.model.search.Search;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean;
import ca.cihi.cims.web.bean.search.CCIReferenceValueComparativeBean.ComparativeType;

/**
 * Converter implementation for transforming {@link CCIReferenceValueComparativeBean} objects into {@link Search}
 * 
 * @author rshnaper
 * 
 */
public class CCIReferenceValueComparativeBeanToSearchConverter extends
		SearchCriteriaBeanToSearchConverter<CCIReferenceValueComparativeBean> {

	@Override
	public void convert(BeanWrapper wrapper, Search search, CriterionProvider criterionProvider,
			CriterionTypeProvider critionTypeProvider) {
		setValue(wrapper, "contextId", CriterionModelConstants.YEAR, search, criterionProvider, critionTypeProvider);
		setValue(wrapper, "priorContextId", CriterionModelConstants.PRIOR_YEAR, search, criterionProvider,
				critionTypeProvider);
		ComparativeType cType = (ComparativeType) wrapper.getPropertyValue("comparativeType");
		setValue(cType.getCode(), CriterionModelConstants.COMPARATIVE_TYPE, search, criterionProvider,
				critionTypeProvider);
		setValue(wrapper, "attributeTypeId", CriterionModelConstants.ATTRIBUTE_TYPE, search, criterionProvider,
				critionTypeProvider);
	}
}
