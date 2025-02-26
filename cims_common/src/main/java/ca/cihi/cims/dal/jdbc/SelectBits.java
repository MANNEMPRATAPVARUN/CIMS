package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

class SelectBits {
	private String queryName;

	private List<String> columns = new ArrayList<String>();
	private List<String> tables = new ArrayList<String>();
	private List<String> wheres = new ArrayList<String>();
	private List<String> withDefinitions = new ArrayList<String>();
	private String connectBy = null;

	private Map<ColumnMapping, String> aliases = new HashMap<ColumnMapping, String>();

	public SelectBits() {
	}

	public String toString() {
		return (queryName != null ? "/* " + queryName + " */ " : "")
				+ (withDefinitions.isEmpty() ? "" : "WITH "
						+ StringUtils.join(withDefinitions, ", ") + " ")
				+ "SELECT "
				+ StringUtils.join(columns, ", ")
				+ " FROM "
				+ StringUtils.join(tables, ", ")
				+ (wheres.isEmpty() ? "" : " WHERE "
						+ StringUtils.join(wheres, " AND "))
				+ (connectBy == null ? "" : " CONNECT BY " + connectBy + " ");
	}

	public SelectBits setQueryName(String queryName) {
		this.queryName = queryName;
		return this;
	}

	public SelectBits addWith(String relation, String query) {
		withDefinitions.add(relation + " as ( " + query + " )");
		return this;
	}

	public void addWhere(String where) {
		wheres.add(where);
	}

	public SelectBits addTable(String table) {
		tables.add(table);
		return this;
	}

	public SelectBits addColumn(String column) {
		columns.add(column);
		return this;
	}

	public void connectBy(String connectBy) {
		this.connectBy = connectBy;
	}

	public SelectBits clearColumns() {
		columns.clear();
		return this;
	}

	/**
	 * Add a column, making sure the name is unique for the query. Returns the
	 * alias.
	 */
	public String addColumn(ColumnMapping column) {

		String alias = column.getColumnName();

		int attempt = 2;
		while (aliases.values().contains(alias)) {
			alias = column.getColumnName() + attempt++;
		}

		aliases.put(column, alias);
		if (column.getColumnName().equals(alias)) {
			columns.add(column.getFullyQualifiedName());
		} else {
			columns.add(column.getFullyQualifiedName() + " AS " + alias);
		}

		return alias;

	}

	public String getAlias(ColumnMapping column) {

		String alias = aliases.get(column);

		return alias == null ? column.getColumnName() : alias;
	}
}