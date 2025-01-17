package ca.cihi.cims.dal.jdbc;

public class ColumnMapping {
	private String propertyName;
	private String columnName;
	private ColumnTranslator translator;
	private ClassORMapping classMapping;

	ColumnMapping(ClassORMapping owner, String propertyName, String columnName,
			ColumnTranslator translator) {
		this.classMapping = owner;
		this.propertyName = propertyName;
		this.columnName = columnName;
		if (translator == null)
			throw new NullPointerException();
		this.translator = translator;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getColumnName() {
		return columnName;
	}

	@SuppressWarnings("rawtypes")
	public ColumnTranslator getTranslator() {
		return translator;
	}

	public String getFullyQualifiedName() {
		return classMapping.getTable() + "." + columnName;
	}

	@Override
	public String toString() {
		return propertyName + "\t" + getFullyQualifiedName();
	}

}
