package ca.cihi.cims.web.controller.changerequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.Language;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.CodeDescription;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.meta.AssigneeType;
import ca.cihi.cims.model.meta.ChangeRequestAssignment;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceCode;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ResourceAccessService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.validator.ChangeRequestValidator;
import ca.cihi.cims.web.customfunction.ResourceAccessFunctions;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
// @SessionAttributes( {WebConstants.REQUEST_VIEW_BEAN})
public class ChangeRequestController {
	private static final Log LOGGER = LogFactory.getLog(ChangeRequestController.class);
	public final static String AJAX_VERSION = "/getBaseClassificationVersions.htm";
	protected static final String LIST_VIEW = "listRequests";
	protected static final String CREATE_CHANGEREQUEST_VIEW = "createRequest";
	// protected static final String CREATE_CHANGEREQUEST_VIEW="requestmanagement/createRequest-body";
	// protected static final String CREATE_CHANGEREQUEST_SUCCESS = "createRequestSuccess";
	protected static final String MANAGE_CHANGEREQUEST = "managechangerequest"; // in changerequest-tiles
	protected static final String MANAGE_CHANGEREQUEST_NOTFOUND = "managechangerequest_notfound"; // in
																									// changerequest-tiles
	protected static final String PRINT_CHANGEREQUEST = "/requestmanagement/printChangeRequest"; // in
																									// changerequest-tiles
	protected static final String MANAGE_CLASSIFICATION = "/requestmanagement/manageClassification";
	
	protected static final String SEARCH_RESULT = "/requestmanagement/searchResults";

	// static final String EDIT_CONTENT_VIEW = "/classification/manage/tempForm";
	// static final String CREATE_CHANGEREQUEST_FROMIFRAME_SUCCESS = "/classification/manage/createRequestSuccess";
	private static final int MAX_AUTOCOMPLETE_SEARCH_RESULTS = 20;
	protected static final String JQUERY_UI_AUTOCOMPLETE_PARAMETER = "term";
	
	private static final int pageSize = 10;
	// private static final String CONTEXT_STATUS_CLOSED = "CLOSED";

	// private RequestService requestService;

	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	private ChangeRequestService changeRequestService;

	@Autowired
	private ChangeRequestValidator changeRequestValidator;

	@Autowired
	private AdminService adminService;

	@Autowired
	private ViewService viewService;

	private ResourceAccessService resourcAccessService;

	private LookupService lookupService;

	@RequestMapping("/addCommentForAdvice.htm")
	public String addCommentForAdvice(HttpSession session, @RequestParam("adviceId") Long adviceId,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model,
			HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);

