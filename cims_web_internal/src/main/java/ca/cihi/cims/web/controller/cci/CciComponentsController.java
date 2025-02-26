package ca.cihi.cims.web.controller.cci;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciApproachTechniqueComponent;
import ca.cihi.cims.content.cci.CciComponent;
import ca.cihi.cims.content.cci.CciDeviceAgentComponent;
import ca.cihi.cims.content.cci.CciGroupComponent;
import ca.cihi.cims.content.cci.CciInterventionComponent;
import ca.cihi.cims.content.cci.CciTabular;
import ca.cihi.cims.content.cci.CciTissueComponent;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciComponentModel;
import ca.cihi.cims.model.CciComponentRefLink;
import ca.cihi.cims.model.CciComponents;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@SessionAttributes( { "cciComponentsForViewer" })
/**
 * Main Controller for CCI Components.  Generalized to handle multiple component related actions
 * 
 * Responsible for:
 * 
 * 1.  Removing components
 * 2.  Saving components
 * 3.  Updating components
 * 4.  Listing sections
 * 5.  Updating/Viewing classification selections (? better wording?)
 * 6.  Viewing component references
 * @author HLee
 *
 */
public class CciComponentsController extends CciComponentsCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private ContextOperations operations;
	@Autowired
	private DisplayTagUtilService dtService;
	@Autowired
	private ContextProvider contextProvider;
	@Autowired
	private ElementOperations elementOperations;
	@Autowired
	private NonContextOperations nonContextOperations;
	@Autowired
	private CommonElementOperations commonOperations;

	// ----------------------------------------------------------------------

	private CciComponent createCciComponent(CciComponentModel cm, ContextAccess context, CciTabular cciTabular) {
		CciComponent comp = null;
		if (cm.getComponentModelType().equals("group")) {
			comp = CciGroupComponent.create(context, cm.getCode().toUpperCase(), cciTabular);
		} else if (cm.getComponentModelType().equals("tissue")) {
			comp = CciTissueComponent.create(context, cm.getCode().toUpperCase(), cciTabular);
		} else if (cm.getComponentModelType().equals("appTech")) {
			comp = CciApproachTechniqueComponent.create(context, cm.getCode().toUpperCase(), cciTabular);
		} else if (cm.getComponentModelType().equals("intervention")) {
			comp = CciInterventionComponent.create(context, cm.getCode().toUpperCase(), cciTabular);
		} else if (cm.getComponentModelType().equals("da")) {
			comp = CciDeviceAgentComponent.create(context, cm.getCode().toUpperCase(), cciTabular);
		}
		return comp;
	}

	public CciAuxService getAuxService() {
		return auxService;
	}

	// There is a better way to do this...
	private Class<?> getClassFromModelType(String componentType) {
		// TODO: Optimize
		if (componentType.equals("group")) {
			return CciGroupComponent.class;
		} else if (componentType.equals("tissue")) {
			return CciTissueComponent.class;
		} else if (componentType.equals("appTech")) {
			return CciApproachTechniqueComponent.class;
		} else if (componentType.equals("intervention")) {
			return CciInterventionComponent.class;
		} else if (componentType.equals("da")) {
			return CciDeviceAgentComponent.class;
		}
		return null;
	}

	public CommonElementOperations getCommonOperations() {
		return commonOperations;
	}

	public ContextProvider getContextProvider() {
		return contextProvider;
	}

	public DisplayTagUtilService getDtService() {
		return dtService;
	}

	public ElementOperations getElementOperations() {
		return elementOperations;
	}

	public NonContextOperations getNonContextOperations() {
		return nonContextOperations;
	}

	public ContextOperations getOperations() {
		return operations;
	}

	private List<FieldError> getRemoveOrStatuseChangeErrors(ContextAccess context, long elementIdToRemove,
			String componentCode, boolean remove) {
		// Load the element
		ElementVersion ev = elementOperations.loadElement(context.getContextId(), elementIdToRemove);
		ConceptVersion concept = (ConceptVersion) ev;
		List<FieldError> errorList = new ArrayList<FieldError>();
		if (remove) {
			boolean isElligible = commonOperations.isConceptEligibleForRemoval(context.getContextId(), concept);
			if (!isElligible) {
				errorList.add(new FieldError("Component", "Component " + componentCode, "Component " + componentCode
						+ " is not eligible to be removed. "));
			}
		} else {
			List<CciComponentRefLink> compRefs = auxService.getComponentReferences(baseClassification, context
					.getContextId().getVersionCode(), elementIdToRemove);
			if (!compRefs.isEmpty()) {
				errorList.add(new FieldError("Component", "Component " + componentCode, "Component " + componentCode
						+ " has dependents and cannot be disabled. "));
			}
		}
		if (remove){
			boolean hasConceptBeenPublished = operations.hasConceptBeenPublished(concept.getElementId());
			if (hasConceptBeenPublished) {
				errorList.add(new FieldError("Component", "Component " + componentCode, "Component " + componentCode
						+ " has been published before. "));
			}
		}
		return errorList;
	}

	@RequestMapping(value = "/sections", method = RequestMethod.GET)
	public @ResponseBody
	Map<String, String> getSections(@RequestParam("versionCode") final String versionCode) {
		return auxService.getCCISections(baseClassification, versionCode);
	}

	/**
	 * List Component References
	 * 
	 */
	@RequestMapping(value = "/listComponentReferences", method = RequestMethod.GET)
	public String listComponentReferences(HttpServletRequest request, HttpSession session, ModelMap model,
			@RequestParam("e") final long elementId, @RequestParam("tabName") String tabName) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		List<CciComponentRefLink> compRefs = auxService.getComponentReferences(baseClassification, viewerModel
				.getVersionCode(), elementId);
		model.addAttribute("components", compRefs);
		model.addAttribute("tabName", tabName);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, compRefs.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "compRefsTable"));
		return LIST_COMPONENT_REFERENCES;
	}

	/**
	 * Loads drop down for the Viewer selection
	 * 
	 * TODO: Pull out the version code stuff into its own method
	 * 
	 */
	@RequestMapping(value = "/cciComponents", method = RequestMethod.GET)
	public String loadViewerMain(HttpServletRequest request, HttpSession session, ModelMap model) {
		String defaultVersionCode = null;
		Map<String, Boolean> versionCodesOpen = new HashMap<String, Boolean>();

		model.addAttribute("command", new CciComponents());
		model.addAttribute("cciComponentsForViewer", new CciComponents());

		// Version Codes
		Collection<ContextIdentifier> contextIdentifiers = contextProvider
				.findBaseClassificationVersionYearVersionCodes(baseClassification);

		List<String> versionCodes = new ArrayList<String>();
		for (ContextIdentifier context : contextIdentifiers) {
			versionCodes.add(context.getVersionCode());
			versionCodesOpen.put(context.getVersionCode(), context.isContextOpen());

			if (context.isContextOpen()) {
				if (defaultVersionCode == null) {
					defaultVersionCode = context.getVersionCode();
				} else {
					int defVerCode = Integer.parseInt(defaultVersionCode);
					long verCode = Integer.parseInt(context.getVersionCode());

					if (verCode < defVerCode) {
						defaultVersionCode = context.getVersionCode();
					}
				}
			}
		}

		Collections.sort(versionCodes);
		Collections.reverse(versionCodes);

		model.addAttribute(MODEL_KEY_VERSION_CODES, versionCodes);
		request.setAttribute(MODEL_KEY_VERSION_CODE_DEFAULT, defaultVersionCode);
		model.addAttribute(MODEL_KEY_READ_ONLY, versionCodesOpen);

		Iterator<String> versionCodeIterator = versionCodes.iterator();
		Map<String, String> sectionOptions = auxService.getCCISections(baseClassification, versionCodeIterator.next());
		model.addAttribute("sections", sectionOptions);

		return LIST_MAIN_PAGE;
	}

	/**
	 * Remove Component
	 * 
	 * @param elementIdToRemove
	 * @param versionCode
	 * @return
	 */
	@RequestMapping(value = "/removeComponent", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse processComponentRemoval(HttpSession session,
			@RequestParam("elementIdToRemove") final long elementIdToRemove,
			@RequestParam("componentCode") final String componentCode) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");

		ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
		// Fix this method to accept context definitions
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);

		LOGGER.debug("trying to remove " + elementIdToRemove);
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = getRemoveOrStatuseChangeErrors(context, elementIdToRemove, componentCode, true);
		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL);
			res.setErrorMessageList(errorList);
		} else {
			try {
				nonContextOperations.remove(context.getContextId(), elementIdToRemove);
				res.setStatus(ValidationResponse.Status.SUCCESS);
				context.persist();
				context.realizeChangeContext(true);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL);
				errorList.add(new FieldError("Component", "Component " + componentCode, "Component " + componentCode
						+ " is not eligible to be removed. " + e.getLocalizedMessage()));
				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}

	// ------------------------------------------------------------------------------

	/**
	 * Save a new Component
	 * 
	 * Additional parameters (Definition) exist to handle Intervention Component logic
	 * 
	 * @param session
	 * @param model
	 * @param cm
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveNewComponent", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse saveNewComponent(HttpSession session, ModelMap model, @Valid CciComponentModel cm,
			BindingResult result, @RequestParam(value = "de", required = false) final String defEng,
			@RequestParam(value = "df", required = false) final String defFra) {
		CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		if (cm.getComponentModelType().equals("intervention")) {
			CollectionUtils.addIgnoreNull(errorList, validateXml(defEng.trim(), "English", false));
			CollectionUtils.addIgnoreNull(errorList, validateXml(defFra.trim(), "French", false));
		}
		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}
		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL);
			res.setErrorMessageList(errorList);
		} else {
			ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
			// Fix this method to accept context definitions
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			try {
				// Get reference to the CCI Section
				Ref<CciTabular> cciTab = ref(CciTabular.class);
				Iterator<CciTabular> sections = context.find(cciTab, cciTab.eq("typeCode", CciTabular.SECTION), cciTab
						.eq("code", viewerModel.getSection()));
				CciTabular cciTabular = sections.next();

				CciComponent comp = createCciComponent(cm, context, cciTabular);
				comp.setShortTitle(Language.ENGLISH.getCode(), cm.getShortDescriptionEng());
				comp.setShortTitle(Language.FRENCH.getCode(), cm.getShortDescriptionFra());
				comp.setLongTitle(Language.ENGLISH.getCode(), cm.getLongDescriptionEng());
				comp.setLongTitle(Language.FRENCH.getCode(), cm.getLongDescriptionFra());
				comp.setStatus(cm.getStatus());

				if (cm.getComponentModelType().equals("intervention")) {
					CciInterventionComponent interventionComponent = (CciInterventionComponent) comp;
					interventionComponent.setDefinitionTitle(Language.ENGLISH.getCode(), defEng);
					interventionComponent.setDefinitionTitle(Language.FRENCH.getCode(), defFra);
				}
				context.persist();
				context.realizeChangeContext(true);

				res.setStatus(ValidationResponse.Status.SUCCESS);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL);
				errorList.add(new FieldError("Component", "Component ", "Component creation has errors: "
						+ e.getLocalizedMessage()));
				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}

	public void setAuxService(CciAuxService auxService) {
		this.auxService = auxService;
	}

	public void setCommonOperations(CommonElementOperations commonOperations) {
		this.commonOperations = commonOperations;
	}

	public void setContextProvider(ContextProvider contextProvider) {
		this.contextProvider = contextProvider;
	}

	public void setDtService(DisplayTagUtilService dtService) {
		this.dtService = dtService;
	}

	public void setElementOperations(ElementOperations elementOperations) {
		this.elementOperations = elementOperations;
	}

	public void setNonContextOperations(NonContextOperations nonContextOperations) {
		this.nonContextOperations = nonContextOperations;
	}

	public void setOperations(ContextOperations operations) {
		this.operations = operations;
	}

	@RequestMapping(value = "/updateComponent", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse updateComponent(HttpSession session, @ModelAttribute("compModel") @Valid CciComponentModel cm,
			BindingResult result, ModelMap model) {
		ValidationResponse res = new ValidationResponse();
		if (result.hasErrors()) {
			res.setStatus(ValidationResponse.Status.FAIL);
			res.setErrorMessageList(result.getFieldErrors());
		} else {
			CciComponents viewerModel = (CciComponents) session.getAttribute("cciComponentsForViewer");
			ContextDefinition cd = ContextDefinition.forVersion(baseClassification, viewerModel.getVersionCode());
			// Fix this method to accept context definitions
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);

			CciComponent obj = context.load(cm.getElementId());
			if (!StringUtils.equals(obj.getStatus(), cm.getStatus())) {
				List<FieldError> errors = getRemoveOrStatuseChangeErrors(context, cm.getElementId(), obj.getCode(),
						false);
				if (!errors.isEmpty()) {
					res.setStatus(ValidationResponse.Status.FAIL);
					res.setErrorMessageList(errors);
					return res;
				}
			}
			obj.setShortTitle(Language.ENGLISH.getCode(), cm.getShortDescriptionEng());
			obj.setShortTitle(Language.FRENCH.getCode(), cm.getShortDescriptionFra());
			obj.setLongTitle(Language.ENGLISH.getCode(), cm.getLongDescriptionEng());
			obj.setLongTitle(Language.FRENCH.getCode(), cm.getLongDescriptionFra());
			obj.setStatus(cm.getStatus());

			context.persist();
			// Note the true - indicates admin, and allows future contexts to be realized.
			context.realizeChangeContext(true);

			String componentRefLink = null;
			if (cm.getComponentModelType().equals("group")) {
				componentRefLink = "sectionGC";
			} else if (cm.getComponentModelType().equals("tissue")) {
				componentRefLink = "sectionTC";
			} else if (cm.getComponentModelType().equals("appTech")) {
				componentRefLink = "sectionATC";
			} else if (cm.getComponentModelType().equals("intervention")) {
				componentRefLink = "sectionIC";
			} else if (cm.getComponentModelType().equals("da")) {
				componentRefLink = "sectionDAC";
			}

			Class<?> c = getClassFromModelType(cm.getComponentModelType());
			List<CciComponentModel> compModel = auxService.getComponents(baseClassification, viewerModel
					.getVersionCode(), viewerModel.getSection(), viewerModel.getStatus(), c, componentRefLink);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("compModel", new CciComponentModel());
			map.put("components", compModel);
			map.put(MODEL_KEY_RESULT_SIZE, compModel.size());
			model.addAllAttributes(map);
			res.setStatus(ValidationResponse.Status.SUCCESS);
		}
		return res;
	}

	@RequestMapping(value = "/cciComponents", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse updateViewerMain(Model model, @Valid CciComponents viewerModel, BindingResult result) {
		ValidationResponse res = new ValidationResponse();
		if (result.hasErrors()) {
			res.setStatus(ValidationResponse.Status.FAIL);
			res.setErrorMessageList(result.getFieldErrors());
		} else {
			FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
			boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;
			viewerModel.setContextFrozen(isContextFrozen);
			res.setContextFrozen(isContextFrozen);
		}
		model.addAttribute("cciComponentsForViewer", viewerModel);
		return res;
	}

}
