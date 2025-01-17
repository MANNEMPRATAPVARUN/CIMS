package ca.cihi.cims.dal.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.jdbc.core.RowMapper;

import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.util.timer.Perf;

/**
 * A row mapper that works on result sets that include a tablename column,
 * allowing the mapper to figure out itself what class it should produce.
 */
@Deprecated
class AnyElementRowMapper<T extends ElementVersion> implements RowMapper<T> {

	private static final Logger LOGGER = LogManager.getLogger(AnyElementRowMapper.class);

	private ORConfig config;

	private SelectBits query;

	public AnyElementRowMapper(ORConfig config, SelectBits query) {
		this.config = config;
		this.query = query;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {
		Perf.start("AnyElementRowMapper.mapRow");
		String tableName = rs.getString("TableName");
		Class elementClass = config.forTableName(tableName);

		LOGGER.trace("Mapping property: " + tableName + "/"
				+ rs.getString("ClassName"));

		if (elementClass == null) {
			// LOGGER.error("No java class defined for rows of TABLENAME="
			// + tableName);
			return null;
		}

		ElementRowMapper row = new ElementRowMapper(elementClass,
				config.getMapping(elementClass), query);
		T mapRow = (T) row.mapRow(rs, rowNum);
		Perf.stop("AnyElementRowMapper.mapRow");
		return mapRow;
	}
}