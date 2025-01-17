package ca.cihi.cims.dal.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface ColumnTranslator<T> {
	/**
	 * Method used by sub classes of {@link BaseRowMapper} to read values from ResultSet
	 * 
	 * @param resultSet
	 * @param columnName
	 * @return
	 * @throws SQLException
	 */
	T read(ResultSet resultSet, String columnName) throws SQLException;

	/**
	 * Convert it to a value that JDBC knows how to write when doing so unsupervised.
	 */
	Object toWritableValue(T value);

	void write(PreparedStatement statement, int parameter, T value) throws SQLException;

}
