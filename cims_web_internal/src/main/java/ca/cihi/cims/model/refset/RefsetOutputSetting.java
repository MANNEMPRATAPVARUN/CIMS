package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class RefsetOutputSetting implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Refset Context Id.
     */
    private Long refsetContextId;

    /**
     * Refset Output Id.
     */
    private Integer refsetOutputId;

    /**
     * Display Order Number.
     */
    private Integer orderNumber;

    /**
     * Output Id - either supplementId or picklist output Id.
     */
    private Long outputId;

    /**
     * Type - either 'picklist or 'supplement'.
     */
    private String type;

    /**
     * Original record type - either 'picklist or 'supplement'.
     */
    private String origType;

    /**
     * Original Output Id - either supplementId or picklist output Id.
     */
    private Long origOutputId;

    public Long getRefsetContextId() {
        return refsetContextId;
    }

    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public Long getOutputId() {
        return outputId;
    }

    public void setRefsetContextId(Long refsetContextId) {
        this.refsetContextId = refsetContextId;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOutputId(Long outputId) {
        this.outputId = outputId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrigType() {
        return origType;
    }

    public Long getOrigOutputId() {
        return origOutputId;
    }

    public void setOrigType(String origType) {
        this.origType = origType;
    }

    public void setOrigOutputId(Long origOutputId) {
        this.origOutputId = origOutputId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((outputId == null) ? 0 : outputId.hashCode());
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
        RefsetOutputSetting other = (RefsetOutputSetting) obj;
        if (outputId == null) {
            if (other.outputId != null)
                return false;
        } else if (!outputId.equals(other.outputId))
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
        return "RefsetOutputSetting [refsetContextId=" + refsetContextId + ", refsetOutputId=" + refsetOutputId
                + ", orderNumber=" + orderNumber + ", outputId=" + outputId + ", type=" + type + ", origType="
                + origType + ", origOutputId=" + origOutputId + "]";
    }
}
