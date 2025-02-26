package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTranslator implements ColumnTranslator<String> {

	@Override
	public String read(ResultSet resultSet, String columnName)
			throws SQLException {
		return resultSet.getString(columnName);
	}

	@Override
	public void write(PreparedStatement statement, int parameter, String value)
			throws SQLException {
		statement.setString(parameter, value);
	}

	@Override
	public Object toWritableValue(String value) {
		return value;
	}

}
