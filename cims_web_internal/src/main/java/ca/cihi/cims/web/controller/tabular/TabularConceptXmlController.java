package ca.cihi.cims.web.controller.tabular;

import static ca.cihi.cims.Language.ENGLISH;
import static ca.cihi.cims.Language.FRENCH;
import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.tabular.TabularConceptXmlModel;
import ca.cihi.cims.model.tabular.TabularConceptXmlType;
import ca.cihi.cims.service.ClassificationService;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.tabular.TabularXmlBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/tabulars/xml/edit")
public class TabularConceptXmlController {

	public static final String VIEW_BASE = "/classification/tabular/";
	public static final String VIEW_EDIT = VIEW_BASE + "xmlInfo";

	public static final String ATTRIBUTE_BEAN = "bean";

	@Autowired
	protected ClassificationService service;
	protected final Log log = LogFactory.getLog(getClass());
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	// ------------------------------------------------------------------

	@ModelAttribute(ATTRIBUTE_BEAN)
	public TabularXmlBean loadBean(
			HttpServletRequest request,
			@ModelAttribute(value = CURRENT_USER) User user, //
			@RequestParam("id") long tabularId, @RequestParam("tab") TabularConceptXmlType type,
			@RequestParam(value = "language", defaultValue = "ENG") String lang) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		TabularConceptXmlModel model = service.getTabularXml(tabularId, type);
		TabularXmlBean bean = new TabularXmlBean();
		bean.setModel(model);
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		Set<Language> languages = service.getChangeRequestLanguages();
		ChangeRequestPermission p = service.getConceptNonInfoPermission(user);
		if (languages.contains(ENGLISH)) {
			bean.setEnglishVisible(p.isCanRead());
			bean.setEnglishEditable(p.isCanWrite(Language.ENGLISH));
		}
		if (languages.contains(FRENCH)) {
			bean.setFrenchVisible(p.isCanRead());
			bean.setFrenchEditable(p.isCanWrite(Language.FRENCH));
		}
		bean.setSaveVisible(p.isCanWrite());
		return bean;
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String localeLanguage,
			@ModelAttribute(ATTRIBUTE_BEAN) TabularXmlBean bean, BindingResult result) {
		OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
		service.saveTabularXml(lock, new ErrorBuilder("model", result), user, bean.getModel());
		if (result.hasErrors()) {
			bean.setResult(BeanResult.INVALID);
		} else {
			bean.setResult(BeanResult.SUCCESS);
			bean.setLockTimestamp(lock.getTimestamp());
		}
		return VIEW_EDIT;
	}

	public void setService(ClassificationService service) {
		this.service = service;
	}

	@RequestMapping(method = GET)
	public String show() {
		return VIEW_EDIT;
	}

}
