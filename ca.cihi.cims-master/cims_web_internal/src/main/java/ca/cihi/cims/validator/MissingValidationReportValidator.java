package ca.cihi.cims.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@Component
public class MissingValidationReportValidator implements Validator {

	private List<String> errorMessages;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ReportViewBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(!errors.hasErrors()){
			ReportViewBean viewBean = (ReportViewBean)target;
			errorMessages=new ArrayList<String>();
			String classification = viewBean.getClassification();
			
			CodeValidator codeValidator = new CodeValidator();
			
			if("ICD-10-CA".equals(classification)){
				String result = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeFrom(), "N", 1, false, "N");
				if(result!=null){
					result = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeFrom(), "N", 1, true, "N");
					if(result!=null)
						errorMessages.add("Code From: C## or ####/# format expected");
				}
				String result1 = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeTo(), "N", 1, false, "N");
				if(result1!=null){
					result1 = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeTo(), "N", 1, true, "N");
					if(result1!=null)
						errorMessages.add("Code To: C## or ####/# format expected");
				}
			}else if("CCI".equals(classification)){
				String result = codeValidator.validate(TabularConceptType.CCI_RUBRIC, viewBean.getCodeFrom().replace(".^^",""), "N", 1, false, "N");
				if(result!=null){
					errorMessages.add("Code From: "+result);
				}
				String result1 = codeValidator.validate(TabularConceptType.CCI_RUBRIC, viewBean.getCodeTo().replace(".^^",""), "N", 1, false, "N");
				if(result1!=null){
					errorMessages.add("Code To: "+result1);
				}
			}
		}
	}

}
