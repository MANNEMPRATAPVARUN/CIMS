package ca.cihi.cims.web.controller.supplement;

import static ca.cihi.cims.WebConstants.CURRENT_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StopWatch;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.model.supplement.SupplementModel;
import ca.cihi.cims.validator.ErrorBuilder;
import ca.cihi.cims.web.bean.BeanResult;
import ca.cihi.cims.web.bean.supplement.SupplementAddBean;

@Controller
@SessionAttributes(CURRENT_USER)
@RequestMapping(value = "/supplements/children/add")
public class SupplementAddController extends SupplementBaseController {

	// ------------------------------------------------------------------

	@ModelAttribute(ATTRIBUTE_BEAN)
	public SupplementAddBean loadBean(HttpServletRequest request, @ModelAttribute(value = CURRENT_USER) User user,
			@RequestParam(value = "id", required = false) Long parentId,
			@RequestParam(value = "root", defaultValue = "false") boolean root,
			@RequestParam(value = "language", defaultValue = "ENG") String language) {
		Language lang = Language.fromString(language);
		StopWatch w = new StopWatch("loadBean: " + parentId);
		SupplementAddBean bean = loadBeanMinimal(parentId, user, lang);
		boolean loadDetails = request.getMethod().equals("GET");
		if (loadDetails) {
			loadDetails(bean, lang, false, w);
		}
		if (config.isTracePerformanceEnabled()) {
			request.setAttribute(ATTRIBUTE_LOADTIME, w.getTotalTimeMillis());
			request.setAttribute(ATTRIBUTE_LOADDETAILS, StringUtils.replace(w.toString(), ";", "\n"));
			if (log.isDebugEnabled()) {
				log.debug("Tabular [" + parentId + "] load time: " + w.getTotalTimeMillis());
			}
		}
		if (root) {
			bean.getModel().setLevel(1);
		}
		log.info(w);
		return bean;
	}

	private SupplementAddBean loadBeanMinimal(Long parentId, User user, Language lang) {
		SupplementModel model = new SupplementModel();
		// workaround
		model.setElementId(parentId);
		model.setStatus(ConceptStatus.ACTIVE);
		SupplementAddBean bean = new SupplementAddBean(model);
		bean.setEdit(false);
		bean.setEditable(true);
		bean.setStatusEditable(true);
		bean.setSaveVisible(true);
		return bean;
	}

	private void loadDetails(SupplementAddBean bean, Language lang, boolean trim, StopWatch w) {
		bean.setLockTimestamp(service.getChangeRequestTimestamp());
		bean.setBreadCrumbs(getBreadcrumbs(bean.getModel(), lang, trim));
	}

	@RequestMapping(method = POST)
	public String save(@ModelAttribute(value = CURRENT_USER) User user,
			@ModelAttribute(ATTRIBUTE_BEAN) SupplementAddBean bean,
			@RequestParam(value = "language", defaultValue = "ENG") String language, //
			BindingResult result) {
		Language lang = Language.fromString(language);
		StopWatch w = new StopWatch("save");

		SupplementModel formChild = bean.getModel();
		formChild.trimSpaces();

		SupplementModel child = new SupplementModel();
		child.setStatus(formChild.getStatus());
		child.setDescription(formChild.getDescription());
		child.setSortOrder(formChild.getSortOrder());
		child.setMatter(formChild.getMatter());
		child.setMarkup(formChild.getMarkup());

		ErrorBuilder errors = new ErrorBuilder("model", result);
		OptimisticLock lock = new OptimisticLock(bean.getLockTimestamp());
		try {
			service.createSupplement(lock, errors, user, formChild.getElementId(), child, lang);
			if (result.hasErrors()) {
				bean.setResult(BeanResult.INVALID);
			} else {
				formChild.setLevel(child.getLevel());
				formChild.setElementId(child.getElementId());
				bean.setResult(BeanResult.SUCCESS);
			}
		} catch (Exception ex) {
			log.error("Error saving", ex);
			bean.setResult(BeanResult.ERROR);
			bean.setErrorMessage(ex.getMessage());
		}
		loadDetails(bean, lang, true, w);
		log.info(w);
		return SupplementBasicInfoEditController.VIEW_EDIT;
	}

	@RequestMapping(method = GET)
	public String show() {
		return SupplementBasicInfoEditController.VIEW_EDIT;
	}

}
