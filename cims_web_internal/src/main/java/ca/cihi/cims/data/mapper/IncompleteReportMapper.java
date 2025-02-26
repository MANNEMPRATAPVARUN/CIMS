package ca.cihi.cims.data.mapper;

import org.apache.ibatis.annotations.Param;

public interface IncompleteReportMapper {

	String checkIndexConcept(@Param("contextId") Long contextId, @Param("conceptId") Long conceptId);

	String checkSupplementConcept(@Param("contextId") Long contextId, @Param("conceptId") Long conceptId);

	String checkTabularConcept(java.util.Map<String, Object> map);

}
