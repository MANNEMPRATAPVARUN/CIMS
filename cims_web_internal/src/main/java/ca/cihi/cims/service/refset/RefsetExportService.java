package ca.cihi.cims.service.refset;

import org.apache.poi.ss.usermodel.Workbook;

import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;

public interface RefsetExportService {
    /**
     * Get Export Excel Sheet Name
     * 
     * @param refsetOutputConfiguration
     *            the refset output configuration.
     * @return the export excel sheet name.
     */
    public String getSheetName(RefsetOutputConfiguration refsetOutputConfiguration);

    /**
     * Get Processor Type.
     * 
     * @return either 'picklist' or 'supplement'
     */
    public String getType();

    /**
     * Create Excel Sheet.
     * 
     * @param refsetOutputConfiguration
     *            the refset output configuration.
     * @param wb
     *            the excel workbook.
     * @param sheetName
     *            the sheet name.
     * @param languageCode
     *            the language code.
     * @param backToTableContent
     *            include back to table of content or not. 
     *            
     * @return the expot sheet name.           
     */
    public String processExport(RefsetOutputConfiguration refsetOutputConfiguration, Workbook wb, String sheetName, String languageCode, boolean backToTableContent);

    /**
     * Create Refset Output Title Sheet.
     * 
     * @param refsetContextId
     *            the refset context id.
     * @param refsetOutputTitle
     *            the refset output title configuration.
     * @param wb
     *            the workbook.
     * @param languageCode
     *            the language code.
     * @param exportSheetNum
     *            number of export sheets.
     */
    public void createExportTitle(Long refsetContextId, RefsetOutputTitleDTO refsetOutputTitle, Workbook wb,
            String languageCode, int exportSheetNum);
}
