package ca.cihi.cims.bll.hg;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import ca.cihi.cims.bll.ContextAccess;
import ca.cihi.cims.bll.ContextProvider;
import ca.cihi.cims.bll.ContextUtils;
import ca.cihi.cims.bll.Identified;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.LanguageSpecific;
import ca.cihi.cims.dal.NamedParamPair;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.dal.jdbc.ORConfig;
import ca.cihi.cims.hg.WrapperFactory;
import ca.cihi.cims.hg.mapper.config.ConceptPropertyConfig;
import ca.cihi.cims.hg.mapper.config.DataPropertyConfig;
import ca.cihi.cims.hg.mapper.config.MappingConfig;
import ca.cihi.cims.hg.mapper.config.PropertyConfig;
import ca.cihi.cims.hg.mapper.config.WrapperConfig;
import ca.cihi.cims.util.timer.Perf;

/**
 * All access to wrappers happens through the {@link ContextAccess} interface, this is the Hyper Generalised
 * implementation of {@link ContextAccess} interface.
 * 
 * Loading specific wrappers by their element IDs, finding them with searches, or persisting changes. Contexts come in
 * two types, base version years and change contexts.
 * 
 * The find API is based on references to wrapper classes and search criteria.
 * 
 * To modify the data, call the setter methods in the wrapper classes, as you would when dealing with any normal Java
 * bean. The changes will exist only in memory until the context is persisted.
 * 
 */
@Component
@Scope("prototype")
public class HGContextAccess implements ContextAccess, ContextElementAccess {

	private final class LoadingIterator<T> implements Iterator<T> {
		private final Iterator<Long> idIterator;

		private LoadingIterator(Iterator<Long> idIterator) {
			this.idIterator = idIterator;
		}

		@Override
		public boolean hasNext() {
			return idIterator.hasNext();
		}

