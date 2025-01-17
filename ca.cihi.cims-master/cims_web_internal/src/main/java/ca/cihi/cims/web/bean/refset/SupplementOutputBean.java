package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;

public class SupplementOutputBean implements Serializable {
    /**
     * Default Serial Verson UID.
     */
    private static final long serialVersionUID = 5678901L;

    /**
     * Supplement Element Id.
     */
    private Long elementId;

    /**
     * Supplement Element Version Id.
     */
    private Long elementVersionId;

    /**
     * Supplement Code.
     */
    private String code;

    /**
     * Supplement Name.
     */
    private String name;

    public Long getElementId() {
        return elementId;
    }

    public Long getElementVersionId() {
        return elementVersionId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setElementId(Long elementId) {
        this.elementId = elementId;
    }

    public void setElementVersionId(Long elementVersionId) {
        this.elementVersionId = elementVersionId;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((elementId == null) ? 0 : elementId.hashCode());
        result = prime * result + ((elementVersionId == null) ? 0 : elementVersionId.hashCode());
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
        SupplementOutputBean other = (SupplementOutputBean) obj;
        if (elementId == null) {
            if (other.elementId != null)
                return false;
        } else if (!elementId.equals(other.elementId))
            return false;
        if (elementVersionId == null) {
            if (other.elementVersionId != null)
                return false;
        } else if (!elementVersionId.equals(other.elementVersionId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SupplementOutputBean [elementId=" + elementId + ", elementVersionId=" + elementVersionId + ", code="
                + code + ", name=" + name + "]";
    }
}
