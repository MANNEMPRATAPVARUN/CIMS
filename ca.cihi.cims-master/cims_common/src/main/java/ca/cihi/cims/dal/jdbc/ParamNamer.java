package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A utility class that generates unique parameter names, certainly unique
 * across a single query.
 */
public class ParamNamer {
	private int paramCount = 0;

	private Map<String, Object> paramMap = new HashMap<String, Object>();

	/**
	 * Submit a new parameter value, store it, and return the name of the
	 * parameter that should be used in building the query.
	 */
	public String param(Object value) {
		String param = "p" + paramCount+++"p";
		paramMap.put(param, value);
		return ":" + param;
	}

	/**
	 * Return parameter names for a bunch of parameterized values at once.
	 */
	public List<String> params(Collection<? extends Object> values) {
		List<String> paramNames = new ArrayList<String>();

		for (Object value : values) {
			paramNames.add(param(value));
		}

		return paramNames;
	}

	public Map<String, Object> getParamMap() {
		return paramMap;
	}

}