package ca.cihi.cims.refset.dto;

import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

public class PicklistColumnEvolutionResultDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer evolutionRecordNum;
	private String conceptCode;
	private String columnName;
	private String columnType;
	private String newValue;
    private String oldValue;
    private String changeTypeDesc;
    private String recordType;
    private Long newValueId;
	private Long oldValueId;
    private String newStatus;
    private String oldStatus;
    private Long recordId;
    private String recordStatus;
    
	public Integer getEvolutionRecordNum() {
		return evolutionRecordNum;
	}
	public void setEvolutionRecordNum(Integer evolutionRecordNum) {
		this.evolutionRecordNum = evolutionRecordNum;
	}
    public Long getRecordId() {
		return recordId;
	}
	public void setRecordId(Long recordId) {
		this.recordId = recordId;
	}
	public Long getNewValueId() {
		return newValueId;
	}
	public void setNewValueId(Long newValueId) {
		this.newValueId = newValueId;
	}
	public Long getOldValueId() {
		return oldValueId;
	}
	public void setOldValueId(Long oldValueId) {
		this.oldValueId = oldValueId;
	}
	public String getNewStatus() {
		return newStatus;
	}
	public void setNewStatus(String newStatus) {
		this.newStatus = newStatus;
	}
	public String getOldStatus() {
		return oldStatus;
	}
	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}
	public String getConceptCode() {
		return conceptCode;
	}
	public void setConceptCode(String conceptCode) {
		this.conceptCode = conceptCode;
	}
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	public String getColumnType() {
		return columnType;
	}
	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		this.newValue = newValue;
	}
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		this.oldValue = oldValue;
	}
	public String getChangeTypeDesc() {
		return changeTypeDesc;
	}
	public void setChangeTypeDesc(String changeTypeDesc) {
		this.changeTypeDesc = changeTypeDesc;
	}
	public String getRecordType() {
		return recordType;
	}
	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}
	
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
    public int hashCode() {
        int hash = 7;
        hash = 31
                * hash
                + (null == conceptCode ? 0 : conceptCode.hashCode());
        hash = 31
                * hash
                + (null == columnName ? 0 : columnName.hashCode());
        hash = 31
                * hash
                + (null == newValue ? 0 : newValue.hashCode());
        hash = 31
                * hash
                + (null == recordType ? 0 : recordType.hashCode());
        /*
        hash = 31
                * hash
                + (null == recordStatus ? 0 : recordStatus.hashCode());
        */
       
        return hash;
        
    }
    
    public boolean equals(final Object anObject) {
        if (this == anObject) {
            return true;
        }
        if ((anObject == null)
            || (anObject.getClass() != this.getClass())) {
            return false;
        }
        PicklistColumnEvolutionResultDTO otherObject = (PicklistColumnEvolutionResultDTO) anObject;
        return ObjectUtils.equals(conceptCode, otherObject.conceptCode)
               && ObjectUtils.equals(columnName, otherObject.columnName)
               && ObjectUtils.equals(newValue, otherObject.newValue)
               && ObjectUtils.equals(recordType, otherObject.recordType);
               /*
               && ObjectUtils.equals(recordStatus, otherObject.recordStatus);
               */
    }
    
    public String toString() {
        return "PicklistColumnEvolutionResultDTO [evolutionRecordNum="
               + evolutionRecordNum
               + ", conceptCode="
               + conceptCode
               + ", columnName="
               + columnName
               + ", newValue="
               + newValue
               + ", oldValue="
               + oldValue
               + ", changeTypeDesc="
               + changeTypeDesc
               + ", recordType="
               + recordType
               + ", newValueId="
               + newValueId
               + ", oldValueId="
               + oldValueId
               + ", newStatus="
               + newStatus
               + ", oldStatus="
               + oldStatus
               + ", recordId="
               + recordId
               + ", recordStatus="
               + recordStatus
               + "]";
    }
}
