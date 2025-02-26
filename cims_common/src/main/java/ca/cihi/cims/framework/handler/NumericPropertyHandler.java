package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.Language;

public class NumericPropertyHandler extends DataPropertyHandler {

	@Override
	protected void insertPropertyWithValue(Long conceptId, Long propertyClasssId,
			ElementIdentifier propertyElementIdentifier, Language language, Object value) {
		insertDataPropertyVersion(conceptId, propertyClasssId, propertyElementIdentifier);
		Map<String, Object> params = new HashMap<>();
		params.put("numericPropertyId", propertyElementIdentifier.getElementVersionId());
		params.put("domainElementId", conceptId);
		params.put("classsId", propertyClasssId);
		params.put("elementId", propertyElementIdentifier.getElementId());
		params.put("numericValue", value);
		getPropertyMapper().insertNumericPropertyWithValue(params);
	}

	@Override
	protected void updatePropertyValue(Long elementVersionId, Language language, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("numericPropertyId", elementVersionId);
		params.put("numericValue", value);
		getPropertyMapper().updateNumericPropertyValue(params);
	}

}
