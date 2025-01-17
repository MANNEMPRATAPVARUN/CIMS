package ca.cihi.cims.web.controller.tabular;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static ca.cihi.cims.model.Classification.CCI;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.web.bean.tabular.TabularValidationSetReportBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/tabulars/report/validationSets")
public class TabularValidationSetReportController extends TabularBaseController {

	public static final String VIEW = "/classification/tabular/validationSetsReport";
	
	@Autowired
	ContextProvider contextProvider;
	@Autowired
	CurrentContext currentContext;
	// ----------------------------------------------------------------

	@ModelAttribute(ATTRIBUTE_BEAN)
	public TabularValidationSetReportBean loadBean( //
			HttpServletRequest request,
			@ModelAttribute(value = CURRENT_USER) User user, //
			@RequestParam("id") long tabularId, //
			@RequestParam(value = "language", defaultValue = "ENG") String localeLanguage) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(localeLanguage);
		TabularValidationSetReportBean bean = new TabularValidationSetReportBean();
		TabularConceptModel concept = service.getTabularConceptById(tabularId, false);
		bean.setCode(concept.getCode());
		bean.setClassification(concept.getClassification());
		if (bean.getClassification() == CCI) {
			bean.setCciValidationSets(service.getCciValidationSets(tabularId, lang));
		} else {
			bean.setIcdValidationSets(service.getIcdValidationSets(tabularId, lang));
		}
		return bean;
	}

	@RequestMapping(method = GET)
	public String show() {
		return VIEW;
	}

}
