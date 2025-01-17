package ca.cihi.cims.service;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ChangeRequestRealization;
import ca.cihi.cims.model.changerequest.ChangeRequestRealizationStatus;
import ca.cihi.cims.model.changerequest.ChangeRequestRealizationStep;
import ca.cihi.cims.model.changerequest.OptimisticLock;
import ca.cihi.cims.service.synchronization.SynchronizationService;

public class RealizationServiceImpl implements RealizationService {

	private static final Log LOGGER = LogFactory.getLog(RealizationServiceImpl.class);

	private ChangeRequestService changeRequestService;
	private ChangeRequestSummaryService changeRequestSummaryService;
	@Autowired
	private ContextProvider contextProvider;
	private LookupService lookupService;

	private SynchronizationService synchronizationService;

	// -----------------------------------------------------------------

	public ChangeRequestService getChangeRequestService() {
		return changeRequestService;
	}

	public ChangeRequestSummaryService getChangeRequestSummaryService() {
		return changeRequestSummaryService;
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	public SynchronizationService getSynchronizationService() {
		return synchronizationService;
	}

	@Override
	public ChangeRequestRealization processRealizingChangeRequest(ChangeRequest changeRequest, User currentUser) {
		ChangeRequestRealization realization = changeRequestService.createChangeRequestRealization(changeRequest);
		// step 1: synchrolize view
		realization.setProcessStep(ChangeRequestRealizationStep.STEP_1_SYNC_VIEW);
		changeRequestService.updateChangeRequestRealization(realization);
		// do sync view
		try {
			synchronizationService.synchronize(new OptimisticLock(Long.MIN_VALUE), currentUser,
					changeRequest.getChangeRequestId());
		} catch (Exception e) {
			LOGGER.info("synchronization exception", e);
			realization.setRealizationStatus(ChangeRequestRealizationStatus.PRE_PROCESS_FAILED);
			realization.setFailedReason("synchronize view failed");
			changeRequestService.updateChangeRequestRealization(realization);
			return realization;
		}
		// step 2: check incomplete
		realization.setProcessStep(ChangeRequestRealizationStep.STEP_2_CHECK_INCOMPLETES);
		changeRequestService.updateChangeRequestRealization(realization);
		if (changeRequestSummaryService.hasIncompleteProperties(changeRequest)) {
			realization.setRealizationStatus(ChangeRequestRealizationStatus.PRE_PROCESS_FAILED);
			realization.setFailedReason("Change request cannot be realized with incompletes");
			changeRequestService.updateChangeRequestRealization(realization);
			return realization;
		}
		// step 3: check conflicts
		realization.setProcessStep(ChangeRequestRealizationStep.STEP_3_CHECK_CONFLICT);
		changeRequestService.updateChangeRequestRealization(realization);
		ContextIdentifier contextId = lookupService.findOpenContextByChangeRquestId(changeRequest.getChangeRequestId());
		ContextAccess context = contextProvider.findContext(contextId);
		// we realize the change request which is not from admin screen
		HashMap<ElementVersion, ElementVersion> elementVersions = context.checkRealizationConflicts(false);
		if (!elementVersions.isEmpty()) {
			realization.setRealizationStatus(ChangeRequestRealizationStatus.PRE_PROCESS_FAILED);
			realization.setFailedReason("Change request cannot be realized with conflicts");
			changeRequestService.updateChangeRequestRealization(realization);
			return realization;
		}
		// step 4: do realization, synchronize this part
		realization.setProcessStep(ChangeRequestRealizationStep.STEP_4_REALIZING);
		changeRequestService.updateChangeRequestRealization(realization);
		try {
			changeRequestService.realizeChangeRequest(changeRequest, realization, currentUser);
		} catch (Exception ex) {
			realization.setRealizationStatus(ChangeRequestRealizationStatus.PROCESS_FAILED);
			realization.setFailedReason("exception happened");
			changeRequestService.updateChangeRequestRealization(realization);
			throw new RuntimeException(ex);
		}
		return realization;

	}

	public void setChangeRequestService(ChangeRequestService changeRequestService) {
		this.changeRequestService = changeRequestService;
	}

	public void setChangeRequestSummaryService(ChangeRequestSummaryService changeRequestSummaryService) {
		this.changeRequestSummaryService = changeRequestSummaryService;
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setSynchronizationService(SynchronizationService synchronizationService) {
		this.synchronizationService = synchronizationService;
	}

}
