package ca.cihi.cims.dal.jdbc;

import java.util.ArrayList;
import java.util.List;

public class UpdateBits {

	private String queryName;
	
	private String tablename;
	private List<String> columns = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();
	private List<String> wheres = new ArrayList<String>();
	private List<String> wheresValues = new ArrayList<String>();

	public UpdateBits(String tablename) {
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
	
	public void addWhere(String where, String whereValue) {
		wheres.add(where);
		wheresValues.add(whereValue);
	}	
	
	public String toString() {
		
		//It seems that if we don't have a where clause we could do a lot of damage here..
		if (wheres.isEmpty()) {
			return null;
		}
		
		return (queryName != null ? "/* " + queryName + " */ " : "")				
				+ "UPDATE " + tablename + " SET "
				+ updateBuilder()
				+ updateWhereBuilder()
				;
	}
	
	private String updateBuilder() {
		String updateSet = "";
		
		//Ensure same length
		if (columns.size() != values.size()) {
			return null;
		}
		
		int numValues = columns.size() - 1;
		
		for (int i = 0; i < columns.size(); i++) {
			updateSet += columns.get(i) + "=" + values.get(i);
			
			if (i == numValues) {
				updateSet += " "; 
			} else {
				updateSet += ",";
			}
		}
		
		return updateSet; 
	}
	
	private String updateWhereBuilder() {
		String whereSet = "";
		
		//Ensure same length
		if (wheres.size() != wheresValues.size()) {
			return null;
		}
		
		if (wheres.isEmpty()) {
			return whereSet;
		} else {
			whereSet += "WHERE ";
		}
		
		int numValues = wheres.size() - 1;
		
		for (int i = 0; i < wheres.size(); i++) {
			whereSet += wheres.get(i) + "=" + wheresValues.get(i);
			
			if (i == numValues) {
				whereSet += " "; 
			} else {
				whereSet += "AND ";
			}
		}
		
		return whereSet;
	}
	
}
