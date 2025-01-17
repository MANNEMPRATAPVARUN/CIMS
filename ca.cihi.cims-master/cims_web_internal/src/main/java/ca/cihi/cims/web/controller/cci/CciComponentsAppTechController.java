package ca.cihi.cims.web.controller.cci;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;

@Controller
@RequestMapping("/appTechComponents")
public class CciComponentsAppTechController extends CciComponentsCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());
	public static final String LIST_COMPONENTS_APP_TECH = PATH_PREFIX + "/listAppTechComponents";
	public static final String PRINT_COMPONENTS_APP_TECH = PATH_PREFIX + "/printAppTechComponents";

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private DisplayTagUtilService dtService;

	// ---------------------------------------------------------------------

	/*
	 * Gets entire list of Components
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(HttpServletRequest request, HttpSession session, ModelMap model) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		List<CciComponentModel> compModels = auxService.getComponentsSQL(baseClassification, viewerModel
				.getVersionCode(), viewerModel.getSection(), viewerModel.getStatus(), "ApproachTechnique",
				"ApproachTechniqueToSectionCPV");
		// model.addAttribute("compModel", new CciComponentModel());
		model.addAttribute("components", compModels);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, compModels.size());
		if ("Y".equals(request.getParameter("print"))) {
			model.addAttribute("print", "Y");
			return PRINT_COMPONENTS_APP_TECH;
		} else {
			model.addAllAttributes(dtService.addForPageLinks(request, "componentTable"));
		}
		return LIST_COMPONENTS_APP_TECH;
	}

}
