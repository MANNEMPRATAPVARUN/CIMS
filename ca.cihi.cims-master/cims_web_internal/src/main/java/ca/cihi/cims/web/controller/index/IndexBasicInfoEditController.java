package ca.cihi.cims.web.controller.index;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.IndexChildRules;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.index.IndexBasicInfoBean;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/indexes/basicInfo/edit")
public class IndexBasicInfoEditController extends IndexBaseController {

	public static final String VIEW_BASE = "/classification/index/";
	public static final String VIEW_EDIT = VIEW_BASE + "indexBasicInfo";
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	// ------------------------------------------------------------------
	public IndexBasicInfoBean getBean(User user, long tabularId, String language, boolean loadDetails, HttpServletRequest request) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(language);
		IndexModel model = service.getIndexById(tabularId, lang);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.I);
		IndexBasicInfoBean bean = new IndexBasicInfoBean(model);
		bean.setEdit(true);
		bean.setEditable(perm.isCanWrite() && service.isIndexEditableShallow(model));
		bean.setStatusEditable(bean.isEditable() && service.isIndexStatusEditable(model));
		bean.setStatusReadonly(bean.isStatusEditable() && service.isAddedInCurrentVersionYear(model));
		bean.setRemoveVisible(perm.isCanWrite() && service.isIndexDeletableShallow(model));
		bean.setSaveVisible(bean.isEditable());
		bean.setResetVisible(bean.isEditable());
		IndexChildRules rules = new IndexChildRules(model, service.isVersionYear());
		if (perm.isCanAdd() && rules.canAdd()) {
			bean.setAddVisible(true);
			bean.setAddType(rules.addableChild());
		}
		if (loadDetails) {
			loadDetails(bean, lang);
		}
		return bean;
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public IndexBasicInfoBean loadBean(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language, @RequestParam("id") long indexId,
			HttpServletRequest request) {
		long start = System.currentTimeMillis();
		boolean loadDetails = request.getMethod().equals("GET");
		IndexBasicInfoBean bean = getBean(user, indexId, language, loadDetails, request);
		if (config.isTracePerformanceEnabled()) {
			long loadTime = System.currentTimeMillis() - start;
			request.setAttribute(ATTRIBUTE_LOADTIME, loadTime);
			if (log.isDebugEnabled()) {
				log.debug("Index [" + indexId + "] load time: " + loadTime);
			}
		}
		return bean;
	}

	private void loadDetails(IndexBasicInfoBean bean, Language lang) {
		bean.setBreadCrumbs(getBreadcrumbs(bean.getElementId(), true));
		if (bean.getModel().isIcdSection4()) {
			bean.setSiteIndicators(service.getIndexSiteIndicators(lang));
		}
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@ModelAttribute(ATTRIBUTE_BEAN) IndexBasicInfoBean bean, BindingResult result, HttpServletRequest request) {
		Language lang = Language.fromString(language);
		IndexModel model = bean.getModel();
		model.trimSpaces();
		OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
		long changeRequestId = 0l;
		try {
			if (request.getParameter("changeRequestId") != null) {
				changeRequestId = Long.parseLong(request.getParameter("changeRequestId"));
			} else {
				changeRequestId = Long.parseLong(request.getParameter("ccp_rid"));
			}
		} catch (NumberFormatException e) {
			log.info("Can not get change requestId");
		}
		try {
			service.saveIndex(lock, new ErrorBuilder("model", result), user, model, lang);
			if (result.hasErrors()) {
				bean.setResult(BeanResult.INVALID);
			} else {
				bean.setNodeTitle(StringEscapeUtils.escapeJavaScript(service.getNodeTitle(model.getElementId(),
						language)));
				if (changeRequestService.isIncomplete(changeRequestId)) {
					bean.setResult(BeanResult.INCOMPLETE);
				} else {
					bean.setResult(BeanResult.SUCCESS);
				}
			}
		} catch (Exception ex) {
			log.error("Error saving", ex);
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
		}
		loadDetails(bean, lang);
		setDefaultBeanFlags(bean);
		return VIEW_EDIT;
	}

	private void setDefaultBeanFlags(IndexBasicInfoBean bean) {
		bean.setEdit(true);
	}

	@RequestMapping(method = GET)
	public String show(@ModelAttribute(value = ATTRIBUTE_BEAN) IndexBasicInfoBean bean,
			@RequestParam(value = "error", required = false) String error) {
		if (error != null) {
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(error);
		}
		return VIEW_EDIT;
	}

	@ExceptionHandler(UnsupportedElementExeption.class)
	public String showUnsupportedElementException() {
		return TabularBasicInfoEditController.VIEW_UNSUPPORTED;
	}

}
