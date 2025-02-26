package ca.cihi.cims.validator;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.springframework.validation.BeanPropertyBindingResult;

import ca.cihi.cims.web.bean.report.ReportViewBean;

public class MissingValidationReportValidatorTest {

	@Test
	public void testValidate(){
		MissingValidationReportValidator validator = new MissingValidationReportValidator();
		ReportViewBean viewBean = new ReportViewBean();
		
		viewBean.setClassification("CCI");
		viewBean.setCodeFrom("1.AA.02.^^");
		viewBean.setCodeTo("1.BB.04.^^");
		
		validator.validate(viewBean, new BeanPropertyBindingResult(viewBean, "reportViewBean"));
		
		assertEquals(0, validator.getErrorMessages().size());
		
		viewBean.setCodeFrom("1.AA02.^^");
		viewBean.setCodeTo("1.BB.4.^^");
		
		validator.validate(viewBean, new BeanPropertyBindingResult(viewBean, "reportViewBean"));
		assertEquals(2, validator.getErrorMessages().size());
		
		viewBean.setClassification("ICD-10-CA");
		viewBean.setCodeFrom("1.AA.02.^^");
		viewBean.setCodeTo("1.BB.04.^^");
		
		validator.validate(viewBean, new BeanPropertyBindingResult(viewBean, "reportViewBean"));
		assertEquals(2, validator.getErrorMessages().size());
		
	}
}
