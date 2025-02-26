package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class RefsetSupplementOutputDTO implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 80912811L;

    /**
     * Refset Supplement Output Id.
     */
    private Integer refsetSupplementOutputId;

    /**
     * Refset Context Id.
     */
    private Long refsetContextId;

    /**
     * Refset Output Id.
     */
    private Integer refsetOutputId;

    /**
     * Supplement Id.
     */
    private Long supplementId;

    /**
     * Output Order Number.
     */
    private Integer orderNumber;

    public Integer getRefsetSupplementOutputId() {
        return refsetSupplementOutputId;
    }

    public Long getRefsetContextId() {
        return refsetContextId;
    }

    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    public Long getSupplementId() {
        return supplementId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setRefsetSupplementOutputId(Integer refsetSupplementOutputId) {
        this.refsetSupplementOutputId = refsetSupplementOutputId;
    }

    public void setRefsetContextId(Long refsetContextId) {
        this.refsetContextId = refsetContextId;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setSupplementId(Long supplementId) {
        this.supplementId = supplementId;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((refsetSupplementOutputId == null) ? 0 : refsetSupplementOutputId.hashCode());
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
        RefsetSupplementOutputDTO other = (RefsetSupplementOutputDTO) obj;
        if (refsetSupplementOutputId == null) {
            if (other.refsetSupplementOutputId != null)
                return false;
        } else if (!refsetSupplementOutputId.equals(other.refsetSupplementOutputId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefsetSupplementOutputDTO [refsetSupplementOutputId=" + refsetSupplementOutputId + ", refsetContextId="
                + refsetContextId + ", refsetOutputId=" + refsetOutputId + ", supplementId=" + supplementId
                + ", orderNumber=" + orderNumber + "]";
    }
}
