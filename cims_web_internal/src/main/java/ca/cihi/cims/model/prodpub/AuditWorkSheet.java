package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.util.List;

public class AuditWorkSheet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4517299345849453264L;
	private String fileName;
	private String titleValue;
	private String worksheetName;
	private List<AuditTable> auditTables;

	public List<AuditTable> getAuditTables() {
		return auditTables;
	}

	public String getFileName() {
		return fileName;
	}

	public String getTitleValue() {
		return titleValue;
	}

	public String getWorksheetName() {
		return worksheetName;
	}

	public void setAuditTables(List<AuditTable> auditTables) {
		this.auditTables = auditTables;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setTitleValue(String titleValue) {
		this.titleValue = titleValue;
	}

	public void setWorksheetName(String worksheetName) {
		this.worksheetName = worksheetName;
	}
}
