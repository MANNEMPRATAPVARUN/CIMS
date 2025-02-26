package ca.cihi.cims.model.refset;

import java.io.Serializable;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class BaseOutputContent implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	  /**
     * Excel Workbook.
     */
    private XSSFWorkbook workbook;

    /**
     * Filename.
     */
    private String fileName;

    /**
     * To indicate if it is a refset output, picklist output or evoluation output
     */
    private String outputType;

	public XSSFWorkbook getWorkbook() {
		return workbook;
	}

	public void setWorkbook(XSSFWorkbook workbook) {
		this.workbook = workbook;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}
}
