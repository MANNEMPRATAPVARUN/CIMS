package ca.cihi.cims.web.controller.index;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexTermReferenceModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.index.IndexTermReferenceBean;
import ca.cihi.cims.web.bean.index.IndexTermReferencesBean;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/indexes/termreferences/edit")
public class IndexTermReferencesEditController extends IndexBaseController {

	public static final int DUMMY_ELEMENT_ID = 1;
	public static final String VIEW_BASE = "/classification/index/";
	public static final String VIEW_EDIT = VIEW_BASE + "indexTermReferences";
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;
	// ------------------------------------------------------------------

	public IndexTermReferencesBean getBean(User user, long tabularId, String language, boolean loadDetails, HttpServletRequest request) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(language);
		IndexModel model = service.getIndexById(tabularId, lang);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.I);
		IndexTermReferencesBean bean = new IndexTermReferencesBean();
		bean.setModel(model);
		bean.setEditable(perm.isCanWrite() && service.isIndexEditableShallow(model));
		if (loadDetails) {
			loadDetails(bean, lang);
		}
		return bean;
	}

	@RequestMapping("breadCrumbs")
	public @ResponseBody
	Map<Long, String> getBreadCrumbs(@RequestParam("ids") String idsString) {
		String[] ids = idsString.split(",");
		Map<Long, String> map = new HashMap<Long, String>(ids.length);
		for (String id : ids) {
			long elementId = Long.parseLong(id);
			map.put(elementId, getBreadcrumbsStarting2(elementId));
		}
		return map;
	}

	private String getBreadcrumbsStarting2(long elementId) {
		String breadcrumbs = getBreadcrumbs(elementId, false);
		int second = StringUtils.ordinalIndexOf(breadcrumbs, ">", 2);
		if (second != -1) {
			breadcrumbs = breadcrumbs.substring(second + 2);
		}
		return breadcrumbs;
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public IndexTermReferencesBean loadBean(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language, @RequestParam("id") long indexId,
			HttpServletRequest request) {
		long start = System.currentTimeMillis();
		boolean loadDetails = request.getMethod().equals("GET");
		IndexTermReferencesBean bean = getBean(user, indexId, language, loadDetails,request);
		if (config.isTracePerformanceEnabled()) {
			long loadTime = System.currentTimeMillis() - start;
			request.setAttribute(ATTRIBUTE_LOADTIME, loadTime);
			if (log.isDebugEnabled()) {
				log.debug("Index [" + indexId + "] load time: " + loadTime);
			}
		}
		return bean;
	}

	@Deprecated
	// FIXME: avoid double BreadCrumps calculation
	private void loadDetails(IndexTermReferencesBean bean, Language lang) {
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		bean.setBreadCrumbs(getBreadcrumbs(bean.getElementId(), true));
		List<IndexTermReferenceBean> refs = new ArrayList<IndexTermReferenceBean>();
		{
			IndexTermReferenceBean dummy = new IndexTermReferenceBean();
			dummy.setElementId(DUMMY_ELEMENT_ID);
			dummy.setCustomDescription("customDescription");
			dummy.setBreadCrumbs("breadCrumbs");
			refs.add(dummy);
		}
		for (IndexTermReferenceModel index : bean.getModel().getIndexReferences()) {
			IndexTermReferenceBean ref = new IndexTermReferenceBean();
			ref.setElementId(index.getElementId());
			ref.setCustomDescription(index.getCustomDescription());
			ref.setBreadCrumbs(getBreadcrumbsStarting2(ref.getElementId()));
			refs.add(ref);
		}
		bean.setReferences(refs);
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@ModelAttribute(ATTRIBUTE_BEAN) IndexTermReferencesBean bean, BindingResult result) {
		Language lang = Language.fromString(language);
		List<IndexTermReferenceModel> list = new ArrayList<IndexTermReferenceModel>();
		Iterator<IndexTermReferenceBean> refs = bean.getReferences().iterator();
		while (refs.hasNext()) {
			IndexTermReferenceBean r = refs.next();
			if (r.getElementId() != 0 && r.getElementId() != DUMMY_ELEMENT_ID && !r.isDeleted()) {
				IndexTermReferenceModel m = new IndexTermReferenceModel();
				m.setElementId(r.getElementId());
				m.setCustomDescription(r.getCustomDescription());
				list.add(m);
			}
		}
		IndexModel model = bean.getModel();
		model.setIndexReferences(list);
		OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
		service.saveIndex(lock, new ErrorBuilder("model", result), user, model, lang);
		if (result.hasErrors()) {
			bean.setResult(BeanResult.INVALID);
		} else {
			bean.setResult(BeanResult.SUCCESS);
		}
		loadDetails(bean, lang);
		return VIEW_EDIT;
	}

	@RequestMapping(method = GET)
	public String show(@ModelAttribute(value = ATTRIBUTE_BEAN) IndexTermReferencesBean bean,
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
