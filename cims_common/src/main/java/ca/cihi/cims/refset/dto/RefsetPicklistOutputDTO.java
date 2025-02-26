package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class RefsetPicklistOutputDTO implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 89037811L;

    /**
     * Refset Output Id.
     */
    private Integer refsetOutputId;

    /**
     * Picklist Output Id.
     */
    private Integer picklistOutputId;

    /**
     * Output Order Number.
     */
    private Integer orderNumber;

    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    public Integer getPicklistOutputId() {
        return picklistOutputId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setPicklistOutputId(Integer picklistOutputId) {
        this.picklistOutputId = picklistOutputId;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((picklistOutputId == null) ? 0 : picklistOutputId.hashCode());
        result = prime * result + ((refsetOutputId == null) ? 0 : refsetOutputId.hashCode());
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
        RefsetPicklistOutputDTO other = (RefsetPicklistOutputDTO) obj;
        if (picklistOutputId == null) {
            if (other.picklistOutputId != null)
                return false;
        } else if (!picklistOutputId.equals(other.picklistOutputId))
            return false;
        if (refsetOutputId == null) {
            if (other.refsetOutputId != null)
                return false;
        } else if (!refsetOutputId.equals(other.refsetOutputId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefsetPicklistOutputDTO [refsetOutputId=" + refsetOutputId + ", picklistOutputId=" + picklistOutputId
                + ", orderNumber=" + orderNumber + "]";
    }
}
