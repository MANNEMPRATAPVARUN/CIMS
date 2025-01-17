package ca.cihi.cims.web.controller.refset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.exception.PropertyKeyNotFoundException;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.refset.RefsetStatusViewBean;

@Controller
@RequestMapping(value = "/refset/refsetStatus")
public class RefsetStatusController {
	private static final Log LOGGER = LogFactory.getLog(RefsetStatusController.class);
	private static final String LIST_REFSET_VIEW = "/refset/refsetStatus";
	private static final String UPDATE_REFSET_STATUS = "updateRefsetStatus";
	private static final String UPDATE_REFSET_STATUS_FILTER = "updateRefsetStatusFilter";
	
	private static final Map<String, String> UPDATE_STATUS_RESPONSE_MESSAGE_MAP = new HashMap<String, String>();
	
	static {
	    UPDATE_STATUS_RESPONSE_MESSAGE_MAP.put("A", "The Refset is Successfully Activated");
	    UPDATE_STATUS_RESPONSE_MESSAGE_MAP.put("D", "The Refset is Successfully Disabled");
	}

	@Autowired
	private RefsetService refsetService;

	private List<RefsetVersion> getRefsetVersionList(String status) {
		return refsetService.getAllRefsets(status);
	}

	public RefsetService getRefsetService() {
		return refsetService;
	}

	public void setRefsetService(RefsetService refsetService) {
		this.refsetService = refsetService;
	}

	@RequestMapping(method = RequestMethod.GET)
	public String setupForm(HttpServletRequest request, HttpSession session, ModelMap model) {
		LOGGER.debug("refsetStatusForm");

		RefsetStatusViewBean viewBean = null;

		RefsetStatusViewBean sessionViewBean = (RefsetStatusViewBean) session
				.getAttribute(WebConstants.REFSETSTATUS_VIEW_BEAN);
		if (sessionViewBean != null) {
			viewBean = sessionViewBean;
		} else {
			viewBean = new RefsetStatusViewBean();
			List<RefsetVersion> refsetVersions = getRefsetVersionList(null);
			viewBean.setRefsetVersionList(refsetVersions);
		}

		model.addAttribute(WebConstants.REFSETSTATUS_VIEW_BEAN, viewBean);

		return LIST_REFSET_VIEW;
	}

	@RequestMapping(value = UPDATE_REFSET_STATUS_FILTER, method = RequestMethod.POST)
	public String updateStatusFilter(@ModelAttribute(WebConstants.REFSETSTATUS_VIEW_BEAN) RefsetStatusViewBean viewBean,
			BindingResult result) {

		viewBean.setRefsetVersionList(getRefsetVersionList(viewBean.getStatusGroup()));

		return LIST_REFSET_VIEW;
	}

	@RequestMapping(value = UPDATE_REFSET_STATUS, method = RequestMethod.POST)
	public String updateStatus(HttpServletRequest request, HttpSession session, ModelMap model,
			final RefsetStatusViewBean refsetStastusViewBean, final BindingResult result) {

		ElementIdentifier elementIdentifier = new ElementIdentifier(
				Long.parseLong(refsetStastusViewBean.getElementId()),
				Long.parseLong(refsetStastusViewBean.getElementVersionId()));	
		
		try {
			refsetService.updateRefsetStatus(Long.parseLong(refsetStastusViewBean.getContextId()), elementIdentifier,
					Status.fromString(refsetStastusViewBean.getNewStatus()), result);
			if (result.hasErrors()) {
				LOGGER.debug("The refset (contextId=" + refsetStastusViewBean.getContextId() + ", elementId="
						+ refsetStastusViewBean.getElementId() + ", elementVersionId="
						+ refsetStastusViewBean.getElementVersionId()
						+ ") can not be disabled because its associated version is in open state.");
			} else {			    
			    String responseMessage = UPDATE_STATUS_RESPONSE_MESSAGE_MAP.get(refsetStastusViewBean.getNewStatus());			   
			    
			    if (responseMessage != null) {
			        result.reject("", responseMessage);    
			    }			    
			}

			refsetStastusViewBean.setRefsetVersionList(getRefsetVersionList(refsetStastusViewBean.getStatusGroup()));

		} catch (PropertyKeyNotFoundException e) {
			LOGGER.error("error occurred while updating refset status for contextId="
					+ refsetStastusViewBean.getContextId() + ", elementId=" + refsetStastusViewBean.getElementId()
					+ ", elementVersionId=" + refsetStastusViewBean.getElementVersionId() + ", status="
					+ refsetStastusViewBean.getNewStatus() + "error message: " + e.getMessage());
		}

		return LIST_REFSET_VIEW;
	}
}
