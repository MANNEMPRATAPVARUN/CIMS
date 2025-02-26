package ca.cihi.cims.model;

public enum ReferenceTable {
	AUX_TABLE_VALUE("AUX_TABLE_VALUE"),
	CHANGE_REQUEST_STATUS("CHANGE_REQUEST_STATUS"),
	CHANGE_REQUEST_LANGUAGE("CHANGE_REQUEST_LANGUAGE"),
	DISTRIBUTION_LIST("DISTRIBUTION_LIST"),
	USER_PROFILE("USER_PROFILE");


	private String tableName;

	private ReferenceTable(String tableName){
		this.tableName =tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
