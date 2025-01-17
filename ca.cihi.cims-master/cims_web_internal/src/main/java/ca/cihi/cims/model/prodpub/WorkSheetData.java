package ca.cihi.cims.model.prodpub;

import java.io.Serializable;
import java.util.List;

public class WorkSheetData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9056144727643960010L;
	private String fileName;
	private String[] headerDescs;
	private String[] headerNewDescs;
	private String[] headerDisabledDescs;

	private String revisionsTitleValue;
	private String newTitleValue;
	private String disabledTitleValue;

	private String worksheetName;

	private List<CCIGenericAttributeAudit> revisedDescriptions;
	private List<CCIGenericAttributeAudit> newDescriptions;
	private List<CCIGenericAttributeAudit> disabledDescriptions;

	public List<CCIGenericAttributeAudit> getDisabledDescriptions() {
		return disabledDescriptions;
	}

	public String getDisabledTitleValue() {
		return disabledTitleValue;
	}

	public String getFileName() {
		return fileName;
	}

	public String[] getHeaderDescs() {
		return headerDescs;
	}

	public String[] getHeaderDisabledDescs() {
		return headerDisabledDescs;
	}

	public String[] getHeaderNewDescs() {
		return headerNewDescs;
	}

	public List<CCIGenericAttributeAudit> getNewDescriptions() {
		return newDescriptions;
	}

	public String getNewTitleValue() {
		return newTitleValue;
	}

	public List<CCIGenericAttributeAudit> getRevisedDescriptions() {
		return revisedDescriptions;
	}

	public String getRevisionsTitleValue() {
		return revisionsTitleValue;
	}

	public String getWorksheetName() {
		return worksheetName;
	}

	public void setDisabledDescriptions(List<CCIGenericAttributeAudit> disabledDescriptions) {
		this.disabledDescriptions = disabledDescriptions;
	}

	public void setDisabledTitleValue(String disabledTitleValue) {
		this.disabledTitleValue = disabledTitleValue;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setHeaderDescs(String[] headerDescs) {
		this.headerDescs = headerDescs;
	}

	public void setHeaderDisabledDescs(String[] headerDisabledDescs) {
		this.headerDisabledDescs = headerDisabledDescs;
	}

	public void setHeaderNewDescs(String[] headerNewDescs) {
		this.headerNewDescs = headerNewDescs;
	}

	public void setNewDescriptions(List<CCIGenericAttributeAudit> newDescriptions) {
		this.newDescriptions = newDescriptions;
	}

	public void setNewTitleValue(String newTitleValue) {
		this.newTitleValue = newTitleValue;
	}

	public void setRevisedDescriptions(List<CCIGenericAttributeAudit> revisedDescriptions) {
		this.revisedDescriptions = revisedDescriptions;
	}

	public void setRevisionsTitleValue(String revisionsTitleValue) {
		this.revisionsTitleValue = revisionsTitleValue;
	}

	public void setWorksheetName(String worksheetName) {
		this.worksheetName = worksheetName;
	}

}
