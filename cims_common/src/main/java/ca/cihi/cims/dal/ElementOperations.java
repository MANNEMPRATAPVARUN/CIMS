package ca.cihi.cims.dal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ca.cihi.cims.bll.hg.SetValuedMap;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.Restriction;

public interface ElementOperations {

	HashMap<ElementVersion, ElementVersion> checkRealizationConflicts(ContextIdentifier context,
			ContextIdentifier baseContext, Collection<ContextIdentifier> newerContexts);

	void closeChangeContext(ContextIdentifier context);

	void closeContext(ContextIdentifier context);

	String determineClassNameByElementId(long elementId);

	// We can have property criteria, which operate on the value of
	// Do we need to know what kind of property it is?
	// Data property, use the 'value' field, and we can determine the DB field
	// from there
	// Concept property, not supported

	// class name criteria (class name must belong in this list)

	String determineContainingIdPath(String baseClassification, long contextId, Long elementId);

	String determineContainingIdPath(String baseClassification, long contextId, String code);

	Long determineContainingPage(String baseClassification, long contextId, long elementId);

	/**
	 * What subclass of ElementVersion is appropriate to a given class name?
	 */
	Class<?> determineElementClass(String baseClassification, String className);

	String determineVersionCodeByElementId(long elementId);

	Iterator<Long> find(ContextIdentifier context, ElementRef element, Collection<Restriction> restrictions);

	List<Long> findAllElementIdsInChangeRequest(ContextIdentifier context);

	HashMap<ElementVersion, ElementVersion> getConflicts(ContextIdentifier context, ContextIdentifier baseContext);

	@Deprecated
	String getIndexPath(long contextId, long elementId);

	ElementVersion loadElement(ContextIdentifier context, long elementId);

	List<ElementVersion> loadElements(ContextIdentifier context, Collection<Long> elementIds);

	/**
	 * Finds properties and inverse properties of the element. Properties have a domainElementId that matches the
	 * provided elementID, inverse properties have a matching rangeElementId.
	 */
	List<PropertyVersion> loadProperties(ContextIdentifier context, Collection<Long> domainElementIds,
			SetValuedMap<Class, String> classesAndCodes, Collection<String> conceptPropertyClasses,
			Collection<String> inverseConceptPropertyClasses);

	/**
	 * Creates an element version suitable for populating in this context. This method will fail if an elementversion
	 * with the same business key already exists in this context.
	 * 
	 * This method doesn't save the ElementVersion to the database, that must be done later using
	 * {@link #updateElement(ContextIdentifier, ElementVersion)}.
	 */
	ElementVersion newInstance(ContextIdentifier contextId, String className, String businessKey);

	HashMap<ElementVersion, ElementVersion> realizeChangeContext(ContextIdentifier context,
			ContextIdentifier baseContext, Collection<ContextIdentifier> newerContexts);

	/*
	 * this is a temp method do realization without check conflicts, will be removed after resolve conflicts done
	 */
	HashMap<ElementVersion, ElementVersion> realizeChangeContextWithoutCheckingConflicts(ContextIdentifier context,
			ContextIdentifier baseContext, Collection<ContextIdentifier> newerContexts);

	Long retrieveNestingLevel(String baseClassification, long contextId, long elementId);

	Long retrieveNumberOfChildrenWithValidation(String baseClassification, long contextId, String code);

	/**
	 * Updates the version of the element in the context. (This may translate to inserting new rows if the element is
	 * actually coming from a base context.)
	 */
	void updateElement(ContextIdentifier context, ElementVersion element);

}
