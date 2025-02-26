package ca.cihi.cims.bll;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementVersion;

/**
 * Represents a unit of work.
 */
public interface ContextAccess {

	/**
	 * same method as Realizes a Change Context, without doing realization, only check conflicts
	 * 
	 * @param isAdmin
	 *            Boolean indicating that this person who is realizing is an admin. Exactly what privileges this entails
	 *            can change.......
	 * @return A Map containing the conflicting Elements. If no conflicts, returns an empty map
	 */
	HashMap<ElementVersion, ElementVersion> checkRealizationConflicts(boolean isAdmin);

	void closeChangeContext();

	void closeContext();

	public ContextAccess createChangeContext(Long requestId);

	/**
	 * Create a context, which bases itself of the your own Context
	 * 
	 * @param isVersionYear
	 *            Whether this context is a Version year
	 * @return ContextAccess to the new context
	 */
	public ContextAccess createContext(boolean isVersionYear);

	<T> T createWrapper(Class<T> wrapperClass, String elementClass, String businessKey);

	String determineContainingIdPath(Long elementId);

	// this method will replace the above one, as we need id path , not only the containingPage Id
	String determineContainingIdPath(String code);

	// TODO: Shortcuts that we really need to remove someday
	Long determineContainingPage(long elementId);

	/**
	 * Discards a change from the change context. This executes once the user clicks "update" on the conflicts report,
	 * which discards the ElementVersion from the change context
	 * 
	 * @param ev
	 *            The element you want discarded
	 */
	void discardFromChangeContext(ElementVersion ev);

	<T> Iterator<T> find(Ref<T> wrapper, FindCriterion... criteria);

	/**
	 * Find all instances of a given wrapper class.
	 * 
	 * @see ContextAccess#load(String)
	 */
	<T> Iterator<T> findAll(Class<T> clazz);

	<T> List<T> findList(Ref<T> wrapper, FindCriterion... criteria);

	<T> T findOne(Ref<T> wrapper, FindCriterion... criteria);

	/**
	 * Returns a map containing the conflicting Elements. If no conflicts, returns an empty map
	 * 
	 * @return Map containing the conflicting Elements. If no conflicts, returns an empty map
	 */
	HashMap<ElementVersion, ElementVersion> getConflicts();

	ContextIdentifier getContextId();

	<T> Collection<T> load(Collection<Long> elementIds);

	/**
	 * Searches the database for an element with this ID, and returns it an appropriate wrapper of the class provided
	 * (or a subclass). If no such element is found in the database with that elementId, this method will return null.
	 */
	<T> T load(long elementId);

	/**
	 * Writes all new and modified elementVersions to the database.
	 */
	void persist();

	/**
	 * Realizes a Change Context
	 * 
	 * @param isAdmin
	 *            Boolean indicating that this person who is realizing is an admin. Exactly what privileges this entails
	 *            can change.......
	 * @return A Map containing the conflicting Elements. If no conflicts, returns an empty map
	 */
	HashMap<ElementVersion, ElementVersion> realizeChangeContext(boolean isAdmin);

	ContextAccess reload();

	List<ElementVersion> retrieveChangesFromChangeContext();

	Long retrieveNestingLevel(long elementId);

	Long retrieveNumberOfChildrenWithValidation(String code);
}
