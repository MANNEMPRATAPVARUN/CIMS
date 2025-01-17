package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class IntegerTranslator implements ColumnTranslator<Integer> {
	@Override
	public Integer read(ResultSet resultSet, String columnName) throws SQLException {
		int intValue = resultSet.getInt(columnName);
		if (resultSet.wasNull()) {
			return null;
		}
		return intValue;
	}

	@Override
	public void write(PreparedStatement statement, int parameter, Integer value) throws SQLException {
		if (value != null) {
			statement.setInt(parameter, value);
		} else {
			statement.setNull(parameter, Types.NUMERIC);
		}

	}

	@Override
	public Object toWritableValue(Integer value) {
		return value;
	}

}
