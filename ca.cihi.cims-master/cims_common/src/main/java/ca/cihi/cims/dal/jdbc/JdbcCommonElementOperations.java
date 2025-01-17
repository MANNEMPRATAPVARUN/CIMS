package ca.cihi.cims.dal.jdbc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.ContextStatus;
import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NamedParamPair;
import ca.cihi.cims.dal.annotations.RequiredForUpdate;


@Component
public class JdbcCommonElementOperations implements CommonElementOperations {

	static final Logger LOGGER = LogManager.getLogger(JdbcCommonElementOperations.class);

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamed;

	@Autowired
	private ORConfig mappingConfig;

	@Autowired
	private Sequences sequences;

	/**
	 * The implementation of {@link CommonElementOperations#buildInsertSqlStmt(Class, ElementVersion, HashMap)} method.
	 * Based on the class provided, this method loop through classORMapping hierarchy to build a list of insert
	 * statements. For example, if the class is TextPropertyVersion, there generated statements will include
	 * TextPropertyVersion, DataPropertyVersion, PropertyVersion, ElementVersion. And there is one prevent delete
	 * statements added at the end to make sure the same element was not in the context before?
	 */
	@Override
	public List<NamedParamPair> buildInsertSqlStmt(Class<?> c, ElementVersion element,
			HashMap<String, Object> defaultMap) {

		ClassORMapping current = mappingConfig.getMapping(c);
		List<NamedParamPair> pairs = new ArrayList<NamedParamPair>();

		while (current != null) {
			InsertBits insert = new InsertBits(current.getTable());
			HashMap<String, Object> elementMap = new HashMap<String, Object>();

			for (ColumnMapping col : current.getColumnMappings()) {
				insert.addColumn(col.getColumnName(), col.getPropertyName());

				ExpressionParser parser = new SpelExpressionParser();
				Object valFromElement;

				try {
					valFromElement = parser.parseExpression(col.getPropertyName()).getValue(element);
				} catch (Exception e) {
					// This actually isn't unexpected. If the ElementVersion
					// type is TextPropertyVersion, but the class passed in is StructureElementVersion, an exception
					// would be thrown while looking for structureId.
					LOGGER.debug("[" + col.getPropertyName() + "] not found in " + element.getClass().getSimpleName());
					valFromElement = null;
				}

				if (valFromElement != null) {
					@SuppressWarnings("unchecked")
					Object writableValue = col.getTranslator().toWritableValue(valFromElement);

					elementMap.put(col.getPropertyName(), writableValue);
					LOGGER.trace("[" + col.getPropertyName() + "] value [" + writableValue + "]");
				} else {
					// Value is not found within the element. See if its
					// available in the default map
					Object o = defaultMap.get(col.getPropertyName());
					elementMap.put(col.getPropertyName(), o);

					if (o == null) {
						LOGGER.debug("[" + col.getPropertyName() + "] value is null");
					} else {
						LOGGER.debug("[" + col.getPropertyName() + "] value [" + o + "]");
					}
				}
			}

			pairs.add(new NamedParamPair(insert.toString(), elementMap));
			current = current.getParent();
		}

		long structureId = (Long) defaultMap.get("structureId");
		long elementId = element.getElementId();
		NamedParamPair deletePair = deleteElementFromStructure(elementId, structureId);
		pairs.add(deletePair);

		return pairs;
	}

	@Override
	public NamedParamPair buildRemoveConceptSqlStmt(ElementVersion elementVersion) {

		if (elementVersion instanceof ConceptVersion) {
			String sql = "UPDATE Element SET elementUUId=:businessKey WHERE elementId=:elementId";

			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("businessKey", elementVersion.getBusinessKey());
			param.put("elementId", elementVersion.getElementId());

			return new NamedParamPair(sql, param);
		} else {
			throw new IllegalArgumentException("Element being removed is not a concept");
		}
	}

