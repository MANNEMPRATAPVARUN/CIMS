package ca.cihi.cims.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.RealizedChange;

public interface ChangeRequestIndexSummaryMapper {

	String findIndexDesc(@Param("maxStructureId") Long maxStructureId, @Param("indexRefId") Long indexRefId);

	List<ConceptModification> findModifiedIndexConceptElementCodes(@Param("changeRequestId") Long changeRequestId,
			@Param("maxStructureId") Long maxStructureId);

	List<ProposedChange> findProposedIndexChanges(@Param("contextId") long contextId,
			@Param("domainElementId") Long domainElementId);

	List<RealizedChange> findRealizedIndexChanges(java.util.Map<String, Object> map);
}
