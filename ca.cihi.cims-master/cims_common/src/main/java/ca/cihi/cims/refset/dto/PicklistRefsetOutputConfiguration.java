package ca.cihi.cims.refset.dto;

public class PicklistRefsetOutputConfiguration
        implements RefsetOutputConfiguration, Comparable<RefsetOutputConfiguration> {
    /**
     * Refset Context Id.
     */
    private Long refsetContextId;

    /**
     * Picklist Output Id.
     */
    private Integer picklistOutputId;

    /**
     * Refset Output Id.
     */
    private Integer refsetOutputId;

    /**
     * Display Name.
     */
    private String displayName;

    /**
     * Supplement/Picklist Order Number.
     */
    private Integer orderNumber;

    /**
     * Picklist Id.
     */
    private Long picklistId;

    @Override
    public Long getOutputId() {
        return picklistOutputId.longValue();
    }

    @Override
    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String getType() {
        return "picklist";
    }

    @Override
    public Integer getOrderNumber() {
        return orderNumber;
    }

    @Override
    public Long getId() {
        return picklistId;
    }

    public Long getPicklistId() {
        return picklistId;
    }

    public void setPicklistId(Long picklistId) {
        this.picklistId = picklistId;
    }

    public void setPicklistOutputId(Integer picklistOutputId) {
        this.picklistOutputId = picklistOutputId;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public Long getRefsetContextId() {
        return refsetContextId;
    }

    public void setRefsetContextId(Long refsetContextId) {
        this.refsetContextId = refsetContextId;
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
        PicklistRefsetOutputConfiguration other = (PicklistRefsetOutputConfiguration) obj;
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
        return "PicklistRefsetOutputConfiguration [refsetContextId=" + refsetContextId + ", picklistOutputId="
                + picklistOutputId + ", refsetOutputId=" + refsetOutputId + ", displayName=" + displayName
                + ", orderNumber=" + orderNumber + ", picklistId=" + picklistId + "]";
    }

    @Override
    public int compareTo(RefsetOutputConfiguration o) {
        return orderNumber > o.getOrderNumber() ? 1 : orderNumber == o.getOrderNumber() ? 0 : -1;
    }
}
