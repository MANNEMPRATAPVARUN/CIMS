package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.Language;

public class ConceptPropertyHandler extends PropertyHandler {

	@Override
	protected void insertPropertyWithValue(Long conceptId, Long propertyClasssId,
			ElementIdentifier propertyElementIdentifier, Language language, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("conceptPropertyId", propertyElementIdentifier.getElementVersionId());
		params.put("domainElementId", conceptId);
		params.put("classsId", propertyClasssId);
		params.put("elementId", propertyElementIdentifier.getElementId());
		params.put("rangeElementId", value);
		getPropertyMapper().insertConceptPropertyWithValue(params);
	}

	@Override
	protected void updatePropertyValue(Long elementVersionId, Language language, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("conceptPropertyId", elementVersionId);
		params.put("rangeElementId", value);
		getPropertyMapper().updateConceptPropertyValue(params);

	}

}