	/**
	 * The implementation of {@link CommonElementOperations#buildUpdateSqlStmt(Class, ElementVersion)} method which
	 * build an update statement with "Update table set ... where idColumn = ?" style.
	 */
	@Override
	public List<NamedParamPair> buildUpdateSqlStmt(Class<?> c, ElementVersion element) {

		ClassORMapping current = mappingConfig.getMapping(c);
		List<NamedParamPair> pairs = new ArrayList<NamedParamPair>();
		String query = null;

		HashMap<String, Object> updateParameters = new HashMap<String, Object>();
		updateParameters.put(current.getIdColumn(), element.getElementVersionId());

		while (current != null) {
			UpdateBits update = new UpdateBits(current.getTable());
			LOGGER.trace("****** " + current.getJavaClass());
			// Annotated fields indicate which needs are required for a update
			List<String> fieldsICareAbout = getRequiredForUpdateFields(current.getJavaClass());

			for (ColumnMapping col : current.getColumnMappings()) {

				if (col.getColumnName().equals(current.getIdColumn())) {
					update.addWhere(current.getIdColumn(), ":" + current.getIdColumn());

					ExpressionParser parser = new SpelExpressionParser();
					Object valFromElement = parser.parseExpression(col.getPropertyName()).getValue(element);

					@SuppressWarnings("unchecked")
					Object writableValue = col.getTranslator().toWritableValue(valFromElement);
					updateParameters.put(col.getColumnName(), writableValue);
					LOGGER.trace("[" + col.getPropertyName() + "] value [" + writableValue + "]");
				} else if (fieldsICareAbout.contains(col.getPropertyName())) {
					fieldsICareAbout.remove(col.getPropertyName());
					update.addColumn(col.getColumnName(), col.getPropertyName());

					ExpressionParser parser = new SpelExpressionParser();
					Object valFromElement = parser.parseExpression(col.getPropertyName()).getValue(element);

					@SuppressWarnings("unchecked")
					Object writableValue = col.getTranslator().toWritableValue(valFromElement);
					updateParameters.put(col.getPropertyName(), writableValue);
					LOGGER.trace("[" + col.getPropertyName() + "] value [" + writableValue + "]");
				} else {
					LOGGER.trace("[" + col.getPropertyName() + "] not necessary for update");
				}
			}

			query = update.toString();

			if (fieldsICareAbout.size() > 0) {
				throw new IllegalArgumentException("Unable to complete update.  These fields not set: "
						+ fieldsICareAbout.toString() + " for property type " + element.getClass().getName());
			}

			pairs.add(new NamedParamPair(query, updateParameters));

			current = current.getParent();
		}

		return pairs;
	}

	private Long checkForBusinessKey(String businessKey) {

		Long elementId;
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("businessKey", businessKey);

		String sql = "SELECT elementId from element where ELEMENTUUID = :businessKey";

		try {
			elementId = jdbcNamed.queryForObject(sql, params, Long.class);
		} catch (EmptyResultDataAccessException e) {
			elementId = null;
		}

		return elementId;
	}

	/**
	 * 
	 * Isolation.READ_COMMITTED A constant indicating that dirty reads are prevented; non-repeatable reads and phantom
	 * reads can occur. This level only prohibits a transaction from reading a row with uncommitted changes in it.
	 * 
	 * Propagation.REQUIRES_NEW Create a new transaction, suspend the current transaction if one exists. Analogous to
	 * EJB transaction attribute of the same name
	 * 
	 * In particular, make sure that you don't call another @Transactional method of the same class - due to limitations
	 * of Spring proxy-based AOP model transactional aspect is applied only to calls that come from the outside of the
	 * class.
	 * 
	 * TODO: Add retry logic
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.READ_COMMITTED)
	public long createOrRetrieveElement(String businessKey, long classId) {

		Long busKeyElementId = checkForBusinessKey(businessKey);

		if (busKeyElementId != null) {
			LOGGER.debug("Business Key [" + businessKey + "] found.  Returning Element Id [" + busKeyElementId + "]");

			return busKeyElementId;
		} else {
			long elementId = sequences.nextValue(Sequences.ELEMENT_ID_SEQUENCE);

			String sql = "INSERT INTO Element (elementId, classId, elementUUId) "
					+ "VALUES ( :elementId, :classId, :businessKey )";
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("elementId", elementId);
			param.put("classId", classId);
			param.put("businessKey", businessKey);

			executeSqlStatement(sql, param);

			LOGGER.debug("Business Key [" + businessKey + "] not found.  Creating Element Id [" + busKeyElementId + "]");

			return elementId;
		}
	}

	@Override
	public NamedParamPair deleteElementFromStructure(long elementId, long structureId) {

		// Build delete statement
		String delete = "DELETE STRUCTUREELEMENTVERSION WHERE STRUCTUREID = :structureId "
				+ "AND ELEMENTID = :elementId";

		HashMap<String, Object> deleteParameters = new HashMap<String, Object>();
		deleteParameters.put("structureId", structureId);
		deleteParameters.put("elementId", elementId);

		return new NamedParamPair(delete, deleteParameters);
	}

	/**
	 * Check that there are no changes to the removed concept in ALL OTHER open change contexts
	 * 
	 * @param contextId
	 * @param concept
	 * @return
	 */
	private boolean doesConceptHasNoChangesElsewhere(ContextIdentifier contextId, ConceptVersion concept) {

		// check if the concept itself is being changed elsewhere
		String query = "select count(*) from structureelementversion sev join structureversion s on "
				+ "sev.structureid = s.structureid and s.contextstatus = :openStatus "
				+ "and s.basestructureid is not null and s.structureid != :structureId "
				+ "where sev.elementid = :eid ";

		// check if any properties of the concept are being changed elsewhere
		String query2 = "select count(*) from structureelementversion sev join structureversion s on "
				+ "sev.structureid = s.structureid and s.contextstatus = :openStatus "
				+ "and s.basestructureid is not null and s.structureid != :structureId "
				+ "where sev.elementid in (select distinct elementid from propertyversion where domainelementid = :eid) ";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eid", concept.getElementId());
		params.put("evid", concept.getElementVersionId());
		params.put("openStatus", ContextStatus.OPEN.name());
		params.put("structureId", contextId.getContextId());

		LOGGER.debug(new SqlFormatter().format(query, params));

		boolean noChangesElsewhere = jdbcNamed.queryForObject(query, params, Integer.class)
				+ jdbcNamed.queryForObject(query2, params, Integer.class) == 0;
		return noChangesElsewhere;
	}

