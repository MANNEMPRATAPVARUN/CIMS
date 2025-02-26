package ca.cihi.cims.web.controller.cci;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.Language;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.content.cci.CciAttribute;
import ca.cihi.cims.content.cci.CciAttributeType;
import ca.cihi.cims.content.cci.CciGenericAttribute;
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciAttributeGenericModel;
import ca.cihi.cims.model.CciAttributeReferenceInContextModel;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/referenceAttributes/{attributeId}/inContext")
public class CciAttributesReferenceInContextController extends CciAttributesCommon {

	protected final Log LOGGER = LogFactory.getLog(getClass());

	private static final String STATUS_ACTIVE = "ACTIVE";
	public static final String LIST_ATTRIBUTE_REFERENCE_ASSOC_GENERIC = PATH_PREFIX + "/referenceAttributeAssocGeneric";
	public static final String LIST_ATTRIBUTE_REFERENCE_ASSOC_GENERIC_NOTES = PATH_PREFIX
			+ "/referenceAttributeAssocGenericNotes";

	@Autowired
	private CciAuxService auxService;
	@Autowired
	private ContextOperations operations;
	@Autowired
	private DisplayTagUtilService dtService;
	@Autowired
	private ElementOperations elementOperations;
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
	ValidationResponse create(HttpSession session, ModelMap model, @Valid CciAttributeReferenceInContextModel cm,
			@PathVariable("attributeId") Long attributeId, BindingResult result) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ValidationResponse res = new ValidationResponse();
		if (result.hasErrors()) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(result.getFieldErrors());
		} else {
			ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			try {
				CciReferenceAttribute refAttribute = context.load(attributeId);
				Ref<CciGenericAttribute> genAttributeRef = ref(CciGenericAttribute.class);
				Ref<CciAttributeType> attributeType = ref(CciAttributeType.class);
				CciGenericAttribute genAttribute = context.findOne(genAttributeRef, genAttributeRef.eq("code", cm
						.getGenericAttributeCode()), attributeType.eq("code", refAttribute.getType().getCode()),
						genAttributeRef.link("type", attributeType));
				CciAttribute attrib = CciAttribute.create(context, refAttribute, genAttribute, refAttribute.getType());
				attrib.setDescription(Language.ENGLISH.getCode(), cm.getDescriptionEng());
				attrib.setDescription(Language.FRENCH.getCode(), cm.getDescriptionFra());
				context.persist();
				context.realizeChangeContext(true);
				res.setStatus(ValidationResponse.Status.SUCCESS.name());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL.name());
				List<FieldError> errorList = new ArrayList<FieldError>();
				errorList.add(new FieldError("", "Attribute ", "Attribute update has errors. "
						+ e.getLocalizedMessage()));
				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}

	/*
	 * DELETE
	 */
	@RequestMapping(value = "/{inContextId}.htm", method = RequestMethod.DELETE)
	public @ResponseBody
	ValidationResponse delete(HttpSession session, @PathVariable("inContextId") Long elementIdToRemove) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
		// Fix this method to accept context definitions
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);
		LOGGER.debug("trying to remove " + elementIdToRemove);
		ValidationResponse res = new ValidationResponse();
		// Load the element
		ElementVersion ev = elementOperations.loadElement(context.getContextId(), elementIdToRemove);
		ConceptVersion conceptToRemove = (ConceptVersion) ev;
		boolean isElligible = commonOperations.isConceptEligibleForRemoval(context.getContextId(), conceptToRemove);
		if (!isElligible) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			List<FieldError> errorList = new ArrayList<FieldError>();
			errorList.add(new FieldError("", "Attribute", "Attribute is not eligible to be removed. "));
			res.setErrorMessageList(errorList);
		} else {
			nonContextOperations.remove(context.getContextId(), elementIdToRemove);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
			context.persist();
			context.realizeChangeContext(true);
		}
		return res;
	}

	private String getAttributesInContext(ModelMap model, HttpServletRequest request, Long attributeId,
			ContextAccess context, Boolean disableEditing) {
		CciAttributeReferenceModel attrModel = auxService.getReferenceAttribute(context.getContextId().getContextId(),
				attributeId);
		String attributeType = auxService.getReferenceAttributeType(context.getContextId().getContextId(), attributeId);
		List<CciAttributeReferenceInContextModel> inContextModels = auxService.getReferenceAttributeInContextSQL(CCI,
				context.getContextId().getVersionCode(), attributeId);
		// Retrieve the entire list of Generic Attribute Codes
		List<String> genericAttributesCodes = new ArrayList<String>();
		if (attrModel != null && attributeType != null) {
			Collection<CciAttributeGenericModel> cciGenericAttributes = auxService.getGenericAttributesSQL(CCI, context
					.getContextId().getVersionCode(), attributeType);
			for (CciAttributeGenericModel genericAttribute : cciGenericAttributes) {
				if (STATUS_ACTIVE.equals(genericAttribute.getStatus())) {
					genericAttributesCodes.add(genericAttribute.getCode());
				}
			}
			Collections.sort(genericAttributesCodes);
		}
		if (inContextModels != null) {
			for (CciAttributeReferenceInContextModel inContextModel : inContextModels) {
				String genCode = inContextModel.getGenericAttributeCode();
				boolean isRemoved = genericAttributesCodes.remove(genCode);
				LOGGER.debug("[" + genCode + "] removed from Generic Attribute Codes list? [" + isRemoved + "]");
			}
		}
		// Check if this reference value has been published before
		boolean hasConceptBeenPublished = operations.hasConceptBeenPublished(attributeId);
		model.addAttribute("attrModel", new CciAttributeReferenceInContextModel());
		model.addAttribute("genericAttributeCodes", genericAttributesCodes);
		model.addAttribute("refAttrModel", attrModel);
		model.addAttribute("hasConceptBeenPublished", hasConceptBeenPublished);
		model.addAttribute("inContextAttributes", inContextModels);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, inContextModels.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "inContextAttributeTable"));
		model.addAttribute("versionCode", context.getContextId().getVersionCode());
		model.addAttribute("disableEditing", disableEditing);
		return LIST_ATTRIBUTE_REFERENCE_ASSOC_GENERIC;
	}

	@ExceptionHandler(BindException.class)
	@ResponseBody
	public ValidationResponse handleValidationException(BindException exception) {
		ValidationResponse errors = new ValidationResponse();
		errors.setStatus(ValidationResponse.Status.FAIL.name());
		errors.setErrorMessageList(exception.getBindingResult().getFieldErrors());
		return errors;
	}

	/*
	 * READ
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String read(ModelMap model, HttpServletRequest request, @PathVariable("attributeId") Long attributeId,
			@RequestParam(value = "versionCode", required = false) String versionCode,
			@RequestParam(value = "contextId", required = false) Long contextId,
			@RequestParam(value = "disableEditing") Boolean disableEditing) {
		ContextAccess context = null;
		if (versionCode != null) {
			ContextDefinition cd = ContextDefinition.forVersion(CCI, versionCode);
			context = contextProvider.findContext(cd);
		} else if (contextId != null) {
			ContextIdentifier contextIdentifier = lookupService.findContextIdentificationById(contextId);
			context = contextProvider.findContext(contextIdentifier);
		}
		if (context == null) {
			throw new IllegalStateException(
					"Context is null. You must provide either 'versionCode' or 'contextId' request parameter");
		}
		return getAttributesInContext(model, request, attributeId, context, disableEditing);
	}

	/*
	 * READ Notes
	 */
	@RequestMapping(value = "/{inContextId}/notes", method = RequestMethod.GET)
	public String readNotes(ModelMap model, HttpServletRequest request, @PathVariable("attributeId") Long attributeId,
			@PathVariable("inContextId") Long inContextId, @RequestParam("versionCode") String versionCode,
			@RequestParam(value = "disableEditing") Boolean disableEditing) {
		ContextDefinition cd = ContextDefinition.forVersion(CCI, versionCode);
		ContextAccess context = contextProvider.findContext(cd);
		CciAttributeGenericModel genAttrModel = auxService.getGenericAttribute(context.getContextId().getContextId(),
				inContextId);
		String genAttrNoteEn = auxService.getAttributeNote(context.getContextId().getContextId(), inContextId,
				Language.ENGLISH);
		String genAttrNoteFr = auxService.getAttributeNote(context.getContextId().getContextId(), inContextId,
				Language.FRENCH);
		CciAttributeReferenceModel refAttrModel = auxService.getReferenceAttribute(context.getContextId()
				.getContextId(), attributeId);
		model.addAttribute("attrModel", refAttrModel);
		model.addAttribute("genCode", genAttrModel.getCode());
		model.addAttribute("icElementId", inContextId);
		model.addAttribute("descEng", genAttrModel.getDescriptionEng());
		model.addAttribute("notesEng", genAttrNoteEn);
		model.addAttribute("notesFra", genAttrNoteFr);
		model.addAttribute("disableEditing", disableEditing);
		return LIST_ATTRIBUTE_REFERENCE_ASSOC_GENERIC_NOTES;
	}

	public void setAuxService(CciAuxService auxService) {
		this.auxService = auxService;
	}

	public void setCommonOperations(CommonElementOperations commonOperations) {
		this.commonOperations = commonOperations;
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

	/*
	 * UPDATE
	 */
	@RequestMapping(value = "/{inContextId}.htm", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse update(HttpSession session, ModelMap model, @Valid CciAttributeReferenceInContextModel cm,
			@PathVariable("inContextId") Long inContextId, BindingResult result) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ValidationResponse res = new ValidationResponse();
		if (result.hasErrors()) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(result.getFieldErrors());
		} else {
			ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			try {
				CciAttribute attrib = context.load(inContextId);
				attrib.setDescription(Language.ENGLISH.getCode(), cm.getDescriptionEng());
				attrib.setDescription(Language.FRENCH.getCode(), cm.getDescriptionFra());
				context.persist();
				context.realizeChangeContext(true);
				res.setStatus(ValidationResponse.Status.SUCCESS.name());
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
				res.setStatus(ValidationResponse.Status.FAIL.name());
				List<FieldError> errorList = new ArrayList<FieldError>();
				errorList.add(new FieldError("", "Attribute ", "Attribute update has errors. "
						+ e.getLocalizedMessage()));
				res.setErrorMessageList(errorList);
			}
		}
		return res;
	}

	/*
	 * UPDATE Notes
	 */
	@RequestMapping(value = "/{inContextId}/notes", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse updateInContextNotes(HttpSession session, @PathVariable("attributeId") Long attributeId,
			@PathVariable("inContextId") Long inContextId,
			@RequestParam(value = "ne", required = true) final String notesEng,
			@RequestParam(value = "nf", required = true) final String notesFra) {
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		try {
			CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
			ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
			ContextAccess baseContext = contextProvider.findContext(cd);
			ContextAccess context = baseContext.createChangeContext(null);
			CciAttribute obj = context.load(inContextId);
			obj.setNote(Language.ENGLISH.getCode(), notesEng);
			obj.setNote(Language.FRENCH.getCode(), notesFra);
			context.persist();
			context.realizeChangeContext(true);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
			errorList.add(new FieldError("", "Attribute", String.format("Attribute update has errors. %s", e
					.getLocalizedMessage())));
		}
		if (!errorList.isEmpty()) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
		}
		return res;
	}

}
