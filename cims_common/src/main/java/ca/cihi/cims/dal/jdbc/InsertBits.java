package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class InsertBits {

	private String tablename;
	private List<String> columns = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();

	public InsertBits(String tablename) {
		this.tablename = tablename;
	}

	public void addColumn(String column) {
		columns.add(column);
		values.add("?");
	}

	public void addColumn(String column, String placeholder) {
		columns.add(column);
		values.add(":" + placeholder);
	}

	public String toString() {
		return "INSERT INTO " + tablename + " ("
				+ StringUtils.join(columns, ", ") + ") VALUES ( "
				+ StringUtils.join(values, ", ") + " )";
	}
}
