package ca.cihi.cims.web.controller.refset;

import javax.servlet.http.HttpServletRequest;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.model.refset.ActionType;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.validator.refset.RefsetValidator;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;
import ca.cihi.cims.web.rule.refset.RefsetEditRule;

/**
 *
 * @author lzhu
 *
 */
@RestController
public class RefsetEditDetailController {
	private static final Log LOGGER = LogFactory.getLog(RefsetEditDetailController.class);
	public static final String REFSET_EDIT_VIEW = "refsetEditDetail";
	private static final String VIEW_BEAN = "viewBean";
	// private static final String STATUS_FAILED = "FAILED";
	private static final String STATUS_SUCCESS = "SUCCESS";
	private static final int EFFECTIVE_YEAR_RANGE = 5;

	@Autowired
	private RefsetService refsetService;
	@Autowired
	private RefsetValidator refsetValidator;
	@Autowired
	private RefsetEditRule refsetEditRule;

	public RefsetEditRule getRefsetEditRule() {
		return refsetEditRule;
	}

	public void setRefsetEditRule(RefsetEditRule refsetEditRule) {
		this.refsetEditRule = refsetEditRule;
	}

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

	@RequestMapping(value = "/refset/refsetEditDetail.htm", method = RequestMethod.GET)
	public ModelAndView setupForm(@RequestParam("contextId") Long contextId, @RequestParam("elementId") Long elementId,
			@RequestParam("elementVersionId") Long elementVersionId, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(REFSET_EDIT_VIEW);
		RefsetConfigDetailBean viewBean = new RefsetConfigDetailBean();
		viewBean.setContextId(contextId);
		viewBean.setElementId(elementId);
		viewBean.setElementVersionId(elementVersionId);
		if ((contextId != null) && (elementId != null) && (elementVersionId != null)) {
			refsetService.populateDataFromRefset(contextId, elementId, elementVersionId, viewBean);
			LdapUserDetails user = (LdapUserDetails) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			refsetService.checkPermission(user, viewBean);
			refsetService.checkPickListContent(viewBean);
			refsetEditRule.applyRule(request, viewBean);
		}
		mav.addObject("activeTab", "refsetConfig");
		mav.addObject(VIEW_BEAN, viewBean);
		mav.addObject("CategoryList", refsetService.getCategoryList());
		mav.addObject("allAssigneeRecipents", refsetService.getRefsetAssigneeRecipents(viewBean.getAssignee()));

		mav.addObject("effectiveYearFromList", refsetService.getEffectiveYearFromList(EFFECTIVE_YEAR_RANGE));
		mav.addObject("effectiveYearToList", refsetService.getEffectiveYearToList(EFFECTIVE_YEAR_RANGE));
		// mav.addObject("ICD10CAYearList", refsetService.getICD10CAYearList());
		// mav.addObject("CCIYearList", refsetService.getCCIYearList());
		mav.addObject("ICD10CAContextInfoList", refsetService.getICD10CAContextInfoList());
		mav.addObject("CCIContextInfoList", refsetService.getCCIContextInfoList());
		mav.addObject("SCTVersionList", refsetService.getSCTVersionList());

		return mav;
	}

