package ca.cihi.cims.model.refset;

import java.io.Serializable;

public class ColumnTypeSearchPropertyMapper implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 1098367L;

    /**
     * Column Type Code.
     */
    private String columnTypeCode;

    /**
     * Search Response Property Name.
     */
    private String searchPropertyName;

    /**
     * Language Code.
     */
    private String languageCode;

    /**
     * Code Formatter Flag.
     */
    private boolean codeFormatter;

    /**
     * Search Response Id Name.
     */
    private String searchIdName;

    public ColumnTypeSearchPropertyMapper(String columnTypeCode, String searchPropertyName, String languageCode,
            boolean codeFormatter, String searchIdName) {
        this.columnTypeCode = columnTypeCode;
        this.searchPropertyName = searchPropertyName;
        this.languageCode = languageCode;
        this.codeFormatter = codeFormatter;
        this.searchIdName = searchIdName;
    }

    public String getColumnTypeCode() {
        return columnTypeCode;
    }

    public String getSearchPropertyName() {
        return searchPropertyName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getHasCodeFormatter() {
        return codeFormatter ? "Y" : "N";
    }

    public String getSearchIdName() {
        return searchIdName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((columnTypeCode == null) ? 0 : columnTypeCode.hashCode());
        result = prime * result + ((searchPropertyName == null) ? 0 : searchPropertyName.hashCode());
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
        ColumnTypeSearchPropertyMapper other = (ColumnTypeSearchPropertyMapper) obj;
        if (columnTypeCode == null) {
            if (other.columnTypeCode != null)
                return false;
        } else if (!columnTypeCode.equals(other.columnTypeCode))
            return false;
        if (searchPropertyName == null) {
            if (other.searchPropertyName != null)
                return false;
        } else if (!searchPropertyName.equals(other.searchPropertyName))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ColumnTypeSearchPropertyMapper [columnTypeCode=" + columnTypeCode + ", searchPropertyName="
                + searchPropertyName + ", languageCode=" + languageCode + ", codeFormatter=" + codeFormatter
                + ", searchIdName=" + searchIdName + "]";
    }
}
