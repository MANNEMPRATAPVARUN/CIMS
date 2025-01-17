package ca.cihi.cims.dal;

import java.util.HashMap;

public class NamedParamPair {

	private String sql;
	private HashMap<String, Object> paramMap = new HashMap<String, Object>();

	public NamedParamPair(String sql, HashMap<String, Object> paramMap) {
		this.sql = sql;
		this.paramMap = paramMap;
	}

	public String getSql() {
		return sql;
	}

	public HashMap<String, Object> getParamMap() {
		return paramMap;
	}
}
