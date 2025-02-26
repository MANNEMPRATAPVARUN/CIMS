package ca.cihi.cims.web.controller.index;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
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
import ca.cihi.cims.model.index.DrugDetailType;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.NeoplasmDetailType;
import ca.cihi.cims.model.index.TabularReferenceModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.index.IndexIcd3An4CodeValueReferencesBean;
import ca.cihi.cims.web.bean.index.IndexTabularReferenceBean;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/indexes/codereferences/editICD34")
public class IndexIcd3And4CodeValueReferencesEditController extends IndexBaseController {

	public static final int DUMMY_ELEMENT_ID = -1;
	public static final String VIEW_BASE = "/classification/index/";
	public static final String VIEW_EDIT = VIEW_BASE + "indexCodeReferencesIcd3And4";
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;
	// ------------------------------------------------------------------

	private TabularReferenceModel fromReferenceBean(IndexTabularReferenceBean value) {
		if (StringUtils.isEmpty(value.getCode()) || value.getElementId() == 0
				|| value.getElementId() == DUMMY_ELEMENT_ID) {
			return null;
		} else {
			TabularReferenceModel model = new TabularReferenceModel();
			model.setCustomDescription(value.getCustomDescription());
			model.setElementId(value.getElementId());
			return model;
		}
	}

	public IndexIcd3An4CodeValueReferencesBean getBean(User user, long tabularId, String language, boolean loadDetails,HttpServletRequest request) {
		//Need to manually set context as HandlerInterceptor is called after @ModelAttribute resolution 
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(language);
		IndexModel model = service.getIndexById(tabularId, lang);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.I);
		IndexIcd3An4CodeValueReferencesBean bean = new IndexIcd3An4CodeValueReferencesBean();
		bean.setModel(model);
		bean.setEditable(perm.isCanWrite() && service.isIndexEditableShallow(model));
		if (loadDetails) {
			loadDetails(bean, lang);
		}
		return bean;
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public IndexIcd3An4CodeValueReferencesBean loadBean(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language, @RequestParam("id") long indexId,
			HttpServletRequest request) {
		long start = System.currentTimeMillis();
		boolean loadDetails = request.getMethod().equals("GET");
		IndexIcd3An4CodeValueReferencesBean bean = getBean(user, indexId, language, loadDetails,request);
		if (config.isTracePerformanceEnabled()) {
			long loadTime = System.currentTimeMillis() - start;
			request.setAttribute(ATTRIBUTE_LOADTIME, loadTime);
			if (log.isDebugEnabled()) {
				log.debug("Index [" + indexId + "] load time: " + loadTime);
			}
		}
		return bean;
	}

	private void loadDetails(IndexIcd3An4CodeValueReferencesBean bean, Language lang) {
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		bean.setBreadCrumbs(getBreadcrumbs(bean.getElementId(), true));
		Map<String, IndexTabularReferenceBean> references = new HashMap<String, IndexTabularReferenceBean>();
		IndexModel model = bean.getModel();
		if (model.isIcd3()) {
			for (DrugDetailType type : DrugDetailType.values()) {
				TabularReferenceModel m = model.getDrugsDetails().get(type);
				references.put(type.name(), toReferenceBean(m));
			}
		} else {
			for (NeoplasmDetailType type : NeoplasmDetailType.values()) {
				TabularReferenceModel m = model.getNeoplasmDetails().get(type);
				references.put(type.name(), toReferenceBean(m));
			}
		}
		bean.setReferences(references);
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@ModelAttribute(ATTRIBUTE_BEAN) IndexIcd3An4CodeValueReferencesBean bean, BindingResult result) {
		Language lang = Language.fromString(language);
		try {
			IndexModel model = bean.getModel();
			model.clearDetails();
			boolean icd3 = model.isIcd3();
			for (Map.Entry<String, IndexTabularReferenceBean> entry : bean.getReferences().entrySet()) {
				TabularReferenceModel m = fromReferenceBean(entry.getValue());
				if (m != null) {
					if (icd3) {
						model.getDrugsDetails().put(DrugDetailType.valueOf(entry.getKey()), m);
					} else {
						model.getNeoplasmDetails().put(NeoplasmDetailType.valueOf(entry.getKey()), m);
					}
				}
			}
			OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
			service.saveIndex(lock, new ErrorBuilder("model", result), user, model, lang);
			if (result.hasErrors()) {
				bean.setResult(BeanResult.INVALID);
			} else {
				bean.setResult(BeanResult.SUCCESS);
			}
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
		}
		loadDetails(bean, lang);
		return VIEW_EDIT;
	}

	@RequestMapping(method = GET)
	public String show(@ModelAttribute(value = ATTRIBUTE_BEAN) IndexIcd3An4CodeValueReferencesBean bean,
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

	private IndexTabularReferenceBean toReferenceBean(TabularReferenceModel m) {
		IndexTabularReferenceBean bean = new IndexTabularReferenceBean();
		if (m != null && m.getElementId() != DUMMY_ELEMENT_ID) {
			try {
				bean.setElementId(m.getElementId());
				bean.setCustomDescription(m.getCustomDescription());
				bean.setCode(service.getTabularConceptLightById(m.getElementId()).getCode());
			} catch (Exception ex) {
				log.error("Error looking up element id: " + m.getElementId(), ex);
			}
		}
		return bean;
	}

}
