package ca.cihi.cims.web.controller.cci;

import static ca.cihi.cims.bll.query.FindCriteria.ref;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
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
import ca.cihi.cims.content.cci.CciReferenceAttribute;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NonContextOperations;
import ca.cihi.cims.model.CciAttributeReferenceModel;
import ca.cihi.cims.model.CciAttributeReferenceRefLink;
import ca.cihi.cims.model.CciAttributes;
import ca.cihi.cims.service.CciAuxService;
import ca.cihi.cims.service.DisplayTagUtilService;
import ca.cihi.cims.web.bean.ValidationResponse;

@Controller
@RequestMapping("/referenceAttributes")
public class CciAttributesReferenceController extends CciAttributesCommon {

	private static final int MAX_CODES_TO_DISPLAY = 10;

	protected final Log LOGGER = LogFactory.getLog(getClass());

	public static final String LIST_ATTRIBUTES_REFERENCE = PATH_PREFIX + "/referenceAttributes";
	public static final String PRINT_ATTRIBUTES_REFERENCE = PATH_PREFIX + "/printReferenceAttributes";
	public static final String LIST_ATTRIBUTE_REFERENCE_REFERENCES = PATH_PREFIX + "/referenceAttributeReferences";
	public static final String LIST_ATTRIBUTE_REFERENCE_NOTES = PATH_PREFIX + "/referenceAttributeNotes";
	public static final String LIST_ATTRIBUTE_REFERENCE_NOTE = PATH_PREFIX + "/referenceAttributeNote";

	// ------------------------------------------------------------------------

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

	// ------------------------------------------------------------------------

