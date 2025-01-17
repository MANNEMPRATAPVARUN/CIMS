package ca.cihi.cims.web.controller.tabular;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.web.bean.tabular.TabularReferenceReportBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@RequestMapping(value = "/tabulars/report/referenceLinks")
public class TabularReferenceLinksReportController extends TabularBaseController {

	public static final String VIEW = "/classification/tabular/referenceLinksReport";

	@Autowired
	private ViewService viewService;
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	// ----------------------------------------------------------------

	@ModelAttribute(ATTRIBUTE_BEAN)
	public TabularReferenceReportBean loadBean(@RequestParam("code") String codeValue, HttpServletRequest request) {
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		long contextId = service.getCurrentContextId();
		String calssification = service.getCurrentBaseClassification();
		TabularReferenceReportBean bean = new TabularReferenceReportBean();
		bean.setCodeValue(codeValue);
		bean.setIndexReferencedLinks(viewService.getIndexBookReferencedLinks(contextId, codeValue, calssification));
		bean.setTabularReferencedLinks(viewService.getTabularReferencedLinks(contextId, codeValue, calssification));
		bean.setSupplementReferencedLinks(viewService
				.getSupplementReferencedLinks(contextId, codeValue, calssification));
		return bean;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	@RequestMapping(method = GET)
	public String show() {
		return VIEW;
	}

}
