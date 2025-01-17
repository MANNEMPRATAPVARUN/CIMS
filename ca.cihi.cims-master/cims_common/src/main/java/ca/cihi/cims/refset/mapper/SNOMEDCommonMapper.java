package ca.cihi.cims.refset.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.refset.dto.SCTDescriptionChangeDTO;

public interface SNOMEDCommonMapper {

	List<SCTDescriptionChangeDTO> findChangedSynonym(@Param("fromVersionCode") String fromVersionCode,
			@Param("toVersionCode") String toVersionCode, @Param("refsetContextId") Long contextId,
			@Param("baseClassificationName") String baseClassificationName);

	List<Long> findExpiredConcepts(@Param("fromVersionCode") String fromVersionCode,
			@Param("toVersionCode") String toVersionCode, @Param("refsetContextId") Long contextId,
			@Param("baseClassificationName") String baseClassificationName);

	List<SCTDescriptionChangeDTO> findChangedFSNs(@Param("fromVersionCode") String fromVersionCode,
			@Param("toVersionCode") String toVersionCode, @Param("refsetContextId") Long contextId,
			@Param("baseClassificationName") String baseClassificationName);

	List<SCTDescriptionChangeDTO> findChangedPreferreds(@Param("fromVersionCode") String fromVersionCode,
			@Param("toVersionCode") String toVersionCode, @Param("refsetContextId") Long contextId,
			@Param("baseClassificationName") String baseClassificationName);

}