	/**
	 * Check that links to the removed concept in ALL OPEN contexts where the Concept is the Range
	 * 
	 * @param concept
	 * @return
	 */
	@Override
	public boolean doesConceptHasRangeLinks(ConceptVersion concept) {

		String query = "select count(*) from conceptpropertyversion cpv join structureelementversion sev on "
				+ "cpv.conceptpropertyid = sev.elementversionid join structureversion s on "
				+ "sev.structureid = s.structureid and s.contextstatus = :openStatus "
				+ "where cpv.rangeelementid = :eid";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eid", concept.getElementId());
		params.put("openStatus", ContextStatus.OPEN.name());

		LOGGER.debug(new SqlFormatter().format(query, params));

		boolean hasRangeLinks = jdbcNamed.queryForObject(query, params, Integer.class) > 0;
		return hasRangeLinks;
	}
	
	/**
	 * Check whether links to the removed concept exist in ALL OPEN contexts (where the Concept is the Range) 
	 * and the linked concepts are not removed in the specified context
	 * 
	 * @param concept
	 * @param contextId
	 * @return
	 */
	private boolean doesConceptHaveRangeLinksElsewhere(ContextIdentifier contextId, ConceptVersion concept) {

		String query = "select count(*) from conceptpropertyversion cpv join structureelementversion sev on "
				+ "cpv.conceptpropertyid = sev.elementversionid join structureversion s on "
				+ "sev.structureid = s.structureid and s.contextstatus = :openStatus "
				+ "where cpv.rangeelementid = :eid"
				+ " and not exists (select * from conceptversion cv, structureelementversion sev1 where sev1.structureid = :contextid" 
				+ " and sev1.elementversionid=cv.conceptid and cv.elementid = cpv.domainelementid and cv.status = :removedStatus)";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eid", concept.getElementId());
		params.put("openStatus", ContextStatus.OPEN.name());
		params.put("removedStatus", ConceptStatus.REMOVED.name());
		params.put("contextid", contextId.getContextId());

		LOGGER.debug(new SqlFormatter().format(query, params));

		boolean hasRangeLinks = jdbcNamed.queryForObject(query, params, Integer.class) > 0;
		return hasRangeLinks;
	}
	

	@Override
	public void executeSqlStatement(NamedParamPair pair) {
		executeSqlStatement(pair.getSql(), pair.getParamMap());
	}

	@Override
	public int executeSqlStatement(String query, HashMap<String, Object> queryParam) {

		LOGGER.debug(new SqlFormatter().format(query, queryParam));
		int numRowsUpdated = jdbcNamed.update(query, queryParam);

		if (numRowsUpdated > 0) {
			LOGGER.debug(numRowsUpdated + " row(s) updated");
		} else {
			// Since we allow deletions on base years, we would hit this line for every transformed concept and cause
			// a error to be printed out. Do not log for DELETE statements
			// Only evaluate if there is an error
			if (!query.startsWith("DELETE")) {
				LOGGER.error(numRowsUpdated + " row(s) updated");
			}
		}

		return numRowsUpdated;
	}

