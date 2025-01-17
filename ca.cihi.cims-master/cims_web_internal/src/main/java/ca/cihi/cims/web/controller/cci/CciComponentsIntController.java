package ca.cihi.cims.web.controller.cci;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.TransformCCIComponentService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/intComponents")
public class CciComponentsIntController extends CciComponentsCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());

	public static final String LIST_COMPONENTS_INTERVENTION = PATH_PREFIX + "/listInterventionComponents";
	public static final String PRINT_COMPONENTS_INTERVENTION = PATH_PREFIX + "/printInterventionComponents";
	public static final String LIST_COMPONENTS_INTERVENTION_DIAGRAM = PATH_PREFIX + "/listInterventionDiagram";
	public static final String LIST_COMPONENTS_INTERVENTION_DEFINITION = PATH_PREFIX + "/listNewInterventionDefinition";

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private DisplayTagUtilService dtService;
	@Autowired
	private ContextProvider contextProvider;
	@Autowired
	private TransformCCIComponentService transformService;

	// ---------------------------------------------------------------------

	/*
	 * Loads Definition Dialog for a new Intervention component
	 */
	@RequestMapping(value = "/newDefinition", method = RequestMethod.GET)
	public String listDiagramNew() {
		return LIST_COMPONENTS_INTERVENTION_DEFINITION;
	}

	/*
	 * READ
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String read(HttpServletRequest request, HttpSession session, ModelMap model) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		List<CciComponentModel> compModels = auxService.getComponentsSQL(baseClassification, viewerModel
				.getVersionCode(), viewerModel.getSection(), viewerModel.getStatus(), "Intervention",
				"InterventionToSectionCPV");
		// model.addAttribute("compModel", new CciComponentModel());
		model.addAttribute("components", compModels);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, compModels.size());
		if ("Y".equals(request.getParameter("print"))) {
			model.addAttribute("print", "Y");
			return PRINT_COMPONENTS_INTERVENTION;
		} else {
			model.addAllAttributes(dtService.addForPageLinks(request, "componentTable"));
		}
		return LIST_COMPONENTS_INTERVENTION;
	}

	/*
	 * READ Diagram
	 */
	@RequestMapping(value = "/diagram", method = RequestMethod.GET)
	public String readDiagram(ModelMap model, HttpServletRequest request, HttpSession session,
			@RequestParam("e") final long elementId) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
		ContextAccess context = contextProvider.findContext(cd);

		CciInterventionComponent obj = context.load(elementId);
		CciComponentModel compModel = CciComponentModel.convert(obj);
		model.addAttribute("viewer", viewerModel);
		model.addAttribute("compModel", compModel);
		model.addAttribute("definitionEng", obj.getDefinitionTitle(Language.ENGLISH.getCode()));
		model.addAttribute("definitionFra", obj.getDefinitionTitle(Language.FRENCH.getCode()));
		return LIST_COMPONENTS_INTERVENTION_DIAGRAM;
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
	
	public void setTransformService(TransformCCIComponentService transformService) {
		this.transformService = transformService;
	}

	/*
	 * UPDATE Diagram
	 */
	@RequestMapping(value = "/diagram", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse updateDiagram(HttpSession session, @RequestParam("e") final long elementId,
			@RequestParam(value = "de", required = false) final String defEng,
			@RequestParam(value = "df", required = false) final String defFra) {
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		CollectionUtils.addIgnoreNull(errorList, validateXml(defEng.trim(), "English", false));
		CollectionUtils.addIgnoreNull(errorList, validateXml(defFra.trim(), "French", false));
		if (errorList.size() > 0) {
			// There are errors in the xml validation. Return back to user as FAIL
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
			ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			CciInterventionComponent obj = context.load(elementId);
			obj.setDefinitionTitle(Language.ENGLISH.getCode(), defEng);
			obj.setDefinitionTitle(Language.FRENCH.getCode(), defFra);
			transformService.transformCciComponent(viewerModel.getBaseClassification(), viewerModel.getVersionCode(), obj, null, context, false);
			context.persist();
			context.realizeChangeContext(true);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
		}
		return res;
	}

	@RequestMapping(value = "/validateDefinitionXML", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse validateDefinitionXML(HttpSession session,
			@RequestParam(value = "de", required = false) final String definitionEng,
			@RequestParam(value = "df", required = false) final String definitionFra) {
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		CollectionUtils.addIgnoreNull(errorList, validateXml(definitionEng.trim(), "English", false));
		CollectionUtils.addIgnoreNull(errorList, validateXml(definitionFra.trim(), "French", false));
		res.setStatus(errorList.size() > 0 ? "FAIL" : "SUCCESS");
		res.setErrorMessageList(errorList);
		return res;
	}

}
