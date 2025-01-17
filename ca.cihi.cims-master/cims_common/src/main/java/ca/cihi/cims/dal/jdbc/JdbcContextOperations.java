package ca.cihi.cims.dal.jdbc;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.ConceptStatus;
import ca.cihi.cims.ContextStatus;
import ca.cihi.cims.dal.BusinessKeyGenerator;
import ca.cihi.cims.dal.ClassService;
import ca.cihi.cims.dal.CommonElementOperations;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.dal.ContextOperations;
import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.NamedParamPair;
import ca.cihi.cims.dal.StructureVersion;

@Component
public class JdbcContextOperations implements ContextOperations {

	private interface WhereAdder {
		public void addWheres(SelectBits select, ParamNamer namer);
	}

	private static final Logger LOGGER = LogManager.getLogger(JdbcContextOperations.class);

	@Autowired
	private ORConfig orConfig;
	@Autowired
	private JdbcTemplate jdbc;
	@Autowired
	private Sequences sequences;
	@Autowired
	private ClassService classService;
	@Autowired
	private FlatElementQueryBuilder builder;
	@Autowired
	private NamedParameterJdbcTemplate jdbcNamed;
	@Autowired
	private CommonElementOperations commonOperations;

	// ------------------------------------------------------------------------------------------------

	private void associateNewBaseContextToBaseContext(long baseContextId, long newContextId) {
		// This process is not quick....
		String sql = "INSERT INTO STRUCTUREELEMENTVERSION (ELEMENTVERSIONID, STRUCTUREID, ELEMENTID, CONTEXTSTATUSDATE) "
				+ "SELECT ELEMENTVERSIONID, :newContextId, ELEMENTID, :contextStatusDate "
				+ "FROM STRUCTUREELEMENTVERSION WHERE STRUCTUREID = :baseContextId";
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("contextStatusDate", new Date());
		param.put("newContextId", newContextId);
		param.put("baseContextId", baseContextId);
		commonOperations.executeSqlStatement(sql, param);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public ContextIdentifier createChangeContext(String baseClassification, String versionCode, boolean isVersionYear,
			Long baseStructureId, Long requestId) {
		return createContext(baseClassification, versionCode, baseStructureId, isVersionYear, requestId);
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public ContextIdentifier createContext(ContextIdentifier baseCI, String versionCode, boolean isVersionYear) {
		ContextIdentifier newContext = createContext(baseCI.getBaseClassification(), versionCode, null, isVersionYear,
				null);
		// Associate new context with base context - done via copying from StructureElementVersion
		associateNewBaseContextToBaseContext(baseCI.getContextId(), newContext.getContextId());
		return newContext;
	}

	private ContextIdentifier createContext(String baseClassification, String versionCode, Long baseStructureId,
			boolean isVersionYear, Long requestId) {
		String businessKey = BusinessKeyGenerator.generateContextBusinesskey(baseClassification);
		List<String> versionCodes = findVersionCodes(baseClassification);
		if (versionCodes.contains(versionCode)) {
			throw new IllegalArgumentException("[" + versionCode + "] already exists for [" + baseClassification + "]");
		}
		long contextClassId = classService.getCachedClassId(baseClassification, baseClassification);
		long elementId = commonOperations.createOrRetrieveElement(businessKey, contextClassId);
		List<NamedParamPair> pairs = new ArrayList<NamedParamPair>();
		// Create new Element Version ID
		long elementVersionId = sequences.nextValue(Sequences.ELEMENTVERSION_ID_SEQUENCE);
		HashMap<String, Object> baseMap = new HashMap<String, Object>();
		baseMap.put("elementId", elementId);
		baseMap.put("elementVersionId", elementVersionId);
		baseMap.put("structureId", elementVersionId);
		baseMap.put("classId", contextClassId);
		baseMap.put("versionCode", versionCode);
		baseMap.put("status", ConceptStatus.ACTIVE.name());
		baseMap.put("versionTimeStamp", new Date());
		baseMap.put("baseStructureId", baseStructureId);
		baseMap.put("contextStatus", ContextStatus.OPEN.name());
		baseMap.put("contextStatusDate", new Date());
		baseMap.put("isVersionYear", isVersionYear == true ? "Y" : "N");
		baseMap.put("businessKey", businessKey);
		baseMap.put("originatingContextId", null);
		baseMap.put("changedFromVersionId", null);
		baseMap.put("changeRequestId", requestId);
		baseMap.put("freezingStatusId", null);
		ClassORMapping mapping = orConfig.getMapping(StructureVersion.class);
		ClassORMapping current = mapping;
		while (current != null) {
			InsertBits insert = new InsertBits(current.getTable());
			for (ColumnMapping col : current.getColumnMappings()) {
				insert.addColumn(col.getColumnName(), col.getPropertyName());
			}
			pairs.add(new NamedParamPair(insert.toString(), baseMap));
			current = current.getParent();
		}
		commonOperations.executeSqlStatements(pairs, true);
		ContextIdentifier newContext = findContextById(baseClassification, elementVersionId);
		return newContext;
	}

	@Override
	public List<String> findBaseClassifications() {
		return jdbc.queryForList("select distinct baseclassificationname from class", String.class);
	}

	@Override
	public List<String> findBaseClassificationVersionCodes(String baseClassification) {
		return jdbc.queryForList("select distinct versioncode from elementversion ev, class c, structureversion s "
				+ "where ev.classid=c.classid and c.baseclassificationname=? and ev.versioncode is not null "
				+ "and ev.elementversionid = s.structureid and s.basestructureid is null order by versioncode",
				new Object[] { baseClassification }, String.class);
	}

	@Override
	public List<ContextIdentifier> findBaseClassificationVersionYearVersionCodes(final String baseClassification) {
		return findContextIdentifiers(baseClassification, new WhereAdder() {
			@Override
			public void addWheres(SelectBits select, ParamNamer namer) {
				select.addWhere("Class.BaseClassificationName=" + namer.param(baseClassification));
				select.addWhere("StructureVersion.basestructureid is null");
				select.addWhere("StructureVersion.isversionyear = 'Y'");
			}
		});
	}

	@Override
	public List<ContextIdentifier> findBaseContextIdentifiers(final String baseClassification) {
		return findContextIdentifiers(baseClassification, new WhereAdder() {
			@Override
			public void addWheres(SelectBits select, ParamNamer namer) {
				select.addWhere("Class.BaseClassificationName=" + namer.param(baseClassification));
				select.addWhere("StructureVersion.basestructureid is null");
			}
		});
	}

	private ContextIdentifier findContext(String baseClassification, WhereAdder adder) {
		ParamNamer namer = new ParamNamer();
		SelectBits select = builder.buildElementQuery(null, namer);
		select.addWhere("Class.ClassName=" + namer.param(baseClassification));
		adder.addWheres(select, namer);
		String query = select.toString();
		LOGGER.debug(new SqlFormatter().format(query, namer.getParamMap()));
		StructureVersion structure = (StructureVersion) jdbcNamed.queryForObject(query, namer.getParamMap(),
				new ElementRowMapper(StructureVersion.class, orConfig.getMapping(StructureVersion.class), select));
		return new ContextIdentifier(structure.getElementVersionId(), structure.getVersionCode(), baseClassification,
				structure.getBaseStructureId(), structure.getContextStatus(), structure.getContextStatusDate(),
				structure.isVersionYear(), structure.getChangeRequestId(), structure.getFreezingStatusId());
	}

	@Override
	public ContextIdentifier findContextById(final String baseClassification, final Long structureId) {
		return findContext(baseClassification, new WhereAdder() {
			@Override
			public void addWheres(SelectBits select, ParamNamer namer) {
				select.addWhere("Class.BaseClassificationName=" + namer.param(baseClassification));
				select.addWhere("ElementVersion.ElementVersionId=" + namer.param(structureId));
			}
		});
	}

	@Override
	public ContextIdentifier findContextForVersion(final String baseClassification, final String versionCode) {
		return findContext(baseClassification, new WhereAdder() {
			@Override
			public void addWheres(SelectBits select, ParamNamer namer) {
				select.addWhere("Class.BaseClassificationName=" + namer.param(baseClassification));
				select.addWhere("ElementVersion.VersionCode=" + namer.param(versionCode));
			}
		});
	}

	private List<ContextIdentifier> findContextIdentifiers(String baseClassification, WhereAdder adder) {
		List<ContextIdentifier> contextIdentifiers = new ArrayList<ContextIdentifier>();
		ParamNamer namer = new ParamNamer();
		SelectBits select = builder.buildElementQuery(null, namer);
		select.addWhere("Class.ClassName=" + namer.param(baseClassification));
		adder.addWheres(select, namer);
		String query = select.toString();
		LOGGER.debug(new SqlFormatter().format(query, namer.getParamMap()));
		List<ElementVersion> elementVersions = jdbcNamed.query(query, namer.getParamMap(), new ElementRowMapper(
				StructureVersion.class, orConfig.getMapping(StructureVersion.class), select));
		for (ElementVersion elementVersion : elementVersions) {
			StructureVersion structure = (StructureVersion) elementVersion;
			ContextIdentifier contextIdentifier = new ContextIdentifier(structure.getElementVersionId(),
					structure.getVersionCode(), baseClassification, structure.getBaseStructureId(),
					structure.getContextStatus(), structure.getContextStatusDate(), structure.isVersionYear(),
					structure.getChangeRequestId(), structure.getFreezingStatusId());
			contextIdentifiers.add(contextIdentifier);
		}
		return contextIdentifiers;
	}

	@Override
	public List<String> findLanguageCodes() {
		return jdbc.queryForList("select languagecode from language", String.class);
	}

	@Override
	public List<ContextIdentifier> findOpenBaseContextIdentifiers(final String baseClassification) {
		return findContextIdentifiers(baseClassification, new WhereAdder() {
			@Override
			public void addWheres(SelectBits select, ParamNamer namer) {
				select.addWhere("Class.BaseClassificationName=" + namer.param(baseClassification));
				select.addWhere("StructureVersion.basestructureid is null");
				select.addWhere("StructureVersion.contextStatus='OPEN'");
			}
		});
	}

	@Override
	public List<String> findVersionCodes(String baseClassification) {
		return jdbc.queryForList("select distinct versioncode from elementversion ev, class c "
				+ "where ev.classid=c.classid and c.baseclassificationname=? and versioncode is not null",
				new Object[] { baseClassification }, String.class);
	}

	@Override
	public boolean hasConceptBeenPublished(long elementId) {

		boolean beenPublished = false;

		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbc).withCatalogName("CIMS_API").withProcedureName(
				"hasConceptBeenPublished");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("elemId", elementId);

		SqlParameterSource in = new MapSqlParameterSource(paramMap);
		Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);

		BigDecimal tmpContextCount = (BigDecimal) simpleJdbcCallResult.get("CONTEXTCOUNT");
		int contextCount = tmpContextCount.intValue();

		if (contextCount == 0) {
			beenPublished = false;
		} else {
			beenPublished = true;
			LOGGER.warn("Concept [" + elementId + "] has been published before");
		}

		return beenPublished;

	}

