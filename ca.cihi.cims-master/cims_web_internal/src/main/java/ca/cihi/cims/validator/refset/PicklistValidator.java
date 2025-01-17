package ca.cihi.cims.validator.refset;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.web.bean.refset.PickListViewBean;

@Component
public class PicklistValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return PickListViewBean.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object target, Errors result) {
		PickListViewBean viewBean = (PickListViewBean) target;
		if ((viewBean.getContextId() == null) || (viewBean.getElementId() == null)
				|| (viewBean.getElementVersionId() == null) || StringUtils.isEmpty(viewBean.getName())
				|| StringUtils.isEmpty(viewBean.getCode())
				|| StringUtils.isEmpty(viewBean.getClassificationStandard())) {
			result.rejectValue("refset.mandoryField", null, "Please fill the mandatory fields");
		}

	}

}
