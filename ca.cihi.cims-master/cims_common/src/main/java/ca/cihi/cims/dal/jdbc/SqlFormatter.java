package ca.cihi.cims.dal.jdbc;

import java.util.Map;

/**
 * A really, really simple SQL formatter for logging.
 */
public class SqlFormatter {

	public String format(String sql, Map<String, Object> params) {
		String formatted = format(sql);

		for (String paramName : params.keySet()) {
			Object paramValue = params.get(paramName);

			paramValue = encodeSqlString(paramValue);
			formatted = formatted.replaceFirst(":" + paramName,	toSqlString(paramValue));
		}

		return formatted;
	}

	private String toSqlString(Object paramValue) {
		if (paramValue instanceof String) {
			return "'" + paramValue + "'";
		} else{
			return "" + paramValue;
		}
	}
	
	private String encodeSqlString(Object paramValue){
	       if (paramValue instanceof String) {	           
	           // Remove the special meaning of dollar sign and backslash
	           return java.util.regex.Matcher.quoteReplacement((String)paramValue);
	        } else{
	            return "" + paramValue;
	        }
	}

	public String format(String sql) {

		String replaceAll = sql.replaceAll(" +", " ").replaceAll(
				"\\s([A-Z ]{5,})", "\n$0");

		return tabIndent(replaceAll);
	}

	private String tabIndent(String foo) {
		StringBuilder builder = new StringBuilder();

		int currentDepth = 0;

		for (int index = 0; index < foo.length(); index++) {

			char c = foo.charAt(index);
			if (c == '(') {
				currentDepth++;
			} else if (c == ')') {
				currentDepth--;
			}

			builder.append(c);
			if (c == '\n') {
				for (int i = 0; i < currentDepth; i++) {
					builder.append('\t');
				}
			}

		}

		return builder.toString();
	}
}
