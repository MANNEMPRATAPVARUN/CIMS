package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.enums.Language;

public class XLSXPropertyHandler extends DataPropertyHandler {

	@Override
	public PropertyDTO findPropertyElementInContext(Long contextId, Long conceptId, String classsName,
			Language language) {
		Map<String, Object> params = new HashMap<>();
		params.put("contextId", contextId);
		params.put("conceptId", conceptId);
		params.put("classsName", classsName);
		params.put("language", language != Language.NOLANGUAGE ? language.getCode() : null);
		return getPropertyMapper().loadXlsxProperty(params);
	}

	@Override
	protected void insertPropertyWithValue(Long conceptId, Long propertyClasssId,
			ElementIdentifier propertyElementIdentifier, Language language, Object value) {
		insertDataPropertyVersion(conceptId, propertyClasssId, propertyElementIdentifier);
		Map<String, Object> params = new HashMap<>();
		params.put("xlsxPropertyId", propertyElementIdentifier.getElementVersionId());
		params.put("domainElementId", conceptId);
		params.put("classsId", propertyClasssId);
		params.put("elementId", propertyElementIdentifier.getElementId());
		params.put("language", language != Language.NOLANGUAGE ? language.getCode() : null);
		params.put("xlsxBlobValue", value);
		getPropertyMapper().insertXLSXPropertyWithValue(params);
	}

	@Override
	protected void updatePropertyValue(Long elementVersionId, Language language, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("xlsxPropertyId", elementVersionId);
		params.put("language", language != Language.NOLANGUAGE ? language.getCode() : null);
		params.put("xlsxBlobValue", value);
		getPropertyMapper().updateXLSXPropertyValue(params);
	}

}
