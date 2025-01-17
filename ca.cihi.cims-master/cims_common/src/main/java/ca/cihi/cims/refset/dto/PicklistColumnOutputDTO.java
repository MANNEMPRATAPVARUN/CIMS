package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class PicklistColumnOutputDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Integer pickListColumnOutputId;
    private Long refsetContextId;
    private Integer picklistOutputId;
    private Long columnId;
    private Integer orderNumber;
    private String displayModeCode;
    private Integer parentPickListColumnOutputId;

    public Integer getPickListColumnOutputId() {
        return pickListColumnOutputId;
    }

    public void setPickListColumnOutputId(Integer pickListColumnOutputId) {
        this.pickListColumnOutputId = pickListColumnOutputId;
    }

    public Long getRefsetContextId() {
        return refsetContextId;
    }

    public void setRefsetContextId(Long refsetContextId) {
        this.refsetContextId = refsetContextId;
    }

    public Integer getPicklistOutputId() {
        return picklistOutputId;
    }

    public void setPicklistOutputId(Integer picklistOutputId) {
        this.picklistOutputId = picklistOutputId;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDisplayModeCode() {
        return displayModeCode;
    }

    public void setDisplayModeCode(String displayModeCode) {
        this.displayModeCode = displayModeCode;
    }

    public Integer getParentPickListColumnOutputId() {
        return parentPickListColumnOutputId;
    }

    public void setParentPickListColumnOutputId(Integer parentPickListColumnOutputId) {
        this.parentPickListColumnOutputId = parentPickListColumnOutputId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((pickListColumnOutputId == null) ? 0 : pickListColumnOutputId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PicklistColumnOutputDTO other = (PicklistColumnOutputDTO) obj;
        if (pickListColumnOutputId == null) {
            if (other.pickListColumnOutputId != null)
                return false;
        } else if (!pickListColumnOutputId.equals(other.pickListColumnOutputId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PicklistColumnOutputDTO [pickListColumnOutputId=" + pickListColumnOutputId + ", refsetContextId="
                + refsetContextId + ", picklistOutputId=" + picklistOutputId + ", columnId=" + columnId
                + ", orderNumber=" + orderNumber + ", displayModeCode=" + displayModeCode
                + ", parentPickListColumnOutputId=" + parentPickListColumnOutputId + "]";
    }
}
