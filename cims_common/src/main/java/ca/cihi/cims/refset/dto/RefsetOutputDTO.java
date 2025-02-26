package ca.cihi.cims.refset.dto;

import java.io.Serializable;

public class RefsetOutputDTO implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 188912378L;

    /**
     * Picklist Output Id.
     */
    private Integer refsetOutputId;

    /**
     * Refset Context Id.
     */
    private Long refsetContextId;

    /**
     * Refset Element Id.
     */
    private Long refsetId;

    /**
     * Output Name.
     */
    private String name;

    /**
     * Language Code.
     */
    private String languageCode;

    /**
     * Output File Name.
     */
    private String filename;

    public Integer getRefsetOutputId() {
        return refsetOutputId;
    }

    public Long getRefsetContextId() {
        return refsetContextId;
    }

    public Long getRefsetId() {
        return refsetId;
    }

    public String getName() {
        return name;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getFilename() {
        return filename;
    }

    public void setRefsetOutputId(Integer refsetOutputId) {
        this.refsetOutputId = refsetOutputId;
    }

    public void setRefsetContextId(Long refsetContextId) {
        this.refsetContextId = refsetContextId;
    }

    public void setRefsetId(Long refsetId) {
        this.refsetId = refsetId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setFilename(String filename) {
        this.filename = filename;
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
        RefsetOutputDTO other = (RefsetOutputDTO) obj;
        if (refsetOutputId == null) {
            if (other.refsetOutputId != null)
                return false;
        } else if (!refsetOutputId.equals(other.refsetOutputId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefsetOutputDTO [refsetOutputId=" + refsetOutputId + ", refsetContextId=" + refsetContextId
                + ", refsetId=" + refsetId + ", name=" + name + ", languageCode=" + languageCode + ", filename="
                + filename + "]";
    }
}
