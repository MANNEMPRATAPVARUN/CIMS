package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class ColumnModel implements Serializable, Comparable<ColumnModel> {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 190632L;

    /**
     * Column Element Id.
     */
    private Long columnElementId;

    /**
     * Column Element Version Id.
     */
    private Long columnElementVersionId;

    /**
     * Container Element Id.
     */
    private Long containerElementId;

    /**
     * Container Element Version Id.
     */
    private Long containerElementVersionId;

    /**
     * Column Type.
     */
    private String columnType;

    /**
     * Revised Column Name.
     */
    private String columnName;

    /**
     * Column Order.
     */
    private Short columnOrder;

    /**
     * Sublist Column or not.
     */
    private boolean sublistColumn;

    /**
     * Allow to add sublist or not.
     */
    private boolean sublistAvailable;

    /**
     * Column Lookup Type.
     */
    private String columnLookupType;

    /**
     * Allow delete or not.
     */
    private boolean isDeleteable;

    /**
     * Language Code.
     */
    private String languageCode;

    public Long getContainerElementId() {
        return containerElementId;
    }

    public Long getContainerElementVersionId() {
        return containerElementVersionId;
    }

    public void setContainerElementId(Long containerElementId) {
        this.containerElementId = containerElementId;
    }

    public void setContainerElementVersionId(Long containerElementVersionId) {
        this.containerElementVersionId = containerElementVersionId;
    }

    public Long getColumnElementId() {
        return columnElementId;
    }

    public Long getColumnElementVersionId() {
        return columnElementVersionId;
    }

    public String getColumnType() {
        return columnType;
    }

    public String getColumnName() {
        return columnName;
    }

    public Short getColumnOrder() {
        return columnOrder;
    }

    public boolean isSublistColumn() {
        return sublistColumn;
    }

    public boolean isSublistAvailable() {
        return sublistAvailable;
    }

    public void setColumnElementId(Long columnElementId) {
        this.columnElementId = columnElementId;
    }

    public void setColumnElementVersionId(Long columnElementVersionId) {
        this.columnElementVersionId = columnElementVersionId;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public void setColumnOrder(Short columnOrder) {
        this.columnOrder = columnOrder;
    }

    public void setSublistColumn(boolean sublistColumn) {
        this.sublistColumn = sublistColumn;
    }

    public void setSublistAvailable(boolean sublistAvailable) {
        this.sublistAvailable = sublistAvailable;
    }

    public String getColumnLookupType() {
        return columnLookupType;
    }

    public void setColumnLookupType(String columnLookupType) {
        this.columnLookupType = columnLookupType;
    }

    public boolean isDeleteable() {
        return isDeleteable;
    }

    public void setDeleteable(boolean isDeleteable) {
        this.isDeleteable = isDeleteable;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getId() {
        return String.valueOf(containerElementId) + "|" + String.valueOf(containerElementVersionId) + "|"
                + String.valueOf(columnElementId) + "|" + String.valueOf(columnElementVersionId) + "|"
                + String.valueOf(sublistColumn) + "|" + columnType + "|" + String.valueOf(isDeleteable);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = (prime * result) + ((columnElementId == null) ? 0 : columnElementId.hashCode());
        result = (prime * result) + ((columnElementVersionId == null) ? 0 : columnElementVersionId.hashCode());
        result = (prime * result) + ((containerElementId == null) ? 0 : containerElementId.hashCode());
        result = (prime * result) + ((containerElementVersionId == null) ? 0 : containerElementVersionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ColumnModel other = (ColumnModel) obj;
        if (columnElementId == null) {
            if (other.columnElementId != null) {
                return false;
            }
        } else if (!columnElementId.equals(other.columnElementId)) {
            return false;
        }
        if (columnElementVersionId == null) {
            if (other.columnElementVersionId != null) {
                return false;
            }
        } else if (!columnElementVersionId.equals(other.columnElementVersionId)) {
            return false;
        }
        if (containerElementId == null) {
            if (other.containerElementId != null) {
                return false;
            }
        } else if (!containerElementId.equals(other.containerElementId)) {
            return false;
        }
        if (containerElementVersionId == null) {
            if (other.containerElementVersionId != null) {
                return false;
            }
        } else if (!containerElementVersionId.equals(other.containerElementVersionId)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ColumnModel [columnElementId=" + columnElementId + ", columnElementVersionId=" + columnElementVersionId
                + ", containerElementId=" + containerElementId + ", containerElementVersionId="
                + containerElementVersionId + ", columnType=" + columnType + ", columnName=" + columnName
                + ", columnOrder=" + columnOrder + ", sublistColumn=" + sublistColumn + ", sublistAvailable="
                + sublistAvailable + ", columnLookupType=" + columnLookupType + ", isDeleteable=" + isDeleteable
                + ", languageCode=" + languageCode + "]";
    }

    @Override
    public int compareTo(ColumnModel o) {
        return columnOrder < o.columnOrder ? -1 : columnOrder > o.columnOrder ? 1 : 0;
    }
}
