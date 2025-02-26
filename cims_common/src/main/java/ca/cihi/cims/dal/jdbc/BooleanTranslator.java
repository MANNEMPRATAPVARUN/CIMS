package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooleanTranslator implements ColumnTranslator<Boolean> {

	@Override
	public Boolean read(ResultSet resultSet, String columnName)
			throws SQLException {
		return fromYN(resultSet.getString(columnName));
	}

	@Override
	public void write(PreparedStatement statement, int parameter, Boolean value)
			throws SQLException {
		statement.setString(parameter, (String) toWritableValue(value));
	}

	@Override
	public Object toWritableValue(Boolean value) {
		if (value == null)
			return null;
		return (value ? "Y" : "N");
	}

	private Boolean fromYN(String value) {
		if (value == null)
			return null;
		else if ("Y".equals(value))
			return true;
		else if ("N".equals(value))
			return false;

		throw new IllegalStateException(
				"Unexpected value found in a boolean Y/N column.");
	}
}
