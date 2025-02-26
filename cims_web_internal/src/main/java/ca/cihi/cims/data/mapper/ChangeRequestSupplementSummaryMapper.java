package ca.cihi.cims.data.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.model.changerequest.ProposedChange;
import ca.cihi.cims.model.changerequest.RealizedChange;

public interface ChangeRequestSupplementSummaryMapper {

	List<ConceptModification> findModifiedSupplementConceptElementCodes(@Param("changeRequestId") Long changeRequestId,
			@Param("maxStructureId") Long maxStructureId, @Param("language") String language);

	List<ProposedChange> findProposedSupplementChanges(@Param("contextId") Long contextId,
			@Param("domainElementId") Long domainElementId, @Param("language") String language);

	List<RealizedChange> findRealizedSupplementChanges(@Param("changeRequestId") Long changeRequestId,
			@Param("domainElementId") Long domainElementId, @Param("language") String language);

}