	@RequestMapping(value = "/refset/refsetEditDetail", method = RequestMethod.POST)
	public RefsetResponse doRefset(final Model model, @ModelAttribute(VIEW_BEAN) final RefsetConfigDetailBean viewBean,
			final BindingResult result) {
		LOGGER.debug("inside doRefset");

		LOGGER.debug("contextId=" + viewBean.getContextId() + " elementId=" + viewBean.getElementId()
				+ "elementVersionId=" + viewBean.getElementVersionId());
		LOGGER.debug("viewbean.getRefsetNameFRE()=" + viewBean.getRefsetNameFRE());
		LOGGER.debug("viewbean.getCategoryId()=" + viewBean.getCategoryId());
		LOGGER.debug("viewbean.getDefinition()=" + viewBean.getDefinition());
		LOGGER.debug("viewbean.getNotes()=" + viewBean.getNotes());
		ActionType actionType = viewBean.getActionType();
		String newAssignee = viewBean.getNewAssignee();
		LOGGER.debug("actionType=" + actionType);
		LOGGER.debug("newAssignee=" + newAssignee);
		RefsetResponse response = new RefsetResponse();

		try {
			switch (actionType) {
			case SAVE:
				refsetValidator.validateEditPage(viewBean, result);
				if (result.hasErrors()) {
					LOGGER.debug("Validation error occurred during saving refset, will display error message");
					RefsetUtils.retrieveErrorMsg(result, response);
					return response;
				}
				if (refsetService.isAssigneeRevoked(viewBean, viewBean.getDisplayAssignee())) {
					RefsetUtils.retrieveErrorMsg(RefsetUtils.ASSIGNEE_REVOKED_MESSAGE, response);
					response.setErrorType(RefsetUtils.ERROR_TYPE_ASSIGNEE_REVOKED);
					return response;
				}
				refsetService.updateRefset(viewBean);
				break;
			case ASSIGN:
				refsetService.assignTo(viewBean, newAssignee);
				break;
			case DROP:
				if (refsetService.isAssigneeRevoked(viewBean, viewBean.getDisplayAssignee())) {
					RefsetUtils.retrieveErrorMsg(RefsetUtils.ASSIGNEE_REVOKED_MESSAGE, response);
					response.setErrorType(RefsetUtils.ERROR_TYPE_ASSIGNEE_REVOKED);
					return response;
				}
				refsetService.removeRefset(viewBean);
				break;
			case CLOSE:
				if (refsetService.isAssigneeRevoked(viewBean, viewBean.getDisplayAssignee())) {
					RefsetUtils.retrieveErrorMsg(RefsetUtils.ASSIGNEE_REVOKED_MESSAGE, response);
					response.setErrorType(RefsetUtils.ERROR_TYPE_ASSIGNEE_REVOKED);
					return response;
				}
				if (!refsetService.picklistExists(viewBean)) {
					RefsetUtils.retrieveErrorMsg(RefsetUtils.REFSET_NO_PICKLIST_MESSAGE, response);
					response.setErrorType(RefsetUtils.ERROR_TYPE_NO_PICKLIST);
					return response;
				}
				refsetService.closeRefsetVersion(viewBean);
				break;
			case CREATE:
				refsetValidator.validateCreate(viewBean, result);
				if (result.hasErrors()) {
					LOGGER.debug("Validation error occurred during saving refset, will display error message");
					RefsetUtils.retrieveErrorMsg(result, response);
					return response;
				}
				if (refsetService.isAssigneeRevoked(viewBean, viewBean.getDisplayAssignee())) {
					RefsetUtils.retrieveErrorMsg(RefsetUtils.ASSIGNEE_REVOKED_MESSAGE, response);
					response.setErrorType(RefsetUtils.ERROR_TYPE_ASSIGNEE_REVOKED);
					return response;
				}
				refsetService.createNewVersion(viewBean);
				response.setContextId(viewBean.getContextId());
				response.setElementId(viewBean.getElementId());
				response.setElementVersionId(viewBean.getElementVersionId());
				break;
			}
		} catch (DuplicateCodeNameException e) {
			LOGGER.error("Error occurred for this action" + actionType, e);
			ObjectError error = new ObjectError("refset.duplicateName", "DuplicatCodeNameException");
			result.addError(error);
		} catch (PropertyKeyNotFoundException e) {
			LOGGER.error("Error occurred for this action" + actionType, e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		} catch (Exception e) {
			LOGGER.error("Error occurred for this action" + actionType, e);
			ObjectError error = new ObjectError("refset.systemError", "System error occurred");
			result.addError(error);
		}

		if (!result.hasErrors()) {
			LOGGER.debug("Successfully " + actionType);
			response.setStatus(STATUS_SUCCESS);
		} else {
			LOGGER.debug("Error occurred during " + actionType + " creating refset, will display error message");
			RefsetUtils.retrieveErrorMsg(result, response);
		}

		LOGGER.debug("doRefset completed");
		return response;
	}

}
