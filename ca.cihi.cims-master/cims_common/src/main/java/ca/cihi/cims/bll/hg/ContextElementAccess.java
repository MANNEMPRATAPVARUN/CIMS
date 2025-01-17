package ca.cihi.cims.bll.hg;

import java.util.Collection;
import java.util.List;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.PropertyVersion;

public interface ContextElementAccess {

	// TODO: It's highly suspicious that these are here, I need to revisit the
	// design of the way that the classes in this package collaborate. Could be
	// I should bite the bullet and have the accessors just using a naked
	// HgContextTransaction.
	<T> T load(long elementId);

	ContextIdentifier getContextId();

	<T> Collection<T> load(Collection<Long> elementIds);

	/**
	 * Preload the elements as well as all of their properties.
	 */
	List<ElementVersion> loadElements(Collection<Long> elementIds);

	ElementVersion getCachedElement(Long elementId);

	// I'm trying this out, seeing if it's okay for the adapters to assume that
	// things are cached already
	List<ElementVersion> getCachedElements(Collection<Long> elementIds);

	Collection<PropertyVersion> findProperties(String elementClassName, long elementId, String languageCode,
					boolean inverse);

	PropertyVersion findProperty(String elementClassName, long domainElementId, String languageCode);

	ElementVersion makeNewElement(String className, String businessKey);

	/**
	 * Marks an element as having been modified.
	 */
	void touch(ElementVersion elementVersion);
}
