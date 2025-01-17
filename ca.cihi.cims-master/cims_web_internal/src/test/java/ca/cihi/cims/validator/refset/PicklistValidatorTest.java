package ca.cihi.cims.validator.refset;

import org.springframework.validation.Errors;
import org.springframework.validation.MapBindingResult;
import org.springframework.validation.Validator;

import ca.cihi.cims.web.bean.refset.PickListViewBean;

import java.util.HashMap;

import org.junit.Test;

public class PicklistValidatorTest {

	@Test
	public void test() {
		Validator validator = new PicklistValidator();

		Errors errors = new MapBindingResult(new HashMap(), "map");

		PickListViewBean viewBean = new PickListViewBean();

		validator.validate(viewBean, errors);

		viewBean.setContextId(1l);
		viewBean.setElementId(1l);
		viewBean.setElementVersionId(1l);
		viewBean.setName("New PickList");
		viewBean.setCode("New Code");
		viewBean.setClassificationStandard("ICD-10-CA");

		validator.validate(viewBean, errors);
	}
}
