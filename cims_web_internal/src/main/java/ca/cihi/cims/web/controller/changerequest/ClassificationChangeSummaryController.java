package ca.cihi.cims.web.controller.changerequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.ConceptType;
import ca.cihi.cims.WebConstants;
import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextDefinition;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguage;
import ca.cihi.cims.model.changerequest.ChangeSummary;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ConflictProposedChange;
import ca.cihi.cims.model.changerequest.ConflictProposedIndexChange;
import ca.cihi.cims.model.changerequest.ConflictProposedSupplementChange;
import ca.cihi.cims.model.changerequest.IncompleteProperty;
import ca.cihi.cims.model.changerequest.IncompleteReport;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.ResolveConflict;
import ca.cihi.cims.model.resourceaccess.AssignmentTypeCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.web.filter.CurrentContext;
import ca.cihi.cims.web.filter.CurrentContextParams;

@Controller
public class ClassificationChangeSummaryController extends ChangeSummaryBaseController {
	protected static final String TABULAR_CHANGE_SUMMARY = "tabularChangeSummary";
	protected static final String PRINT_TABULAR_SUMMARY = "/requestmanagement/changeSummary/printTabularChangeSummary";
	protected static final String INCOMPLETE_TABULAR_REPORT = "/requestmanagement/changeSummary/incompleteReport";
	protected static final String TABULAR_CONFLICTS_RESOLVE_POPUP = "/requestmanagement/changeSummary/resolveConflicts";

	protected static final String INDEX_REFERENCE_POPUP = "/requestmanagement/changeSummary/indexReferenceView";
	protected static final String INDEX_REFERENCE_CONFLICT_POPUP = "/requestmanagement/changeSummary/indexReferenceProposedAndConflict";
	protected static final String INDEX_CHANGE_SUMMARY = "indexChangeSummary";
	protected static final String PRINT_INDEX_SUMMARY = "/requestmanagement/changeSummary/printIndexChangeSummary";
	protected static final String INDEX_CONFLICTS_RESOLVE_POPUP = "/requestmanagement/changeSummary/resolveIndexConflicts";
	protected static final String INCOMPLETE_INDEX_REPORT = "/requestmanagement/changeSummary/indexIncompleteReport";

	protected static final String SUPPLEMENT_CHANGE_SUMMARY = "supplementChangeSummary";
	protected static final String PRINT_SUPPLEMENT_SUMMARY = "/requestmanagement/changeSummary/printSupplementChangeSummary";
	protected static final String INCOMPLETE_SUPPLEMENT_REPORT = "/requestmanagement/changeSummary/supplementIncompleteReport";
	protected static final String SUPPLEMENT_CONFLICTS_RESOLVE_POPUP = "/requestmanagement/changeSummary/resolveSupplementConflicts";

	protected static final String NO_CONFLICT = "no_conflict";
	private static final long TABULAR_VALIDATION_ID = 0;

	protected final Log LOGGER = LogFactory.getLog(getClass());
	
	@Autowired
	private CurrentContext currentContext;
	@Autowired
	private ContextProvider contextProvider;

	@RequestMapping("/indexChangeSummary.htm")
	public String classificationIndexSummary(final Model model,
			final @RequestParam("changeRequestId") long changeRequestId, final HttpSession session) {

		LOGGER.debug("Classification Index Change Summary: " + changeRequestId);

		ChangeRequest changeRequest = setIndexChangeSummaryData(changeRequestId, session);

		boolean isOldestOpenContext = true;
		List<ContextIdentifier> nonClosedBaseContextIds = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequest.getBaseClassification());
		int baseContextVersionYear = Integer.parseInt(changeRequest.getBaseVersionCode());
		for (ContextIdentifier contextId : nonClosedBaseContextIds) {
			int openBaseContextVersionYear = Integer.parseInt(contextId.getVersionCode());
			if (baseContextVersionYear > openBaseContextVersionYear) {
				isOldestOpenContext = false;
				break;
			}
		}
		boolean isIncomplete = changeRequestService.isIncomplete(changeRequestId);
		model.addAttribute("isIncomplete", isIncomplete);
		model.addAttribute("isOldestOpenContext", isOldestOpenContext);
		model.addAttribute("activeTab", "classificationChangeSummary");
		model.addAttribute("changeRequestDTO", changeRequest);
		session.setAttribute("viewMode", Boolean.TRUE);

