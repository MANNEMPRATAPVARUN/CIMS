package ca.cihi.cims.web.controller.refset;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.validator.refset.RefsetValidator;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

/**
 *
 * @author lzhu
 *
 */
@RestController
public class RefsetConfigDetailController {
	private static final Log LOGGER = LogFactory.getLog(RefsetConfigDetailController.class);
	public static final String REFSET_CONFIG_VIEW = "refsetConfigDetail";
	public static final String REFSET_EDIT_VIEW = "refsetEditDetail";
	private static final String VIEW_BEAN = "viewBean";
	private static final int EFFECTIVE_YEAR_RANGE = 5;
	// private static final String STATUS_FAILED = "FAILED";
	private static final String STATUS_SUCCESS = "SUCCESS";

	@Autowired
	private RefsetService refsetService;
	@Autowired
	private RefsetValidator refsetValidator;

	public RefsetValidator getRefsetValidator() {
		return refsetValidator;
	}

	public void setRefsetValidator(RefsetValidator refsetValidator) {
		this.refsetValidator = refsetValidator;
	}

	public RefsetService getRefsetService() {
		return refsetService;
	}

	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	@RequestMapping(value = "/refset/refsetConfigDetail.htm", method = RequestMethod.GET)
	public ModelAndView setupForm() throws Exception {
		LOGGER.debug("inside setupForm method");
		ModelAndView mav = new ModelAndView();
		mav.setViewName(REFSET_CONFIG_VIEW);
		RefsetConfigDetailBean viewBean = new RefsetConfigDetailBean();
		mav.addObject(VIEW_BEAN, viewBean);
		mav.addObject("effectiveYearFromList", refsetService.getEffectiveYearFromList(EFFECTIVE_YEAR_RANGE));
		mav.addObject("effectiveYearToList", refsetService.getEffectiveYearToList(EFFECTIVE_YEAR_RANGE));
		// mav.addObject("ICD10CAYearList", refsetService.getICD10CAYearList());
		// mav.addObject("CCIYearList", refsetService.getCCIYearList());
		mav.addObject("ICD10CAContextInfoList", refsetService.getICD10CAContextInfoList());
		mav.addObject("CCIContextInfoList", refsetService.getCCIContextInfoList());
		mav.addObject("SCTVersionList", refsetService.getSCTVersionList());
		mav.addObject("CategoryList", refsetService.getCategoryList());

		LOGGER.debug(
				"refsetService.getICD10CAContextInfoList().size()=" + refsetService.getICD10CAContextInfoList().size());
		LOGGER.debug("refsetService.getCCIContextInfoList().size()=" + refsetService.getCCIContextInfoList().size());

		return mav;
	}

	@RequestMapping(value = "/refset/refsetConfigDetail", method = RequestMethod.POST)
	public RefsetResponse createRefset(final Model model,
			@ModelAttribute(VIEW_BEAN) final RefsetConfigDetailBean viewBean, final BindingResult result) {
		LOGGER.debug("inside createRefset");
		RefsetResponse response = new RefsetResponse();
		refsetValidator.validate(viewBean, result);

		if (result.hasErrors()) {
			LOGGER.debug("Validation error occurred during creating refset, will display error message");
			RefsetUtils.retrieveErrorMsg(result, response);
			return response;
		}

		Refset refset = null;
		try {
			LdapUserDetails user = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			refset = refsetService.createRefset(viewBean, user.getUsername());
			refsetService.setContextElementInfo(response, refset);
		} catch (DuplicateCodeNameException e) {
			LOGGER.error("Error occurred during createRefwet", e);
			ObjectError error = new ObjectError("refset.dupCode", "Refset code or name is duplicated");
			result.addError(error);
		} catch (Exception e) {
			LOGGER.error("Error occurred during createRefset", e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		}
		LOGGER.debug("going to set validation response");
		if (!result.hasErrors()) {
			LOGGER.debug("Successfully created refset, will directed to edit page");
			LOGGER.debug("response.getContextId()=" + response.getContextId());
			LOGGER.debug("response.getElementId()=" + response.getElementId());
			LOGGER.debug("response.getElementVersionId()=" + response.getElementVersionId());
			LOGGER.debug("response.getCategoryName()=" + response.getCategoryName());
			response.setStatus(STATUS_SUCCESS);
		} else {
			LOGGER.debug("Error occurred during creating refset, will display error message");
			RefsetUtils.retrieveErrorMsg(result, response);
		}
		return response;
	}

}
