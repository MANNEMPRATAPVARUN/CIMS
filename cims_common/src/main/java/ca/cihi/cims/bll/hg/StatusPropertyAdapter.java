package ca.cihi.cims.bll.hg;

import org.apache.logging.log4j.Logger;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;


import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.hg.mapper.config.StatusPropertyConfig;

/**
 * This is the implementation of a property "get" method within a dynamic proxy.
 * 
 * @author MPrescott
 */
public class StatusPropertyAdapter extends PropertyAdapter<StatusPropertyConfig> {

	private static final Logger LOGGER = LogManager.getLogger(StatusPropertyAdapter.class);

	public StatusPropertyAdapter(Identified owner, StatusPropertyConfig config, ContextElementAccess operations) {
		super(owner, config, operations);
	}

	public Object getValue(InvocationDetails details) {
		return getOperations().getCachedElement(getOwner().getElementId()).getStatus();
	}

	@Override
	public void setValue(InvocationDetails details) {

		// Find the wrapper's defining element (probably a ConceptVersion)
		ElementVersion element = getOperations().getCachedElement(getOwner().getElementId());

		if (Objects.equals(element.getStatus(), details.getSetValue())) {
			LOGGER.info("Determined the status set value is the same as the current value.  Not saving...");
		} else {
			// Set the status to whatever the caller wanted
			element.setStatus((String) details.getSetValue());

			// Let the framework know the element was modified
			getOperations().touch(element);
		}
	}
}