		changeRequestValidator.validateAddCommentForAdviceButton(changeRequestDTO, adviceId, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.addCommentForAdvice(changeRequestDTO, adviceId, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	@RequestMapping("/addCommentForChangeRequest.htm")
	public String addCommentForChangeRequest(HttpSession session,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model,
			HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.updateChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	@RequestMapping("/addCommentForQuestion.htm")
	public String addCommentForQuestion(HttpSession session, @RequestParam("questionId") Long questionId,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model,
			HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);

		changeRequestValidator.validateAddCommentForQuestionButton(changeRequestDTO, questionId, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.addCommentForQuestion(changeRequestDTO, questionId, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	@RequestMapping("/assignAndTransferChangeRequest.htm")
	public String assignAndTransferChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.assignAndTransferChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	@RequestMapping("/assignChangeRequest.htm")
	public String assignChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.assignChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	/*
	 * the user click save button in uc10, for create a new change request
	 */
	@RequestMapping("/createChangeRequest.htm")
	public String createChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			// List<ObjectError> objectErrors =result.getGlobalErrors();
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);

			prepareDropDownsForCreate(model, changeRequestDTO);

			return CREATE_CHANGEREQUEST_VIEW;
		} else {

			changeRequestService.createChangeRequest(changeRequestDTO, currentUser);

			model.addAttribute("updatedSuccessfully", true);
			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	/*
	 * when the user click the defer button
	 */
	@RequestMapping("/deferChangeRequest.htm")
	public String deferChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		// changeRequestDTO.setCurrentUser(currentUser);
		changeRequestValidator.validateDeferButton(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			long deferredChangeRequestId = changeRequestDTO.getChangeRequestId();
			long deferredToBaseContextId = changeRequestDTO.getDeferredToBaseContextId();
			model.addAttribute("deferredToBaseContextId", deferredToBaseContextId);

			changeRequestService.deferChangeRequest(changeRequestDTO, currentUser);

			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, deferredChangeRequestId, session);
		}
	}
	
	@RequestMapping("/getSearchResults.htm")
	public String searchChangeRequests(Model model, @RequestParam("searchType") String searchType, @RequestParam("code") String code,
			HttpSession session, HttpServletRequest request) {
		List<ChangeRequest> changeRequests = new ArrayList<>();
		
		if (searchType.equals("code")) {
			
			changeRequests = changeRequestService.findChangeRequestsByCode(code);
			
		} else {
			
			changeRequests = changeRequestService.findChangeRequestsByLeadTerm(code);
			
		}
		int numOfChangeRequests = changeRequests.size();
		
		model.addAttribute("searchResults", changeRequests);
		model.addAttribute("pageSize", pageSize);
		model.addAttribute("resultSize", numOfChangeRequests);
		
		return SEARCH_RESULT;
	}


	@RequestMapping("/deleteChangeRequest.htm")
	public String deleteChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		// the validation for delete is not needed
		//changeRequestValidator.validate(changeRequestDTO, result);
		// if (result.hasErrors()) { // validation error
		// 	List<FieldError> fieldErrors = result.getFieldErrors();
		// 	model.addAttribute("fieldErrors", fieldErrors);
		// 	prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
		// 	return MANAGE_CHANGEREQUEST;
		// } else {
		// 	changeRequestService.deleteChangeRequest(changeRequestDTO, currentUser);
		// 	prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
		// 	return MANAGE_CHANGEREQUEST;
		// }
		changeRequestService.deleteChangeRequest(changeRequestDTO, currentUser);
		prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
		return MANAGE_CHANGEREQUEST;

	}

	@RequestMapping("/getAdviceForChangeRequest.htm")
	public String getAdviceForChangeRequest(HttpSession session,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@RequestParam(value = "adviceMsg", required = true) String adviceMsg,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model,
			HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);
		changeRequestDTO.getAdvice().setMessage(adviceMsg);

		changeRequestValidator.validateGetAdviceButton(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.getAdviceForChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	@RequestMapping(value = "refreshDeferDropDown", method = RequestMethod.GET)
	public @ResponseBody
	List<ContextIdentifier> getDeferVersionForClassification(
			@RequestParam("baseClassification") final String baseClassification,
			@RequestParam("baseContextId") final Long baseContextId) {
		Collection<ContextIdentifier> contextIdentifiers = lookupService
				.findNonClosedBaseContextIdentifiers(baseClassification);
		String baseVersionCode = null;
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			if (contextIdentifier.getContextId() == baseContextId) {
				baseVersionCode = contextIdentifier.getVersionCode();
				break;
			}
		}
		// need prepare the dropdown for Defer to Year
		List<ContextIdentifier> deferableContextIdentifiers = new ArrayList<ContextIdentifier>();
		// sort deferableContextIdentifiers , default to upcoming version year
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			// / years after the current version year
			if (contextIdentifier.getVersionCode().compareTo(baseVersionCode) > 0 && contextIdentifier.isVersionYear()) {
				deferableContextIdentifiers.add(contextIdentifier);
			}
		}
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			if (contextIdentifier.getVersionCode().compareTo(baseVersionCode) > 0 && !contextIdentifier.isVersionYear()) {
				deferableContextIdentifiers.add(contextIdentifier);
			}
		}

