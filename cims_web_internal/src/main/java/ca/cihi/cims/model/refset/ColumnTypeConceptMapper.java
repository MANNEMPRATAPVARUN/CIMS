package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class ColumnTypeConceptMapper implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 8901278L;

    /**
     * Concept Id.
     */
    private Long conceptId;

    /**
     * Conecept Code.
     */
    private String conceptCode;

    /**
     * Column Type.
     */
    private String columnTypeCode;

    public Long getConceptId() {
        return conceptId;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }

    public void setColumnTypeCode(String columnTypeCode) {
        this.columnTypeCode = columnTypeCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnTypeCode == null) ? 0 : columnTypeCode.hashCode());
        result = prime * result + ((conceptId == null) ? 0 : conceptId.hashCode());
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
        ColumnTypeConceptMapper other = (ColumnTypeConceptMapper) obj;
        if (columnTypeCode == null) {
            if (other.columnTypeCode != null)
                return false;
        } else if (!columnTypeCode.equals(other.columnTypeCode))
            return false;
        if (conceptId == null) {
            if (other.conceptId != null)
                return false;
        } else if (!conceptId.equals(other.conceptId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ColumnTypeConceptMapper [conceptId=" + conceptId + ", conceptCode=" + conceptCode + ", columnTypeCode="
                + columnTypeCode + "]";
    }
}
