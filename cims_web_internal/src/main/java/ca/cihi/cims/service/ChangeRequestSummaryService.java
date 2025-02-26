package ca.cihi.cims.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.RealizedChange;
import ca.cihi.cims.model.changerequest.ResolveConflict;
import ca.cihi.cims.model.changerequest.ValidationChange;

public interface ChangeRequestSummaryService {

	String findHtmlTextFromHtmlPropertyId(final Long htmlPropertyId);

	String findIndexDesc(final Long maxStructureId, final Long indexRefId);

	Long findMaxStructureId(final Long changeRequestId);

	ConceptModification findModifiedConceptElementCode(final Long changeRequestId, final Long maxStructureId,
			final Long validationId);

	List<ConceptModification> findModifiedConceptElementCodes(final Long changeRequestId, final Long maxStructureId);

	ConceptModification findModifiedIndexConceptElementCode(final Long changeRequestId, final Long maxStructureId,
			final Long elementId);

	List<ConceptModification> findModifiedIndexConceptElementCodes(final Long changeRequestId, final Long maxStructureId);

	List<ConceptModification> findModifiedSupplementConceptElementCodes(final Long changeRequestId,
			final Long maxStructureId, String language);

	List<ProposedChange> findProposedAndConflictIndexChanges(Long contextId, Long domainElementId);

	List<ProposedChange> findProposedAndConflictSupplementChanges(Long contextId, Long domainElementId, String language);

	List<ProposedChange> findProposedAndConflictTabularChanges(final Long contextId, final Long domainElementId);

	List<ProposedChange> findProposedAndConflictValidationChanges(final String classification, final Long contextId,
			final Long validationId);

	List<ProposedChange> findProposedIndexChanges(final long contextId, final Long domainElementId);

	String findProposedStatus(final Long contextId, final Long domainElementId);

	List<ProposedChange> findProposedStatusChanges(final Long contextId, final Long domainElementId);

	List<ProposedChange> findProposedSupplementChanges(final Long contextId, final Long domainElementId, String language);

	List<ProposedChange> findProposedTabularChanges(final Long contextId, final Long domainElementId);

	List<ValidationChange> findProposedValidationChanges(final String classification, final Long contextId,
			final Long validationId, boolean showOldValue);

	List<RealizedChange> findRealizedIndexChanges(final Long changeRequestId, final Long domainElementId);

	String findRealizedStatus(final Long changeRequestId, final Long domainElementId);

	List<RealizedChange> findRealizedStatusChanges(final Long changeRequestId, final Long domainElementId);

	List<RealizedChange> findRealizedSupplementChanges(final Long changeRequestId, final Long domainElementId,
			String language);

	List<RealizedChange> findRealizedTabularChanges(final Long changeRequestId, final Long domainElementId);

	List<RealizedChange> findRealizedValidationChanges(final String classification, final Long changeRequestId,
			final Long validationId);

	String findXmlTextFromXmlPropertyId(final Long xmlPropertyId);

	boolean hasIncompleteProperties(final ChangeRequest changeRequest);

	List<ValidationChange> processRealizedValidationChange(final HashMap<String, RealizedChange> realizedValidationMap,
			final String classification);

	@Transactional
	void resolveConflicts(ResolveConflict resolveConflict, User currentUser);

	@Transactional
	void resolveIndexConflicts(ResolveConflict resolveConflict, User currentUser);

	@Transactional
	void resolveSupplementConflicts(ResolveConflict resolveConflict, User currentUser);

}
