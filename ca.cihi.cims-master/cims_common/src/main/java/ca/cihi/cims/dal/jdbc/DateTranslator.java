package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class DateTranslator implements ColumnTranslator<Date> {

	@Override
	public Date read(ResultSet resultSet, String columnName) throws SQLException {
		return resultSet.getDate(columnName);
	}

	@Override
	public void write(PreparedStatement statement, int parameter, Date value) throws SQLException {
		statement.setDate(parameter, value);		
	}

	@Override
	public Object toWritableValue(Date value) {
		return value;
	}
}
