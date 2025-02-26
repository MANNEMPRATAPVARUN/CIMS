package ca.cihi.cims.framework.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.framework.dto.ContextDTO;

public interface ContextMapper {

	/**
	 * params should only contains contextId, classId, elementId and baseContextId
	 *
	 * @param params
	 */
	void createStructure(Map<String, Object> params);

	ContextDTO findContextDTO(@Param("contextId") Long contextId);

	void remove(@Param("contextId") Long contextId);

	void closeContext(@Param("contextId") Long contextId);

	/**
	 *
	 * @param elementId
	 * @return
	 */
	Long getLatestClosedVersion(@Param("elementId") Long elementId);

	/**
	 *
	 * @param elementId
	 * @return
	 */
	int getOpenVersionCount(@Param("elementId") Long elementId);

}
