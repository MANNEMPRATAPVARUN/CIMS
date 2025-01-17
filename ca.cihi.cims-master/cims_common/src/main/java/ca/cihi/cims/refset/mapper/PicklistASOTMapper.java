package ca.cihi.cims.refset.mapper;

import java.util.Map;

import org.apache.ibatis.annotations.Param;

public interface PicklistASOTMapper {

	/**
	 * cleanup existing data
	 *
	 * @param picklistId
	 */
	void initAsotRelease(@Param("picklistId") Integer picklistId);

	/**
	 * insert picklist
	 *
	 * @param picklistId
	 *            the id of the picklist output
	 * @param picklistCode
	 * @param refsetCode
	 * @param refsetVersionCode
	 * @param languageCode
	 * @param picklistStatusCode
	 */
	void insertPicklist(@Param("picklistId") Integer picklistId, @Param("picklistCode") String picklistCode,
			@Param("refsetCode") String refsetCode, @Param("refsetVersionCode") String refsetVersionCode,
			@Param("languageCode") String languageCode, @Param("picklistStatusCode") String picklistStatusCode);

	/**
	 * insert column
	 *
	 * @param picklistColumnId
	 *            the id of the picklist column output
	 * @param picklistId
	 *            the id of the picklist output
	 * @param columnDesc
	 * @param columnTypeDesc
	 */
	void insertColumn(@Param("picklistColumnId") Integer picklistColumnId, @Param("picklistId") Integer picklistId,
			@Param("columnDesc") String columnDesc, @Param("columnTypeDesc") String columnTypeDesc);

	/**
	 * insert record
	 *
	 * @param paramMap
	 *            params include recordId and picklistId
	 */
	void insertRecord(Map<String, Object> paramMap);

	/**
	 * insert record value
	 *
	 * @param recordColumnValueId
	 * @param picklistColumnId
	 * @param recordId
	 * @param valueText
	 */
	void insertRecordValue(@Param("recordColumnValueId") Long recordColumnValueId,
			@Param("picklistColumnId") Integer picklistColumnId, @Param("recordId") Long recordId,
			@Param("valueText") String valueText);
}
