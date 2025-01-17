package ca.cihi.cims.web.controller.changerequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ResolveConflict;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.ChangeRequestSummaryService;
import ca.cihi.cims.service.IncompleteReportService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.RealizationService;
import ca.cihi.cims.service.ResourceAccessService;
import ca.cihi.cims.util.DiffMatchPatch;
import ca.cihi.cims.util.DiffMatchPatch.Diff;
import ca.cihi.cims.validator.ChangeRequestValidator;
import ca.cihi.cims.web.filter.CurrentContext;

@Controller
public abstract class ChangeSummaryBaseController {
	protected static final String XML_PROPERTY_POPUP = "/requestmanagement/changeSummary/xmlPropertyView";
	protected static final String VALIDATION_CONFLICT_POPUP = "/requestmanagement/changeSummary/validationProposedAndConflict";

	protected ChangeRequestService changeRequestService;
	@Autowired
	protected ChangeRequestValidator changeRequestValidator;
	@Autowired
	protected RealizationService realizationService;
	protected ChangeRequestSummaryService changeRequestSummaryService;
	protected ResourceAccessService resourcAccessService;
	protected LookupService lookupService;
	@Autowired
	protected IncompleteReportService incompleteReportService;
	@Autowired
	protected ContextProvider provider;
	@Autowired
	protected CurrentContext context;
	@Autowired
	protected ElementOperations elementOperations;

	protected final Log LOGGER = LogFactory.getLog(getClass());

	@RequestMapping("/acceptChangeRequest.htm")
	public String acceptChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		changeRequestService.acceptChangeRequest(changeRequestDTO, currentUser);
		model.addAttribute("updatedSuccessfully", true);

