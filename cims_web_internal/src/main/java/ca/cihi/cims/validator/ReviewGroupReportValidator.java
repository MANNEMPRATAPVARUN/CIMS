package ca.cihi.cims.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.cihi.cims.model.tabular.TabularConceptType;
import ca.cihi.cims.web.bean.report.ReviewGroupReportViewBean;

@Component
public class ReviewGroupReportValidator implements Validator {

	private List<String> errorMessages;

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(List<String> errorMessages) {
		this.errorMessages = errorMessages;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ReviewGroupReportViewBean.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		if(!errors.hasErrors()){
			ReviewGroupReportViewBean viewBean = (ReviewGroupReportViewBean)target;
			errorMessages=new ArrayList<String>();
			String classification = viewBean.getClassification();
			String requestCategory = viewBean.getRequestCategory();
			CodeValidator codeValidator = new CodeValidator();

			if("ICD-10-CA".equals(classification)&&"Tabular".equals(requestCategory)){
                if (viewBean.getCodeFrom()!=null&&!viewBean.getCodeFrom().equals("")) {
				    String result = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeFrom(), "N", 1, false, "N");
				    if(result!=null){
				    	result = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeFrom(), "N", 1, true, "N");
				    	if(result!=null){
				    		errorMessages.add("Code From: C## or ####/# format expected");
				    	}
				    }
			    }
                if (viewBean.getCodeTo()!=null&&!viewBean.getCodeTo().equals("")) {
				    String result1 = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeTo(), "N", 1, false, "N");
				    if(result1!=null){
				    	result1 = codeValidator.validate(TabularConceptType.ICD_CATEGORY, viewBean.getCodeTo(), "N", 1, true, "N");
				    	if(result1!=null)
				    		errorMessages.add("Code To: C## or ####/# format expected");
				    }
			    }

			}else if("CCI".equals(classification)&&"Tabular".equals(requestCategory)){
                if (viewBean.getCodeFrom()!=null&&!viewBean.getCodeFrom().equals("")) {
				    String result = codeValidator.validate(TabularConceptType.CCI_RUBRIC, viewBean.getCodeFrom().replace(".^^",""), "N", 1, false, "N");
				    if(result!=null){
					    errorMessages.add("Code From: "+result);
				    }
			    }
                if (viewBean.getCodeTo()!=null&&!viewBean.getCodeTo().equals("")) {
				    String result1 = codeValidator.validate(TabularConceptType.CCI_RUBRIC, viewBean.getCodeTo().replace(".^^",""), "N", 1, false, "N");
				    if(result1!=null){
					    errorMessages.add("Code To: "+result1);
				    }
			    }
			}

            if ( (viewBean.getCodeFrom()!=null&&!viewBean.getCodeFrom().equals("")) &&
			     (viewBean.getCodeTo()==null||viewBean.getCodeTo().equals("")) ) {
			    errorMessages.add("Code From: must be blank when Code To is blank");
			}

            if ( (viewBean.getCodeTo()!=null&&!viewBean.getCodeTo().equals("")) &&
				 (viewBean.getCodeFrom()==null||viewBean.getCodeFrom().equals("")) ) {
			    errorMessages.add("Code To: must be blank when Code From is blank");
			}

			if("Index".equals(requestCategory)){
				if (viewBean.getIndexBook()!=null&&!viewBean.getIndexBook().equals("")&&viewBean.getLeadTerm().equals("")) {
				    errorMessages.add("Lead Term: cannot be blank when index book is not blank");
				}
				if ((viewBean.getIndexBook()==null||viewBean.getIndexBook().equals(""))&&!viewBean.getLeadTerm().equals("")) {
				    errorMessages.add("Lead Term: must be blank when Index Book is blank");
				}
			}

		}
	}

}
