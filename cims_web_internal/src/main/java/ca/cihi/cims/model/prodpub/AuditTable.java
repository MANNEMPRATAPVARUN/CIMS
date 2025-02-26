package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.util.List;

public class AuditTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4394104007068610555L;
	private String tableTitle;
	private String[] headerDesc;
	private List<AuditData> auditData;
	private String tableType;

	public List<AuditData> getAuditData() {
		return auditData;
	}

	public String[] getHeaderDesc() {
		return headerDesc;
	}

	public String getTableTitle() {
		return tableTitle;
	}

	public String getTableType() {
		return tableType;
	}

	public void setAuditData(List<AuditData> auditData) {
		this.auditData = auditData;
	}

	public void setHeaderDesc(String[] headerDesc) {
		this.headerDesc = headerDesc;
	}

	public void setTableTitle(String tableTitle) {
		this.tableTitle = tableTitle;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

}
