package ca.cihi.cims.web.controller.index;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jasig.cas.client.authentication.RegexUrlPatternMatcherStrategy;
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
import ca.cihi.cims.model.index.IndexCategoryReferenceModel;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.index.IndexCategoryReferenceBean;
import ca.cihi.cims.web.bean.index.IndexIcd1An2AndCCICodeValueReferencesBean;
import ca.cihi.cims.web.controller.tabular.TabularBasicInfoEditController;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/indexes/codereferences/editICD12CCI")
public class IndexIcd1And2AndCCICodeValueReferencesEditController extends IndexBaseController {

	public static final int DUMMY_ELEMENT_ID = 1;
	public static final String VIEW_BASE = "/classification/index/";
	public static final String VIEW_EDIT = VIEW_BASE + "indexCodeReferencesIcd1And2AndCci";
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;
	// ------------------------------------------------------------------

	public IndexIcd1An2AndCCICodeValueReferencesBean getBean(User user, long tabularId, String language,
			boolean loadDetails, HttpServletRequest request) {
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(language);
		IndexModel model = service.getIndexById(tabularId, lang);
		ChangeRequestPermission perm = service.getConceptInfoPermission(user, ChangeRequestCategory.I);
		IndexIcd1An2AndCCICodeValueReferencesBean bean = new IndexIcd1An2AndCCICodeValueReferencesBean();
		bean.setModel(model);
		bean.setEditable(perm.isCanWrite() && service.isIndexEditableShallow(model));
		if (loadDetails) {
			loadDetails(bean, lang);
		}
		return bean;
	}

	@ModelAttribute(ATTRIBUTE_BEAN)
	public IndexIcd1An2AndCCICodeValueReferencesBean loadBean(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language, @RequestParam("id") long indexId,
			HttpServletRequest request) {
		long start = System.currentTimeMillis();
		boolean loadDetails = request.getMethod().equals("GET");
		IndexIcd1An2AndCCICodeValueReferencesBean bean = getBean(user, indexId, language, loadDetails,request);
		if (config.isTracePerformanceEnabled()) {
			long loadTime = System.currentTimeMillis() - start;
			request.setAttribute(ATTRIBUTE_LOADTIME, loadTime);
			if (log.isDebugEnabled()) {
				log.debug("Index [" + indexId + "] load time: " + loadTime);
			}
		}
		return bean;
	}

	private void loadDetails(IndexIcd1An2AndCCICodeValueReferencesBean bean, Language lang) {
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		bean.setBreadCrumbs(getBreadcrumbs(bean.getElementId(), true));
		List<IndexCategoryReferenceBean> refs = new ArrayList<IndexCategoryReferenceBean>();
		{
			IndexCategoryReferenceBean dummy = new IndexCategoryReferenceBean();
			dummy.setMainElementId(DUMMY_ELEMENT_ID);
			refs.add(dummy);
		}
		for (IndexCategoryReferenceModel m : bean.getModel().getCategoryReferences()) {
			IndexCategoryReferenceBean b = new IndexCategoryReferenceBean();
			b.setMainElementId(m.getMainElementId());
			b.setMainCode(m.getMainCode());
			b.setMainCustomDescription(m.getMainCustomDescription());
			b.setMainDaggerAsterisk(m.getMainDaggerAsterisk());
			b.setPairedElementId(m.getPairedElementId());
			b.setPairedCode(m.getPairedCode());
			b.setPairedCustomDescription(m.getPairedCustomDescription());
			b.setPairedDaggerAsterisk(m.getPairedDaggerAsterisk());
			refs.add(b);
		}
		bean.setReferences(refs);

	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "language", defaultValue = "ENG") String language,
			@ModelAttribute(ATTRIBUTE_BEAN) IndexIcd1An2AndCCICodeValueReferencesBean bean, BindingResult result) {
		Language lang = Language.fromString(language);
		try {
			boolean icd1 = bean.getModel().isIcd1();
			List<IndexCategoryReferenceModel> list = new ArrayList<IndexCategoryReferenceModel>();
			Iterator<IndexCategoryReferenceBean> refs = bean.getReferences().iterator();
			while (refs.hasNext()) {
				IndexCategoryReferenceBean bb = refs.next();
				if (bb.getMainElementId() != DUMMY_ELEMENT_ID && !bb.isDeleted() && !bb.isBlank()) {
					IndexCategoryReferenceModel m = new IndexCategoryReferenceModel();
					m.setMainElementId(bb.getMainElementId());
					m.setMainCode(bb.getMainCode());
					m.setMainCustomDescription(bb.getMainCustomDescription());
					m.setMainDaggerAsterisk(bb.getMainDaggerAsterisk());
					if (icd1) {
						m.setPairedElementId(bb.getPairedElementId());
						m.setPairedCode(bb.getPairedCode());
						m.setPairedCustomDescription(bb.getPairedCustomDescription());
						m.setPairedDaggerAsterisk(bb.getPairedDaggerAsterisk());
					}
					list.add(m);
				}
			}
			IndexModel model = bean.getModel();
			model.setCategoryReferences(list);
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
	public String show(@ModelAttribute(value = ATTRIBUTE_BEAN) IndexIcd1An2AndCCICodeValueReferencesBean bean,
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
