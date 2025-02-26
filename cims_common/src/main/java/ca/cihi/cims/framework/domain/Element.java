package ca.cihi.cims.framework.domain;

import java.util.UUID;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.dto.ElementDTO;
import ca.cihi.cims.framework.handler.ElementHandler;

/**
 * @author tyang
 * @version 1.0
 * @created 13-Jun-2016 10:46:46 AM
 */
public class Element {

	/**
	 * return ElementHandler.existsInContext(businessKey, context.contextId)
	 *
	 * @param businessKey
	 * @param context
	 */
	public static Boolean existsInContext(String businessKey, Long contextId) {
		ElementHandler elementHandler = (ElementHandler) ApplicationContextProvider.getApplicationContext()
				.getBean("frameworkElementHandler");
		return elementHandler.existsInContext(businessKey, contextId);
	}

	public static ElementDTO findElementInContext(Long contextId, Long elementId) {
		ElementHandler elementHandler = (ElementHandler) ApplicationContextProvider.getApplicationContext()
				.getBean("frameworkElementHandler");
		return elementHandler.findElementInContext(contextId, elementId);
	}

	/**
	 * Generates a business key as a UUID
	 */
	public static String generateBusinessKey() {

		return UUID.randomUUID().toString();
	}

	private Classs classs;

	private Context context;

	private ElementIdentifier elementIdentifier;

	public Element() {

	}

	/**
	 * Constructs the element object based on the data in input parameters.
	 *
	 * @param classs
	 * @param context
	 * @param elementIdentifier
	 */
	public Element(Classs classs, Context context, ElementIdentifier elementIdentifier) {
		this.classs = classs;
		this.elementIdentifier = elementIdentifier;
		this.context = context;
	}

	public Element(Classs classs, ElementIdentifier elementIdentifier) {
		this.classs = classs;
		this.elementIdentifier = elementIdentifier;
	}

	public Classs getClasss() {
		return classs;
	}

	public String getBaseClassificationName() {
		return getClasss().getBaseClassificationName();
	}

	public Context getContext() {
		return this.context;
	}

	public ElementIdentifier getContextElementIdentifier() {
		return getContext().getElementIdentifier();
	}

	public ElementIdentifier getElementIdentifier() {
		return this.elementIdentifier;
	}

	public void setClasss(Classs classs) {
		this.classs = classs;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	/**
	 *
	 * @param elementIdentifier
	 */
	public void setElementIdentifier(ElementIdentifier elementIdentifier) {
		this.elementIdentifier = elementIdentifier;
	}

}