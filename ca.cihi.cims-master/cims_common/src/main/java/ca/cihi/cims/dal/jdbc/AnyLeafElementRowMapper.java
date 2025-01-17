package ca.cihi.cims.dal.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.dal.PropertyVersion;

/**
 * A row mapper that works on result sets that include a tablename column,
 * allowing the mapper to figure out itself what class it should produce. The
 * actual mapping is done by a {@link LeafElementRowMapper}.
 * 
 * @see LeafElementRowMapper
 */
class AnyLeafElementRowMapper<T extends ElementVersion> implements ResultSetExtractor<List<PropertyVersion>> {

	private static final Logger LOGGER = LogManager.getLogger(AnyLeafElementRowMapper.class);

	private ORConfig config;

	private Map<ColAliasKey, String> columnAliases;

	// This is something of a hack; this list is here because the query can
	// return stray rows of table names we didn't ask for.
	private Set<Class> expectedClasses;

	public AnyLeafElementRowMapper(ORConfig config, Map<ColAliasKey, String> columnAliases, Set<Class> expectedClasses) {
		this.config = config;
		this.columnAliases = columnAliases;
		this.expectedClasses = expectedClasses;
	}

	@Override
	public List<PropertyVersion> extractData(ResultSet rs) throws SQLException, DataAccessException {

		List<PropertyVersion> results = new ArrayList<PropertyVersion>();

		nextRow: while (rs.next()) {

			String tableName = rs.getString("TableName");
			Class elementClass = config.forTableName(tableName);

			if (!expectedClasses.contains(elementClass)) {
				// This is a temporary fix for the query sometimes returning
				// rows of properties we didn't ask for
				continue nextRow;
			}

			if (LOGGER.isTraceEnabled()) {
				LOGGER.trace("Mapping property: " + tableName + "/" + rs.getString("ClassName"));
			}

			if (elementClass == null) {
				// LOGGER.error("No java class defined for rows of TABLENAME="
				// + tableName);
				return null;
			}

			LeafElementRowMapper row = new LeafElementRowMapper(elementClass, config.getMapping(elementClass),
					columnAliases);
			PropertyVersion property = (PropertyVersion) row.mapRow(rs);

			results.add(property);
		}

		return results;
	}
}