package ca.cihi.cims.model.refset;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class RefsetOutputContent implements Serializable {
    /**
     * Default Serial Version UID.
     */
    private static final long serialVersionUID = 678901237L;

    /**
     * Excel Workbook.
     */
    private XSSFWorkbook workbook;

    /**
     * Refset Output Filename.
     */
    private String outputFilename;

    public XSSFWorkbook getWorkbook() {
        return workbook;
    }

    public String getOutputFilename() {
        return outputFilename;
    }

    public void setWorkbook(XSSFWorkbook workbook) {
        this.workbook = workbook;
    }

    public void setOutputFilename(String outputFilename) {
        this.outputFilename = outputFilename;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((outputFilename == null) ? 0 : outputFilename.hashCode());
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
        RefsetOutputContent other = (RefsetOutputContent) obj;
        if (outputFilename == null) {
            if (other.outputFilename != null)
                return false;
        } else if (!outputFilename.equals(other.outputFilename))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RefsetOutputContent [outputFilename=" + outputFilename + "]";
    }
}
