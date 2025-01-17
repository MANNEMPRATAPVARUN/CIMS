package ca.cihi.cims.framework.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.framework.dto.ElementDTO;

public interface ElementMapper {

	Integer countInContext(@Param("businessKey") String businessKey, @Param("contextId") Long contextId);

	Integer countSv(@Param("elementVersionId") Long elementVersionId);

	Long createContextVersion(Map<String, Object> params);

	Long createElement(Map<String, Object> params);

	Long createElementVersionInContext(Map<String, Object> params);

	ElementDTO findElementInContext(Map<String, Object> params);

	void updateVersionTimestamp(@Param("elementVersionId") Long elementVersionId);

}
