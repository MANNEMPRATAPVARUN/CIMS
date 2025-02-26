package ca.cihi.cims.web.controller.index;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.index.IndexModel;
import ca.cihi.cims.model.index.IndexType;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.index.IndexAddBean;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/indexes/children/add")
public class IndexAddController extends IndexBaseController {

	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;
	// ------------------------------------------------------------------

	@ModelAttribute(ATTRIBUTE_BEAN)
	public IndexAddBean loadBean(HttpServletRequest request,
			@ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "id", required = false) Long parentId, //
			@RequestParam(value = "type") IndexType type,
			@RequestParam(value = "language", defaultValue = "ENG") String language) {
		ContextDefinition definition = new CurrentContextParams().definition(request);
		ContextAccess context = contextProvider.findContext(definition);
		currentContext.makeCurrentContext(context);
		request.setAttribute("automaticContextParams", new CurrentContextParams()
				.urlParameters(context.getContextId()));
		Language lang = Language.fromString(language);
		StopWatch w = new StopWatch("loadBean: " + parentId);
		IndexAddBean bean = loadBeanMinimal(parentId, type, user, lang);
		boolean loadDetails = request.getMethod().equals("GET");
		if (loadDetails) {
			loadDetails(bean, false, w);
		}
		if (config.isTracePerformanceEnabled()) {
			request.setAttribute(ATTRIBUTE_LOADTIME, w.getTotalTimeMillis());
			request.setAttribute(ATTRIBUTE_LOADDETAILS, StringUtils.replace(w.toString(), ";", "\n"));
			if (log.isDebugEnabled()) {
				log.debug("Tabular [" + parentId + "] load time: " + w.getTotalTimeMillis());
			}
		}
		log.info(w);
		return bean;
	}

	private IndexAddBean loadBeanMinimal(Long parentId, IndexType type, User user, Language lang) {
		IndexModel model = new IndexModel();
		// workaround
		model.setElementId(parentId);
		model.setStatus(ConceptStatus.ACTIVE);
		model.setType(type);

		IndexAddBean bean = new IndexAddBean(model);
		bean.setEdit(false);
		bean.setEditable(true);
		bean.setStatusEditable(true);
		bean.setSaveVisible(true);
		return bean;
	}

	private void loadDetails(IndexAddBean bean, boolean trim, StopWatch w) {
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		bean.setBreadCrumbs(getBreadcrumbs(bean.getElementId(), trim));
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@ModelAttribute(ATTRIBUTE_BEAN) IndexAddBean bean,
			@RequestParam(value = "language", defaultValue = "ENG") String language, //
			BindingResult result) {
		Language lang = Language.fromString(language);
		StopWatch w = new StopWatch("save");

		IndexModel formChild = bean.getModel();
		formChild.trimSpaces();

		IndexModel child = new IndexModel();
		child.setNote(formChild.getNote());
		child.setStatus(formChild.getStatus());
		child.setDescription(formChild.getDescription());

		ErrorBuilder errors = new ErrorBuilder("model", result);
		OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
		try {
			service.createIndex(lock, errors, user, formChild.getElementId(), child, lang);
			if (result.hasErrors()) {
				bean.setResult(BeanResult.INVALID);
			} else {
				formChild.setElementId(child.getElementId());
				bean.setResult(BeanResult.SUCCESS);
			}
		} catch (Exception ex) {
			log.error("Error saving", ex);
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
		}
		loadDetails(bean, true, w);
		log.info(w);
		return IndexBasicInfoEditController.VIEW_EDIT;
	}

	@RequestMapping(method = GET)
	public String show() {
		return IndexBasicInfoEditController.VIEW_EDIT;
	}

}
