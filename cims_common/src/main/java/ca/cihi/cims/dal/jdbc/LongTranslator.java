package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.springframework.jdbc.core.SqlTypeValue;

public class LongTranslator implements ColumnTranslator<Long> {

	@Override
	public Long read(ResultSet resultSet, String columnName) throws SQLException {
		long longValue = resultSet.getLong(columnName);
		if (resultSet.wasNull()) {
			return null;
		}
		return longValue;
	}

	@Override
	public void write(PreparedStatement statement, int parameter, Long value) throws SQLException {
		if (value != null) {
			statement.setLong(parameter, value);
		} else {
			statement.setNull(parameter, Types.NUMERIC);
		}
	}

	@Override
	public Object toWritableValue(Long value) {
		return value;
	}

}
