package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class RefsetOutputTitleDTO implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 34290781L;

    /**
     * Refset Output Id.
     */
    private Integer refsetOutputId;

    /**
     * Document Title.
     */
    private String title;

    /**
     * Supplement Id.
     */
    private Long supplementId;

    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    public String getTitle() {
        return title;
    }

    public Long getSupplementId() {
        return supplementId;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSupplementId(Long supplementId) {
        this.supplementId = supplementId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
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
        RefsetOutputTitleDTO other = (RefsetOutputTitleDTO) obj;
        if (refsetOutputId == null) {
            if (other.refsetOutputId != null)
                return false;
        } else if (!refsetOutputId.equals(other.refsetOutputId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefsetOutputTitleDTO [refsetOutputId=" + refsetOutputId + ", title=" + title + ", supplementId="
                + supplementId + "]";
    }
}
