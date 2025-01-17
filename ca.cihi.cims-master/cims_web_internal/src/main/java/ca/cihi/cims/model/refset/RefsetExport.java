package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class RefsetExport implements Serializable, Comparable<RefsetExport> {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 36789011L;

    /**
     * Cell Value.
     */
    private String cellValue;

    /**
     * Cell Wrap Text Required.
     */
    private boolean cellWrapText;

    /**
     * Column Order Number.
     */
    private Integer orderNumber;

    /**
     * Column Id.
     */
    private Long columnId;

    /**
     * Is Sublist Column.
     */
    private boolean sublistColumn;

    /**
     * Number of Collapse Values.
     */
    private int numCollapseValues;

    public String getCellValue() {
        return cellValue;
    }

    public boolean isCellWrapText() {
        return cellWrapText;
    }

    public void setCellValue(String cellValue) {
        this.cellValue = cellValue;
    }

    public void setCellWrapText(boolean cellWrapText) {
        this.cellWrapText = cellWrapText;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Long getColumnId() {
        return columnId;
    }

    public void setColumnId(Long columnId) {
        this.columnId = columnId;
    }

    public boolean isSublistColumn() {
        return sublistColumn;
    }

    public void setSublistColumn(boolean sublistColumn) {
        this.sublistColumn = sublistColumn;
    }

    public int getNumCollapseValues() {
        return numCollapseValues;
    }

    public void setNumCollapseValues(int numCollapseValues) {
        this.numCollapseValues = numCollapseValues;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnId == null) ? 0 : columnId.hashCode());
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
        RefsetExport other = (RefsetExport) obj;
        if (columnId == null) {
            if (other.columnId != null)
                return false;
        } else if (!columnId.equals(other.columnId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefsetExport [cellValue=" + cellValue + ", cellWrapText=" + cellWrapText + ", orderNumber="
                + orderNumber + ", columnId=" + columnId + ", sublistColumn=" + sublistColumn + ", numCollapseValues="
                + numCollapseValues + "]";
    }

    @Override
    public int compareTo(RefsetExport o) {
        return orderNumber > o.orderNumber ? 1
                : columnId > o.columnId ? 1 
                : (cellValue!=null && o.cellValue!=null && cellValue.compareTo(o.cellValue)<0) || (cellValue==null && o.cellValue!=null) ? 1
                : orderNumber == o.orderNumber && columnId == o.columnId && ((cellValue==null && o.cellValue==null) || (cellValue!=null && cellValue.equals(o.cellValue)))? 0 : -1;
    }
}