		@Override
		public T next() {
			if (!idIterator.hasNext()) {
				throw new IllegalStateException("Do not call next() when there is no next element.");
			}
			return (T) load(idIterator.next());
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static class PropertyCacheKey {
		private final String elementClassName;

		private final long domainElementId;

		private final String languageCode;

		private final boolean inverse;

		public PropertyCacheKey(String elementClassName, long domainElementId, String languageCode, boolean inverse) {
			this.elementClassName = elementClassName;
			this.domainElementId = domainElementId;
			this.languageCode = languageCode;
			this.inverse = inverse;
		}

		@Override
		public boolean equals(Object obj) {
			Perf.start("PropertyCacheKey.equals");
			boolean reflectionEquals = EqualsBuilder.reflectionEquals(this, obj);
			Perf.stop("PropertyCacheKey.equals");
			return reflectionEquals;
		}

		@Override
		public int hashCode() {
			Perf.start("PropertyCacheKey.hashCode");
			int reflectionHashCode = HashCodeBuilder.reflectionHashCode(this);
			Perf.stop("PropertyCacheKey.hashCode");
			return reflectionHashCode;
		}

		@Override
		public String toString() {
			return "[elementClassName=" + elementClassName + ",domainElementId=" + domainElementId + ",languageCode="
					+ languageCode + ", inverse=" + inverse + "]";
		}
	}

	// Gets set to true if the context needs reloading. Such as when a element is discarded
	private boolean needsReload = false;

	/**
	 * When iterating, we'll prefetch a certain number of elements and all their properties as a batch, hovering up
	 * everything in two queries.
	 */
	private static final int PREFETCH_ELEMENT_COUNT = 200;

	private ContextIdentifier contextId;

	private boolean autoCommit = false;

	// A cache of elements we've already loaded, indexed by their id
	private final Map<Long, ElementVersion> elementCache = new HashMap<Long, ElementVersion>();

	// A cache of property element Ids for a given search mode
	private final SetValuedMap<PropertyCacheKey, Long> propertyIdCache = new SetValuedMap<PropertyCacheKey, Long>();

	private final Map<Long, Object> wrapperCache = new HashMap<Long, Object>();

	// Stores the IDs of all concepts whose properties have been loaded
	private final Set<Long> elementsWhosePropertiesAreLoaded = new HashSet<Long>();

	private final Set<ElementVersion> dirtyElements = new HashSet<ElementVersion>();

	@Autowired
	private WrapperFactory wrapperFactory;

	@Autowired
	private MappingConfig mappingConfig;

	@Autowired
	private ElementOperations elementOperations;

	@Autowired
	private ContextOperations contextOperations;

	@Autowired
	CommonElementOperations commonOperations;

	@Autowired
	private ContextProvider provider;

	private static final Logger LOGGER = LogManager.getLogger(HGContextAccess.class);

	public HGContextAccess() {
	}

	// void setContextId(ContextIdentifier contextId) {
	// this.contextId = contextId;
	// }

	public HGContextAccess(ContextIdentifier contextId) {
		this.contextId = contextId;
	}

	public HGContextAccess(ContextIdentifier contextId, boolean autoCommit) {
		this.contextId = contextId;
		this.autoCommit = autoCommit;
	}

	public boolean autoCommitEnabled() {
		return autoCommit;
	}

	private void cache(List<? extends ElementVersion> elements) {
		for (ElementVersion element : elements) {
			if (element != null) {
				elementCache.put(element.getElementId(), element);
				if (element instanceof PropertyVersion) {
					cacheProperty((PropertyVersion) element);
				}
			}
		}
	}

	private void cacheProperty(PropertyVersion property) {
		if (property == null) {
			// TODO: Remove this code once we're not querying unsupported
			// property types!
			LOGGER.error("Caching a null property, probably a result of an unsupported property type.");
			return;
		}

		PropertyCacheKey key = new PropertyCacheKey(property.getClassName(), property.getDomainElementId(),
				getLanguage(property), false);
		LOGGER.debug("Caching property " + property.getElementId() + " with key " + key);
		propertyIdCache.put(key, property.getElementId());

		// Concept properties must be stored the other way around as well
		if (property instanceof ConceptPropertyVersion) {
			ConceptPropertyVersion conProp = (ConceptPropertyVersion) property;

			// If null do not cache
			Long rangeElementId = conProp.getRangeElementId();

			if (rangeElementId != null) {
				PropertyCacheKey inverseKey = new PropertyCacheKey(conProp.getClassName(), rangeElementId,
						getLanguage(conProp), true);
				LOGGER.debug("Caching property " + property.getElementId() + " with key " + inverseKey);
				propertyIdCache.put(inverseKey, conProp.getElementId());
			} else {
				LOGGER.trace("Skipping caching of property " + property.getElementId()
						+ ".  ConceptPropertyVersion rangeElementId is null");
			}

		}
	}

	@Override
	public HashMap<ElementVersion, ElementVersion> checkRealizationConflicts(boolean isAdmin) {

		ContextUtils.ensureContextDoesNotRequireReload(contextId, needsReload);

		// TODO: Consider adding isAdmin to ContextAccess if in the future this admin can do other things not normally
		// allowed

		ContextUtils.ensureChangeContext(contextId);
		ContextUtils.ensureContextIsOpen(contextId);

		ContextIdentifier baseContext = contextOperations.findContextById(contextId.getBaseClassification(),
				contextId.getBaseStructureId());

		ContextUtils.ensureContextIsOpen(baseContext);

		Collection<ContextIdentifier> openBaseContexts = contextOperations.findOpenBaseContextIdentifiers(contextId
				.getBaseClassification());

		// Admins can realize change contexts whenever they feel like it
		if (!isAdmin) {
			ContextUtils.ensureContextIsOldest(baseContext, openBaseContexts);
		}

		Collection<ContextIdentifier> newerContexts = ContextUtils.returnNewerContexts(baseContext, openBaseContexts);

		HashMap<ElementVersion, ElementVersion> conflicts = elementOperations.checkRealizationConflicts(contextId,
				baseContext, newerContexts);

		// A reload is required because once realized, any removed elements still exist in cache. Reloading will
		// ensure it does not get loaded.
		if (conflicts.size() == 0) {
			needsReload = true;
		}

		return conflicts; // Conflicts can be empty
	}

	@Override
	public void closeChangeContext() {

		ContextUtils.ensureChangeContext(contextId);
		ContextUtils.ensureContextIsOpen(contextId);

		elementOperations.closeChangeContext(contextId);
	}

	@Override
	public void closeContext() {

		ContextUtils.ensureNotAChangeContext(contextId);
		elementOperations.closeContext(contextId);
	}

	@Override
	public ContextAccess createChangeContext(Long requestId) {
		return provider.createChangeContext(contextId, requestId);
	}

	@Override
	public ContextAccess createContext(boolean isVersionYear) {
		return provider.createContext(contextId, isVersionYear);
	}

	/**
	 * Based on {@link ORConfig} built upon application start, create wrapper instance, if the element not exist, create
	 * it in the database as well.
	 */
	@Override
	public <T> T createWrapper(Class<T> wrapperClass, String elementClass, String businessKey) {

		ConceptVersion element = (ConceptVersion) makeNewElement(elementClass, businessKey);
		touch(element);
		return makeWrapper(wrapperClass, element);
	}

	@Override
	public String determineContainingIdPath(Long elementId) {
		return elementOperations.determineContainingIdPath(contextId.getBaseClassification(), contextId.getContextId(),
				elementId);
	}

	@Override
	public String determineContainingIdPath(String code) {
		return elementOperations.determineContainingIdPath(contextId.getBaseClassification(), contextId.getContextId(),
				code);
	}

	@Override
	public Long determineContainingPage(long elementId) {
		return elementOperations.determineContainingPage(contextId.getBaseClassification(), contextId.getContextId(),
				elementId);
	}

	@Override
	public void discardFromChangeContext(ElementVersion ev) {

		// If you discard something, the context will need to be reloaded to clear out the cache of the old element
		needsReload = true;
		// TODO: Remove element from cache, then a reload shouldn't be required. Further analysis is required to
		// confirm this however...

		ContextUtils.ensureChangeContext(contextId);
		ContextUtils.ensureContextIsOpen(contextId);

		NamedParamPair pair = commonOperations.deleteElementFromStructure(ev.getElementId(), contextId.getContextId());
		commonOperations.executeSqlStatement(pair);
	}

	private void ensurePropertiesLoaded(List<ElementVersion> elementsWhosePropertiesWeShouldLoad) {

		// NOTE: This is one place where we're biased against non-concepts
		// with properties, only concepts have their properties preloaded.
		List<Long> elementIdsWhosePropertiesToLoad = extractIdsOfConcepts(elementsWhosePropertiesWeShouldLoad);

		// Don't load any that are already loaded
		elementIdsWhosePropertiesToLoad.removeAll(elementsWhosePropertiesAreLoaded);

		if (elementIdsWhosePropertiesToLoad.isEmpty()) {
			return;
		}

		SetValuedMap<Class, String> dataProperties = new SetValuedMap<Class, String>();
		List<String> inverseConceptPropertyClasses = new ArrayList<String>();
		List<String> conceptPropertyClasses = new ArrayList<String>();

		for (WrapperConfig wrapperConfig : findWrapperConfigs(elementsWhosePropertiesWeShouldLoad)) {

			if (wrapperConfig == null) {
				throw new IllegalArgumentException("No mapping config found.");
			}

			for (PropertyConfig prop : wrapperConfig.getProperties().values()) {

				if (prop instanceof ConceptPropertyConfig) {
					ConceptPropertyConfig cpg = (ConceptPropertyConfig) prop;

					(cpg.isInverse() ? inverseConceptPropertyClasses : conceptPropertyClasses).add(cpg
							.getPropertyElementClassName());
				} else if (prop instanceof DataPropertyConfig) {
					DataPropertyConfig dataProp = (DataPropertyConfig) prop;

					dataProperties.put(dataProp.getPropertyElementClass(), dataProp.getPropertyElementClassName());
				}
				// We don't care about class or status properties, those are
				// fields in the concept element.

			}

		}

		ArrayList<String> empty = new ArrayList<String>();

		List<PropertyVersion> props = null;

		// As an optimization, first look for only the data properties
		props = elementOperations.loadProperties(contextId, elementIdsWhosePropertiesToLoad, dataProperties, empty,
				empty);

		SetValuedMap<Class, String> conceptProperty = new SetValuedMap<Class, String>();
		conceptProperty.put(ConceptPropertyVersion.class, null);

		// Now call it a second time to get the concept properties.
		props.addAll(elementOperations.loadProperties(contextId, elementIdsWhosePropertiesToLoad, conceptProperty,
				conceptPropertyClasses, inverseConceptPropertyClasses));

		cache(props);

		elementsWhosePropertiesAreLoaded.addAll(elementIdsWhosePropertiesToLoad);
	}

	private List<Long> extractIdsOfConcepts(List<ElementVersion> elements) {
		List<Long> conceptVersionIds = new ArrayList<Long>();
		for (ElementVersion element : elements) {
			if (element instanceof ConceptVersion) {
				conceptVersionIds.add(element.getElementId());
			}
		}
		return conceptVersionIds;
	}

	/**
	 * Entry method of the search implementation. The find methods, and the criterion-producing methods of {@link Ref}
	 * (e.g. ref.eq, ref.like) produce a data structure that represents a search.
	 * 
	 * @param wrapper
	 * @param criteria
	 * @return an Iterator of wrapper class instance.
	 */
	private <T> Iterator<T> find(Ref<T> wrapper, Collection<FindCriterion> criteria) {

		ElementQueryAssembler query = new ElementQueryAssembler(wrapper, criteria, mappingConfig);

		Iterator<Long> find = elementOperations.find(contextId, query.getTargetElement(), query.getRestrictions());

		return prefetchingIterator(wrapper.getWrapperClass(), find);
	}

	@Override
	public <T> Iterator<T> find(Ref<T> wrapper, FindCriterion... criteria) {
		return find(wrapper, criteria == null ? new ArrayList<FindCriterion>() : asList(criteria));
	}

	@Override
	public <T> Iterator<T> findAll(final Class<T> clazz) {
		return find(new Ref<T>(clazz));
	}

	@Override
	public <T> List<T> findList(Ref<T> wrapper, FindCriterion... criteria) {
		Iterator<T> find = find(wrapper, criteria);

		List<T> list = new ArrayList<T>();
		while (find.hasNext()) {
			list.add(find.next());
		}
		return list;
	}

	@Override
	public <T> T findOne(Ref<T> wrapper, FindCriterion... criteria) {
		Iterator<T> find = find(wrapper, criteria);

		try {
			if (find.hasNext()) {
				return find.next();
			}
			return null;
		} finally {
			if (find.hasNext()) {
				throw new IllegalStateException("findOne has returned more than one result");
			}
		}
	}

	/**
	 * 
	 * @param elementClassName
	 *            The class name of the property
	 * @param forElementId
	 *            Either the domain element ID of the property, or the range - in the case of inverse concept properties
	 * @param languageCode
	 * @param inverse
	 *            Indicates that elementId is the range element id of concept properties - we're looking for properties
	 *            that reference the elementId
	 * @return
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<PropertyVersion> findProperties(String elementClassName, long forElementId, String languageCode,
			boolean inverse) {

		// Look in the cache
		PropertyCacheKey key = new PropertyCacheKey(elementClassName, forElementId, languageCode, inverse);

		// Which cache we use depends on whether it's an inverse lookup or not
		if (propertyIdCache.containsKey(key)) {
			Set<Long> ids = propertyIdCache.get(key);
			LOGGER.debug("Property cache hit: " + key + " yields elements " + ids);

			return (List) getCachedElements(ids);

			// return (Collection<PropertyVersion>) (Collection)
			// loadElements(ids);
		}

		LOGGER.debug("Property cache miss: " + key);

		return Collections.emptyList();
	}

	@Override
	public PropertyVersion findProperty(String elementClassName, long forElementId, String languageCode) {

		Collection<PropertyVersion> props = findProperties(elementClassName, forElementId, languageCode, false);

		if (props.size() > 1) {
			throw new IllegalStateException("Attempting to find a single property for elementClass=" + elementClassName
					+ ", domainElementId=" + forElementId + ", languageCode=" + languageCode + " but found "
					+ props.size());
		}

		if (props.isEmpty()) {
			return null;
		}

		return props.iterator().next();
	}

	private Set<WrapperConfig> findWrapperConfigs(List<ElementVersion> elementsWhosePropertiesWeShouldLoad) {
		Set<WrapperConfig> wrapperConfigs = new HashSet<WrapperConfig>();
		for (ElementVersion element : elementsWhosePropertiesWeShouldLoad) {

			WrapperConfig wrapperConfig = mappingConfig.forElementClassName(contextId.getBaseClassification(),
					element.getClassName());

			if (wrapperConfig == null) {
				LOGGER.fatal("No wrapping config found for bc=" + contextId.getBaseClassification() + " elementClass="
						+ element.getClassName());
			}

			wrapperConfigs.add(wrapperConfig);
		}
		return wrapperConfigs;
	}

	@Override
	public ElementVersion getCachedElement(Long elementId) {
		return elementCache.get(elementId);
	}

	@Override
	public List<ElementVersion> getCachedElements(Collection<Long> elementIds) {
		List<ElementVersion> results = new ArrayList<ElementVersion>(elementIds.size());
		for (Long id : elementIds) {
			results.add(elementCache.get(id));
		}
		return results;
	}

	@Override
	public HashMap<ElementVersion, ElementVersion> getConflicts() {

		ContextUtils.ensureChangeContext(contextId);

		ContextIdentifier baseContext = contextOperations.findContextById(contextId.getBaseClassification(),
				contextId.getBaseStructureId());

		return elementOperations.getConflicts(contextId, baseContext);
	}

	@Override
	public ContextIdentifier getContextId() {
		return contextId;
	}

	private String getLanguage(PropertyVersion property) {
		String language = null;
		if (property instanceof LanguageSpecific) {
			language = ((LanguageSpecific) property).getLanguageCode();
		}

		return language;
	}

	private Class getWrapperClassFor(ElementVersion element) {
		return mappingConfig.forElementClassName(contextId.getBaseClassification(), element.getClassName())
				.getWrapperClass();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Collection<T> load(Collection<Long> elementIds) {

		// TODO: Uh, why am I calling load elements given that the elements
		// might already have cached wrappers?

		// It's not obvious that I have to load elements at all, though if I
		// don't, I probably need to call ensurePropertiesLoaded instead
		List<ElementVersion> elements = loadElements(elementIds);

		// ensurePropertiesLoaded(new ArrayList<Long>(elementIds), true);

		List<T> wrappers = new ArrayList<T>();

		for (ElementVersion element : elements) {
			// for (Long elementId : elementIds) {
			// System.err.println(element);
			long elementId = element.getElementId();

			if (wrapperCache.containsKey(elementId)) {
				wrappers.add((T) wrapperCache.get(elementId));
			} else {
				T newInstance = wrapperFactory.newInstance((Class<T>) getWrapperClassFor(element), this);
				((Identified) newInstance).setElementId(elementId);

				wrapperCache.put(elementId, newInstance);
				wrappers.add(newInstance);
			}
		}
		return wrappers;
	}

	@Override
	public <T> T load(long elementId) {
		Collection<T> wrappers = load(asList(elementId));
		if (wrappers.isEmpty()) {
			return null;
		}
		return wrappers.iterator().next();
	}

	/**
	 * Implementation of {@link ContextElementAccess#loadElements(Collection)} method, calls
	 * {@link ElementOperations#loadElements(ContextIdentifier, Collection)} method to load ElmentVersion and its
	 * properties from database, then put it in to cache.
	 */
	@Override
	public List<ElementVersion> loadElements(Collection<Long> elementIds) {

		// TODO: Consider not even loading the central element if the wrapper
		// doesn't contain relevant properties (status, class name) - though,
		// most will, so YAGNI.

		Set<Long> uncachedIds = new HashSet<Long>(elementIds);
		uncachedIds.removeAll(elementCache.keySet());

		if (!uncachedIds.isEmpty()) {
			// LOGGER.debug("Loading " + uncachedIds.size() + " instances of " +
			// wrapperClass.getSimpleName());
			List<ElementVersion> elements = elementOperations.loadElements(contextId, uncachedIds);

			cache(elements);

			LOGGER.debug("Ensuring properties loaded from all concepts in " + elements);
			ensurePropertiesLoaded(elements);
		}

		return getCachedElements(elementIds);
	}

	@Override
	public ElementVersion makeNewElement(String className, String businessKey) {

		ElementVersion element = elementOperations.newInstance(contextId, className, businessKey);

		element.setClassName(className);
		element.setBusinessKey(businessKey);
		element.setStatus("ACTIVE");

		return element;
	}

	private <T> T makeWrapper(Class<T> wrapperClass, ConceptVersion element) {
		T newInstance = wrapperFactory.newInstance(wrapperClass, this);
		((Identified) newInstance).setElementId(element.getElementId());

		wrapperCache.put(element.getElementId(), newInstance);

		return newInstance;
	}

	@Override
	public void persist() {

		ContextUtils.ensureContextDoesNotRequireReload(contextId, needsReload);

		if (contextId.isContextOpen()) {
			Iterator<ElementVersion> dirties = dirtyElements.iterator();

			if (!dirties.hasNext()) {
				LOGGER.info("Nothing to persist");
			}

			while (dirties.hasNext()) {
				elementOperations.updateElement(contextId, dirties.next());
				dirties.remove();
			}
		} else {
			LOGGER.warn("An attempt to persist on a closed context has occurred.  Skipped. " + contextId);
		}
	}

	private <T> Iterator<T> prefetchingIterator(final Class<T> wrapperClass, Iterator<Long> ids) {

		return new LoadingIterator<T>(new PrefetchingIdIterator(ids, PREFETCH_ELEMENT_COUNT, this));
	}

	@Override
	public HashMap<ElementVersion, ElementVersion> realizeChangeContext(boolean isAdmin) {

		ContextUtils.ensureContextDoesNotRequireReload(contextId, needsReload);

		// TODO: Consider adding isAdmin to ContextAccess if in the future this admin can do other things not normally
		// allowed

		ContextUtils.ensureChangeContext(contextId);
		ContextUtils.ensureContextIsOpen(contextId);

		ContextIdentifier baseContext = contextOperations.findContextById(contextId.getBaseClassification(),
				contextId.getBaseStructureId());

		ContextUtils.ensureContextIsOpen(baseContext);

		Collection<ContextIdentifier> openBaseContexts = contextOperations.findOpenBaseContextIdentifiers(contextId
				.getBaseClassification());

		// Admins can realize change contexts whenever they feel like it
		if (!isAdmin) {
			ContextUtils.ensureContextIsOldest(baseContext, openBaseContexts);
		}

		Collection<ContextIdentifier> newerContexts = ContextUtils.returnNewerContexts(baseContext, openBaseContexts);
		/*
		 * HashMap<ElementVersion, ElementVersion> conflicts =
		 * elementOperations.realizeChangeContextWithoutCheckingConflicts(contextId, baseContext, newerContexts);
		 */
		/* switch to check conflicts now */
		HashMap<ElementVersion, ElementVersion> conflicts = elementOperations.realizeChangeContext(contextId,
				baseContext, newerContexts);

		// A reload is required because once realized, any removed elements still exist in cache. Reloading will
		// ensure it does not get loaded.
		if (conflicts.size() == 0) {
			needsReload = true;
		}

		return conflicts; // Conflicts can be empty
	}

	@Override
	public ContextAccess reload() {
		// if (!needsReload) {
		// LOGGER.warn("Context does not need reloading");
		// return this;
		// } else {

		needsReload = false;
		return provider.findContext(contextId);
		// }
	}

	@Override
	public List<ElementVersion> retrieveChangesFromChangeContext() {

		ContextUtils.ensureChangeContext(contextId);
		ContextUtils.ensureContextIsOpen(contextId);

		List<Long> crElementIds = elementOperations.findAllElementIdsInChangeRequest(contextId);
		return elementOperations.loadElements(contextId, crElementIds);
	}

	@Override
	public Long retrieveNestingLevel(long elementId) {
		return elementOperations.retrieveNestingLevel(contextId.getBaseClassification(), contextId.getContextId(),
				elementId);
	}

	@Override
	public Long retrieveNumberOfChildrenWithValidation(String code) {
		return elementOperations.retrieveNumberOfChildrenWithValidation(contextId.getBaseClassification(),
				contextId.getContextId(), code);
	}

	@Override
	public String toString() {
		return "HgContext[" + contextId + "]";
	}

	@Override
	public void touch(ElementVersion elementVersion) {
		dirtyElements.add(elementVersion);
		cache(asList(elementVersion));

		if (autoCommitEnabled()) {
			persist();
		}
	}
}
