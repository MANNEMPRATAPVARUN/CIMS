package ca.cihi.cims.web.controller.tabular;

import static ca.cihi.cims.Language.ENGLISH;
import static ca.cihi.cims.Language.FRENCH;
import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static ca.cihi.cims.model.Classification.ICD;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.CCI_GROUP;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_BLOCK;
import static ca.cihi.cims.model.tabular.TabularConceptType.ICD_CATEGORY;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import ca.cihi.cims.content.cci.CciInvasivenessLevel;
import ca.cihi.cims.exception.RootElementExeption;
import ca.cihi.cims.exception.UnsupportedElementExeption;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.access.ChangeRequestPermission;
import ca.cihi.cims.model.changerequest.ChangeRequestCategory;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.tabular.TabularConceptChildRules;
import ca.cihi.cims.model.tabular.TabularConceptModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.tabular.TabularBasicInfoBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/tabulars/basicInfo/edit")
public class TabularBasicInfoEditController extends TabularBaseController {

	private static final String VIEW_BASE = "/classification/tabular/";
	private static final String VIEW_EDIT = VIEW_BASE + "tabularBasicInfo";
	public static final String VIEW_ROOT = VIEW_BASE + "tabularRootInfo";
	public static final String VIEW_BLANK = VIEW_BASE + "blankInfo";
	public static final String VIEW_UNSUPPORTED = VIEW_BASE + "unsupportedInfo";

	private static final String ATTRIBUTE_CLASSIFICATION = "classification";
	private static final String ATTRIBUTE_CAN_ADD = "canAdd";
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;
	// ------------------------------------------------------------------

