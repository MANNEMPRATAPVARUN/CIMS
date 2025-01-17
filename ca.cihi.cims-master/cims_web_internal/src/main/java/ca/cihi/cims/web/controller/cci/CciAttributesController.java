package ca.cihi.cims.web.controller.cci;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@SessionAttributes( { CciAttributesCommon.cciAttributesForViewer })
@RequestMapping("/cciAttributes")
public class CciAttributesController extends CciAttributesCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());

	// ---------------------------------------------------------------------

	/*
	 * READ
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String read(HttpServletRequest request, HttpSession session, ModelMap model) {
		model.addAttribute("command", new CciAttributes());
		model.addAttribute(CciAttributesCommon.cciAttributesForViewer, new CciAttributes());
		return LIST_MAIN_PAGE;
	}

	/*
	 * UPDATE
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse update(Model model, @Valid CciAttributes viewerModel, BindingResult result) {
		ValidationResponse res = new ValidationResponse();
		if (result.hasErrors()) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(result.getFieldErrors());
		} else {
			FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
			boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;
			viewerModel.setContextFrozen(isContextFrozen);
			res.setContextFrozen(isContextFrozen);
		}
		model.addAttribute(CciAttributesCommon.cciAttributesForViewer, viewerModel);
		return res;
	}
}
