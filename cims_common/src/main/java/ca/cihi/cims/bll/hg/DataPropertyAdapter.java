package ca.cihi.cims.bll.hg;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.dal.DataPropertyVersion;
import ca.cihi.cims.dal.FormatIndicatable;
import ca.cihi.cims.dal.LanguageSpecific;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.hg.mapper.config.DataPropertyConfig;

/**
 * This is the implementation of a property "get" method within a dynamic proxy.
 * 
 * @author MPrescott
 */
@SuppressWarnings( { "unchecked" })
public class DataPropertyAdapter extends PropertyElementAdapter<DataPropertyConfig> {

	private static final Logger LOGGER = LogManager.getLogger(DataPropertyAdapter.class);

	// -------------------------------------------------------------

	public DataPropertyAdapter(Identified owner, DataPropertyConfig config, ContextElementAccess operations) {
		super(owner, config, operations);
	}

	@Override
	public Object getValue(InvocationDetails details) {
		DataPropertyVersion property = (DataPropertyVersion) loadProperty(details.getLanguageCode());
		ExpressionParser parser = new SpelExpressionParser();
		Object propertyValue;
		if (property == null) {
			propertyValue = null;
		} else {
			propertyValue = parser.parseExpression(details.getField()).getValue(property);
		}
		return propertyValue;
	}

	private PropertyVersion loadProperty(String languageCode) {
		PropertyVersion element = getOperations().findProperty(getConfig().getPropertyElementClassName(),
				getOwner().getElementId(), languageCode);
		return element;
	}

	@Override
	public void setValue(InvocationDetails details) {
		DataPropertyVersion property = (DataPropertyVersion) loadProperty(details.getLanguageCode());
		if (property == null) {
			if (details.getSetValue() == null) {
				LOGGER.info("Value being set for new property is null.  Not saving...");
				return;
			}
			LOGGER.debug("Element not found for " + getConfig() + ", making new one.");
			// The property doesn't exist in the database
			property = makeNewElement(details);
			if (property instanceof LanguageSpecific) {
				((LanguageSpecific) property).setLanguageCode(details.getLanguageCode());
			}
		}
		if (property instanceof FormatIndicatable) {
			((FormatIndicatable) property).setFormat(details.getPropertyFormat());
		}
		if (property.isSameValue(details.getSetValue())) {
			LOGGER.info("Determined the set value is the same as the current value.  Not saving...");
			return;
		}
		property.setValue(details.getSetValue());
		getOperations().touch(property);
	}
}