		return classificationSummary(model, changeRequestDTO, session);
	}

	@RequestMapping("/approveChangeRequest.htm")
	public String approveChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		changeRequestService.approveChangeRequest(changeRequestDTO, currentUser);
		model.addAttribute("updatedSuccessfully", true);
		return classificationSummary(model, changeRequestDTO, session);
	}

	public abstract String classificationSummary(final Model model, ChangeRequestDTO changeRequestDTO,
			final HttpSession session);

	protected String getBreadCrumbs(final Long contextId, final long conceptId) {
		String breadCrumbs = elementOperations.getIndexPath(contextId, conceptId);
		if (breadCrumbs != null) {
			int last = breadCrumbs.lastIndexOf(">");
			if (last == -1) {
				breadCrumbs = null;
			} else {
				breadCrumbs = breadCrumbs.substring(0, last);
			}
		}
		return breadCrumbs;
	}

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public ChangeRequestSummaryService getChangeRequestSummaryService() {
		return changeRequestSummaryService;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	@RequestMapping("/getRealizationState.htm")
	public void getRealizationProcessState(@RequestParam("changeRequestId") long changeRequestId,
			HttpServletResponse response) throws IOException {
		ChangeRequestRealization runningRealization = changeRequestService
				.findCurrentRunningRealizationByChangeRequestId(changeRequestId);
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.flushBuffer();
		PrintWriter out = response.getWriter();
		out.write(runningRealization.getProcessStep().getStepDescription());
		out.flush();
		out.close();
	}

	public ResourceAccessService getResourcAccessService() {
		return resourcAccessService;
	}

	@InitBinder
	public void initBinder(final WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping("/qaDoneChangeRequest.htm")
	public String qaDoneChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		changeRequestService.qaDoneChangeRequest(changeRequestDTO, currentUser);
		model.addAttribute("updatedSuccessfully", true);

		return classificationSummary(model, changeRequestDTO, session);
	}

	@RequestMapping("/readyForRealizeChangeRequest.htm")
	public String readyForRealizeChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		changeRequestService.readyForRealizeChangeRequest(changeRequestDTO, currentUser);
		model.addAttribute("updatedSuccessfully", true);

		return classificationSummary(model, changeRequestDTO, session);
	}

	/*
	 * the following codes are for the buttons on the change summary screen,
	 */

	@RequestMapping("/readyForTranslationChangeRequest.htm")
	public String readyForTranslationChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		changeRequestService.readyForTranslationChangeRequest(changeRequestDTO, currentUser);
		model.addAttribute("updatedSuccessfully", true);

		return classificationSummary(model, changeRequestDTO, session);
	}

	@RequestMapping("/readyForValidationChangeRequest.htm")
	public String readyForValidationChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		changeRequestService.readyForValidationChangeRequest(changeRequestDTO, currentUser);
		model.addAttribute("updatedSuccessfully", true);

		return classificationSummary(model, changeRequestDTO, session);
	}

	/*
	 * for user click the realize button on top, Don't remove this one, It is the right method
	 */
	@RequestMapping("/realizeChangeRequest.htm")
	public String realizeChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		ChangeRequestRealization runningRealization = changeRequestService.findRunningRealization();

		if (runningRealization != null) { // there is old realization running
			model.addAttribute("runningRealization", runningRealization);
		} else {
			ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(changeRequestDTO
					.getChangeRequestId());
			ContextAccess access = provider.findContext(contextIdentifier);
			context.makeCurrentContext(access);
			ChangeRequestRealization realization = realizationService.processRealizingChangeRequest(changeRequestDTO,
					currentUser);

			model.addAttribute("realization", realization);
		}
		return classificationSummary(model, changeRequestDTO, session);
	}

	@RequestMapping("/submitResolveConflicts.htm")
	public String resolveConflicts(final Model model, ResolveConflict resolveConflict, HttpSession session) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		Long changeRequestId = resolveConflict.getChangeRequestId();
		ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(resolveConflict
				.getChangeRequestId());
		ContextAccess access = provider.findContext(contextIdentifier);
		context.makeCurrentContext(access);
		changeRequestSummaryService.resolveConflicts(resolveConflict, currentUser);

		return viewResolveConflicts(model, changeRequestId, session);
	}

	@RequestMapping("/submitResolveIndexConflicts.htm")
	public String resolveIndexConflicts(final Model model, ResolveConflict resolveConflict, HttpSession session) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		Long changeRequestId = resolveConflict.getChangeRequestId();
		ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(resolveConflict
				.getChangeRequestId());
		ContextAccess access = provider.findContext(contextIdentifier);
		context.makeCurrentContext(access);

		changeRequestSummaryService.resolveIndexConflicts(resolveConflict, currentUser);

		return viewResolveIndexConflicts(model, changeRequestId, session);
	}

	@RequestMapping("/submitResolveSupplementConflicts.htm")
	public String resolveSupplementConflicts(final Model model, ResolveConflict resolveConflict, HttpSession session) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		Long changeRequestId = resolveConflict.getChangeRequestId();
		ContextIdentifier contextIdentifier = lookupService.findOpenContextByChangeRquestId(resolveConflict
				.getChangeRequestId());
		ContextAccess access = provider.findContext(contextIdentifier);
		context.makeCurrentContext(access);

		changeRequestSummaryService.resolveSupplementConflicts(resolveConflict, currentUser);

		return viewResolveSupplementConflicts(model, changeRequestId, session);
	}

	@RequestMapping("/sendBackChangeRequest.htm")
	public String sendBackChangeRequest(HttpSession session, ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {

		changeRequestValidator.validateSendBackButton(changeRequestDTO, result);
		if (result.hasErrors()) {
			FieldError fieldError = result.getFieldError("rationaleForIncomplete");
			String errorMsg = fieldError.getDefaultMessage();
			model.addAttribute("errorMsg", errorMsg);
			model.addAttribute("rationaleForIncomplete", changeRequestDTO.getRationaleForIncomplete());
		} else {
			User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
			changeRequestService.sendBackChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);
		}

		return classificationSummary(model, changeRequestDTO, session);
	}

	@Autowired
	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	@Autowired
	public void setChangeRequestSummaryService(ChangeRequestSummaryService changeRequestSummaryService) {
		this.changeRequestSummaryService = changeRequestSummaryService;
	}

	public void setChangeRequestValidator(ChangeRequestValidator changeRequestValidator) {
		this.changeRequestValidator = changeRequestValidator;
	}

	public void setContext(CurrentContext context) {
		this.context = context;
	}

	public void setElementOperations(ElementOperations elementOperations) {
		this.elementOperations = elementOperations;
	}

	public void setIncompleteReportService(IncompleteReportService incompleteReportService) {
		this.incompleteReportService = incompleteReportService;
	}

	@Autowired
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setProvider(ContextProvider provider) {
		this.provider = provider;
	}

	public void setRealizationService(RealizationService realizationService) {
		this.realizationService = realizationService;
	}

	@Autowired
	public void setResourcAccessService(ResourceAccessService resourcAccessService) {
		this.resourcAccessService = resourcAccessService;
	}

	@RequestMapping("/showProposedAndConflict.htm")
	public String showValidationProposedAndConflict(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, @RequestParam("validationId") long validationId,
			@RequestParam("elementVersionId") long elementVersionId, HttpSession session) {

		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);
		String classification = changeRequest.getBaseClassification();
		long contextId = changeContext.getContextId();

		// Get modified concept list
		ConceptModification conceptModification = changeRequestSummaryService.findModifiedConceptElementCode(
				changeRequestId, maxStructureId, validationId);
		// Long validationId = conceptModification.getValidationId();
		conceptModification.setProposedValidationChanges(changeRequestSummaryService.findProposedValidationChanges(
				classification, contextId, validationId, false));
		model.addAttribute("changeRequest", changeRequest);
		model.addAttribute("conceptModification", conceptModification);

		return VALIDATION_CONFLICT_POPUP;

	}

	@RequestMapping("/viewHtmlProperty.htm")
	public String viewHtmlProperty(final Model model, final @RequestParam("htmlPropertyId") long htmlPropertyId,
			final @RequestParam("fieldName") String fieldName, final @RequestParam("code") String code,
			final @RequestParam("category") String category) {

		LOGGER.debug("View Html Property: " + htmlPropertyId);

		String htmlString = changeRequestSummaryService.findHtmlTextFromHtmlPropertyId(htmlPropertyId);
		htmlString = htmlString == null ? "" : htmlString;

		model.addAttribute("xmlString", htmlString);
		model.addAttribute("fieldName", fieldName);
		model.addAttribute("code", code);
		model.addAttribute("category", category);

		return XML_PROPERTY_POPUP;
	}

	public abstract String viewResolveConflicts(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, HttpSession session);

	public abstract String viewResolveIndexConflicts(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, HttpSession session);

	public abstract String viewResolveSupplementConflicts(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, HttpSession session);

	@RequestMapping("/viewXmlProperty.htm")
	public String viewXmlProperty(final Model model, final @RequestParam("xmlPropertyId") long xmlPropertyId,
			final @RequestParam("fieldName") String fieldName, final @RequestParam("code") String code,
			final @RequestParam("category") String category) {

		LOGGER.debug("View Xml Property: " + xmlPropertyId);

		String xmlString = changeRequestSummaryService.findXmlTextFromXmlPropertyId(xmlPropertyId);
		xmlString = xmlString == null ? "" : xmlString;

		model.addAttribute("xmlString", xmlString);
		model.addAttribute("fieldName", fieldName);
		model.addAttribute("code", code);
		model.addAttribute("category", category);

		return XML_PROPERTY_POPUP;
	}

	@RequestMapping("/viewXmlPropertyDiff.htm")
	public String viewXmlPropertyDiff(final Model model,
			final @RequestParam("xmlPropertyIdOriginal") long xmlPropertyIdOriginal,
			final @RequestParam("xmlPropertyIdChanged") long xmlPropertyIdChanged,
			final @RequestParam("fieldName") String fieldName, final @RequestParam("code") String code,
			final @RequestParam("category") String category) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("View Xml Property originalId: " + xmlPropertyIdOriginal + ", changedId: "
					+ xmlPropertyIdChanged);
		}

		String xmlStringOriginal = changeRequestSummaryService.findXmlTextFromXmlPropertyId(xmlPropertyIdOriginal);
		String xmlStringChanged = changeRequestSummaryService.findXmlTextFromXmlPropertyId(xmlPropertyIdChanged);
		xmlStringOriginal = xmlStringOriginal == null ? "" : xmlStringOriginal;
		xmlStringChanged = xmlStringChanged == null ? "" : xmlStringChanged;

		DiffMatchPatch dif = new DiffMatchPatch();
		LinkedList<Diff> diffs = dif.diff_main(xmlStringOriginal, xmlStringChanged);
		dif.diff_cleanupSemantic(diffs);

		String diffHtml = dif.diff_prettyHtml(diffs).replace("&para;", "");

		model.addAttribute("xmlDifference", diffHtml);
		model.addAttribute("fieldName", fieldName);
		model.addAttribute("code", code);
		model.addAttribute("category", category);

		return XML_PROPERTY_POPUP;
	}
}
