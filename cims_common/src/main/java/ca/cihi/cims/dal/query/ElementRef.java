package ca.cihi.cims.dal.query;

import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;

/**
 * A reference to an element for use in
 * {@link ElementOperations#find(ca.cihi.cims.dal.ContextIdentifier, ElementRef, java.util.Collection)
 * finding elements}.
 */
public class ElementRef {
	private Class elementClass;

	private String name;

	public <T extends ElementVersion> ElementRef(Class<T> elementClass) {
		this.elementClass = elementClass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class getElementClass() {
		return elementClass;
	}

	@Override
	public String toString() {
		return "ElementRef[class=" + elementClass.getSimpleName() + (name == null ? "" : " (" + name + ")") + "]";
	}
}
