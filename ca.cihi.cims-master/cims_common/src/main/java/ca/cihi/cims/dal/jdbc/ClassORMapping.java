package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * This class holds Class to Database structure mapping, which will be used to translate from the ElementVersion and
 * related subclasses to the database structure, and back again.
 * 
 */
public class ClassORMapping {
	private final ClassORMapping parent;
	private final Class javaClass;
	private final String table;
	private final String idColumn;
	private final Collection<ClassORMapping> children = new ArrayList<ClassORMapping>();

	private final Collection<ColumnMapping> columnMappings = new ArrayList<ColumnMapping>();

	private final Collection<ColumnMapping> mappingsForChildren = new ArrayList<ColumnMapping>();

	/**
	 * Constructor to define Class and Table mapping with idColumn
	 * 
	 * @param parent
	 * @param javaClass
	 * @param table
	 * @param idColumn
	 */
	public ClassORMapping(ClassORMapping parent, Class javaClass, String table, String idColumn) {
		this.parent = parent;

		if (parent != null) {
			parent.addChild(this);
			// Inherit parent columns
			mappingsForChildren.addAll(parent.mappingsForChildren);
			columnMappings.addAll(mappingsForChildren);
		}

		this.javaClass = javaClass;
		this.table = table;
		this.idColumn = idColumn;
	}

	private void addChild(ClassORMapping child) {
		children.add(child);
	}

	/**
	 * Add private column to the column mapping list.
	 * 
	 * @param property
	 * @param column
	 * @param translator
	 * @return
	 */
	public ColumnMapping column(String property, String column, ColumnTranslator translator) {
		ColumnMapping colMapping = new ColumnMapping(this, property, column, translator);
		columnMappings.add(colMapping);
		return colMapping;
	}

	/**
	 * Add public column to be inherited by sub class to the column mapping list.
	 * 
	 * @param property
	 * @param column
	 * @param translator
	 * @param passToChildren
	 * @return
	 */
	public ColumnMapping column(String property, String column, ColumnTranslator translator, boolean passToChildren) {
		ColumnMapping col = column(property, column, translator);
		mappingsForChildren.add(col);
		return col;
	}

	public ColumnMapping findColumn(String propertyName) {
		for (ColumnMapping col : columnMappings) {
			if (col.getPropertyName().equals(propertyName)) {
				return col;
			}
		}
		return null;
	}

	public Collection<ClassORMapping> getChildren() {
		return children;
	}

	public Collection<ColumnMapping> getColumnMappings() {
		return Collections.unmodifiableCollection(columnMappings);
	}

	public String getIdColumn() {
		return idColumn;
	}

	public Class getJavaClass() {
		return javaClass;
	}

	public ClassORMapping getParent() {
		return parent;
	}

	public String getTable() {
		return table;
	}

	public String idRef() {
		return getTable() + "." + getIdColumn();
	}
}
