package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.DuplicateConceptException;
import ca.cihi.cims.bll.ContextUtils;
import ca.cihi.cims.bll.hg.SetValuedMap;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptPropertyVersion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.DataPropertyVersion;
import ca.cihi.cims.dal.ElementOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NamedParamPair;
import ca.cihi.cims.dal.PropertyVersion;
import ca.cihi.cims.dal.StructureElement;
import ca.cihi.cims.dal.query.ClassIn;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.FieldIn;
import ca.cihi.cims.dal.query.MightBeA;
import ca.cihi.cims.dal.query.OrRestriction;
import ca.cihi.cims.dal.query.Restriction;

@Component
public class JdbcElementOperations implements ElementOperations {

	static final Logger LOGGER = LogManager.getLogger(JdbcElementOperations.class);

	private static final int ORACLE_MAX_NUM_FOR_IN = 1000;

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamed;

	@Autowired
	private ContextRestrictionQueryBuilder queryBuilder;

	@Autowired
	private FlatElementQueryBuilder flatQueryBuilder;

	@Autowired
	private ORConfig mappingConfig;

	@Autowired
	private Sequences sequences;

	@Autowired
	ClassService classService;

	@Autowired
	CommonElementOperations commonOperations;

	private final SqlFormatter sqlFormat = new SqlFormatter();

	private boolean businessKeyExistsInContext(String businessKey, ContextIdentifier contextId) {
		String query = "select count(*) FROM ELEMENT e "
				+ "JOIN STRUCTUREELEMENTVERSION sev ON e.elementid = sev.elementid "
				+ "and sev.structureid IN (:structureId, :baseStructureId) " + "WHERE e.ELEMENTUUID = :businessKey";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("structureId", contextId.getContextId());
		params.put("baseStructureId", contextId.getBaseStructureId());
		params.put("businessKey", businessKey);

		LOGGER.debug(new SqlFormatter().format(query, params));

		boolean existsInContext = jdbcNamed.queryForObject(query, params, Integer.class) > 0;
		return existsInContext;
	}

