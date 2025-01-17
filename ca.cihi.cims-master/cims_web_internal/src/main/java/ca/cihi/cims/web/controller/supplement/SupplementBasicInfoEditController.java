package ca.cihi.cims.web.controller.supplement;

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
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.supplement.SupplementChildRules;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.supplement.SupplementBasicInfoBean;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/supplements/basicInfo/edit")
public class SupplementBasicInfoEditController extends SupplementBaseController {

	public static final String VIEW_BASE = "/classification/supplement/";
	public static final String VIEW_EDIT = VIEW_BASE + "supplementBasicInfo";
	public static final String VIEW_ROOT = VIEW_BASE + "supplementRootInfo";

	private static final String ATTRIBUTE_CAN_ADD = "canAdd";
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	// ------------------------------------------------------------------

	public SupplementBasicInfoBean getBean(User user, long id, String language, boolean loadDetails,HttpServletRequest request) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(language);
		SupplementModel model = service.getSupplementById(id, lang);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.S);
		SupplementBasicInfoBean bean = new SupplementBasicInfoBean(model);
		bean.setEdit(true);
		bean.setEditable(perm.isCanWrite() && service.isSupplementEditableShallow(model));
		bean.setStatusEditable(bean.isEditable() && service.isSupplementStatusEditable(model));
		bean.setStatusReadonly(bean.isStatusEditable() && service.isAddedInCurrentVersionYear(model));
		bean.setRemoveVisible(perm.isCanWrite() && service.isSupplementDeletableShallow(model));
		bean.setSaveVisible(bean.isEditable());
		bean.setResetVisible(bean.isEditable());
		SupplementChildRules rules = new SupplementChildRules(model, service.isVersionYear());
		bean.setAddVisible(perm.isCanAdd() && rules.canAdd());
		if (loadDetails) {
			loadDetails(bean, lang);
		}
		bean.setYear(service.getCurrentChangeRequestYear());
		return bean;
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public SupplementBasicInfoBean loadBean(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language, @RequestParam("id") long id,
			HttpServletRequest request) {
		long start = System.currentTimeMillis();
		boolean loadDetails = request.getMethod().equals("GET");
		SupplementBasicInfoBean bean = getBean(user, id, language, loadDetails,request);
		if (config.isTracePerformanceEnabled()) {
			long loadTime = System.currentTimeMillis() - start;
			request.setAttribute(ATTRIBUTE_LOADTIME, loadTime);
			if (log.isDebugEnabled()) {
				log.debug("Supplement [" + id + "] load time: " + loadTime);
			}
		}
		return bean;
	}

	private void loadDetails(SupplementBasicInfoBean bean, Language lang) {
		bean.setBreadCrumbs(getBreadcrumbs(bean.getModel(), lang, true));
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@ModelAttribute(ATTRIBUTE_BEAN) SupplementBasicInfoBean bean, BindingResult result,
			HttpServletRequest request) {
		Language lang = Language.fromString(language);
		SupplementModel model = bean.getModel();
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
			service.saveSupplement(lock, new ErrorBuilder("model", result), user, model, lang);
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

	private void setDefaultBeanFlags(SupplementBasicInfoBean bean) {
		bean.setEdit(true);
	}

	@RequestMapping(method = GET)
	public String show(@ModelAttribute(value = ATTRIBUTE_BEAN) SupplementBasicInfoBean bean,
			@RequestParam(value = "error", required = false) String error) {
		if (error != null) {
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(error);
		}
		return VIEW_EDIT;
	}

	@ExceptionHandler(RootElementExeption.class)
	public String showRootException(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute(CURRENT_USER);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.S);
		boolean canAdd = perm.isCanAdd();
		if (canAdd) {
			request.setAttribute(ATTRIBUTE_CAN_ADD, canAdd);
			return VIEW_ROOT;
		} else {
			return TabularBasicInfoEditController.VIEW_BLANK;
		}
	}

	@ExceptionHandler(UnsupportedElementExeption.class)
	public String showUnsupportedElementException() {
		return TabularBasicInfoEditController.VIEW_UNSUPPORTED;
	}

}
