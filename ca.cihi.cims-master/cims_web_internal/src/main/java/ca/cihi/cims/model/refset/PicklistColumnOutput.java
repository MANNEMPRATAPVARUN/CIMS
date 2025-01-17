package ca.cihi.cims.model.refset;

import java.io.Serializable;
import java.util.List;

public class PicklistColumnOutput implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 789033367L;

    /**
     * Picklist Column Output Id.
     */
    private Integer picklistColumnOutputId;

    /**
     * Picklist Column Element Id.
     */
    private Long elementId;

    /**
     * Output Order Number.
     */
    private Integer orderNumber;

    /**
     * Display Mode.
     */
    private String displayMode;

    /**
     * Parent Output Id.
     */
    private Long parentOutputId;

    /**
     * Sublist Column Output.
     */
    private List<PicklistColumnOutput> sublist;

    /**
     * This column is selected or not.
     */
    private boolean checked;

    public Long getElementId() {
        return elementId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public String getDisplayMode() {
        return displayMode;
    }

    public Long getParentOutputId() {
        return parentOutputId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setDisplayMode(String displayMode) {
        this.displayMode = displayMode;
    }

    public void setParentOutputId(Long parentOutputId) {
        this.parentOutputId = parentOutputId;
    }

    public List<PicklistColumnOutput> getSublist() {
        return sublist;
    }

    public void setSublist(List<PicklistColumnOutput> sublist) {
        this.sublist = sublist;
    }

    public Integer getPicklistColumnOutputId() {
        return picklistColumnOutputId;
    }

    public void setPicklistColumnOutputId(Integer picklistColumnOutputId) {
        this.picklistColumnOutputId = picklistColumnOutputId;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
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
        PicklistColumnOutput other = (PicklistColumnOutput) obj;
        if (elementId == null) {
            if (other.elementId != null)
                return false;
        } else if (!elementId.equals(other.elementId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PicklistColumnOutput [picklistColumnOutputId=" + picklistColumnOutputId + ", elementId=" + elementId
                + ", orderNumber=" + orderNumber + ", displayMode=" + displayMode + ", parentOutputId=" + parentOutputId
                + ", sublist=" + sublist + ", checked=" + checked + "]";
    }
}