	// This method needs to be revisited to see what it should be doing, or merged into somewhere else..
	private void checkElementVersionValidity(List<ElementVersion> crElements, List<ElementVersion> baseElements) {
		// Check for size, ensure the CR has same or more elements (same if only modify existing properties, more if
		// new ones were added. Not sure if these should throw those errors, need to revisit
		if (crElements.size() == 0) {
			// TODO: HYL: dont think we should be throwing an exception. It can cause a lot of problems later on
			// throw new IllegalArgumentException("Unable to realize change request... nothing to do");
			LOGGER.info("Unable to realize change request... nothing to do");
		} else if (!(crElements.size() >= baseElements.size())) {
			throw new IllegalArgumentException("Unable to realize change request... unable to evaluate changes");
		}

	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public HashMap<ElementVersion, ElementVersion> checkRealizationConflicts(ContextIdentifier contextId,
			ContextIdentifier baseContext, Collection<ContextIdentifier> newerContexts) {

		// Get elementIds in Change Request
		List<Long> crElementIds = findAllElementIdsInChangeRequest(contextId);

		// Load elements from both contexts
		List<ElementVersion> crElements = loadElements(contextId, crElementIds);
		List<ElementVersion> baseElements = loadElements(baseContext, crElementIds);

		checkElementVersionValidity(crElements, baseElements);
		HashMap<ElementVersion, ElementVersion> conflicts = getConflicts(crElements, baseElements);
		return conflicts;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public void closeChangeContext(ContextIdentifier context) {
		ContextUtils.ensureChangeContext(context);
		ContextUtils.ensureContextIsOpen(context);
		String update = null;
		HashMap<String, Object> updateParameters = new HashMap<String, Object>();
		context.closeContext();
		update = "UPDATE STRUCTUREVERSION SET CONTEXTSTATUS = :contextStatus, "
				+ "CONTEXTSTATUSDATE = :contextStatusDate WHERE STRUCTUREID = :structureId";
		updateParameters.put("contextStatus", context.getContextStatus());
		updateParameters.put("contextStatusDate", context.getContextStatusDate());
		updateParameters.put("structureId", context.getContextId());
		commonOperations.executeSqlStatement(update, updateParameters);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public void closeContext(ContextIdentifier context) {
		ContextUtils.ensureNotAChangeContext(context);
		String update = null;
		HashMap<String, Object> updateParameters = new HashMap<String, Object>();
		context.closeContext();
		update = "UPDATE STRUCTUREVERSION SET CONTEXTSTATUS = :contextStatus, "
				+ "CONTEXTSTATUSDATE = :contextStatusDate WHERE STRUCTUREID = :structureId";
		updateParameters.put("contextStatus", context.getContextStatus());
		updateParameters.put("contextStatusDate", context.getContextStatusDate());
		updateParameters.put("structureId", context.getContextId());
		commonOperations.executeSqlStatement(update, updateParameters);
	}

	private boolean compareConceptVersion(ConceptVersion crEV, ConceptVersion baseEV) {
		boolean sameStatus = Objects.equals(crEV.getStatus(), baseEV.getStatus());
		if (sameStatus) {
			LOGGER.info("[" + crEV.getBusinessKey() + "] conflict but status same.  Allow update...");
			return true;
		} else {
			// Tiger for incident #36641, user create and disable a validation rule in one Change Request and create the
			// same validation rule in another change request
			if (ConceptStatus.DISABLED.name().equals(baseEV.getStatus())) {
				LOGGER.info("[" + baseEV.getBusinessKey() + "] conflict but base status is DISABLED. Allow update...");
				return true;
			}

			LOGGER.info("[" + crEV.getBusinessKey() + "] conflict and status do not match.  Cannot allow update...");
		}
		return false;
	}

	private boolean compareCPV(ConceptPropertyVersion crEV, ConceptPropertyVersion baseEV) {
		boolean sameDomain = Objects.equals(crEV.getDomainElementId(), baseEV.getDomainElementId());
		boolean sameRange = Objects.equals(crEV.getRangeElementId(), baseEV.getRangeElementId());
		if (sameDomain && sameRange) {
			LOGGER.info("[" + crEV.getBusinessKey() + "] conflict but domain/range are same.  Allow update...");
			return true;
		} else {
			LOGGER.info("[" + crEV.getBusinessKey() + "] conflict and values do not match.  Cannot allow update...");
		}
		return false;
	}

	private boolean compareDataProperty(DataPropertyVersion crEV, DataPropertyVersion baseEV) {
		boolean sameValue = Objects.equals(crEV.getValue(), baseEV.getValue());
		// boolean sameStatus = Objects.equal(crEV.getStatus(), baseEV.getStatus());
		if (sameValue) {
			LOGGER.info("[" + crEV.getBusinessKey() + "] conflict but values are same.  Allow update...");
			return true;
		} else {
			LOGGER.info("[" + crEV.getBusinessKey() + "] conflict and values do not match.  Cannot allow update...");
		}
		return false;
	}

	@Override
	public String determineClassNameByElementId(long elementId) {
		String query = "select c.classname from element e, class c where e.elementid = :elementId "
				+ "and e.classid = c.classid";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("elementId", elementId);
		LOGGER.debug(new SqlFormatter().format(query, params));
		List<String> classNames = jdbcNamed.queryForList(query, params, String.class);
		String className;
		if (classNames.isEmpty()) {
			LOGGER.warn("No class name found for ElementId: [" + elementId + "]");
			className = null;
		} else {
			className = classNames.get(0);
		}
		return className;
	}

	@Override
	public String determineContainingIdPath(String baseClassification, long contextId, Long elementId) {
		String sql = "select CIMS_UTIL.retrieveContainingIdPathbyEId(:baseClassification, :contextId, :elemId) from dual";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("baseClassification", baseClassification);
		paramMap.put("contextId", contextId);
		paramMap.put("elemId", elementId);
		return jdbcNamed.queryForObject(sql, paramMap, String.class);
	}

	@Override
	public String determineContainingIdPath(String baseClassification, long contextId, String code) {
		String sql = "select CIMS_UTIL.retrieveContainingIdPathbyCode(:baseClassification, :contextId, :code) from dual";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("baseClassification", baseClassification);
		paramMap.put("contextId", contextId);
		paramMap.put("code", code);
		return jdbcNamed.queryForObject(sql, paramMap, String.class);
	}

	@Override
	public Long determineContainingPage(String baseClassification, long contextId, long elementId) {
		String sql = "select CIMS_UTIL.retrieveContainingPagebyId(:baseClassification, :contextId, :elemId) from dual";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("baseClassification", baseClassification);
		paramMap.put("contextId", contextId);
		paramMap.put("elemId", elementId);
		return jdbcNamed.queryForObject(sql, paramMap, Long.class);
	}

	@Override
	public Class<?> determineElementClass(String baseClassification, String className) {
		String tableName = classService.getCachedTableName(baseClassification, className);
		Class forTableName = mappingConfig.forTableName(tableName);
		if (forTableName == null) {
			throw new IllegalArgumentException(
					"Mapping not defined for [" + className + "] with tableName [" + tableName + "]");
		}
		return forTableName;
	}

	/**
	 * Evaluate both ElementVersions to determine if it can be realized
	 *
	 * One of these checks must be true
	 *
	 * (1) Base ElementVersion is null - Indicates the change you are doing is NEW
	 *
	 * (2) Base ElementVersion is not null. Matches Change Context ElementVersion changedFromVersionId
	 *
	 * (3) The value in both ElementVersions match
	 *
	 * @param crEV
	 *            Changed ElementVersions from the change context
	 * @param baseEV
	 *            The related ElementVersions from a base context
	 * @return True if the series of checks determines that it can be realized
	 */
	private boolean determineIfRealizable(ElementVersion crEV, ElementVersion baseEV) {
		LOGGER.debug("Base Element null? " + Objects.equals(baseEV, null));
		LOGGER.debug("CR Element null? " + Objects.equals(crEV, null));
		if (baseEV == null) {
			// Indicates this is a new version, so we allow
			return true;
		}
		// baseEV is not null, so something exists in the base context.
		Long changedFromVersionId = crEV.getChangedFromVersionId();
		Long baseVersionId = baseEV.getElementVersionId();
		LOGGER.debug("[" + changedFromVersionId + "/" + baseVersionId + "]");

		// Realizable if versions match
		if (Objects.equals(changedFromVersionId, baseVersionId)) {
			LOGGER.info("[" + crEV.getBusinessKey() + "] determined to be saveable");
			return true;
		} else {
			LOGGER.debug("[" + crEV.getBusinessKey() + "] versions do not match");
		}
		// Versions do not match. If values match, it is realizable
		if (crEV instanceof DataPropertyVersion) {
			return compareDataProperty((DataPropertyVersion) crEV, (DataPropertyVersion) baseEV);
		} else if (crEV instanceof ConceptVersion) {
			return compareConceptVersion((ConceptVersion) crEV, (ConceptVersion) baseEV);
		} else if (crEV instanceof ConceptPropertyVersion) {
			return compareCPV((ConceptPropertyVersion) crEV, (ConceptPropertyVersion) baseEV);
		}
		return false;
	}

	@Override
	public String determineVersionCodeByElementId(long elementId) {
		String query = "select distinct versioncode from structureelementversion sev, elementversion ev "
				+ "where sev.elementid = :elementId and sev.elementversionid = ev.elementversionid";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("elementId", elementId);

		LOGGER.debug(new SqlFormatter().format(query, params));

		List<String> versionCodes = jdbcNamed.queryForList(query, params, String.class);
		Collections.sort(versionCodes);
		String versionCode;

		if (versionCodes.isEmpty()) {
			LOGGER.warn("No version found for ElementId: [" + elementId + "]");
			versionCode = null;
		} else {
			versionCode = versionCodes.get(0);
		}
		return versionCode;
	}

	private boolean exclusivelyOwnedByContext(ContextIdentifier context, ElementVersion element) {
		String query = "select case when existsInContext=1 and existsInOthers=0 then 1 else 0 end " + "from ( select "
				+ "  (select count(*) " + "  from structureelementversion "
				+ "  where elementversionid=:elementVersionId "
				+ "  and structureid <> :structureId) as existsInOthers, " + "  (select count(*) "
				+ "  from structureelementversion " + "  where elementversionid=:elementVersionId "
				+ "  and structureid = :structureId) as existsInContext " + "from dual)";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("structureId", context.getContextId());
		params.put("elementVersionId", element.getElementVersionId());

		LOGGER.debug(new SqlFormatter().format(query, params));

		boolean exclusivelyOwned = jdbcNamed.queryForObject(query, params, Integer.class) == 1;
		return exclusivelyOwned;
	}

	@Override
	public Iterator<Long> find(ContextIdentifier context, ElementRef element, Collection<Restriction> restrictions) {
		ParamNamer namer = new ParamNamer();
		ElementQueryBuilder elementQueryBuilder = new ElementQueryBuilder(context, mappingConfig, queryBuilder, namer);

		LOGGER.debug("Finding " + element);
		if (LOGGER.isTraceEnabled()) {
			for (Restriction restriction : restrictions) {
				LOGGER.trace("\t" + restriction);
			}
		}

		SelectBits query = elementQueryBuilder.buildElementIdQuery(element, restrictions);
		// Now run the query!
		LOGGER.debug(new SqlFormatter().format(query.toString(), namer.getParamMap()));
		List<Long> elementIds = jdbcNamed.queryForList(query.toString(), namer.getParamMap(), Long.class);

		return Collections.unmodifiableList(elementIds).iterator();
	}

	@Override
	public List<Long> findAllElementIdsInChangeRequest(ContextIdentifier context) {
		String sql = "select distinct elementid from structureelementversion where structureid=:structureId";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("structureId", context.getContextId());
		List<Long> crElementIds = jdbcNamed.queryForList(sql, paramMap, Long.class);
		return crElementIds;
	}

	@Override
	public HashMap<ElementVersion, ElementVersion> getConflicts(ContextIdentifier context,
			ContextIdentifier baseContext) {
		ContextUtils.ensureChangeContext(context);
		ContextUtils.ensureContextIsOpen(context);

		// Get elementIds in Change Request
		List<Long> crElementIds = findAllElementIdsInChangeRequest(context);

		// Load elements from both contexts
		List<ElementVersion> crElements = loadElements(context, crElementIds);
		List<ElementVersion> baseElements = loadElements(baseContext, crElementIds);

		HashMap<ElementVersion, ElementVersion> badElementVersions = getConflicts(crElements, baseElements);
		return badElementVersions;
	}

	private HashMap<ElementVersion, ElementVersion> getConflicts(List<ElementVersion> crElements,
			List<ElementVersion> baseElements) {
		HashMap<ElementVersion, ElementVersion> badElementVersions = new HashMap<ElementVersion, ElementVersion>();
		for (ElementVersion crEV : crElements) {
			ElementVersion baseEV = getElementVersionFromList(baseElements, crEV.getElementId());
			if (!determineIfRealizable(crEV, baseEV)) {
				LOGGER.warn("[" + crEV.getBusinessKey() + "] is not saveable");
				badElementVersions.put(crEV, baseEV);
			}
		}
		return badElementVersions;
	}

	private ElementVersion getElementVersionFromList(List<ElementVersion> baseElements, Long elementId) {
		for (ElementVersion ev : baseElements) {
			LOGGER.trace("Looping through " + ev.getBusinessKey());
			if (ev.getElementId() == elementId) {
				LOGGER.trace("Found match with " + elementId);
				return ev;
			}
		}
		return null;
	}

	@Override
	public String getIndexPath(long contextId, long elementId) {
		String sql = "select CIMS_UTIL.getIndexPath(:elemId, :contextId ) from dual";
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("contextId", contextId);
		paramMap.put("elemId", elementId);
		return jdbcNamed.queryForObject(sql, paramMap, String.class);
	}

	private void insertNewElementVersionRows(ContextIdentifier context, ElementVersion element) {
		Class c = element.getClass();
		List<NamedParamPair> pairs = new ArrayList<NamedParamPair>();

		element.setOriginatingContextId(context.getContextId());
		element.setChangedFromVersionId(null);

		if (element.getElementVersionId() != null) {
			// We are inserting a new element, even though one exists. We are
			// therefore replacing the version that already exists.
			element.setChangedFromVersionId(element.getElementVersionId());
		}

		// A new version ID is assigned here, even if the element already had
		// one (because we're using this row as a template for a fresh version).
		element.setElementVersionId(sequences.nextValue(Sequences.ELEMENTVERSION_ID_SEQUENCE));

		HashMap<String, Object> defaultMap = new HashMap<String, Object>();
		defaultMap.put("versionTimeStamp", new Date());
		defaultMap.put("versionCode", context.getVersionCode());
		defaultMap.put("structureId", context.getContextId());

		pairs.addAll(commonOperations.buildInsertSqlStmt(StructureElement.class, element, defaultMap));
		pairs.addAll(commonOperations.buildInsertSqlStmt(c, element, defaultMap));

		commonOperations.executeSqlStatements(pairs, true);
	}

	private Object instantiate(Class clazz) {
		try {
			return clazz.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException(clazz.getSimpleName() + " uninstantiable.");
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(clazz.getSimpleName() + " uninstantiable.");
		}
	}

	@Override
	public ElementVersion loadElement(ContextIdentifier context, long elementId) {
		List<ElementVersion> elements = loadElements(context, Arrays.asList(elementId));
		if (elements.isEmpty()) {
			return null;
		}
		return elements.get(0);
	}

	@Override
	public List<ElementVersion> loadElements(ContextIdentifier context, Collection<Long> elementIds) {
		if (elementIds.isEmpty()) {
			return Collections.emptyList();
		}
		ParamNamer namer = new ParamNamer();
		SelectBits query = flatQueryBuilder.buildElementQuery(context, namer);

		// String whereClause = "Element.ElementId" + " IN ( " + StringUtils.join(namer.params(elementIds), ", ") + "
		// )";
		String whereClause = null;
		if (elementIds.size() <= ORACLE_MAX_NUM_FOR_IN) {
			whereClause = "Element.ElementId" + " IN ( " + StringUtils.join(namer.params(elementIds), ", ") + " )";
		} else {
			whereClause = "(Element.ElementId" + splitElementIds(namer, elementIds) + " )";
		}

		query.addWhere(whereClause);

		LOGGER.debug("loadElements " + elementIds);
		LOGGER.debug(sqlFormat.format(query.toString(), namer.getParamMap()));

		return jdbcNamed.query(query.toString(), namer.getParamMap(),
				new AnyElementRowMapper<ElementVersion>(mappingConfig, query));
	}

	private String splitElementIds(ParamNamer namer, Collection<Long> elementIds) {
		StringBuffer whereClauseSB = new StringBuffer();
		List<Collection<Long>> subElementIdsLists = new ArrayList<Collection<Long>>();

		List<Long> elementIdList = new ArrayList<Long>(elementIds);

		for (int start = 0; start <= elementIds.size(); start += ORACLE_MAX_NUM_FOR_IN) {
			int end = Math.min(start + ORACLE_MAX_NUM_FOR_IN, elementIds.size());
			if (end == start) {
				break;
			}
			List<Long> sublist = elementIdList.subList(start, end);
			Collection<Long> subElementIds = new HashSet<Long>(sublist);
			subElementIdsLists.add(subElementIds);
		}

		for (int i = 0; i < subElementIdsLists.size(); i++) {
			if (i == 0) {
				whereClauseSB.append(" IN ( ");
			} else {
				whereClauseSB.append(" OR Element.ElementId IN ( ");
			}
			whereClauseSB.append(StringUtils.join(namer.params(subElementIdsLists.get(i)), ", ") + " )");
		}
		return whereClauseSB.toString();
	}

	/**
	 * This is an improved implementation of property loading. Given a list of element Java classes and the classnames
	 * we care about, it will build a minimalist query to load them all.
	 */
	@Override
	public List<PropertyVersion> loadProperties(ContextIdentifier context, Collection<Long> domainElementIds,
			SetValuedMap<Class, String> classesAndCodes, Collection<String> conceptPropertyClasses,
			Collection<String> inverseConceptPropertyClasses) {

		List<Restriction> restrictions = new ArrayList<Restriction>();

		Map<Class, ElementRef> refs = new HashMap<Class, ElementRef>();

		ElementRef propVersion = new ElementRef(PropertyVersion.class);
		refs.put(PropertyVersion.class, propVersion);

		for (Class clazz : classesAndCodes.keySet()) {
			ElementRef ref = new ElementRef(clazz);
			refs.put(clazz, ref);

			Set<String> classNames = classesAndCodes.get(clazz);
			// Null is a hack to stop the list collapsing, a way of saying 'I
			// want this elementVersion subclass, but not sure what classes'
			classNames.remove(null);
			if (!classNames.isEmpty()) {
				restrictions.add(new ClassIn(ref, classNames));
			}

			restrictions.add(new MightBeA(propVersion, ref));
		}

		// TODO: Consider doing this differently. This may not be the optimal
		// way to sort this out by any means. I wonder if a different way of
		// organizing the parameters would lend itself to better optimizations.
		{
			List<Restriction> linkRestrictions = new ArrayList<Restriction>();

			if (classesAndCodes.keySet().contains(ConceptPropertyVersion.class)
					&& !inverseConceptPropertyClasses.isEmpty()) {
				linkRestrictions
						.add(new FieldIn(refs.get(ConceptPropertyVersion.class), "rangeElementId", domainElementIds));
			}
			linkRestrictions.add(new FieldIn(refs.get(PropertyVersion.class), "domainElementId", domainElementIds));

			if (linkRestrictions.size() > 1) {
				restrictions.add(new OrRestriction(linkRestrictions));
			} else {
				restrictions.addAll(linkRestrictions);
			}
		}

		// for (Restriction r : restrictions)
		// System.err.println(r);

		ParamNamer namer = new ParamNamer();
		NestedElementQueryBuilder builder = new NestedElementQueryBuilder(context, mappingConfig, queryBuilder, namer);

		SelectBits query = builder.buildQuery(restrictions);
		query.setQueryName("Property Query");

		// Add Class and Table columns so that we can determine the type of
		// each row that comes back
		query.addTable("Class");
		query.addColumn("Class.ClassName");
		query.addColumn("Class.TableName");
		query.addWhere("Class.ClassId = " + builder.alias(propVersion) + ".ClassId");

		query.addTable("Element");
		query.addColumn("Element.elementuuid");
		query.addWhere("Element.elementid = " + builder.alias(propVersion) + ".elementid");

		Map<ColAliasKey, String> columnAliases = new HashMap<ColAliasKey, String>();

		// Now create unique aliases for each column to deal with potential
		// duplicates (e.g. LanguageCode)
		int suffix = 0;
		for (Class clazz : classesAndCodes.keySet()) {
			ClassORMapping map = mappingConfig.getMapping(clazz);

			query.addColumn(map.getIdColumn());

			for (ColumnMapping col : map.getColumnMappings()) {

				String tableAlias = builder.alias(refs.get(clazz));

				String colAlias = col.getColumnName() + suffix++;

				query.addColumn(tableAlias + "." + col.getColumnName() + " AS " + colAlias);

				// Store the column alias so it can be given to the row
				// mapper later, since it has to pull values out of the columns
				columnAliases.put(new ColAliasKey(clazz, col), colAlias);
			}
		}

		LOGGER.debug(new SqlFormatter().format(query.toString(), namer.getParamMap()));

		List<PropertyVersion> results = jdbcNamed.query(query.toString(), namer.getParamMap(),
				new AnyLeafElementRowMapper<PropertyVersion>(mappingConfig, columnAliases, classesAndCodes.keySet()));

		return results;
	}

	@Override
	public ElementVersion newInstance(ContextIdentifier contextId, String className, String businessKey) {

		/**
		 * this thing needs to check if the elementversionid already exists in the context. Need to check both for
		 * effective
		 */
		if (businessKeyExistsInContext(businessKey, contextId)) {
			throw new DuplicateConceptException(
					"[" + businessKey + "] already exists in [" + contextId.getBaseClassification() + "]");
		}

		Class elementClass = determineElementClass(contextId.getBaseClassification(), className);

		ElementVersion element = (ElementVersion) instantiate(elementClass);
		element.setClassName(className);
		element.setBusinessKey(businessKey);

		long classId = classService.getCachedClassId(contextId.getBaseClassification(), element.getClassName());
		element.setClassId(classId);

		Long elementId = commonOperations.createOrRetrieveElement(element.getBusinessKey(), element.getClassId());
		element.setElementId(elementId);

		return element;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public HashMap<ElementVersion, ElementVersion> realizeChangeContext(ContextIdentifier contextId,
			ContextIdentifier baseContext, Collection<ContextIdentifier> newerContexts) {

		// Get elementIds in Change Request
		List<Long> crElementIds = findAllElementIdsInChangeRequest(contextId);

		// Load elements from both contexts
		List<ElementVersion> crElements = loadElements(contextId, crElementIds);
		List<ElementVersion> baseElements = loadElements(baseContext, crElementIds);

		checkElementVersionValidity(crElements, baseElements);
		HashMap<ElementVersion, ElementVersion> conflicts = getConflicts(crElements, baseElements);

		if (conflicts.size() > 0) {
			LOGGER.warn("Conflicts detected.  Unable to realize change request.");
			return conflicts;
		}

		for (ElementVersion crEV : crElements) {

			LOGGER.info("Processing [" + crEV.getBusinessKey() + "] in CR [" + contextId.getVersionCode() + "]");
			if (crEV.getStatus().equals(ConceptStatus.REMOVED.name())) {
				// Check removal eligibility once more
				if (!commonOperations.isConceptEligibleForRemoval(contextId, (ConceptVersion) crEV)) {
					throw new IllegalArgumentException("Concept cannot be removed.  [" + crEV.getBusinessKey() + "]");
				}

				crEV.setBusinessKey(BusinessKeyGenerator.modifyKeyToRemoved(crEV.getBusinessKey()));
			}

			realizeElement(baseContext, crEV);

			for (ContextIdentifier newerContext : newerContexts) {
				realizeElement(newerContext, crEV);
			}
		}

		closeChangeContext(contextId);

		// Return the empty conflicts map to the user
		return conflicts;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public HashMap<ElementVersion, ElementVersion> realizeChangeContextWithoutCheckingConflicts(
			ContextIdentifier contextId, ContextIdentifier baseContext, Collection<ContextIdentifier> newerContexts) {
		HashMap<ElementVersion, ElementVersion> conflicts = new HashMap<ElementVersion, ElementVersion>();
		// Get elementIds in Change Request
		List<Long> crElementIds = findAllElementIdsInChangeRequest(contextId);

		// Load elements from both contexts
		List<ElementVersion> crElements = loadElements(contextId, crElementIds);
		List<ElementVersion> baseElements = loadElements(baseContext, crElementIds);
		checkElementVersionValidity(crElements, baseElements);
		for (ElementVersion crEV : crElements) {
			LOGGER.info("Processing [" + crEV.getBusinessKey() + "] in CR [" + contextId.getVersionCode() + "]");
			if (crEV.getStatus().equals(ConceptStatus.REMOVED.name())) {
				// Check removal eligibility once more
				if (!commonOperations.isConceptEligibleForRemoval(contextId, (ConceptVersion) crEV)) {
					throw new IllegalArgumentException("Concept cannot be removed.  [" + crEV.getBusinessKey() + "]");
				}

				crEV.setBusinessKey(BusinessKeyGenerator.modifyKeyToRemoved(crEV.getBusinessKey()));
			}

			realizeElement(baseContext, crEV);

			for (ContextIdentifier newerContext : newerContexts) {
				realizeElement(newerContext, crEV);
			}
		}

		closeChangeContext(contextId);
		// Return the empty conflicts map to the user
		return conflicts;
	}

	/**
	 * Realizing an element means working with the StructureElementVersion table. It "realizes" that change to the
	 * context year you want
	 *
	 * @param context
	 *            The Context you are making the change to
	 * @param element
	 *            The Element Version you are changing
	 */
	private void realizeElement(ContextIdentifier context, ElementVersion element) {

		List<NamedParamPair> pairs = new ArrayList<NamedParamPair>();
		boolean isNewToContext = element.getChangedFromVersionId() == null;

		if (isNewToContext) {
			HashMap<String, Object> defaultMap = new HashMap<String, Object>();
			defaultMap.put("structureId", context.getContextId());

			pairs.addAll(commonOperations.buildInsertSqlStmt(StructureElement.class, element, defaultMap));

		} else {
			String sevUpdate = "UPDATE STRUCTUREELEMENTVERSION SET ELEMENTVERSIONID = :elementVersionId "
					+ "WHERE ELEMENTID = :elementId AND STRUCTUREID = :baseStructureId";

			HashMap<String, Object> defaultMap = new HashMap<String, Object>();
			defaultMap.put("elementVersionId", element.getElementVersionId());
			defaultMap.put("baseStructureId", context.getContextId());
			defaultMap.put("elementId", element.getElementId());

			pairs.add(new NamedParamPair(sevUpdate, defaultMap));

		}

		// Updates the elements business key with the 'removed' version set prior
		// To Howard from Tiger & Malina: moved this outside of if/else to address the case where we create and then
		// remove immediately before realizing
		if (element.getStatus().equals(ConceptStatus.REMOVED.name())) {
			pairs.add(commonOperations.buildRemoveConceptSqlStmt(element));
		}

		commonOperations.executeSqlStatements(pairs, true);
	}

	@Override
	public Long retrieveNestingLevel(String baseClassification, long contextId, long elementId) {

		String sql = "select CIMS_UTIL.retrieveCodeNestingLevel(:baseClassification, :contextId, :elemId) from dual";

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("baseClassification", baseClassification);
		paramMap.put("contextId", contextId);
		paramMap.put("elemId", elementId);

		return jdbcNamed.queryForObject(sql, paramMap, Long.class);
	}

	@Override
	public Long retrieveNumberOfChildrenWithValidation(String baseClassification, long contextId, String code) {

		String sql = "select CIMS_UTIL.numberOfChildrenWithValidation(:baseClassification, :contextId, :code) from dual";

		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("baseClassification", baseClassification);
		paramMap.put("contextId", contextId);
		paramMap.put("code", code);

		return jdbcNamed.queryForObject(sql, paramMap, Long.class);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public void updateElement(ContextIdentifier context, ElementVersion element) {
		if (exclusivelyOwnedByContext(context, element)) {
			updateElementVersionRows(element);
		} else {
			insertNewElementVersionRows(context, element);
		}
	}

	private void updateElementVersionRows(ElementVersion element) {
		Class c = element.getClass();

		List<NamedParamPair> pairs = commonOperations.buildUpdateSqlStmt(c, element);

		commonOperations.executeSqlStatements(pairs, true);
	}

	// Possibly use ConflictSummary?
	// private ConflictSummary getConflicts1(ContextIdentifier context, List<ElementVersion> crElements,
	// List<ElementVersion> baseElements) {
	//
	// ConflictSummary conflicts = new ConflictSummary(context);
	//
	// for (ElementVersion crEV : crElements) {
	// ElementVersion baseEV = getElementVersionFromList(baseElements, crEV.getElementId());
	//
	// if (!determineIfSaveable(crEV, baseEV)) {
	// LOGGER.warn("[" + crEV.getBusinessKey() + "] is not saveable");
	// conflicts.add(crEV, baseEV);
	// }
	// }
	//
	// return conflicts;
	// }

}