	/*
	 * CREATE
	 */
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse create(HttpSession session, ModelMap model, @Valid CciAttributeReferenceModel cm,
			BindingResult result) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		// Custom Constraint isn't enough to auto validate. For now, add in this bit of code to check the code starts
		// with the correct attribute type.
		if (!cm.getCode().startsWith(viewerModel.getAttributeType())) {
			errorList.add(new FieldError("", "Attribute ", "Code must start with the correct letter"));
		}
		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}
		// boolean isContextFrozen = getContextFreezingStatus(viewerModel.getVersionCode());
		FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
		boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;
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
				CciReferenceAttribute referenceAttribute = CciReferenceAttribute
						.create(context, cm.getCode(), attrType);
				referenceAttribute.setDescription(Language.ENGLISH.getCode(), cm.getDescriptionEng());
				referenceAttribute.setDescription(Language.FRENCH.getCode(), cm.getDescriptionFra());
				referenceAttribute.setStatus(cm.getStatus());
				referenceAttribute.setType(attrType);
				referenceAttribute.setMandatory(BooleanUtils.toBoolean(cm.getMandatory()));
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
		boolean hasConceptBeenPublished = operations.hasConceptBeenPublished(attributeId);
		if (!isElligible) {
			errorList.add(new FieldError("", "Attribute", "Attribute is not eligible to be removed. "));
		}
		if (hasConceptBeenPublished) {
			errorList.add(new FieldError("", "Attribute", "Attribute has been published before. "));
		}

		// boolean isContextFrozen = getContextFreezingStatus(viewerModel.getVersionCode());
		FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
		boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;

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
		List<CciAttributeReferenceModel> refAttrModel = auxService.getReferenceAttributesSQL(CCI, viewerModel
				.getVersionCode(), viewerModel.getAttributeType());
		// Remove the status we dont want
		List<CciAttributeReferenceModel> refAttrModelWithStatus = new ArrayList<CciAttributeReferenceModel>();
		for (CciAttributeReferenceModel referenceAttributeModel : refAttrModel) {
			if (viewerModel.getStatus().equalsIgnoreCase(referenceAttributeModel.getStatus())
					|| viewerModel.getStatus().equalsIgnoreCase("ALL")) {
				refAttrModelWithStatus.add(referenceAttributeModel);
			}
		}
		model.addAttribute("attrModel", new CciAttributeReferenceModel());
		model.addAttribute("referenceAttributes", refAttrModelWithStatus);
		if ("Y".equals(request.getParameter("print"))) {
			model.addAttribute("print", "Y");
			return PRINT_ATTRIBUTES_REFERENCE;
		} else {
			model.addAttribute(MODEL_KEY_RESULT_SIZE, refAttrModelWithStatus.size());
			model.addAllAttributes(dtService.addForPageLinks(request, "attributeTable"));
		}

		return LIST_ATTRIBUTES_REFERENCE;
	}

	@RequestMapping(value = "/{attributeId}/note", method = RequestMethod.GET)
	public String readNote(ModelMap model, HttpServletRequest request, @PathVariable("attributeId") Long attributeId,
			@RequestParam("contextId") Long contextId, @RequestParam(value = "language") String language) {
		ContextIdentifier contextIdentifier = lookupService.findContextIdentificationById(contextId);
		ContextAccess context = contextProvider.findContext(contextIdentifier);
		String genAttrNote = auxService.getReferenceAttributeNoteDescription(context.getContextId().getContextId(),
				attributeId, Language.fromString(language));

		CciAttributeReferenceModel refAttrModel = auxService.getReferenceAttribute(context.getContextId()
				.getContextId(), attributeId);
		model.addAttribute("attrModel", refAttrModel);
		model.addAttribute("note", genAttrNote);
		model.addAttribute("language", language);

		return LIST_ATTRIBUTE_REFERENCE_NOTE;
	}

	/*
	 * READ Notes
	 */
	@RequestMapping(value = "/{attributeId}/notes", method = RequestMethod.GET)
	public String readNotes(ModelMap model, HttpServletRequest request, HttpSession session,
			@PathVariable("attributeId") Long attributeId) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
		ContextAccess context = contextProvider.findContext(cd);
		CciAttributeReferenceModel attrModel = auxService.getReferenceAttribute(context.getContextId().getContextId(),
				attributeId);
		String notesEn = auxService.getReferenceAttributeNoteDescription(context.getContextId().getContextId(),
				attributeId, Language.ENGLISH);
		String notesFr = auxService.getReferenceAttributeNoteDescription(context.getContextId().getContextId(),
				attributeId, Language.FRENCH);
		model.addAttribute("viewer", viewerModel);
		model.addAttribute("attrModel", attrModel);
		model.addAttribute("notesEng", notesEn);
		model.addAttribute("notesFra", notesFr);

		return LIST_ATTRIBUTE_REFERENCE_NOTES;
	}

	/*
	 * READ References
	 */
	@RequestMapping(value = "/{attributeId}/references", method = RequestMethod.GET)
	public String readReferences(HttpServletRequest request, HttpSession session, ModelMap model,
			@PathVariable("attributeId") Long attributeId, @RequestParam("c") final String referenceAttributeCode) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		List<CciAttributeReferenceRefLink> refList = auxService.getReferenceAttributeReferences(CCI, viewerModel
				.getVersionCode(), attributeId, referenceAttributeCode, viewerModel.getAttributeType());
		model.addAttribute("attributes", refList);
		model.addAttribute(MODEL_KEY_RESULT_SIZE, refList.size());
		model.addAllAttributes(dtService.addForPageLinks(request, "attrRefsTable"));
		return LIST_ATTRIBUTE_REFERENCE_REFERENCES;
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
	@RequestMapping(value = "/{attributeId}.htm", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse update(HttpServletRequest request, HttpSession session,
			@PathVariable("attributeId") Long attributeId,
			@ModelAttribute("attrModel") @Valid CciAttributeReferenceModel cm, BindingResult result, ModelMap model) {
		ValidationResponse res = new ValidationResponse();
		List<FieldError> errorList = new ArrayList<FieldError>();
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");

		if (result.hasErrors()) {
			errorList.addAll(result.getFieldErrors());
		}
		FreezingStatus freezingStatus = getContextFreezingStatus(viewerModel.getVersionCode());
		boolean isContextFrozen = FreezingStatus.TAB == freezingStatus || FreezingStatus.ALL == freezingStatus;

		if (isContextFrozen) {
			errorList.add(new FieldError("", "",
					"The CCI classification table package is being generated. Changes to generic attributes, "
							+ "reference values and in-context generic description are restricted."));
		}
		validateStatusChange(attributeId, viewerModel.getVersionCode(), cm, errorList);
		if (errorList.size() > 0) {
			res.setStatus(ValidationResponse.Status.FAIL.name());
			res.setErrorMessageList(errorList);
		} else {
			updateReferenceAttribute(viewerModel, cm, model);
			res.setStatus(ValidationResponse.Status.SUCCESS.name());
		}
		return res;
	}

	/*
	 * UPDATE Notes
	 */
	@RequestMapping(value = "/{attributeId}/notes", method = RequestMethod.POST)
	public @ResponseBody
	ValidationResponse updateNotes(HttpSession session, @PathVariable("attributeId") Long attributeId,
			@RequestParam(value = "ne", required = false) final String notesEng,
			@RequestParam(value = "nf", required = false) final String notesFra) {
		CciAttributes viewerModel = (CciAttributes) session.getAttribute("cciAttributesForViewer");
		ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);

		CciReferenceAttribute obj = context.load(attributeId);
		obj.setNoteDescription(Language.ENGLISH.getCode(), notesEng);
		obj.setNoteDescription(Language.FRENCH.getCode(), notesFra);

		context.persist();
		context.realizeChangeContext(true);

		ValidationResponse res = new ValidationResponse();
		res.setStatus(ValidationResponse.Status.SUCCESS.name());

		return res;
	}

	private void updateReferenceAttribute(CciAttributes viewerModel, CciAttributeReferenceModel cm, ModelMap model) {
		ContextDefinition cd = ContextDefinition.forVersion(CCI, viewerModel.getVersionCode());
		ContextAccess baseContext = contextProvider.findContext(cd);
		ContextAccess context = baseContext.createChangeContext(null);

		CciReferenceAttribute obj = context.load(cm.getElementId());
		obj.setDescription(Language.ENGLISH.getCode(), cm.getDescriptionEng());
		obj.setDescription(Language.FRENCH.getCode(), cm.getDescriptionFra());
		obj.setMandatory(BooleanUtils.toBoolean(cm.getMandatory()));
		obj.setStatus(StringUtils.upperCase(cm.getStatus()));

		context.persist();
		context.realizeChangeContext(true);
	}

	/**
	 * CSRE-953: if user is attempting to disable the reference attribute for an open year, check that no code
	 * validation references exist
	 * 
	 * @param attributeId
	 * @param versionCode
	 * @param cm
	 * @param errorList
	 */
	private void validateStatusChange(Long attributeId, String versionCode, CciAttributeReferenceModel cm,
			List<FieldError> errorList) {
		ContextIdentifier contextIdentifier = lookupService.findBaseContextIdentifierByClassificationAndYear(CCI,
				versionCode);
		CciAttributeReferenceModel attrModel = auxService.getReferenceAttribute(contextIdentifier.getContextId(),
				attributeId);
		if (attrModel != null && !attrModel.getStatus().equalsIgnoreCase(cm.getStatus())
				&& ConceptStatus.ACTIVE.name().equalsIgnoreCase(attrModel.getStatus())
				&& contextIdentifier.isContextOpen()) {
			String attributeType = auxService.getReferenceAttributeType(contextIdentifier.getContextId(), attributeId);
			List<CciAttributeReferenceRefLink> validationReferences = auxService.getReferenceAttributeReferences(CCI,
					versionCode, attributeId, cm.getCode(), attributeType);
			if (validationReferences != null && !validationReferences.isEmpty()) {
				String errorMessage = null;
				if (validationReferences.size() > MAX_CODES_TO_DISPLAY) {
					errorMessage = messageSource.getMessage("ref.attribute.disable.validation.short",
							new Object[] { validationReferences.size() }, LocaleContextHolder.getLocale());
				} else {
					StringBuilder codes = new StringBuilder();
					for (CciAttributeReferenceRefLink reference : validationReferences) {
						if (codes.length() != 0) {
							codes.append(", ");
						}
						codes.append(reference.getTabularCode());
					}
					errorMessage = messageSource.getMessage("ref.attribute.disable.validation.detailed",
							new Object[] { codes.toString() }, LocaleContextHolder.getLocale());
				}
				errorList.add(new FieldError("", "", errorMessage));
			}
		}
	}

}
