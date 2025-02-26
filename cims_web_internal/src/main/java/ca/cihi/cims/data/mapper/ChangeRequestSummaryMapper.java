package ca.cihi.cims.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.RealizedChange;

public interface ChangeRequestSummaryMapper {

	void deleteStructureElementVersion(Long elementVersionId);

	Long findBaseElementVersionIdByRealizedContext(@Param("realizedByContextId") Long realizedByContextId,
			@Param("elementVersionId") Long elementVersionId, @Param("currentContextId") Long currentContextId);

	Long findConflictRealizedByChangeRequestId(@Param("currentElementVersionID") Long currentElementVersionID,
			@Param("currentContextId") Long currentContextId);

	ContextIdentifier findConflictRealizedByContext(@Param("currentElementVersionID") Long currentElementVersionID,
			@Param("currentContextId") Long currentContextId);

	String findHtmlTextFromHtmlPropertyId(Long htmlPropertyId);

	Long findMaxStructureId(Long changeRequestId);

	List<ConceptModification> findModifiedConceptElementCodes(java.util.Map<String, Object> map);

	String findProposedStatus(@Param("contextId") long contextId, @Param("domainElementId") Long domainElementId);

	List<ProposedChange> findProposedStatusChanges(@Param("contextId") long contextId,
			@Param("domainElementId") Long domainElementId);

	List<ProposedChange> findProposedTabularChanges(@Param("contextId") long contextId,
			@Param("domainElementId") Long domainElementId);

	List<ProposedChange> findProposedValidationChanges(java.util.Map<String, Object> map);

	String findRealizedStatus(@Param("changeRequestId") Long changeRequestId,
			@Param("domainElementId") Long domainElementId);

	List<RealizedChange> findRealizedStatusChanges(java.util.Map<String, Object> map);

	List<RealizedChange> findRealizedTabularChanges(java.util.Map<String, Object> map);

	List<RealizedChange> findRealizedValidationChanges(java.util.Map<String, Object> map);

	List<Long> findValidationConceptAndPropertyIdsByValidationDefinitionElementVersionId(
			@Param("validationDefinitionElementVersionId") Long validationDefinitionElementVersionId,
			@Param("currentContextId") Long currentContextId);

	String findXmlTextFromXmlPropertyId(Long xmlPropertyId);

	void updateElementVersionChangedFromVersionId(@Param("changedFromVersionId") Long changedFromVersionId,
			@Param("elementVersionId") Long elementVersionId);

}
