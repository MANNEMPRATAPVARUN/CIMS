package ca.cihi.cims.dal;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;


@Service
public class ClassService {

	private final Logger LOGGER = LogManager.getLogger(getClass());

	@Autowired
	private NamedParameterJdbcTemplate jdbcNamed;

	public long getCachedClassId(String baseClassification, String className) {
		return getCacheItem(baseClassification, className, "classId", Long.class);
	}

	public String getCachedTableName(String baseClassification, String className) {
		return getCacheItem(baseClassification, className, "tableName", String.class);
	}

	public String getCachedFriendlyName(String baseClassification, String className) {
		return getCacheItem(baseClassification, className, "friendlyName", String.class);
	}

	@SuppressWarnings("unchecked")
	@Cacheable("ICD-10-CA")
	private <T> T getCacheItem(String baseClassification, String className, String cacheField, Class<T> clazz) {
		T fieldValue = null;

		CIMSClass c = lookforSingleValue(baseClassification, className);

		ExpressionParser parser = new SpelExpressionParser();
		fieldValue = (T) parser.parseExpression(cacheField).getValue(c);

		return fieldValue;
	}

	private CIMSClass lookforSingleValue(final String baseClassification, String className) {

		String sql = "select * from Class where BaseClassificationName=:bc and UPPER(classname)=:className";
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("bc", baseClassification);
		map.put("className", StringUtils.upperCase(className));

		SqlRowSet sqlrs = jdbcNamed.queryForRowSet(sql, map);

		while (sqlrs.next()) {
			CIMSClass c = new CIMSClass(sqlrs.getLong("classId"), sqlrs.getString("tableName"),
							sqlrs.getString("friendlyName"));
			return c;
		}
		return null;
	}

	private class CIMSClass {

		private long classId;
		private String tableName;
		private String friendlyName;

		CIMSClass(long classId, String tableName, String friendlyName) {
			setClassId(classId);
			setTableName(tableName);
			setFriendlyName(friendlyName);
		}

		public long getClassId() {
			return classId;
		}

		private void setClassId(long classId) {
			this.classId = classId;
		}

		public String getTableName() {
			return tableName;
		}

		private void setTableName(String tableName) {
			this.tableName = tableName;
		}

		public String getFriendlyName() {
			return friendlyName;
		}

		private void setFriendlyName(String friendlyName) {
			this.friendlyName = friendlyName;
		}
	}

}
