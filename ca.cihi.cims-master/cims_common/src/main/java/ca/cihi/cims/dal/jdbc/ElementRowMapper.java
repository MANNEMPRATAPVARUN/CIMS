package ca.cihi.cims.dal.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.jdbc.core.RowMapper;

import ca.cihi.cims.dal.ElementVersion;

/**
 * This class turns a row in a ResultSet into a subclass of ElementVersion. It
 * will also navigate up the subclass's inheritance hierarchy until it reaches
 * ElementVersion. For example, if populating a TextPropertyVersion, it will
 * also try to populate the fields of the parent DataPropertyVersion,
 * PropertyVersion, and ElementVersion parent classes.
 * 
 * In contrast, the {@link LeafElementRowMapper} does not look at parent
 * classes, and only maps the properties from the final, "leaf" table/classes.
 * 
 */
class ElementRowMapper extends BaseRowMapper implements RowMapper<ElementVersion> {

	static final Logger LOGGER = LogManager.getLogger(ElementRowMapper.class);

	private SelectBits query;

	ElementRowMapper(Class resultClass, ClassORMapping mapping, SelectBits query) {
		super(resultClass, mapping);
		this.query = query;
	}

	@Override
	public ElementVersion mapRow(ResultSet rs, int arg1) throws SQLException {
		return mapRow(rs);
	}

	void setPropertiesFromResultSet(ResultSet rs, ClassORMapping mapping, final ElementVersion result)
			throws SQLException {

		// These properties are mapped manually, since they are the result of a
		// sneaky join
		String classname = rs.getString("ClassName");
		result.setClassName(classname);
		String businessKey = rs.getString("ElementUUID");
		result.setBusinessKey(businessKey);

		ClassORMapping current = mapping;

		while (current != null) {

			for (ColumnMapping col : current.getColumnMappings()) {
				LOGGER.trace("Fetching value from col " + col.getColumnName());

				Object dbValue = col.getTranslator().read(rs, query.getAlias(col));

				logColumnValue(col, dbValue);

				ExpressionParser parser = new SpelExpressionParser();
				parser.parseExpression(col.getPropertyName()).setValue(result, dbValue);
			}
			current = current.getParent();
		}
	}
}