		return INDEX_CHANGE_SUMMARY;
	}

	@Override
	public String classificationSummary(Model model, ChangeRequestDTO changeRequestDTO, HttpSession session) {
		String conceptTypeCode = changeRequestDTO.getCategory().getCode();
		if (ConceptType.I.getCode().equals(conceptTypeCode)) {
			return classificationIndexSummary(model, changeRequestDTO.getChangeRequestId(), session);
		} else if (ConceptType.S.getCode().equals(conceptTypeCode)) {
			return classificationSupplementSummary(model, changeRequestDTO.getChangeRequestId(), session);
		} else {
			return classificationTabularSummary(model, changeRequestDTO.getChangeRequestId(), session);
		}
	}

	@RequestMapping("/supplementChangeSummary.htm")
	public String classificationSupplementSummary(final Model model,
			final @RequestParam("changeRequestId") long changeRequestId, final HttpSession session) {

		LOGGER.debug("Classification Supplement Change Summary: " + changeRequestId);

		ChangeRequest changeRequest = setSupplementChangeSummaryData(changeRequestId, session);

		boolean isOldestOpenContext = true;
		List<ContextIdentifier> nonClosedBaseContextIds = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequest.getBaseClassification());
		int baseContextVersionYear = Integer.parseInt(changeRequest.getBaseVersionCode());
		for (ContextIdentifier contextId : nonClosedBaseContextIds) {
			int openBaseContextVersionYear = Integer.parseInt(contextId.getVersionCode());
			if (baseContextVersionYear > openBaseContextVersionYear) {
				isOldestOpenContext = false;
				break;
			}
		}
		boolean isIncomplete = changeRequestService.isIncomplete(changeRequestId);
		model.addAttribute("isIncomplete", isIncomplete);
		model.addAttribute("isOldestOpenContext", isOldestOpenContext);
		model.addAttribute("activeTab", "classificationChangeSummary");
		model.addAttribute("changeRequestDTO", changeRequest);
		session.setAttribute("viewMode", Boolean.TRUE);

		return SUPPLEMENT_CHANGE_SUMMARY;
	}

	@RequestMapping("/tabularChangeSummary.htm")
	public String classificationTabularSummary(final Model model,
			final @RequestParam("changeRequestId") long changeRequestId, final HttpSession session) {
		LOGGER.debug("Classification Change Summary: " + changeRequestId);

		ChangeRequest changeRequest = setChangeSummaryData(changeRequestId, session);
		if (model.containsAttribute("rationaleForIncomplete")) {
			changeRequest.setRationaleForIncomplete((String) model.asMap().get("rationaleForIncomplete"));
		}

		boolean isOldestOpenContext = true;
		List<ContextIdentifier> nonClosedBaseContextIds = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequest.getBaseClassification());
		int baseContextVersionYear = Integer.parseInt(changeRequest.getBaseVersionCode());
		for (ContextIdentifier contextId : nonClosedBaseContextIds) {
			int openBaseContextVersionYear = Integer.parseInt(contextId.getVersionCode());
			if (baseContextVersionYear > openBaseContextVersionYear) {
				isOldestOpenContext = false;
				break;
			}
		}
		boolean isIncomplete = changeRequestService.isIncomplete(changeRequestId);
		model.addAttribute("isIncomplete", isIncomplete);
		model.addAttribute("isOldestOpenContext", isOldestOpenContext);
		model.addAttribute("activeTab", "classificationChangeSummary");
		model.addAttribute("changeRequestDTO", changeRequest);
		session.setAttribute("viewMode", Boolean.TRUE);

		return TABULAR_CHANGE_SUMMARY;
	}

	@RequestMapping("/printChangeSummary.htm")
	public String printClassificationSummary(final Model model,
			final @RequestParam("changeRequestId") long changeRequestId, final HttpSession session) {

		ChangeRequest changeRequest = setChangeSummaryData(changeRequestId, session);

		boolean isOldestOpenContext = true;
		List<ContextIdentifier> nonClosedBaseContextIds = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequest.getBaseClassification());
		int baseContextVersionYear = Integer.parseInt(changeRequest.getBaseVersionCode());
		for (ContextIdentifier contextId : nonClosedBaseContextIds) {
			int openBaseContextVersionYear = Integer.parseInt(contextId.getVersionCode());
			if (baseContextVersionYear > openBaseContextVersionYear) {
				isOldestOpenContext = false;
				break;
			}
		}
		model.addAttribute("isOldestOpenContext", isOldestOpenContext);
		model.addAttribute("activeTab", "classificationChangeSummary");
		model.addAttribute("changeRequestDTO", changeRequest);
		session.setAttribute("viewMode", Boolean.TRUE);

		return PRINT_TABULAR_SUMMARY;
	}

	@RequestMapping("/printIndexChangeSummary.htm")
	public String printIndexChangeSummary(final Model model,
			final @RequestParam("changeRequestId") long changeRequestId, final HttpSession session) {

		LOGGER.debug("Classification Change Summary: " + changeRequestId);

		ChangeRequest changeRequest = setIndexChangeSummaryData(changeRequestId, session);

		boolean isOldestOpenContext = true;
		List<ContextIdentifier> nonClosedBaseContextIds = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequest.getBaseClassification());
		int baseContextVersionYear = Integer.parseInt(changeRequest.getBaseVersionCode());
		for (ContextIdentifier contextId : nonClosedBaseContextIds) {
			int openBaseContextVersionYear = Integer.parseInt(contextId.getVersionCode());
			if (baseContextVersionYear > openBaseContextVersionYear) {
				isOldestOpenContext = false;
				break;
			}
		}
		model.addAttribute("isOldestOpenContext", isOldestOpenContext);
		model.addAttribute("activeTab", "classificationChangeSummary");
		model.addAttribute("changeRequestDTO", changeRequest);
		session.setAttribute("viewMode", Boolean.TRUE);

		return PRINT_INDEX_SUMMARY;
	}

	@RequestMapping("/printSupplementChangeSummary.htm")
	public String printSupplementChangeSummary(final Model model,
			final @RequestParam("changeRequestId") long changeRequestId, final HttpSession session) {

		LOGGER.debug("Classification Change Summary: " + changeRequestId);

		ChangeRequest changeRequest = setSupplementChangeSummaryData(changeRequestId, session);

		boolean isOldestOpenContext = true;
		List<ContextIdentifier> nonClosedBaseContextIds = lookupService
				.findNonClosedBaseContextIdentifiers(changeRequest.getBaseClassification());
		int baseContextVersionYear = Integer.parseInt(changeRequest.getBaseVersionCode());
		for (ContextIdentifier contextId : nonClosedBaseContextIds) {
			int openBaseContextVersionYear = Integer.parseInt(contextId.getVersionCode());
			if (baseContextVersionYear > openBaseContextVersionYear) {
				isOldestOpenContext = false;
				break;
			}
		}
		model.addAttribute("isOldestOpenContext", isOldestOpenContext);
		model.addAttribute("activeTab", "classificationChangeSummary");
		model.addAttribute("changeRequestDTO", changeRequest);
		session.setAttribute("viewMode", Boolean.TRUE);

		return PRINT_SUPPLEMENT_SUMMARY;
	}

	private List<ConceptModification> removeDuplicates(List<ConceptModification> dbConceptModifications) {
		List<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		List<Long> conceptIds = new ArrayList<Long>();

		for (ConceptModification conceptModification : dbConceptModifications) {
			if (!conceptIds.contains(conceptModification.getElementId())) {
				conceptIds.add(conceptModification.getElementId());
				conceptModifications.add(conceptModification);
			}
		}

		return conceptModifications;
	}

	private ChangeRequest setChangeSummaryData(final long changeRequestId, final HttpSession session) {

		ChangeRequest changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
		if (changeRequest.getAssigneeUserId() != null) {
			if (currentUser.getUserId().longValue() == changeRequest.getAssigneeUserId().longValue()) {
				assignmentTypeCode = AssignmentTypeCode.ASSIGNEE;
				if (currentUser.getUserId().longValue() == changeRequest.getOwnerId().longValue()) {
					assignmentTypeCode = AssignmentTypeCode.OWNER_ASSIGNEE;
				}
			}
		}
		List<ResourceAccess> resourceAccesses = resourcAccessService.findCurrentUserResourceAccesses(
				currentUser.getRoles(), changeRequest.getStatus(), assignmentTypeCode,
				ChangeRequestLanguage.fromString(changeRequest.getLanguageCode()));
		currentUser.setResourceAccesses(resourceAccesses);

		ChangeSummary changeSummary = new ChangeSummary();
		changeRequest.setChangeSummary(changeSummary);

		// Get the maxStructureid
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);
		String classification = changeRequest.getBaseClassification();

		// Process the realized validation changes and filter out conceptModifications with no changes
		ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		if (changeContext != null) {
			long contextId = changeContext.getContextId();

			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedConceptElementCodes(changeRequestId, maxStructureId);

			HashMap<String, ConceptModification> presentConceptMap = new LinkedHashMap<String, ConceptModification>();

			for (ConceptModification conceptModification : rawConceptModifications) {
				Long structureId = conceptModification.getStructureId();
				Long conceptId = conceptModification.getElementId();
				String conceptCode = conceptModification.getCode();
				Long validationId = conceptModification.getValidationId();

				// Combine tabular changes and validation changes of a concept as one ConceptModification object.
				ConceptModification presentConceptMod;
				if (presentConceptMap.containsKey(conceptCode)) {
					presentConceptMod = presentConceptMap.get(conceptCode);
				} else {
					presentConceptMod = conceptModification;
					presentConceptMap.put(conceptCode, presentConceptMod);
				}

				if (changeContext != null) {
					// Get the proposed status
					String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

					// if the concept is removed, present status only
					if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
						presentConceptMod.setProposedTabularChanges(changeRequestSummaryService
								.findProposedStatusChanges(contextId, conceptId));
					} else {
						// Get the proposed tabular and validation changes
						if (validationId == null || TABULAR_VALIDATION_ID == validationId) {
							presentConceptMod.setProposedTabularChanges(changeRequestSummaryService
									.findProposedTabularChanges(contextId, conceptId));
						} else {
							// only search the proposed validation changes for current open context
							if (maxStructureId.equals(structureId)) {
								presentConceptMod.setProposedValidationChanges(changeRequestSummaryService
										.findProposedValidationChanges(classification, contextId, validationId, true));
							}
						}
					}
				}

				// Get the realized status
				String realizedStatus = changeRequestSummaryService.findRealizedStatus(changeRequestId, conceptId);

				// if the concept is removed, present status only
				if (ConceptStatus.REMOVED.toString().equals(realizedStatus)) {
					presentConceptMod.setRealizedTabularChanges(changeRequestSummaryService.findRealizedStatusChanges(
							changeRequestId, conceptId));
				} else {
					// Get the realized tabular and validation changes
					if (validationId == null || TABULAR_VALIDATION_ID == validationId) {
						presentConceptMod.setRealizedTabularChanges(changeRequestSummaryService
								.findRealizedTabularChanges(changeRequestId, conceptId));
					} else {
						presentConceptMod.setRawRealizedValidationChanges(changeRequestSummaryService
								.findRealizedValidationChanges(classification, structureId, validationId));
					}
				}
			}

			for (String key : presentConceptMap.keySet()) {
				ConceptModification presentConceptMod = presentConceptMap.get(key);

				if (presentConceptMod.getRawRealizedValidationChanges() != null
						&& presentConceptMod.getRawRealizedValidationChanges().size() > 0) {
					presentConceptMod.setRealizedValidationChanges(changeRequestSummaryService
							.processRealizedValidationChange(presentConceptMod.getRawRealizedValidationChanges(),
									classification));
				}

				if ((presentConceptMod.getProposedTabularChanges() != null && presentConceptMod
						.getProposedTabularChanges().size() > 0)
						|| (presentConceptMod.getProposedValidationChanges() != null && presentConceptMod
								.getProposedValidationChanges().size() > 0)
						|| (presentConceptMod.getRealizedTabularChanges() != null && presentConceptMod
								.getRealizedTabularChanges().size() > 0)
						|| (presentConceptMod.getRealizedValidationChanges() != null && presentConceptMod
								.getRealizedValidationChanges().size() > 0)) {
					conceptModifications.add(presentConceptMod);
				}
			}
		}

		// Set the noChange flag
		if (conceptModifications.size() == 0) {
			changeSummary.setNoChange(true);
		}

		changeSummary.setConceptModifications(conceptModifications);

		return changeRequest;
	}

	private ChangeRequest setIndexChangeSummaryData(final long changeRequestId, final HttpSession session) {

		ChangeRequest changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
		if (changeRequest.getAssigneeUserId() != null) {
			if (currentUser.getUserId().longValue() == changeRequest.getAssigneeUserId().longValue()) {
				assignmentTypeCode = AssignmentTypeCode.ASSIGNEE;
				if (currentUser.getUserId().longValue() == changeRequest.getOwnerId().longValue()) {
					assignmentTypeCode = AssignmentTypeCode.OWNER_ASSIGNEE;
				}
			}
		}
		List<ResourceAccess> resourceAccesses = resourcAccessService.findCurrentUserResourceAccesses(
				currentUser.getRoles(), changeRequest.getStatus(), assignmentTypeCode,
				ChangeRequestLanguage.fromString(changeRequest.getLanguageCode()));
		currentUser.setResourceAccesses(resourceAccesses);

		ChangeSummary changeSummary = new ChangeSummary();
		changeRequest.setChangeSummary(changeSummary);

		// Get the maxStructureid
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

		// Process the realized changes and filter out conceptModifications with no changes
		ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		if (changeContext != null) {
			long contextId = changeContext.getContextId();

			// Get modified concept list
			List<ConceptModification> dbConceptModifications = changeRequestSummaryService
					.findModifiedIndexConceptElementCodes(changeRequestId, maxStructureId);

			// Remove the duplicate elements
			List<ConceptModification> rawConceptModifications = removeDuplicates(dbConceptModifications);

			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();

				conceptModification.setBreadCrumbs(trimBreadCrumbs(conceptModification.getBreadCrumbs()));

				if (changeContext != null) {
					// Get the proposed status
					String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

					// if the concept is removed, present status only
					if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
						conceptModification.setProposedIndexChanges(changeRequestSummaryService
								.findProposedStatusChanges(contextId, conceptId));
					} else {
						conceptModification.setProposedIndexChanges(changeRequestSummaryService
								.findProposedIndexChanges(contextId, conceptId));
					}
				}

				// Get the realized status
				String realizedStatus = changeRequestSummaryService.findRealizedStatus(changeRequestId, conceptId);

				// if the concept is removed, present status only
				if (ConceptStatus.REMOVED.toString().equals(realizedStatus)) {
					conceptModification.setRealizedIndexChanges(changeRequestSummaryService.findRealizedStatusChanges(
							changeRequestId, conceptId));

				} else {
					// Get the realized index changes
					conceptModification.setRealizedIndexChanges(changeRequestSummaryService.findRealizedIndexChanges(
							changeRequestId, conceptId));
				}
			}

			// filter out the none-change records
			for (ConceptModification presentConceptMod : rawConceptModifications) {
				if ((presentConceptMod.getProposedIndexChanges() != null && presentConceptMod.getProposedIndexChanges()
						.size() > 0)
						|| (presentConceptMod.getProposedIndexRefChange() != null)
						|| (presentConceptMod.getRealizedIndexChanges() != null && presentConceptMod
								.getRealizedIndexChanges().size() > 0)
						|| (presentConceptMod.getRealizedIndexRefChange() != null)) {
					conceptModifications.add(presentConceptMod);
				}
			}
		}

		// Set the noChange flag
		if (conceptModifications.size() == 0) {
			changeSummary.setNoChange(true);
		}

		changeSummary.setConceptModifications(conceptModifications);

		return changeRequest;
	}

	private ChangeRequest setSupplementChangeSummaryData(final long changeRequestId, final HttpSession session) {

		ChangeRequest changeRequest = changeRequestService.findCourseGrainedChangeRequestDTOById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		User currentUser = (User) session.getAttribute(WebConstants.CURRENT_USER);
		AssignmentTypeCode assignmentTypeCode = AssignmentTypeCode.NO_ASSIGNEE;
		if (changeRequest.getAssigneeUserId() != null) {
			if (currentUser.getUserId().longValue() == changeRequest.getAssigneeUserId().longValue()) {
				assignmentTypeCode = AssignmentTypeCode.ASSIGNEE;
				if (currentUser.getUserId().longValue() == changeRequest.getOwnerId().longValue()) {
					assignmentTypeCode = AssignmentTypeCode.OWNER_ASSIGNEE;
				}
			}
		}
		List<ResourceAccess> resourceAccesses = resourcAccessService.findCurrentUserResourceAccesses(
				currentUser.getRoles(), changeRequest.getStatus(), assignmentTypeCode,
				ChangeRequestLanguage.fromString(changeRequest.getLanguageCode()));
		currentUser.setResourceAccesses(resourceAccesses);

		ChangeSummary changeSummary = new ChangeSummary();
		changeRequest.setChangeSummary(changeSummary);

		// Get the maxStructureid
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

		// Process the realized changes and filter out conceptModifications with no changes
		ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
		if (changeContext != null) {
			long contextId = changeContext.getContextId();

			String language = changeRequest.getLanguageCode();

			// Get modified concept list
			List<ConceptModification> dbConceptModifications = changeRequestSummaryService
					.findModifiedSupplementConceptElementCodes(changeRequestId, maxStructureId, language);

			// Remove the duplicate elements
			List<ConceptModification> rawConceptModifications = removeDuplicates(dbConceptModifications);

			for (ConceptModification conceptModification : rawConceptModifications) {

				Long conceptId = conceptModification.getElementId();

				conceptModification.setBreadCrumbs(trimBreadCrumbs(conceptModification.getBreadCrumbs()));

				if (changeContext != null) {
					// Get the proposed status
					String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

					// if the concept is removed, present status only
					if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
						conceptModification.setProposedSupplementChanges(changeRequestSummaryService
								.findProposedStatusChanges(contextId, conceptId));
					} else {
						conceptModification.setProposedSupplementChanges(changeRequestSummaryService
								.findProposedSupplementChanges(contextId, conceptId, language));
					}
				}

				// Get the realized status
				String realizedStatus = changeRequestSummaryService.findRealizedStatus(changeRequestId, conceptId);

				// if the concept is removed, present status only
				if (ConceptStatus.REMOVED.toString().equals(realizedStatus)) {
					conceptModification.setRealizedSupplementChanges(changeRequestSummaryService
							.findRealizedStatusChanges(changeRequestId, conceptId));

				} else {
					// Get the realized supplement changes
					conceptModification.setRealizedSupplementChanges(changeRequestSummaryService
							.findRealizedSupplementChanges(changeRequestId, conceptId, language));
				}
			}

			// filter out the none-change records
			for (ConceptModification presentConceptMod : rawConceptModifications) {
				if ((presentConceptMod.getProposedSupplementChanges() != null && presentConceptMod
						.getProposedSupplementChanges().size() > 0)
						|| (presentConceptMod.getRealizedSupplementChanges() != null && presentConceptMod
								.getRealizedSupplementChanges().size() > 0)) {
					conceptModifications.add(presentConceptMod);
				}
			}
		}

		// Set the noChange flag
		if (conceptModifications.size() == 0) {
			changeSummary.setNoChange(true);
		}

		changeSummary.setConceptModifications(conceptModifications);

		return changeRequest;
	}

	@RequestMapping("/showIndexReferenceProposedAndConflict.htm")
	public String showIndexReferenceProposedAndConflict(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, @RequestParam("elementId") long elementId,
			HttpSession session) {

		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

		ConceptModification conceptModification = changeRequestSummaryService.findModifiedIndexConceptElementCode(
				changeRequestId, maxStructureId, elementId);
		Long conceptId = conceptModification.getElementId();
		conceptModification.setBreadCrumbs(trimBreadCrumbs(conceptModification.getBreadCrumbs()));

		if (changeContext != null) {
			long contextId = changeContext.getContextId();
			// Get the proposed status
			String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

			// if the concept is removed, present status only
			if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
				conceptModification.setProposedIndexChanges(changeRequestSummaryService.findProposedStatusChanges(
						contextId, conceptId));
			} else {
				conceptModification.setProposedIndexChanges(changeRequestSummaryService.findProposedIndexChanges(
						contextId, conceptId));
			}
		}

		model.addAttribute("changeRequest", changeRequest);

		model.addAttribute("conceptModification", conceptModification);

		return INDEX_REFERENCE_CONFLICT_POPUP;

	}

	private String trimBreadCrumbs(String breadCrumbs) {
		if (breadCrumbs != null) {
			int last = breadCrumbs.lastIndexOf(">");
			if (last == -1) {
				breadCrumbs = "";
			} else {
				breadCrumbs = breadCrumbs.substring(0, last);
			}
		}
		return breadCrumbs;
	}

	@RequestMapping("/viewIndexIncompleteReport.htm")
	public String viewIndexIncompleteReport(final Model model, @RequestParam("changeRequestId") long changeRequestId,
			HttpSession session) {
		LOGGER.debug("View Incomplete Report: " + changeRequestId);

		IncompleteReport incompleteReport = new IncompleteReport();

		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);

		incompleteReport.setChangeRequest(changeRequest);

		ArrayList<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		// Only show incomplete report for open context
		if (changeContext != null) {

			// Get the maxStructureid
			Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedIndexConceptElementCodes(changeRequestId, maxStructureId);

			List<String> codeList = new ArrayList<String>();
			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				Long contextId = conceptModification.getStructureId();

				String code = conceptModification.getCode();

				if (!codeList.contains(code)) {
					codeList.add(code);
					List<IncompleteProperty> incPropertyList = incompleteReportService.checkIndexConcept(
							changeContext.getContextId(), conceptId, code);

					// Set hierarchical path
					for (IncompleteProperty incompleteProperty : incPropertyList) {
						incompleteProperty.setBreadCrumbs(trimBreadCrumbs(conceptModification.getBreadCrumbs()));
					}

					incompleteProperties.addAll(incPropertyList);
				}
			}
		}

		incompleteReport.setIncomProperties(incompleteProperties);

		model.addAttribute("incompleteReport", incompleteReport);

		return INCOMPLETE_INDEX_REPORT;

	}

	@RequestMapping("/viewIndexReference.htm")
	public String viewIndexReference(final Model model, @RequestParam("changeRequestId") long changeRequestId,
			@RequestParam("indexRefId") String indexRefId) {
		LOGGER.debug("View Index Reference: " + indexRefId);

		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

		String indexIdString = indexRefId.substring(indexRefId.lastIndexOf("/") + 1);
		Long indexId = (indexIdString != null) ? Long.parseLong(indexIdString) : 0;

		String indexDesc = changeRequestSummaryService.findIndexDesc(maxStructureId, indexId);
		String indexPath = getBreadCrumbs(maxStructureId, indexId);

		model.addAttribute("indexTerm", indexDesc);
		model.addAttribute("indexPath", indexPath);

		return INDEX_REFERENCE_POPUP;

	}

	@Override
	@RequestMapping("/resolveConflicts.htm")
	public String viewResolveConflicts(final Model model, @RequestParam("changeRequestId") long changeRequestId,
			HttpSession session) {
		LOGGER.debug("resolveConflicts: " + changeRequestId);
		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);
		String classification = changeRequest.getBaseClassification();

		List<ConflictProposedChange> conflictChanges = new ArrayList<ConflictProposedChange>();

		if (changeContext != null) {
			long contextId = changeContext.getContextId();

			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedConceptElementCodes(changeRequestId, maxStructureId);

			HashMap<String, ConceptModification> presentConceptMap = new LinkedHashMap<String, ConceptModification>();

			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				String conceptCode = conceptModification.getCode();
				Long validationId = conceptModification.getValidationId();

				// Combine tabular changes and validation changes of a concept as one ConceptModification object.
				ConceptModification presentConceptMod;
				if (presentConceptMap.containsKey(conceptCode)) {
					presentConceptMod = presentConceptMap.get(conceptCode);
				} else {
					presentConceptMod = conceptModification;
					presentConceptMap.put(conceptCode, presentConceptMod);
				}
				// Get the proposed status
				String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

				// if the concept is removed, present status only
				if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
					presentConceptMod.setProposedAndConflictTabularChanges(changeRequestSummaryService
							.findProposedStatusChanges(contextId, conceptId));
				} else {
					// Get the proposed tabular and validation changes
					if (validationId == null || TABULAR_VALIDATION_ID == validationId) {
						presentConceptMod.setProposedAndConflictTabularChanges(changeRequestSummaryService
								.findProposedAndConflictTabularChanges(contextId, conceptId));
					} else {
						presentConceptMod.setProposedAndConflictValidationChanges(changeRequestSummaryService
								.findProposedAndConflictValidationChanges(classification, contextId, validationId));
					}
				}
			}

			// Filter out conceptModifications with no changes
			ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
			for (String key : presentConceptMap.keySet()) {
				ConceptModification presentConceptMod = presentConceptMap.get(key);
				if ((presentConceptMod.getProposedAndConflictTabularChanges() != null && presentConceptMod
						.getProposedAndConflictTabularChanges().size() > 0)
						|| (presentConceptMod.getProposedAndConflictValidationChanges() != null && presentConceptMod
								.getProposedAndConflictValidationChanges().size() > 0)) {
					conceptModifications.add(presentConceptMod);
				}
			}

			for (ConceptModification conceptModification : conceptModifications) {
				List<ProposedChange> proposedAndConflictTabularChanges = conceptModification
						.getProposedAndConflictTabularChanges();
				List<ProposedChange> proposedAndConflictValidationChanges = conceptModification
						.getProposedAndConflictValidationChanges();
				if (proposedAndConflictTabularChanges != null && proposedAndConflictTabularChanges.size() > 0) {
					for (ProposedChange proposedTabularChange : proposedAndConflictTabularChanges) {
						if (!NO_CONFLICT.equalsIgnoreCase(proposedTabularChange.getConflictValue())) {
							ConflictProposedChange conflictProposedChange = new ConflictProposedChange();
							conflictProposedChange.setChangeType(ConflictProposedChange.ChangeType_Tabular);
							conflictProposedChange.setElementId(conceptModification.getElementId());
							conflictProposedChange.setValidationId(conceptModification.getValidationId());
							conflictProposedChange.setCode(conceptModification.getCode());
							conflictProposedChange.copyFrom(proposedTabularChange);
							conflictChanges.add(conflictProposedChange);
						}
					}
				}
				if (proposedAndConflictValidationChanges != null && proposedAndConflictValidationChanges.size() > 0) {
					for (ProposedChange proposedValidationChange : proposedAndConflictValidationChanges) {
						if (StringUtils.isNotBlank(proposedValidationChange.getConflictValue())
								&& !NO_CONFLICT.equalsIgnoreCase(proposedValidationChange.getConflictValue())) {
							ConflictProposedChange conflictProposedChange = new ConflictProposedChange();
							conflictProposedChange.setChangeType(ConflictProposedChange.ChangeType_Validation);
							conflictProposedChange.setElementId(conceptModification.getElementId());
							conflictProposedChange.setValidationId(conceptModification.getValidationId());
							conflictProposedChange.setCode(conceptModification.getCode());
							conflictProposedChange.copyFrom(proposedValidationChange);
							conflictChanges.add(conflictProposedChange);
						}
					}
				}

			}
		}

		ResolveConflict resolveConflict = new ResolveConflict();
		resolveConflict.setCurrentContextId(maxStructureId);
		resolveConflict.setConflictChanges(conflictChanges);
		model.addAttribute("changeRequest", changeRequest);
		model.addAttribute("resolveConflict", resolveConflict);
		return TABULAR_CONFLICTS_RESOLVE_POPUP;

	}

	@Override
	@RequestMapping("/resolveIndexConflicts.htm")
	public String viewResolveIndexConflicts(final Model model, @RequestParam("changeRequestId") long changeRequestId,
			HttpSession session) {
		LOGGER.debug("viewResolveIndexConflicts: " + changeRequestId);
		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

		List<ConflictProposedIndexChange> conflictIndexChanges = new ArrayList<ConflictProposedIndexChange>();

		if (changeContext != null) {
			long contextId = changeContext.getContextId();
			ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedIndexConceptElementCodes(changeRequestId, maxStructureId);

			// HashMap<String, ConceptModification> presentConceptMap = new LinkedHashMap<String,
			// ConceptModification>();

			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				conceptModification.setBreadCrumbs(trimBreadCrumbs(conceptModification.getBreadCrumbs()));

				if (changeContext != null && conceptModification.getStructureId() == contextId) {
					// Get the proposed status
					String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

					// if the concept is removed, present status only
					if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
						conceptModification.setProposedAndConflictIndexChanges(changeRequestSummaryService
								.findProposedStatusChanges(contextId, conceptId));
					} else {
						conceptModification.setProposedAndConflictIndexChanges(changeRequestSummaryService
								.findProposedAndConflictIndexChanges(contextId, conceptId));
					}
				}
			}
			// filter out the none-change records
			for (ConceptModification presentConceptMod : rawConceptModifications) {
				if ((presentConceptMod.getProposedAndConflictIndexChanges() != null && presentConceptMod
						.getProposedAndConflictIndexChanges().size() > 0)
						|| presentConceptMod.getProposedIndexRefChange() != null) {
					conceptModifications.add(presentConceptMod);
				}
			}

			for (ConceptModification conceptModification : conceptModifications) {
				List<ProposedChange> proposedAndConflictIndexChanges = conceptModification
						.getProposedAndConflictIndexChanges();

				if (proposedAndConflictIndexChanges != null && proposedAndConflictIndexChanges.size() > 0) {
					for (ProposedChange proposedIndexChange : proposedAndConflictIndexChanges) {
						if (!NO_CONFLICT.equalsIgnoreCase(proposedIndexChange.getConflictValue())) {
							ConflictProposedIndexChange conflictProposedIndexChange = new ConflictProposedIndexChange();
							conflictProposedIndexChange.setChangeType(ConflictProposedIndexChange.ChangeType_Index);
							conflictProposedIndexChange.setElementId(conceptModification.getElementId());

							conflictProposedIndexChange.setIndexTerm(conceptModification.getCode());
							conflictProposedIndexChange.setHierarchicalPath(conceptModification.getBreadCrumbs());
							conflictProposedIndexChange.copyFrom(proposedIndexChange);
							conflictIndexChanges.add(conflictProposedIndexChange);
						}
					}
				}

				ProposedChange proposedAndConflictIndexRefChange = conceptModification.getProposedIndexRefChange();
				if (proposedAndConflictIndexRefChange != null
						&& !NO_CONFLICT.equalsIgnoreCase(proposedAndConflictIndexRefChange.getConflictValue())) {
					ConflictProposedIndexChange conflictProposedIndexChange = new ConflictProposedIndexChange();
					conflictProposedIndexChange.setChangeType(ConflictProposedIndexChange.ChangeType_IndexRef);
					conflictProposedIndexChange.setElementId(conceptModification.getElementId());

					conflictProposedIndexChange.setIndexTerm(conceptModification.getCode());
					conflictProposedIndexChange.setHierarchicalPath(conceptModification.getBreadCrumbs());
					conflictProposedIndexChange.copyFrom(proposedAndConflictIndexRefChange);
					conflictIndexChanges.add(conflictProposedIndexChange);
				}

			}
		}

		ResolveConflict resolveConflict = new ResolveConflict();
		resolveConflict.setCurrentContextId(maxStructureId);
		resolveConflict.setConflictIndexChanges(conflictIndexChanges);
		model.addAttribute("changeRequest", changeRequest);
		model.addAttribute("resolveConflict", resolveConflict);
		return INDEX_CONFLICTS_RESOLVE_POPUP;

	}

	@Override
	@RequestMapping("/resolveSupplementConflicts.htm")
	public String viewResolveSupplementConflicts(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, HttpSession session) {
		LOGGER.debug("viewResolveSupplementConflicts: " + changeRequestId);
		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);
		Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

		List<ConflictProposedSupplementChange> conflictSupplementChanges = new ArrayList<ConflictProposedSupplementChange>();

		if (changeContext != null) {
			long contextId = changeContext.getContextId();
			ArrayList<ConceptModification> conceptModifications = new ArrayList<ConceptModification>();
			String language = changeRequest.getLanguageCode();
			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedSupplementConceptElementCodes(changeRequestId, maxStructureId, language);

			// HashMap<String, ConceptModification> presentConceptMap = new LinkedHashMap<String,
			// ConceptModification>();

			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				conceptModification.setBreadCrumbs(trimBreadCrumbs(conceptModification.getBreadCrumbs()));

				if (changeContext != null && conceptModification.getStructureId() == contextId) {
					// Get the proposed status
					String proposedStatus = changeRequestSummaryService.findProposedStatus(contextId, conceptId);

					// if the concept is removed, present status only
					if (ConceptStatus.REMOVED.toString().equals(proposedStatus)) {
						conceptModification.setProposedAndConflictSupplementChanges(changeRequestSummaryService
								.findProposedStatusChanges(contextId, conceptId));
					} else {
						conceptModification.setProposedAndConflictSupplementChanges(changeRequestSummaryService
								.findProposedAndConflictSupplementChanges(contextId, conceptId, language));
					}
				}
			}
			// filter out the none-change records
			for (ConceptModification presentConceptMod : rawConceptModifications) {
				if (presentConceptMod.getProposedAndConflictSupplementChanges() != null
						&& presentConceptMod.getProposedAndConflictSupplementChanges().size() > 0) {
					conceptModifications.add(presentConceptMod);
				}
			}

			for (ConceptModification conceptModification : conceptModifications) {
				List<ProposedChange> proposedAndConflictSupplementChanges = conceptModification
						.getProposedAndConflictSupplementChanges();

				if (proposedAndConflictSupplementChanges != null && proposedAndConflictSupplementChanges.size() > 0) {
					for (ProposedChange proposedSupplementChange : proposedAndConflictSupplementChanges) {
						if (!NO_CONFLICT.equalsIgnoreCase(proposedSupplementChange.getConflictValue())) {
							ConflictProposedSupplementChange conflictProposedSupplementChange = new ConflictProposedSupplementChange();
							conflictProposedSupplementChange
									.setChangeType(ConflictProposedSupplementChange.CHANGETYPE_SUPPLEMENT);
							conflictProposedSupplementChange.setElementId(conceptModification.getElementId());

							conflictProposedSupplementChange.setSupplement(conceptModification.getCode());
							conflictProposedSupplementChange.setHierarchicalPath(conceptModification.getBreadCrumbs());
							conflictProposedSupplementChange.copyFrom(proposedSupplementChange);
							conflictSupplementChanges.add(conflictProposedSupplementChange);
						}
					}
				}
			}
		}

		ResolveConflict resolveConflict = new ResolveConflict();
		resolveConflict.setCurrentContextId(maxStructureId);
		resolveConflict.setConflictSupplementChanges(conflictSupplementChanges);
		model.addAttribute("changeRequest", changeRequest);
		model.addAttribute("resolveConflict", resolveConflict);
		return SUPPLEMENT_CONFLICTS_RESOLVE_POPUP;

	}

	@RequestMapping("/viewSupplementIncompleteReport.htm")
	public String viewSupplementIncompleteReport(final Model model,
			@RequestParam("changeRequestId") long changeRequestId, HttpSession session) {
		LOGGER.debug("View Incomplete Report: " + changeRequestId);

		IncompleteReport incompleteReport = new IncompleteReport();

		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);

		incompleteReport.setChangeRequest(changeRequest);

		ArrayList<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		// Only show incomplete report for open context
		if (changeContext != null) {

			// Get the maxStructureid
			Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);
			String language = changeRequest.getLanguageCode();

			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedSupplementConceptElementCodes(changeRequestId, maxStructureId, language);

			List<String> codeList = new ArrayList<String>();
			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				String code = conceptModification.getCode();

				if (!codeList.contains(code)) {
					codeList.add(code);
					List<IncompleteProperty> incPropertyList = incompleteReportService.checkSupplementConcept(
							changeContext.getContextId(), conceptId, code);

					// Set hierarchical path
					for (IncompleteProperty incompleteProperty : incPropertyList) {
						incompleteProperty.setBreadCrumbs(trimBreadCrumbs(incompleteProperty.getBreadCrumbs()));
					}

					incompleteProperties.addAll(incPropertyList);
				}
			}
		}

		incompleteReport.setIncomProperties(incompleteProperties);

		model.addAttribute("incompleteReport", incompleteReport);

		return INCOMPLETE_SUPPLEMENT_REPORT;

	}

	@RequestMapping("/viewIncompleteReport.htm")
	public String viewTabularIncompleteReport(final Model model, @RequestParam("changeRequestId") long changeRequestId,
			HttpSession session) {
		LOGGER.debug("View Incomplete Report: " + changeRequestId);

		IncompleteReport incompleteReport = new IncompleteReport();

		ChangeRequest changeRequest = changeRequestService.findLightWeightChangeRequestById(changeRequestId);
		ContextIdentifier changeContext = lookupService.findOpenContextByChangeRquestId(changeRequestId);

		incompleteReport.setChangeRequest(changeRequest);

		ArrayList<IncompleteProperty> incompleteProperties = new ArrayList<IncompleteProperty>();

		// Only show incomplete report for open context
		if (changeContext != null) {

			// Get the maxStructureid
			Long maxStructureId = changeRequestSummaryService.findMaxStructureId(changeRequestId);

			// Get modified concept list
			List<ConceptModification> rawConceptModifications = changeRequestSummaryService
					.findModifiedConceptElementCodes(changeRequestId, maxStructureId);

			List<String> codeList = new ArrayList<String>();
			for (ConceptModification conceptModification : rawConceptModifications) {
				Long conceptId = conceptModification.getElementId();
				String code = conceptModification.getCode();

				if (!codeList.contains(code)) {
					codeList.add(code);
					incompleteProperties.addAll(incompleteReportService.checkTabularConcept(
							changeContext.getContextId(), conceptId, changeContext.getIsVersionYear(), code));
				}
			}
		}

		incompleteReport.setIncomProperties(incompleteProperties);

		model.addAttribute("incompleteReport", incompleteReport);

		return INCOMPLETE_TABULAR_REPORT;

	}

}