	@Override
	/**
	 * Changes a properties 'changedFromVersionId' to the base context version Id.  
	 * By doing so, you eliminate the conflict you would have received had you realized the change context.
	 * 
	 * Current response codes.  Add or modify as required
	 * 0 Success
	 * 1 Context is not a change context
	 * 2 Context is not open
	 * 3 Property Version does not exist within the change context
	 * 4 Property Version does not exist within the base context
	 * 9 Unknown problem.  Requires debug
	 */
	public boolean reBaseChangedFromVersionId(long elementId, long contextId, long classId, String languageCode) {

		boolean isSuccess = false;
		HashMap<Integer, String> responseCodeMap = new HashMap<Integer, String>();
		responseCodeMap.put(0, "Success");
		responseCodeMap.put(1, "Context is not a change context");
		responseCodeMap.put(2, "Context is not open");
		responseCodeMap.put(3, "Property Version does not exist within the change context");
		responseCodeMap.put(4, "Property Version does not exist within the base context");
		responseCodeMap.put(9, "Unknown problem.  Requires debug");

		SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(jdbc).withCatalogName("CIMS_API").withProcedureName(
				"reBaseChangedFromVersionId");
		HashMap<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("elemId", elementId);
		paramMap.put("contextId", contextId);
		paramMap.put("cid", classId);
		paramMap.put("lang", languageCode);
		// paramMap.put("response_code", response_code);

		SqlParameterSource in = new MapSqlParameterSource(paramMap);
		Map<String, Object> simpleJdbcCallResult = simpleJdbcCall.execute(in);

		BigDecimal tmpResponse_Code = (BigDecimal) simpleJdbcCallResult.get("RESPONSE_CODE");
		int responseCode = tmpResponse_Code.intValue();

		if (responseCode == 0) {
			isSuccess = true;
		} else {
			if ((responseCode == 4) || (responseCode == 3)) {
				// Though not truly successful, we will allow this scenario
				isSuccess = true;
			}
			LOGGER.warn("Response code [" + responseCode + "] from reBaseChangedFromVersionId.  Response: "
					+ responseCodeMap.get(responseCode));
		}

		return isSuccess;
	}

	@Override
	@Transactional(rollbackFor = { Exception.class, RuntimeException.class })
	public void updateChangeContextStatus(long contextId, String status) {

		boolean isValidStatus = false;

		for (ContextStatus contextStatus : ContextStatus.values()) {
			if (contextStatus.name().equals(status)) {
				isValidStatus = true;
			}
		}

		if (isValidStatus) {
			String update = null;
			HashMap<String, Object> updateParameters = new HashMap<String, Object>();

			update = "UPDATE STRUCTUREVERSION SET CONTEXTSTATUS = :contextStatus, "
					+ "CONTEXTSTATUSDATE = :contextStatusDate WHERE STRUCTUREID = :structureId";
			updateParameters.put("contextStatus", status);
			updateParameters.put("contextStatusDate", new Date());
			updateParameters.put("structureId", contextId);
			commonOperations.executeSqlStatement(update, updateParameters);

			LOGGER.info("Status [" + status + "] updated to context id [" + contextId + "]");
		} else {
			LOGGER.error("Status [" + status + "] is not a valid context status for context id [" + contextId
					+ "].  Cannot update change context status");
		}

	}
}
