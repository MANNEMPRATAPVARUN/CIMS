package ca.cihi.cims.web.bean.refset;

public class PickListColumnEvolutionViewBean {
	
	private Integer evolutionRecordNum;
	private String classificationCode;
	private String columnName;
	private String oldValue;
	private String newValue;
	private String changeType;
	private String changeTypeDesc;
	
	public String getChangeTypeDesc() {
		return changeTypeDesc;
	}
	public void setChangeTypeDesc(String changeTypeDesc) {
		this.changeTypeDesc = changeTypeDesc;
	}
	public Integer getEvolutionRecordNum() {
		return evolutionRecordNum;
	}
	public void setEvolutionRecordNum(Integer evolutionRecordNum) {
		this.evolutionRecordNum = evolutionRecordNum;
	}
	public String getClassificationCode() {
		return classificationCode;
	}
	public void setClassificationCode(String classificationCode) {
		this.classificationCode = classificationCode;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getChangeType() {
		return changeType;
	}
	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}
	
}
