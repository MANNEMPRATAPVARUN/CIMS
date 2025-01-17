package ca.cihi.cims.bll.hg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.hg.mapper.config.ConceptPropertyConfig;

/**
 * This is the implementation of a property "get" method within a dynamic proxy.
 * 
 * @author MPrescott
 */
public class ConceptPropertyAdapter extends PropertyElementAdapter<ConceptPropertyConfig> {

	private static final Logger LOGGER = LogManager.getLogger(ConceptPropertyAdapter.class);

	public ConceptPropertyAdapter(Identified owner, ConceptPropertyConfig config, ContextElementAccess operations) {
		super(owner, config, operations);
	}

	public Object getValue(InvocationDetails details) {
		Long myElementId = getOwner().getElementId();

		ConceptPropertyConfig config = getConfig();

		boolean inverse = config.isInverse();
		Collection<PropertyVersion> properties = getOperations().findProperties(config.getPropertyElementClassName(),
						myElementId, null, inverse);

		List<Long> conceptIds = new ArrayList<Long>();

		for (PropertyVersion prop : properties) {

			ConceptPropertyVersion conProp = (ConceptPropertyVersion) prop;

			Long conceptId = inverse ? (Long) conProp.getDomainElementId() : conProp.getRangeElementId();

			if (conceptId != null) {
				conceptIds.add(conceptId);
			}

		}

		Collection<Object> concepts = getOperations().load(conceptIds);

		if (config.isCollection()) {
			return concepts;
		} else {
			if (concepts.isEmpty()) {
				return null;
			} else if (concepts.size() > 1) {
				throw new IllegalStateException("Multiple concepts returned for scalar concept property!");
			} else {
				return concepts.iterator().next();
			}
		}
	}

	@Override
	public void setValue(InvocationDetails details) {

		Long domainElementId = null;
		Long rangeElementId = null;

		ConceptPropertyVersion property = (ConceptPropertyVersion) loadProperty();

		Identified setValue = (Identified) details.getSetValue();

		if (setValue != null) {
			rangeElementId = setValue.getElementId();
		}
		
		if (property == null) {
			
			if (rangeElementId == null) {
				LOGGER.info("Range being set for new property is null.  Not saving...");
				return;
			}
			
			domainElementId = getOwner().getElementId();
			LOGGER.debug("Domain Element ID: " + domainElementId);
			LOGGER.debug("Range Element ID: " + rangeElementId);

			// The property doesn't exist in the database
			property = makeNewElement(details);
			property.setRangeElementId(rangeElementId);
		} else {
			domainElementId = getOwner().getElementId();

			if (!isSetValueDifferent(rangeElementId, property.getRangeElementId())) {
				LOGGER.info("Determined the range being set to the same value.  Not saving...");
				return;
			}

			LOGGER.debug("Domain Element ID: " + domainElementId);
			LOGGER.debug("Range Element ID: " + rangeElementId);

			property.setDomainElementId(domainElementId);
			property.setRangeElementId(rangeElementId);

			// We are going to have to deal with this at some point, but we will
			// need to know about inverse...
		}

		getOperations().touch(property);
	}

	/**
	 * Ensures that the value being set is different than the current value.
	 * If its found that the value is the same, it returns and stops execution
	 * 
	 * @param rangeElementId
	 * @param currentRangeElementId
	 * @return
	 */
	private boolean isSetValueDifferent(Long rangeElementId, Long currentRangeElementId) {

		boolean isDifferent = true;

		if (Objects.equals(currentRangeElementId, rangeElementId)) {
			isDifferent = false;
		}

		return isDifferent;
	}

	private PropertyVersion loadProperty() {

		PropertyVersion element = getOperations().findProperty(getConfig().getPropertyElementClassName(),
						getOwner().getElementId(), null);

		return element;
	}

}