		return deferableContextIdentifiers;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public ResourceAccessService getResourcAccessService() {
		return resourcAccessService;
	}

	/*
	 * when user select classification from the drop down
	 */
	@RequestMapping(value = AJAX_VERSION, method = RequestMethod.GET)
	public @ResponseBody
	List<ContextIdentifier> getVersionForClassification(
			@RequestParam("baseClassification") final String baseClassification) {
		List<ContextIdentifier> contextIdentifiers = lookupService
				.findNonClosedBaseContextIdentifiers(baseClassification);
		return contextIdentifiers;
	}

	@InitBinder
	public void initBinder(final WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));

		binder.registerCustomEditor(List.class, "reviewGroups", new CustomCollectionEditor(List.class) {
			@Override
			protected Object convertElement(Object element) {
				Long id = null;
				Distribution reviewGroup = null;
				if (element instanceof String) {
					id = Long.parseLong((String) element);
				}
				if (id != null) {
					reviewGroup = new Distribution();
					reviewGroup.setDistributionlistid(id);
				}

				return reviewGroup;
			}
		});
	}

	/*
	 * When the user click the link from the menu
	 */
	@RequestMapping("/initCreateChangeRequest.htm")
	public String initCreateChangeRequest(Model model) {

		ChangeRequestDTO changeRequestDTO = new ChangeRequestDTO();
		model.addAttribute("changeRequestDTO", changeRequestDTO);
		prepareDropDownsForCreate(model, changeRequestDTO);
		return CREATE_CHANGEREQUEST_VIEW;

	}

	private Collection<String> loadBaseClassifications() {
		return lookupService.findBaseClassifications();
	}
	
	@RequestMapping("/manageChangeRequest.htm")
	public String manageChangeRequest(final Model model, @RequestParam("changeRequestId") Long changeRequestId,
			HttpSession session) {
		LOGGER.debug(" manageChangeRequest :" + changeRequestId);

		String errorMessage = null;
		try {
			ChangeRequestDTO changeRequestDTO = changeRequestService
					.findCourseGrainedChangeRequestDTOById(changeRequestId);
			if (changeRequestDTO == null) {
				LOGGER.info("changeRequestDTO is null!");
				errorMessage = "Change Request Not Found.";
				model.addAttribute("errorMessage", errorMessage);
				return MANAGE_CHANGEREQUEST_NOTFOUND;
			}

			if (changeRequestDTO.getStatus() == ChangeRequestStatus.DELETED) {
				LOGGER.info("changeRequestDTO is deleted!");
				errorMessage = "Change Request Not Found.";
				model.addAttribute("errorMessage", errorMessage);
				return MANAGE_CHANGEREQUEST_NOTFOUND;
			}

			ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
			/*
			 * ContextIdentifier baseContext = null; if (changeContext != null) { baseContext =
			 * lookupService.findContextIdentificationById(changeContext.getBaseStructureId()); }
			 */
			/*
			 * if (changeContext==null) { LOGGER.info("changeContext is null!"); errorMessage =
			 * "Change Request Not Found."; model.addAttribute("errorMessage", errorMessage); return
			 * MANAGE_CHANGEREQUEST_NOTFOUND; }
			 */

			User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
			/*
			 * if (currentUser==null) { LOGGER.info("currentUser is null!"); errorMessage = "Change Request Not Found.";
			 * model.addAttribute("errorMessage", errorMessage); return MANAGE_CHANGEREQUEST_NOTFOUND; }
			 */

			AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
			if (changeRequestDTO.getAssigneeUserId() != null) {
				if (currentUser.getUserId().longValue() == changeRequestDTO.getAssigneeUserId().longValue()) {
					assignmentTypeCode = AssignmentTypeCode.ASSIGNEE;
					if (currentUser.getUserId().longValue() == changeRequestDTO.getOwnerId().longValue()) {
						assignmentTypeCode = AssignmentTypeCode.OWNER_ASSIGNEE;
					}
				}
			}
			List<ResourceAccess> resourceAccesses = resourcAccessService.findCurrentUserResourceAccesses(
					currentUser.getRoles(), changeRequestDTO.getStatus(), assignmentTypeCode,
					ChangeRequestLanguage.fromString(changeRequestDTO.getLanguageCode()));
			currentUser.setResourceAccesses(resourceAccesses);
			if (changeRequestDTO.getStatus() == ChangeRequestStatus.DEFERRED) {
				changeRequestDTO.setDeferredToBaseContextId(changeRequestDTO.getDeferredTo().getBaseContextId());
			}

			String concurrentError = (String) session.getAttribute("concurrentError");
			if ("Y".equals(concurrentError)) {
				session.removeAttribute("concurrentError");
				model.addAttribute("concurrentError", true);
			}

			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);

			// model.addAttribute("baseContext", baseContext);
			model.addAttribute("activeTab", "manageChangeRequest");
			model.addAttribute("changeRequestDTO", changeRequestDTO);
			model.addAttribute("changeContext", changeContext);
			
			//CIMS-120: Allow admins or change request owner to add comments after request is CLOSED - APPROVED, while classification version year still open			
			boolean allowClosedCommenting = false;
			if((currentUser.getUserId() == changeRequestDTO.getOwner().getUserId() || currentUser.isAdministrator()) 
				&& lookupService.findOpenVersionYears(changeRequestDTO.getBaseClassification()).contains(changeRequestDTO.getBaseVersionCode()) 
				&& changeRequestDTO.getStatus().equals(ChangeRequestStatus.CLOSED_APPROVED)){
				allowClosedCommenting = true;
			}
			model.addAttribute("allowClosedCommenting", allowClosedCommenting);
			//CIMS-120 END
		} catch (Exception e) {
			new RuntimeException(e);
			// LOGGER.debug(" manageChangeRequest error:" + e.getMessage());
			LOGGER.info(" manageChangeRequest error:" + e.getMessage());
			errorMessage = "Change Request Not Found.";
			model.addAttribute("errorMessage", errorMessage);
			return MANAGE_CHANGEREQUEST_NOTFOUND;
		}

		return MANAGE_CHANGEREQUEST;
	}

	@RequestMapping("/manageClassification.htm")
	public String manageClassification(final Model model, @RequestParam("changeRequestId") Long changeRequestId,
			@RequestParam("language") String language, HttpSession session, HttpServletRequest request) {
		LOGGER.debug(" manageClassification :" + changeRequestId);
		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		
		ContextAccess context = contextProvider.findContext(changeContext);
		currentContext.makeCurrentContext(context);
        
		request.setAttribute("automaticContextParams", new CurrentContextParams()
                .urlParameters(context.getContextId()));

		
		if (language == null || language.equalsIgnoreCase(ChangeRequestLanguage.ALL.getCode())) {
			language = Language.ENGLISH.getCode();
		}

		List<CodeDescription> allBookIndexes = viewService.getAllBookIndexes(changeRequest.getBaseClassification(),
				changeRequest.getBaseContextId(), language);

		model.addAttribute("activeTab", "manageClassification");
		model.addAttribute("changeRequestDTO", changeRequest);
		model.addAttribute("changeContext", changeContext);
		model.addAttribute("allBookIndexes", allBookIndexes);
		model.addAttribute("language", language);
		if (request.getParameter("key") != null) {
			model.addAttribute("activateNode", request.getParameter("key"));
		}

		session.setAttribute("viewMode", Boolean.TRUE);
		return MANAGE_CLASSIFICATION;
	}

	/*
	 * prepare dropdown options for UC 10, create change request
	 */
	private void prepareDropDownsForCreate(Model model, ChangeRequestDTO changeRequestDTO) {
		Collection<String> baseClassifications = loadBaseClassifications();
		model.addAttribute("baseClassifications", baseClassifications);
		List<AuxTableValue> changeTypes = adminService.getAuxTableValues(AuxTableValue.AUX_CODE_CHANGE_TYPE);
		model.addAttribute("changeTypes", changeTypes);
		List<AuxTableValue> changeNatures = adminService.getAuxTableValues(AuxTableValue.AUX_CODE_CHANGE_NATURE);
		model.addAttribute("changeNatures", changeNatures);
		List<AuxTableValue> requestors = adminService.getAuxTableValues(AuxTableValue.AUX_CODE_REQUESTOR);
		model.addAttribute("requestors", requestors);

		Collection<ContextIdentifier> openedContextIdentifiers = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequestDTO.getBaseClassification());
		model.addAttribute("openedContextIdentifiers", openedContextIdentifiers);

		// by default DL - Classification Reviewer are mandatory, this list only have the Ids, we need the id and
		// distribution name
		List<Distribution> selectedReviewGroupIds = changeRequestDTO.getReviewGroups();

		List<Distribution> allReviewGroups = adminService.getReviewGroupList();

		for (Distribution reviewGroup : allReviewGroups) {
			if (reviewGroup.getDistributionlistid().longValue() == Distribution.DL_ID_Classification.longValue()) {
				allReviewGroups.remove(reviewGroup);
				break;
			}
		}
		List<Distribution> availableReviewGroups = new ArrayList<Distribution>();
		List<Distribution> selectedReviewGroups = new ArrayList<Distribution>();
		if (selectedReviewGroupIds != null && selectedReviewGroupIds.size() > 0) {
			for (Distribution dl : allReviewGroups) {
				boolean inSelected = false;
				for (Distribution selectedDL : selectedReviewGroupIds) {
					if (dl.getDistributionlistid().longValue() == selectedDL.getDistributionlistid().longValue()) {
						inSelected = true;
						break;
					}
				}
				if (inSelected) {
					selectedReviewGroups.add(dl);
				} else {
					availableReviewGroups.add(dl);
				}
			}
		} else {
			availableReviewGroups = allReviewGroups;
		}

		model.addAttribute("reviewGroups", availableReviewGroups);
		model.addAttribute("selectedReviewGroups", selectedReviewGroups);

	}

	private void prepareDropDownsForUpdate(Model model, ChangeRequestDTO changeRequestDTO, User currentUser) {
		prepareDropDownsForCreate(model, changeRequestDTO);
		// need call the getVersionForClassification to get all the version code,
		String baseClassification = changeRequestDTO.getBaseClassification();
		String baseVersionCode = changeRequestDTO.getBaseVersionCode();
		Collection<ContextIdentifier> contextIdentifiers = lookupService
				.findNonClosedBaseContextIdentifiers(baseClassification);

		ContextIdentifier currentBaseContext = lookupService.findContextIdentificationById(changeRequestDTO
				.getBaseContextId());

		boolean isCurrentBaseContextInDropDown = false;
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			if (contextIdentifier.getContextId() == currentBaseContext.getContextId()) {
				isCurrentBaseContextInDropDown = true;
				break;
			}
		}
		if (!isCurrentBaseContextInDropDown) { // add it in
			contextIdentifiers.add(currentBaseContext);
		}

		model.addAttribute("contextIdentifiers", contextIdentifiers);
		// need prepare the dropdown for Defer to Year
		List<ContextIdentifier> deferableContextIdentifiers = new ArrayList<ContextIdentifier>();
		// sort deferableContextIdentifiers , default to upcoming version year
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			// // years after the current version year
			if (contextIdentifier.getVersionCode().compareTo(baseVersionCode) > 0 && contextIdentifier.isVersionYear()) {
				deferableContextIdentifiers.add(contextIdentifier);
			}
		}
		for (ContextIdentifier contextIdentifier : contextIdentifiers) {
			if (contextIdentifier.getVersionCode().compareTo(baseVersionCode) > 0 && !contextIdentifier.isVersionYear()) {
				deferableContextIdentifiers.add(contextIdentifier);
			}
		}

		model.addAttribute("deferableContextIdentifiers", deferableContextIdentifiers);

		if (ResourceAccessFunctions.hasExecuteAccess(currentUser, ResourceCode.BUTTON_ASSIGN)) {
			// prepare the assignment DL/Users dropdown when user click assign button
			Map<String, List<ChangeRequestAssignment>> changeRequestAssignmentMetaMap = lookupService
					.loadAllChangeRequestAssignmentMetaDataToMap();
			String key = new StringBuilder(String.valueOf(changeRequestDTO.getStatus().getStatusId())).append(
					changeRequestDTO.getLanguageCode()).toString();
			List<ChangeRequestAssignment> validAssigneeDls = changeRequestAssignmentMetaMap.get(key);
			// need cache DL and User
			List<CodeDescription> allAssigneeRecipents = new ArrayList<CodeDescription>();
			if (validAssigneeDls != null && validAssigneeDls.size() > 0) {
				for (ChangeRequestAssignment changeRequestAssignment : validAssigneeDls) {
					if (changeRequestAssignment.getAssigneeType().equals(AssigneeType.BOTH)) { // both dl and the users
																								// in the dl
						Distribution dl = adminService.getDistributionById(changeRequestAssignment.getAssignToDL());
						allAssigneeRecipents.add(new CodeDescription("DL_" + dl.getDistributionlistid(), dl.getName()));
						List<User> usersInDL = adminService.getRecipientsByDistributionId(changeRequestAssignment
								.getAssignToDL());
						for (User userInDL : usersInDL) { // filter out current assignee
							if (changeRequestDTO.getAssigneeUserId() != null) {
								if (userInDL.getUserId().longValue() != changeRequestDTO.getAssigneeUserId()
										.longValue()) {
									allAssigneeRecipents.add(new CodeDescription("USER_" + userInDL.getUserId(),
											"&nbsp; &nbsp;&nbsp;&nbsp; " + userInDL.getUsername()));
								}
							} else {
								allAssigneeRecipents.add(new CodeDescription("USER_" + userInDL.getUserId(),
										"&nbsp; &nbsp;&nbsp;&nbsp; " + userInDL.getUsername()));
							}
						}

					} else {
						if (changeRequestAssignment.getAssigneeType().equals(AssigneeType.GROUP)) { // only dl, no need
																									// to get users in
																									// the dl
							Distribution dl = adminService.getDistributionById(changeRequestAssignment.getAssignToDL());
							allAssigneeRecipents.add(new CodeDescription("DL_" + dl.getDistributionlistid(), dl
									.getName()));
						} else {
							if (changeRequestAssignment.getAssigneeType().equals(AssigneeType.INDIVIDUAL)) { // only
																												// users
																												// in
																												// the
																												// DL
								List<User> usersInDL = adminService
										.getRecipientsByDistributionId(changeRequestAssignment.getAssignToDL());
								for (User userInDL : usersInDL) { // filter out current assignee
									if (changeRequestDTO.getAssigneeUserId() != null) {
										if (userInDL.getUserId().longValue() != changeRequestDTO.getAssigneeUserId()
												.longValue()) {
											allAssigneeRecipents.add(new CodeDescription(
													"USER_" + userInDL.getUserId(), userInDL.getUsername()));
										}
									} else {
										allAssigneeRecipents.add(new CodeDescription("USER_" + userInDL.getUserId(),
												userInDL.getUsername()));
									}
								}
							}
						}
					}
				}
			}
			model.addAttribute("allAssigneeRecipents", allAssigneeRecipents);
		}

		if (ResourceAccessFunctions.hasExecuteAccess(currentUser, ResourceCode.BUTTON_OWNER_TRANSFER)
				|| ResourceAccessFunctions.hasExecuteAccess(currentUser, ResourceCode.BUTTON_ASSIGNOWNER)) {
			List<Long> dlIds = new ArrayList<Long>();
			dlIds.add(Distribution.DL_ID_ADMINISTRATOR);
			// if it is 'ENG' request, target owner can be english content developers or administrators
			if (ChangeRequestLanguage.ENG.getCode().equalsIgnoreCase(changeRequestDTO.getLanguageCode())) {
				dlIds.add(Distribution.DL_ID_ENContentDeveloper);
			}
			// if it is 'FRA' request, target owner can be French content developers or administrators
			if (ChangeRequestLanguage.FRA.getCode().equalsIgnoreCase(changeRequestDTO.getLanguageCode())) {
				dlIds.add(Distribution.DL_ID_FRContentDeveloper);
			}
			if (ChangeRequestLanguage.ALL.getCode().equalsIgnoreCase(changeRequestDTO.getLanguageCode())) {
				dlIds.add(Distribution.DL_ID_ENContentDeveloper);
				dlIds.add(Distribution.DL_ID_FRContentDeveloper);
			}
			List<User> nextOwners = adminService.findDistinctUsersInDistributionListIds(dlIds);
			// filter out current owner
			for (User nextOwner : nextOwners) {
				if (changeRequestDTO.getOwnerId() != null) {
					if (nextOwner.getUserId().longValue() == changeRequestDTO.getOwnerId().longValue()) {
						nextOwners.remove(nextOwner);
						break;
					}
				}
			}
			model.addAttribute("allOwnerRecipents", nextOwners);
		}

		// every DLS except Review Groups
		if (ResourceAccessFunctions.hasExecuteAccess(currentUser, ResourceCode.BUTTON_GET_ADVICE)) {
			List<CodeDescription> allAdvisors = new ArrayList<CodeDescription>();

			List<Distribution> allAdvisorDLs = adminService.getAdvisorDistributionList();
			for (Distribution advisorDL : allAdvisorDLs) {
				allAdvisors.add(new CodeDescription("DL_" + advisorDL.getDistributionlistid(), advisorDL.getName()));
				List<User> usersInDistribution = adminService.getRecipientsByDistributionId(advisorDL
						.getDistributionlistid());
				for (User user : usersInDistribution) {
					if (user.getUserId().longValue() != currentUser.getUserId().longValue()) {
						allAdvisors.add(new CodeDescription("USER_" + user.getUserId(), "&nbsp; &nbsp;&nbsp;&nbsp; "
								+ user.getUsername()));
					}
				}
			}
			model.addAttribute("allAdvisors", allAdvisors);
		}
	}

	@RequestMapping("/printChangeRequest.htm")
	public String printChangeRequest(final Model model, @RequestParam("changeRequestId") Long changeRequestId,
			HttpSession session) {
		LOGGER.debug(" printChangeRequest :" + changeRequestId);
		ChangeRequestDTO changeRequestDTO = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
		if (changeRequestDTO.getAssigneeUserId() != null) {
			if (currentUser.getUserId().longValue() == changeRequestDTO.getAssigneeUserId().longValue()) {
				assignmentTypeCode = AssignmentTypeCode.ASSIGNEE;
				if (currentUser.getUserId().longValue() == changeRequestDTO.getOwnerId().longValue()) {
					assignmentTypeCode = AssignmentTypeCode.OWNER_ASSIGNEE;
				}
			}
		}
		List<ResourceAccess> resourceAccesses = resourcAccessService.findCurrentUserResourceAccesses(
				currentUser.getRoles(), changeRequestDTO.getStatus(), assignmentTypeCode,
				ChangeRequestLanguage.fromString(changeRequestDTO.getLanguageCode()));
		currentUser.setResourceAccesses(resourceAccesses);

		model.addAttribute("activeTab", "manageChangeRequest");
		model.addAttribute("changeRequestDTO", changeRequestDTO);
		model.addAttribute("changeContext", changeContext);
		return PRINT_CHANGEREQUEST;
	}

	@RequestMapping("/readyForAccept.htm")
	public String readyForAccept(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.readyForAccept(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	/*
	 * when the user click the reject button
	 */
	@RequestMapping("/rejectChangeRequest.htm")
	public String rejectChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		// changeRequestDTO.setCurrentUser(currentUser);
		changeRequestValidator.validateRejectButton(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.rejectChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	/*
	 * user click save button on UC11, for update a change request
	 */
	@RequestMapping("/saveChangeRequest.htm")
	public String saveChangeRequest(HttpSession session,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {

			changeRequestService.updateChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);
			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	// assignAndTransferChangeRequest.htm

	@RequestMapping("/searchPatternTopic.htm")
	public @ResponseBody
	List<String> searchPatternTopic(HttpServletRequest request, HttpServletResponse response) {
		String searchString = request.getParameter(JQUERY_UI_AUTOCOMPLETE_PARAMETER);
		String baseContextId = request.getParameter("baseContextId");
		if (baseContextId != null && StringUtils.isNotEmpty(baseContextId)) {
			return changeRequestService.searchPatternTopicByContext(searchString, Long.valueOf(baseContextId),
					MAX_AUTOCOMPLETE_SEARCH_RESULTS);
		} else {
			return changeRequestService.searchPatternTopic(searchString, MAX_AUTOCOMPLETE_SEARCH_RESULTS);
		}
	}

	@RequestMapping("/sendForReviewChangeRequest.htm")
	public String sendForReviewChangeRequest(HttpSession session, @RequestParam("questionIndex") int questionIndex,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model,
			HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);

		changeRequestValidator.validateSendForReviewButton(changeRequestDTO, questionIndex, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.sendQuestionForReviewer(changeRequestDTO, questionIndex, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@Autowired
	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setChangeRequestValidator(ChangeRequestValidator changeRequestValidator) {
		this.changeRequestValidator = changeRequestValidator;
	}

	@Autowired
	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	@Autowired
	public void setResourcAccessService(ResourceAccessService resourcAccessService) {
		this.resourcAccessService = resourcAccessService;
	}

	public void setViewService(ViewService viewService) {
		this.viewService = viewService;
	}

	/*
	 * only the initiator can click submit button on UC11,
	 */
	@RequestMapping("/submitChangeRequest.htm")
	public String submitChangeRequest(HttpSession session,
			@RequestParam(value = "lastActiveSectionDiv", required = false) String lastActiveSectionDiv,
			@Valid ChangeRequestDTO changeRequestDTO, final BindingResult result, Model model,
			HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		model.addAttribute("lastActiveSectionDiv", lastActiveSectionDiv);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.submitChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);
			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	/*
	 * when the user click the take over button
	 */
	@RequestMapping("/takeOverChangeRequest.htm")
	public String takeOverChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		// changeRequestDTO.setCurrentUser(currentUser);
		changeRequestValidator.validateTakeOverButton(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.takeOverChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);
			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	@RequestMapping("/transferChangeRequestOwnership.htm")
	public String transferChangeRequestOwnership(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		changeRequestValidator.validate(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.transferChangeRequestOwnerShip(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);

			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	/*
	 * when the user click the validate button
	 */
	@RequestMapping("/validateChangeRequest.htm")
	public String validateChangeRequest(HttpSession session, @Valid ChangeRequestDTO changeRequestDTO,
			final BindingResult result, Model model, HttpServletRequest request) {
		LOGGER.debug(" validate changeRequestId :" + changeRequestDTO.getChangeRequestId());
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);

		// changeRequestDTO.setCurrentUser(currentUser);
		changeRequestValidator.validateValidButton(changeRequestDTO, result);
		if (result.hasErrors()) { // validation error
			List<FieldError> fieldErrors = result.getFieldErrors();
			model.addAttribute("fieldErrors", fieldErrors);
			prepareDropDownsForUpdate(model, changeRequestDTO, currentUser);
			return MANAGE_CHANGEREQUEST;
		} else {
			changeRequestService.validateChangeRequest(changeRequestDTO, currentUser);
			model.addAttribute("updatedSuccessfully", true);
			return manageChangeRequest(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

}
