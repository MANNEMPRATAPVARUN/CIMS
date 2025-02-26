package ca.cihi.cims.refset.dto;

public class SupplementRefsetOutputConfiguration
        implements RefsetOutputConfiguration, Comparable<RefsetOutputConfiguration> {
    /**
     * Refset Context Id.     
     */
    private Long refsetContextId;
    
    /**
     * Refset Supplement Output Id.
     */
    private Long supplementId;

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

    @Override
    public Long getOutputId() {
        return supplementId;
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
        return "supplement";
    }

    @Override
    public Integer getOrderNumber() {
        return orderNumber;
    }    

    @Override
    public Long getId() {        
        return supplementId;
    }

    public void setSupplementId(Long supplementId) {
        this.supplementId = supplementId;
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
        result = prime * result + ((refsetOutputId == null) ? 0 : refsetOutputId.hashCode());
        result = prime * result + ((supplementId == null) ? 0 : supplementId.hashCode());
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
        SupplementRefsetOutputConfiguration other = (SupplementRefsetOutputConfiguration) obj;
        if (refsetOutputId == null) {
            if (other.refsetOutputId != null)
                return false;
        } else if (!refsetOutputId.equals(other.refsetOutputId))
            return false;
        if (supplementId == null) {
            if (other.supplementId != null)
                return false;
        } else if (!supplementId.equals(other.supplementId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SupplementRefsetOutputConfiguration [refsetContextId=" + refsetContextId + ", supplementId="
                + supplementId + ", refsetOutputId=" + refsetOutputId + ", displayName=" + displayName
                + ", orderNumber=" + orderNumber + "]";
    }

    @Override
    public int compareTo(RefsetOutputConfiguration o) {
        return orderNumber > o.getOrderNumber() ? 1 : orderNumber == o.getOrderNumber() ? 0 : -1;
    }
}
