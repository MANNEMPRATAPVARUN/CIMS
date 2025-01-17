package ca.cihi.cims.framework.handler;

import java.util.HashMap;
import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.Language;

public class TextPropertyHandler extends DataPropertyHandler {

	@Override
	protected void insertPropertyWithValue(Long conceptId, Long propertyClasssId,
			ElementIdentifier propertyElementIdentifier, Language language, Object value) {
		insertDataPropertyVersion(conceptId, propertyClasssId, propertyElementIdentifier);
		Map<String, Object> params = new HashMap<>();
		params.put("textPropertyId", propertyElementIdentifier.getElementVersionId());
		params.put("domainElementId", conceptId);
		params.put("classsId", propertyClasssId);
		params.put("elementId", propertyElementIdentifier.getElementId());
		params.put("language", language != Language.NOLANGUAGE ? language.getCode() : null);
		params.put("text", value);
		getPropertyMapper().insertTextPropertyWithValue(params);
	}

	@Override
	protected void updatePropertyValue(Long elementVersionId, Language language, Object value) {
		Map<String, Object> params = new HashMap<>();
		params.put("textPropertyId", elementVersionId);
		params.put("language", language != Language.NOLANGUAGE ? language.getCode() : null);
		params.put("text", value);
		getPropertyMapper().updateTextPropertyValue(params);
	}

}