	@Override
	public void executeSqlStatements(List<NamedParamPair> pairs, boolean reverse) {

		if (reverse) {
			// Reverse your lists so you have proper sequential SQL statements
			Collections.reverse(pairs);
		}

		for (NamedParamPair npp : pairs) {
			executeSqlStatement(npp.getSql(), npp.getParamMap());
		}
	}

	private Iterable<Field> getFieldsUpTo(Class<?> startClass) {

		List<Field> currentClassFields = new ArrayList<>(Arrays.asList(startClass.getDeclaredFields()));
		Class<?> parentClass = startClass.getSuperclass();

		if (parentClass != null && !(parentClass.equals(java.lang.Object.class))) {
			List<Field> parentClassFields = (List<Field>) getFieldsUpTo(parentClass);
			currentClassFields.addAll(parentClassFields);
		}

		return currentClassFields;
	}

	/**
	 * Checks the class and sees if it has the {@link RequiredForUpdate} annotation If it does, it returns the field
	 * name in a string list.
	 * 
	 * Remove at some point, and add to the OR Mapping instead
	 * 
	 * @param c
	 * @return
	 * @see RequiredForUpdate
	 */
	@Override
	public List<String> getRequiredForUpdateFields(Class<?> c) {

		List<String> fieldsICareAbout = new ArrayList<String>();

		Iterable<Field> i = getFieldsUpTo(c);
		Iterator<Field> iFields = i.iterator();

		while (iFields.hasNext()) {
			Field field = iFields.next();
			if (field.isAnnotationPresent(RequiredForUpdate.class)) {
				fieldsICareAbout.add(field.getName());
			}
		}

		return fieldsICareAbout;
	}

	/**
	 * Checks to see if the concept you are trying to remove was created in that same context. If so, we do not allow
	 * this, and the correct way to go about this is to discard the change context.
	 * 
	 * @param contextId
	 * @param concept
	 * @return
	 */
	private boolean isConceptCreatedInCurrentContext(ContextIdentifier contextId, ConceptVersion concept) {
		LOGGER.info(contextId.getContextId());
		LOGGER.info(concept.getOriginatingContextId());

		return (Objects.equals(contextId.getContextId(), concept.getOriginatingContextId()));
	}

	/**
	 * Check if the removed concept was created in the current year – by checking if any version of the concept is
	 * linked to a previous base context version.
	 * 
	 * We use the structure ID. It is a sequence in the database, and thus if any structure ID's that link to this
	 * concept is lower than the provided structure ID then we know it was not created in that year
	 * 
	 * @param contextId
	 * @param concept
	 * @return
	 */
	private boolean isConceptCreatedInCurrentYear(ContextIdentifier contextId, ConceptVersion concept) {

		// String query = "select count(*) from structureelementversion where elementid = :eid "
		// + "and elementversionid = :evid and structureID < :structureId";

		// check if there is any previous base context that is linked to the concept
		String query = "select count(*) from structureelementversion sev, structureversion sv where "
				+ "sev.structureid = sv.structureid " + "and sv.basestructureid is null " + "and sev.elementid = :eid "
				+ "and sv.structureID < :structureId";

		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("eid", concept.getElementId());
		params.put("evid", concept.getElementVersionId());
		params.put("structureId", contextId.getBaseStructureId());

		LOGGER.debug(new SqlFormatter().format(query, params));

		boolean createdThisYear = jdbcNamed.queryForObject(query, params, Integer.class) == 0;
		return createdThisYear;
	}

	/**
	 * Implementation of {@link CommonElementOperations#isConceptEligibleForRemoval(ContextIdentifier, ConceptVersion)}.
	 * Goes through several checks to ensure that the Concept is eligible to be removed
	 * 
	 * @param contextId
	 * @param conceptToRemove
	 * @return
	 */
	@Override
	public boolean isConceptEligibleForRemoval(ContextIdentifier contextId, ConceptVersion conceptToRemove) {

		boolean elligible = true;

		if (!(isConceptCreatedInCurrentYear(contextId, conceptToRemove))) {
			elligible = false;
		}

		if (!(doesConceptHasNoChangesElsewhere(contextId, conceptToRemove))) {
			elligible = false;
		}

		if (doesConceptHaveRangeLinksElsewhere(contextId,conceptToRemove)) {
			elligible = false;
		}

		return elligible;
	}

}
