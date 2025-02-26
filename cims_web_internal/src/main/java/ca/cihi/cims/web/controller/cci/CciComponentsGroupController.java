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
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.service.TransformCCIComponentService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/groupComponents")
public class CciComponentsGroupController extends CciComponentsCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());

	public static final String LIST_COMPONENTS_GROUP = PATH_PREFIX + "/listGroupComponents";
	public static final String PRINT_COMPONENTS_GROUP = PATH_PREFIX + "/printGroupComponents";
	public static final String LIST_COMPONENTS_GROUP_DIAGRAM = PATH_PREFIX + "/listGroupDiagram";

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
	 * READ
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String read(HttpServletRequest request, HttpSession session, ModelMap model) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		List<CciComponentModel> compModels = auxService.getComponentsSQL(baseClassification, viewerModel
				.getVersionCode(), viewerModel.getSection(), viewerModel.getStatus(), "GroupComp",
				"GroupCompToSectionCPV");
		model.addAttribute("components", compModels);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, compModels.size());
		if ("Y".equals(request.getParameter("print"))) {
			model.addAttribute("print", "Y");
			return PRINT_COMPONENTS_GROUP;
		} else {
			model.addAllAttributes(dtService.addForPageLinks(request, "componentTable"));
		}
		return LIST_COMPONENTS_GROUP;
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
		CciGroupComponent obj = context.load(elementId);
		CciComponentModel compModel = CciComponentModel.convert(obj);
		model.addAttribute("viewer", viewerModel);
		model.addAttribute("compModel", compModel);
		model.addAttribute("definitionEng", obj.getDefinitionTitle(Language.ENGLISH.getCode()));
		model.addAttribute("definitionFra", obj.getDefinitionTitle(Language.FRENCH.getCode()));
		return LIST_COMPONENTS_GROUP_DIAGRAM;
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
		CollectionUtils.addIgnoreNull(errorList, validateXml(defEng, "ENG", true));
		CollectionUtils.addIgnoreNull(errorList, validateXml(defFra, "FRA", true));
		if (errorList.size() > 0) {
			// There are errors in the xml validation. Return back to user as FAIL
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
			ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			CciGroupComponent obj = context.load(elementId);
			obj.setDefinitionTitle(Language.ENGLISH.getCode(), defEng);
			obj.setDefinitionTitle(Language.FRENCH.getCode(), defFra);
			transformService.transformCciComponent(viewerModel.getBaseClassification(), viewerModel.getVersionCode(), obj, null, context, false);
			context.persist();
			context.realizeChangeContext(true);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
		}
		return res;
	}

}
