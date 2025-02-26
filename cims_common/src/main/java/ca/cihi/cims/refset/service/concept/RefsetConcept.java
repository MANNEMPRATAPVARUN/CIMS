package ca.cihi.cims.refset.service.concept;

import ca.cihi.cims.framework.ElementIdentifier;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:12:00 PM
 */
public interface RefsetConcept {

	public ElementIdentifier getContextElementIdentifier();

	public ElementIdentifier getElementIdentifier();

	public void remove();

}