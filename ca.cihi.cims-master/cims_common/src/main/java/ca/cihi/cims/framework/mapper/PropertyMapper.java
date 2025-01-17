package ca.cihi.cims.framework.mapper;

import java.util.Map;

import ca.cihi.cims.framework.dto.PropertyDTO;

public interface PropertyMapper {

	/**
	 * Count if a text value exists
	 *
	 * @param params
	 * @return
	 */
	Integer countExistsValue(Map<String, Object> params);

	/**
	 * find the property element in the context with the given conceptId and classsName and language
	 *
	 * @param params
	 * @return
	 */
	PropertyDTO findPropertyInContext(Map<String, Object> params);

	/**
	 * insert into conceptpropertyversion table
	 *
	 * @param params
	 */
	void insertConceptPropertyWithValue(Map<String, Object> params);

	/**
	 * insert into datapropertyversion table
	 *
	 * @param params
	 */
	void insertDataPropertyVersion(Map<String, Object> params);

	/**
	 * insert into numericproperty table
	 *
	 * @param params
	 */
	void insertNumericPropertyWithValue(Map<String, Object> params);

	/**
	 * insert into propertyversion table
	 *
	 * @param params
	 */
	void insertPropertyVersion(Map<String, Object> params);

	/**
	 * insert into textpropertyversion table
	 *
	 * @param params
	 */
	void insertTextPropertyWithValue(Map<String, Object> params);

	/**
	 * insert xlsxpropertyversion table
	 *
	 * @param params
	 */
	void insertXLSXPropertyWithValue(Map<String, Object> params);

	/**
	 * used to process xlsxproperty, blob value
	 *
	 * @param params
	 * @return
	 */
	PropertyDTO loadXlsxProperty(Map<String, Object> params);

	/**
	 * update conceptpropertyversion table with the rangeelementid provided for the elementversionid
	 *
	 * @param params
	 */
	void updateConceptPropertyValue(Map<String, Object> params);

	/**
	 * update numericpropertyversion table with numericvalue provided for the elementversionid
	 *
	 * @param params
	 */
	void updateNumericPropertyValue(Map<String, Object> params);

	/**
	 * update textpropertyversion table with language and text provided for the elementversionid
	 *
	 * @param params
	 */
	void updateTextPropertyValue(Map<String, Object> params);

	/**
	 * update xlsxpropertyversion table with language and xlsxblobvalue provided for the elementversionid
	 *
	 * @param params
	 */
	void updateXLSXPropertyValue(Map<String, Object> params);
}