	public TabularBasicInfoBean getBean(User user, long tabularId,HttpServletRequest request) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		TabularConceptModel model = service.getTabularConceptById(tabularId);
		TabularBasicInfoBean bean = new TabularBasicInfoBean(model);
		bean.setLockTimestamp(service.getChangeRequestTimestamp());

		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.T);
		bean.setEditable(perm.isCanWrite());
		bean.setEnglishEditable(perm.isCanWrite(ENGLISH));
		bean.setFrenchEditable(perm.isCanWrite(FRENCH));
		bean.setEdit(true);
		bean.setCodeVisible(true);
		bean.setCodeEditable(service.isTabularCodeEditable(model));
		bean.setUserTitleEditable(service.isUserTitleEditable(model));
		if (model.isIcdCategory()) {
			bean.setDaggerAsteriskVisible(true);
			bean.setDaggerAsteriskEditable(service.isDaggerAsteriskEditable(model));
			bean.setDaggerAsteriskTypes(service.getDaggerAsteriskTypes());
			bean.setCanadianEnhancementVisible(true);
			bean.setCanadianEnhancementEditable(service.isCanadianEnhancementEditable(model));
		}
		bean.setStatusVisible(bean.isEdit());
		bean.setStatusEditable(service.isTabularStatusEditable(model));
		if (model.isCciCode()) {
			bean.setInvasivenessLevelVisible(true);
			bean.setInvasivenessLevelEditable(perm.isCanWrite());
			// TODO: we must load details only if GET or POST is failed
			Map<Long, String> map = new TreeMap<Long, String>();
			map.put(-1L, "");
			for (CciInvasivenessLevel level : service.getCciInvasivenessLevels()) {
				map.put(level.getElementId(), level.getDescription(ENGLISH.getCode()));
			}
			bean.setCciInvasivenessLevels(map);
		}
		if (model.isCciRubric() || model.isIcdCategory()) {
			bean.setChildTableVisible(true);
			bean.setChildTable(model.isChildTable());
		}
		Set<Language> languages = service.getChangeRequestLanguages();
		bean.setEnglishVisible(languages.contains(ENGLISH));
		bean.setFrenchVisible(languages.contains(FRENCH));
		boolean bilingualRequest = languages.containsAll(Language.ALL);

		bean.setRemoveVisible(perm.isCanDelete() && bilingualRequest);
		bean.setSaveVisible(bean.isEditable());
		bean.setResetVisible(bean.isEditable());
		bean.setReferenceLinksVisible(!bean.isAdd() && !(model.isCciSection() || model.isIcdChapter()));
		bean.setAddQualifierVisible(false);
		if (perm.isCanAdd() && bilingualRequest) {
			TabularConceptChildRules rules = new TabularConceptChildRules(model, service.isVersionYear());
			if (model.getClassification() == ICD) {
				boolean canAddBlock = rules.canAdd(ICD_BLOCK);
				boolean canAddCategory = rules.canAdd(ICD_CATEGORY);
				if (canAddBlock && canAddCategory) {
					bean.setAddBlockType(ICD_BLOCK);
					bean.setAddCategoryType(ICD_CATEGORY);
				} else {
					bean.setAddCodeVisible(canAddBlock || canAddCategory);
					bean.setAddType(rules.addableChild());
				}
			} else {
				if (rules.canAdd()) {
					if (model.getType() == CCI_BLOCK) {
						boolean canAddBlock = rules.canAdd(CCI_BLOCK);
						boolean canAddGroup = rules.canAdd(CCI_GROUP);
						if (canAddBlock && canAddGroup) {
							bean.setAddBlockType(CCI_BLOCK);
							bean.setAddGroupType(CCI_GROUP);
						} else {
							bean.setAddCodeVisible(true);
							bean.setAddType(rules.addableChild());
						}
					} else {
						bean.setAddCodeVisible(true);
						bean.setAddType(rules.addableChild());
					}
				}
			}
		}
		return bean;
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public TabularBasicInfoBean loadBean(@ModelAttribute(CURRENT_USER) User user, @RequestParam("id") long tabularId,
			HttpServletRequest request) {
		long start = System.currentTimeMillis();
		TabularBasicInfoBean bean = getBean(user, tabularId,request);
		if (config.isTracePerformanceEnabled()) {
			long loadTime = System.currentTimeMillis() - start;
			request.setAttribute(ATTRIBUTE_LOADTIME, loadTime);
			if (log.isDebugEnabled()) {
				log.debug("Tabular [" + tabularId + "] load time: " + loadTime);
			}
		}
		return bean;
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@ModelAttribute(ATTRIBUTE_BEAN) TabularBasicInfoBean bean, BindingResult result, HttpServletRequest request) {
		TabularConceptModel model = bean.getModel();
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
			service.saveTabular(lock, new ErrorBuilder("model", result), user, model);
			if (result.hasErrors()) {
				bean.setResult(BeanResult.INVALID);
			} else {
				bean.setLockTimestamp(lock.getTimestamp());
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
		setDefaultBeanFlags(bean);
		return VIEW_EDIT;
	}

	private void setDefaultBeanFlags(TabularBasicInfoBean bean) {
		bean.setEdit(true);
	}

	@RequestMapping(method = GET)
	public String show(@ModelAttribute(value = ATTRIBUTE_BEAN) TabularBasicInfoBean bean,
			@RequestParam(value = "error", required = false) String error, HttpSession session) {
		if (error != null) {
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(error);
		}
		return VIEW_EDIT;
	}

	@ExceptionHandler(RootElementExeption.class)
	public String showRootException(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute(CURRENT_USER);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.T);
		boolean canAdd = perm.isCanAdd() && service.getChangeRequestLanguages().containsAll(Language.ALL);
		if (canAdd) {
			request.setAttribute(ATTRIBUTE_CAN_ADD, canAdd);
			request.setAttribute(ATTRIBUTE_CLASSIFICATION, service.getCurrentClassification());
			return VIEW_ROOT;
		} else {
			return VIEW_BLANK;
		}
	}

	@ExceptionHandler(UnsupportedElementExeption.class)
	public String showUnsupportedElementException() {
		return VIEW_UNSUPPORTED;
	}

}
