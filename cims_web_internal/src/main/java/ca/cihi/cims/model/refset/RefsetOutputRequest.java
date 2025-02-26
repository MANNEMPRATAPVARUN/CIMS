package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class RefsetOutputRequest implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 58901234L;

    /**
     * Refset Context Id.
     */
    public Long refsetContextId;

    /**
     * Refset Element Id.
     */
    public Long elementId;

    /**
     * Refset Element Version Id.
     */
    public Long elementVersionId;

    /**
     * Refset Output Id.
     */
    public Integer refsetOutputId;

    /**
     * Language Code.
     */
    public String languageCode;

    public Long getRefsetContextId() {
        return refsetContextId;
    }

    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setRefsetContextId(Long refsetContextId) {
        this.refsetContextId = refsetContextId;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Long getElementId() {
        return elementId;
    }

    public Long getElementVersionId() {
        return elementVersionId;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public void setElementVersionId(Long elementVersionId) {
        this.elementVersionId = elementVersionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((languageCode == null) ? 0 : languageCode.hashCode());
        result = prime * result + ((refsetContextId == null) ? 0 : refsetContextId.hashCode());
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
        RefsetOutputRequest other = (RefsetOutputRequest) obj;
        if (languageCode == null) {
            if (other.languageCode != null)
                return false;
        } else if (!languageCode.equals(other.languageCode))
            return false;
        if (refsetContextId == null) {
            if (other.refsetContextId != null)
                return false;
        } else if (!refsetContextId.equals(other.refsetContextId))
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
        return "RefsetOutputRequest [refsetContextId=" + refsetContextId + ", elementId=" + elementId
                + ", elementVersionId=" + elementVersionId + ", refsetOutputId=" + refsetOutputId + ", languageCode="
                + languageCode + "]";
    }
}
