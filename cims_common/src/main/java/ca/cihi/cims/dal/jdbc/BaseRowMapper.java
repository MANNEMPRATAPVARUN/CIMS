package ca.cihi.cims.dal.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.jdbc.core.RowMapper;

import ca.cihi.cims.dal.ElementVersion;
import ca.cihi.cims.util.timer.Perf;

public abstract class BaseRowMapper {

	static final int DEBUG_VALUES_TRUNCATE_LENGTH = 50;

	static final Logger LOGGER = LogManager.getLogger(BaseRowMapper.class);

	protected final Class resultClass;

	protected ClassORMapping mapping;

	public BaseRowMapper(Class resultClass, ClassORMapping mapping) {
		if (resultClass == null) {
			throw new IllegalArgumentException("Invalid result class: null");
		}
		this.resultClass = resultClass;
		this.mapping = mapping;
	}

	abstract void setPropertiesFromResultSet(ResultSet rs, ClassORMapping mapping, final ElementVersion result)
			throws SQLException;

	ElementVersion mapRow(ResultSet rs) throws SQLException {
		final ElementVersion result = newInstance(resultClass);
		Perf.start("ElementRowMapper.mapRow");
		dumpResultColumnMetadata(rs);

		setPropertiesFromResultSet(rs, mapping, result);
		Perf.stop("ElementRowMapper.mapRow");
		return result;
	}

	protected void logColumnValue(ColumnMapping col, Object dbValue) {
		if (LOGGER.isTraceEnabled()) {

			String dbValueStr = ("" + dbValue);
			dbValueStr = dbValueStr.substring(0, Math.min(dbValueStr.length(), DEBUG_VALUES_TRUNCATE_LENGTH))
					.replaceAll("\\s+", " ");
			String message = col.getColumnName() + "=" + dbValueStr;
			LOGGER.trace(message);
		}
	}

	protected void dumpResultColumnMetadata(ResultSet rs) throws SQLException {
		if (!LOGGER.isTraceEnabled()) {
			return;
		}
		ResultSetMetaData rsMetadata = rs.getMetaData();
		for (int col = 1; col <= rsMetadata.getColumnCount(); col++) {

			StringBuilder b = new StringBuilder();

			b.append(rsMetadata.getColumnName(col) + " ");
			b.append(rsMetadata.getColumnTypeName(col) + " ");
			b.append(rsMetadata.getColumnClassName(col) + " ");

			LOGGER.trace(b);
		}
	}

	protected ElementVersion newInstance(final Class resultClass) {
		try {
			return (ElementVersion) resultClass.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalStateException(e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

}