package ca.cihi.cims.dal.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import ca.cihi.cims.dal.ElementVersion;

/**
 * The LeafElementRowMapper is a lot like the ElementRowMapper in that it
 * translates a suitably populated JDBC ResultSet into a subclass of
 * ElementVersion. Unlike the ElementRowMapper, however, this class assumes that
 * only the tip ('leaf') table will be in the result set. For example, if
 * loading a TextPropertyVersion, it will only try to read fields from the
 * TextPropertyVersion table, not from DataPropertyVersion, PropertyVersion, or
 * ElementVersion. By and large this is sufficient, because we've denormalized
 * the database so much that everything important is in the leaf tables anyways.
 * 
 * @see AnyLeafElementRowMapper
 * @see ElementRowMapper
 * 
 * @author mprescott
 * 
 */
class LeafElementRowMapper extends BaseRowMapper {

	private static final int DEBUG_VALUES_TRUNCATE_LENGTH = 50;

	private static final Logger LOGGER = LogManager.getLogger(LeafElementRowMapper.class);

	private Map<ColAliasKey, String> columnAliases;

	LeafElementRowMapper(Class resultClass, ClassORMapping mapping, Map<ColAliasKey, String> columnAliases) {
		super(resultClass, mapping);
		this.columnAliases = columnAliases;
	}

	void setPropertiesFromResultSet(ResultSet rs, ClassORMapping mapping, final ElementVersion result)
			throws SQLException {

		// These properties are mapped manually, since they are the result of a
		// sneaky join
		String classname = rs.getString("ClassName");
		result.setClassName(classname);
		result.setBusinessKey(rs.getString("ElementUUID"));

		LOGGER.trace("Fetching value from ID col " + mapping.getIdColumn());
		result.setElementVersionId(rs.getLong(mapping.getIdColumn()));

		for (ColumnMapping col : mapping.getColumnMappings()) {
			LOGGER.trace("Fetching value from col " + col.getColumnName());

			Object dbValue = col.getTranslator().read(rs, columnAliases.get(new ColAliasKey(resultClass, col)));

			logColumnValue(col, dbValue);

			ExpressionParser parser = new SpelExpressionParser();
			parser.parseExpression(col.getPropertyName()).setValue(result, dbValue);
		}
	}

}