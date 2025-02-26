package ca.cihi.cims.validator.refset;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.web.bean.refset.PickListColumnBean;

@Component
public class ColumnValidator implements Validator {

	@Override
	public boolean supports(Class<?> arg0) {
		return PickListColumnBean.class.isAssignableFrom(arg0);
	}

	@Override
	public void validate(Object target, Errors result) {
		PickListColumnBean columnBean = (PickListColumnBean) target;
		if (columnBean.getContextId() == null) {
			result.reject("refset.systemError", "Please provide contextId, system error!");
		}
		if ((columnBean.getColumnOrder() == null) || (columnBean.getColumnType() == null)
				|| StringUtils.isEmpty(columnBean.getRevisedColumnName())) {
			result.reject("refset.mandatoryField", "Please fill the mandatory fields!");
		}

	}

}
