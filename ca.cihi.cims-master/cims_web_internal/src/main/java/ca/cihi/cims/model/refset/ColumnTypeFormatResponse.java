package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class ColumnTypeFormatResponse implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 190122L;

    /**
     * Column Type.
     */
    private String columnType;

    /**
     * Concept Id.
     */
    private Long conceptId;

    /**
     * Concept Value.
     */
    private String concepetValue;

    /**
     * Column Element Id.
     */
    private Long columnElementId;

    public String getColumnType() {
        return columnType;
    }

    public Long getConceptId() {
        return conceptId;
    }

    public String getConcepetValue() {
        return concepetValue;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setConceptId(Long conceptId) {
        this.conceptId = conceptId;
    }

    public void setConcepetValue(String concepetValue) {
        this.concepetValue = concepetValue;
    }

    public Long getColumnElementId() {
        return columnElementId;
    }

    public void setColumnElementId(Long columnElementId) {
        this.columnElementId = columnElementId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnType == null) ? 0 : columnType.hashCode());
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
        ColumnTypeFormatResponse other = (ColumnTypeFormatResponse) obj;
        if (columnType == null) {
            if (other.columnType != null)
                return false;
        } else if (!columnType.equals(other.columnType))
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
        return "ColumnTypeFormatResponse [columnType=" + columnType + ", conceptId=" + conceptId + ", concepetValue="
                + concepetValue + ", columnElementId=" + columnElementId + "]";
    }
}
