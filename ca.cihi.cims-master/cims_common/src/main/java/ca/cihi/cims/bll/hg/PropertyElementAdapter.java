package ca.cihi.cims.bll.hg;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.hg.mapper.config.PropertyElementConfig;

public abstract class PropertyElementAdapter<T extends PropertyElementConfig> extends PropertyAdapter<T> {

	public PropertyElementAdapter(Identified owner, T config, ContextElementAccess operations) {
		super(owner, config, operations);
	}

	protected String propertyBusinessKey(InvocationDetails details) {
		String baseClassification = getOperations().getContextId().getBaseClassification();

		return BusinessKeyGenerator.propertyBusinessKey(baseClassification, getConfig().getPropertyElementClassName(),
						getConfig().getPropertyElementClass(), getOwner().getElementId(), details.getLanguageCode());
	}

	public <P extends PropertyVersion> P makeNewElement(InvocationDetails details) {

		@SuppressWarnings("unchecked")
		P property = (P) getOperations().makeNewElement(getConfig().getPropertyElementClassName(),
						propertyBusinessKey(details));
		property.setStatus("ACTIVE");
		property.setDomainElementId(getOwner().getElementId());

		return property;
	}

}
