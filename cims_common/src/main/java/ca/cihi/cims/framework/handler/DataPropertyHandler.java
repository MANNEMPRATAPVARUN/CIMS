package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;

public abstract class DataPropertyHandler extends PropertyHandler {

	protected void insertDataPropertyVersion(Long conceptId, Long propertyClasssId,
			ElementIdentifier propertyElementIdentifier) {
		Map<String, Object> params = new HashMap<>();
		params.put("dataPropertyId", propertyElementIdentifier.getElementVersionId());
		params.put("domainElementId", conceptId);
		params.put("classsId", propertyClasssId);
		params.put("elementId", propertyElementIdentifier.getElementId());
		getPropertyMapper().insertDataPropertyVersion(params);
	}

}
