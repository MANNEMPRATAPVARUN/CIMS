package ca.cihi.cims.web.controller.cci;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.FreezingStatus;
import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciGenericAttribute;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeGenericRefLink;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/genericAttributes")
public class CciAttributesGenericController extends CciAttributesCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());

	public static final String LIST_ATTRIBUTES_GENERIC = PATH_PREFIX + "/genericAttributes";
	public static final String PRINT_ATTRIBUTES_GENERIC = PATH_PREFIX + "/printGenericAttributes";
	public static final String LIST_ATTRIBUTE_GENERIC_REFERENCES = PATH_PREFIX + "/genericAttributeReferences";

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private DisplayTagUtilService dtService;
	@Autowired
	private ElementOperations elementOperations;
	@Autowired
	private ContextOperations contextOperations;
	@Autowired
	private CommonElementOperations commonOperations;
	@Autowired
	private NonContextOperations nonContextOperations;

	// ---------------------------------------------------------------------

	/*
	 * CREATE
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse create(HttpSession session, ModelMap model, @Valid CciAttributeGenericModel cm,
			BindingResult result) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
		boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;
		// boolean isContextFrozen = getContextFreezingStatus(viewerModel.getVersionCode());
		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}
		if (isContextFrozen) {
			errorList.add(new FieldError("", "",
					"The CCI classification table package is being generated. Changes to generic attributes, "
							+ "reference values and in-context generic description are restricted."));
		}
		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
			// Fix this method to accept context definitions
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			try {
				// Get Attribute Type reference
				Ref<CciAttributeType> attrTypeRef = ref(CciAttributeType.class);
				CciAttributeType attrType = context.findOne(attrTypeRef, attrTypeRef.eq("code", viewerModel
						.getAttributeType()));
				CciGenericAttribute genericAttribute = CciGenericAttribute.create(context, cm.getCode(), attrType);
				genericAttribute.setDescription(Language.ENGLISH.getCode(), cm.getDescriptionEng());
				genericAttribute.setDescription(Language.FRENCH.getCode(), cm.getDescriptionFra());
				genericAttribute.setStatus(cm.getStatus());
				genericAttribute.setType(attrType);
				context.persist();
				context.realizeChangeContext(true);
				res.setStatus(ValidationResponse.Status.SUCCESS.name());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL.name());
				errorList.add(new FieldError("Component", "Component ", "Component creation has errors: "
						+ e.getLocalizedMessage()));
				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}

	/*
	 * DELETE
	 */
	@RequestMapping(value = "/{attributeId}.htm", method = RequestMethod.DELETE)
	public @ResponseBody
	ValidationResponse delete(HttpSession session, @PathVariable("attributeId") Long attributeId) {
		List<FieldError> errorList = new ArrayList<FieldError>();
		CciAttributes viewerModel = (CciAttributes) session.getAttribute(CciAttributesCommon.cciAttributesForViewer);
		ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
		// Fix this method to accept context definitions
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);
		LOGGER.debug("trying to remove " + attributeId);
		ValidationResponse res = new ValidationResponse();
		// Load the element
		ElementVersion ev = elementOperations.loadElement(context.getContextId(), attributeId);
		ConceptVersion conceptToRemove = (ConceptVersion) ev;
		boolean isElligible = commonOperations.isConceptEligibleForRemoval(context.getContextId(), conceptToRemove);
		boolean hasConceptBeenPublished = contextOperations.hasConceptBeenPublished(attributeId);
		FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
		boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;
		// boolean isContextFrozen = getContextFreezingStatus(viewerModel.getVersionCode());
		if (!isElligible) {
			errorList.add(new FieldError("", "Attribute", "Attribute is not eligible to be removed. "));
		}
		if (hasConceptBeenPublished) {
			errorList.add(new FieldError("", "Attribute", "Attribute has been published before. "));
		}
		if (isContextFrozen) {
			errorList.add(new FieldError("", "",
					"The CCI classification table package is being generated. Changes to generic attributes, "
							+ "reference values and in-context generic description are restricted."));
		}
		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			nonContextOperations.remove(context.getContextId(), attributeId);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
			context.persist();
			context.realizeChangeContext(true);
		}
		return res;
	}

	/*
	 * READ
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String read(HttpServletRequest request, HttpSession session, ModelMap model) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		List<CciAttributeGenericModel> genAttrModel = auxService.getGenericAttributesSQL(CCI, viewerModel
				.getVersionCode(), viewerModel.getAttributeType());
		// Remove the status we dont want
		List<CciAttributeGenericModel> genAttrModelWithStatus = new ArrayList<CciAttributeGenericModel>();
		for (CciAttributeGenericModel genericAttributeModel : genAttrModel) {
			if (viewerModel.getStatus().equalsIgnoreCase(genericAttributeModel.getStatus())
					|| viewerModel.getStatus().equalsIgnoreCase("ALL")) {
				genAttrModelWithStatus.add(genericAttributeModel);
			}
		}
		model.addAttribute("attrModel", new CciAttributeGenericModel());
		model.addAttribute("genericAttributes", genAttrModelWithStatus);
		if ("Y".equals(request.getParameter("print"))) {
			model.addAttribute("print", "Y");
			return PRINT_ATTRIBUTES_GENERIC;
		} else {
			model.addAttribute(MODEL_KEY_RESULT_SIZE, genAttrModelWithStatus.size());
			model.addAllAttributes(dtService.addForPageLinks(request, "attributeTable"));
		}
		return LIST_ATTRIBUTES_GENERIC;
	}

	/*
	 * READ References
	 */
	@RequestMapping(value = "/{attributeId}/references", method = RequestMethod.GET)
	public String readReferences(HttpServletRequest request, HttpSession session, ModelMap model,
			@PathVariable("attributeId") Long attributeId, @RequestParam("c") final String genericAttributeCode) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		List<CciAttributeGenericRefLink> refList = auxService.getGenericAttributeReferencesSQL(CCI, viewerModel
				.getVersionCode(), attributeId, genericAttributeCode);
		model.addAttribute("attributes", refList);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, refList.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "attrRefsTable"));
		return LIST_ATTRIBUTE_GENERIC_REFERENCES;
	}

	public void setAuxService(CciAuxService auxService) {
		this.auxService = auxService;
	}

	public void setCommonOperations(CommonElementOperations commonOperations) {
		this.commonOperations = commonOperations;
	}

	public void setContextOperations(ContextOperations contextOperations) {
		this.contextOperations = contextOperations;
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

	/*
	 * UPDATE
	 */
	@RequestMapping(value = "/{attributeId}.htm", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse update(HttpServletRequest request, HttpSession session,
			@PathVariable("attributeId") Long attributeId,
			@ModelAttribute("attrModel") @Valid CciAttributeGenericModel cm, BindingResult result, ModelMap model) {
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		List<CciAttributeGenericRefLink> refList = auxService.getGenericAttributeReferencesSQL(CCI, viewerModel
				.getVersionCode(), attributeId, cm.getCode());
		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}
		if (refList.size() > 0 && cm.getStatus().equalsIgnoreCase(ConceptStatus.DISABLED.name())) {
			// Check if generic attribute has reference values associated with it. If so, cannot disable
			errorList.add(new FieldError("", "Generic Attribute ",
					"Cannot disable a generic attribute if it is associated with a reference value"));
		}
		FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
		boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;
		// boolean isContextFrozen = getContextFreezingStatus(viewerModel.getVersionCode());
		if (isContextFrozen) {
			errorList.add(new FieldError("", "",
					"The CCI classification table package is being generated. Changes to generic attributes, "
							+ "reference values and in-context generic description are restricted."));
		}
		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			updateGenericAttribute(viewerModel, cm, model);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
		}
		return res;
	}

	private void updateGenericAttribute(CciAttributes viewerModel, CciAttributeGenericModel cm, ModelMap model) {
		ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);
		CciGenericAttribute obj = context.load(cm.getElementId());
		obj.setDescription(Language.ENGLISH.getCode(), cm.getDescriptionEng());
		obj.setDescription(Language.FRENCH.getCode(), cm.getDescriptionFra());
		obj.setStatus(StringUtils.upperCase(cm.getStatus()));
		context.persist();
		context.realizeChangeContext(true);
	}

}
