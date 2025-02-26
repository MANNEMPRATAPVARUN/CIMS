package ca.cihi.cims.model.prodpub;

import java.io.Serializable;

public class PublicationStatistics implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String tableName;
	private String language;
	private int count;

	public PublicationStatistics(String tableName, String language, int count) {
		this.tableName = tableName;
		this.language = language;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public String getLanguage() {
		return language;
	}

	public String getTableName() {
		return tableName;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

}
