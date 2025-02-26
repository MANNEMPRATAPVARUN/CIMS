package ca.cihi.cims.web.controller.cci;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciAgentGroup;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/daComponents")
public class CciComponentsDaController extends CciComponentsCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());
	public static final String LIST_COMPONENTS_DEVICE_AGENT = PATH_PREFIX + "/listDAComponents";
	public static final String PRINT_COMPONENTS_DEVICE_AGENT = PATH_PREFIX + "/printDAComponents";
	public static final String LIST_COMPONENTS_DEVICE_AGENT_DIAGRAM = PATH_PREFIX + "/listDADiagram";

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private DisplayTagUtilService dtService;
	@Autowired
	private ContextProvider contextProvider;

	// ---------------------------------------------------------------------

	/*
	 * Gets entire list of Components
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String list(HttpServletRequest request, HttpSession session, ModelMap model) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		List<CciComponentModel> compModels = auxService.getComponentsSQL(baseClassification, viewerModel
				.getVersionCode(), viewerModel.getSection(), viewerModel.getStatus(), "DeviceAgent",
				"DeviceAgentToSectionCPV");
		// model.addAttribute("compModel", new CciComponentModel());
		model.addAttribute("components", compModels);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, compModels.size());
		if ("Y".equals(request.getParameter("print"))) {
			model.addAttribute("print", "Y");
			return PRINT_COMPONENTS_DEVICE_AGENT;
		} else {
			model.addAllAttributes(dtService.addForPageLinks(request, "componentTable"));
		}
		return LIST_COMPONENTS_DEVICE_AGENT;
	}

	/*
	 * Gets one component and loads the diagram info
	 */
	@RequestMapping(value = "/diagram", method = RequestMethod.GET)
	public String listDiagram(ModelMap model, HttpServletRequest request, HttpSession session,
			@RequestParam("e") long elementId) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
		ContextAccess context = contextProvider.findContext(cd);
		CciDeviceAgentComponent obj = context.load(elementId);
		CciAgentGroup ag = obj.getAgentGroup();
		model.addAttribute("groupCode", ag == null ? null : ag.getElementId());
		model.addAttribute("viewer", viewerModel);
		model.addAttribute("section", viewerModel.getSection());
		model.addAttribute("versionCode", viewerModel.getVersionCode());
		model.addAttribute("atcCode", obj.getAgentATCCode());
		model.addAttribute("agentExampleEng", obj.getAgentExample(Language.ENGLISH.getCode()));
		model.addAttribute("agentExampleFra", obj.getAgentExample(Language.FRENCH.getCode()));
		model.addAttribute("agentTypeEng", obj.getAgentTypeDescription(Language.ENGLISH.getCode()));
		model.addAttribute("agentTypeFra", obj.getAgentTypeDescription(Language.FRENCH.getCode()));
		model.addAttribute("code", obj.getCode());
		model.addAttribute("shortTitleEng", obj.getShortTitle(Language.ENGLISH.getCode()));
		model.addAttribute("elementId", obj.getElementId());
		Iterator<CciAgentGroup> agentGroups = context.findAll(CciAgentGroup.class);
		Map<String, String> agentGroupOptions = new TreeMap<String, String>();
		while (agentGroups.hasNext()) {
			CciAgentGroup cciAG = agentGroups.next();
			if (ConceptStatus.ACTIVE.name().equals(cciAG.getStatus())) {
				agentGroupOptions.put(String.valueOf(cciAG.getElementId()), cciAG.getDescription(Language.ENGLISH
						.getCode()));
			}
		}
		model.addAttribute("agentGroups", agentGroupOptions);
		return LIST_COMPONENTS_DEVICE_AGENT_DIAGRAM;
	}

	public void setAuxService(CciAuxService auxService) {
		this.auxService = auxService;
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setDtService(DisplayTagUtilService dtService) {
		this.dtService = dtService;
	}

	/*
	 * Update a Diagram
	 */
	@RequestMapping(value = "/diagram", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse updateDiagram(HttpSession session, @RequestParam("e") final long elementId,
			@RequestParam(value = "ate", required = false) final String agentTypeEng,
			@RequestParam(value = "atf", required = false) final String agentTypeFra,
			@RequestParam(value = "aee", required = false) final String agentExEng,
			@RequestParam(value = "aef", required = false) final String agentExFra,
			@RequestParam(value = "atcCode", required = false) final String agentATCCode,
			@RequestParam(value = "ag", required = false) final String agentGroup) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);
		CciAgentGroup ag = context.load(Long.valueOf(agentGroup));
		CciDeviceAgentComponent obj = context.load(elementId);
		obj.setAgentTypeDescription(Language.ENGLISH.getCode(), agentTypeEng);
		obj.setAgentTypeDescription(Language.FRENCH.getCode(), agentTypeFra);
		obj.setAgentExample(Language.ENGLISH.getCode(), agentExEng);
		obj.setAgentExample(Language.FRENCH.getCode(), agentExFra);
		obj.setAgentATCCode(agentATCCode);
		obj.setAgentGroup(ag);
		context.persist();
		context.realizeChangeContext(true);
		ValidationResponse res = new ValidationResponse();
		res.setStatus(ValidationResponse.Status.SUCCESS.name());
		return res;
	}

}
