package ca.cihi.cims.framework.domain;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.config.PropertyKey;
import ca.cihi.cims.framework.dto.PropertyDTO;
import ca.cihi.cims.framework.handler.PropertyHandler;

/**
 * @author MIftimie
 * @version 1.0
 * @created 13-Jun-2016 10:47:00 AM
 */
public class Property extends Element {

	/**
	 * Returns true whether a text property exists with the specified value in the specified context if any (language
	 * independent). Assert that at least one of tableNameFilter is specified. If no context is specified, it will
	 * search only in the contexts where the context class tableName matches tableNameFilter.
	 *
	 * @param conceptClasssName
	 * @param propertyClasssName
	 * @param value
	 * @param context
	 * @param tableNameFilter
	 * @return
	 */
	public static boolean checkDuplicateValue(Long conceptElementId, String conceptClasssName,
			String propertyClasssName, String value, ElementIdentifier contextElementIdentifier,
			String tableNameFilter) {
		return PropertyHandler.checkDuplicateValue(conceptElementId, conceptClasssName, propertyClasssName, value,
				contextElementIdentifier, tableNameFilter);
	}

	/**
	 * - loads the property data from the database by delegating to handler if found - elementIdentifier = new
	 * ElementIdentifier(elementId, elementVersionId)
	 *
	 * - instantiates Property object as follows: ---
	 * super(ClasssHandler.getClass(context.classs.baseClassificationName, key. className), context, ) --- if not found
	 * in DB ---- Property( contextId, conceptId, key, 0, 0, null) --- else ---- Property( contextId, conceptId, key,
	 * elementId, elementVersionId, value) return Property object Note that if the property was not found in the
	 * database, the elementVersionId, elementid and value would be set to null and the object would still be
	 * instantiated.
	 *
	 * @param context
	 * @param conceptId
	 * @param key
	 */
	public static Property loadProperty(Context context, Long conceptId, PropertyKey key) {
		PropertyHandler handler = PropertyHandler.findHandler(key.getPropertyType());
		PropertyDTO dto = handler.findPropertyElementInContext(context.getContextId(), conceptId, key.getClassName(),
				key.getLanguage());
		if (dto != null) {
			return new Property(dto, context);
		}
		return new Property(context, key, null, null);
	}

	private PropertyKey key;

	private PropertyValue value;

	/**
	 *
	 * @param context
	 * @param conceptId
	 * @param key
	 * @param elementId
	 * @param elementVersionId
	 * @param value
	 */
	public Property(Context context, PropertyKey key, ElementIdentifier elementIdentifier, PropertyValue value) {
		super(Classs.findByName(key.getClassName(), context.getBaseClassificationName()), context, elementIdentifier);
		this.setKey(key);
		this.setValue(value);
	}

	public Property(PropertyDTO dto, Context context) {
		this.setContext(context);
		this.setClasss(new Classs(dto.getClasss()));
		this.setElementIdentifier(dto.getElementIdentifier());

		this.setKey(new PropertyKey(dto.getClasss().getClasssName(), dto.getLanguage(), dto.getPropertyType()));
		this.setValue(new PropertyValue(dto.getValue()));
	}

	public PropertyKey getKey() {
		return key;
	}

	/**
	 * returns value
	 */
	public PropertyValue getValue() {
		return this.value;
	}

	public void setKey(PropertyKey key) {
		this.key = key;
	}

	public void setValue(PropertyValue value) {
		this.value = value;
	}

	/**
	 * - if (value equals this value) return; - PropertyHandler.findHandler(key.propertyType) - elementIdentifier =
	 * handler.updateProperty( contextId, conceptElementIdentifier, classs.classsId, key.language, elementIdentifier,
	 * value.value) - this.value = value - setElementIdentifier.(elementIdentifier);
	 *
	 * @param value
	 * @param conceptElementIdentifier
	 */
	public void setValue(PropertyValue value, ElementIdentifier conceptElementIdentifier) {
		if ((value == null) || (value.getValue() == null)) {
			return;
		}
		if (!(value.getValue() instanceof byte[])
				&& value.getValue().equals(this.value != null ? this.value.getValue() : null)) {
			// for xlsx property, do not compare value
			return;
		}
		PropertyHandler handler = PropertyHandler.findHandler(this.key.getPropertyType());
		ElementIdentifier elementIdentifier = handler.updateProperty(getContext().getContextId(),
				conceptElementIdentifier, getClasss().getClassId(), getKey().getLanguage(), getElementIdentifier(),
				value.getValue());
		setElementIdentifier(elementIdentifier);
		setValue(value);
	}

}