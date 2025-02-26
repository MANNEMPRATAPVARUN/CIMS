package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ByteArrayTranslator implements ColumnTranslator<byte[]> {

	@Override
	public byte[] read(ResultSet resultSet, String columnName)
			throws SQLException {
		return resultSet.getBytes(columnName);
	}

	@Override
	public void write(PreparedStatement statement, int parameter, byte[] value)
			throws SQLException {
		statement.setBytes(parameter, value);
	}

	@Override
	public Object toWritableValue(byte[] value) {
		return value;
	}

}
