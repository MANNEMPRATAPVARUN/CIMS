package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.model.refset.RefsetOutputContent;
import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.model.refset.RefsetOutputSetting;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.PicklistRefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.dto.RefsetSupplementOutputDTO;
import ca.cihi.cims.refset.dto.SupplementRefsetOutputConfiguration;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.util.RefsetExportUtils;

@Service
public class RefsetOutputServiceImpl implements RefsetOutputService {
    /**
     * Reference to list of refset output DAO service.
     */
    @Autowired
    private List<RefsetOutputDAOService> refsetOutputDAOServiceList;

    /**
     * Reference to refset supplemnt output filter.
     */
    @Autowired
    private SavedRefsetSupplementOutputFilter savedRefsetSupplementOutputFilter;

    /**
     * Reference to list of refset export service.
     */
    @Autowired
    private List<RefsetExportService> refsetExportServiceList;

    @Override
    public List<RefsetOutputConfiguration> getAvailableRefsetOutput(RefsetOutputRequest refsetOutputRequest) {
        List<PicklistOutputDTO> picklistOutputList = RefsetFactory
                .getPicklistOutputConfigByRefsetContextId(refsetOutputRequest.getRefsetContextId());

        LanguageRefsetPicklistOutputFilter languageRefsetPicklistOutputFilter = new LanguageRefsetPicklistOutputFilter();
        EmptyRefsetPicklistOutputFilter emptyRefsetPicklistOutputFilter = new EmptyRefsetPicklistOutputFilter();
        SavedRefsetPicklistOutputFilter savedRefsetPicklistOutputFilter = new SavedRefsetPicklistOutputFilter();

        languageRefsetPicklistOutputFilter.setSuccessor(emptyRefsetPicklistOutputFilter);
        emptyRefsetPicklistOutputFilter.setSuccessor(savedRefsetPicklistOutputFilter);

        RefsetPicklistOutputFilter refsetPicklistOutputFilter = languageRefsetPicklistOutputFilter;
        List<PicklistOutputDTO> pickOutputList = refsetPicklistOutputFilter
                .getAvailableRefsetOutput(refsetOutputRequest, picklistOutputList);

        while (refsetPicklistOutputFilter.getSuccessor() != null) {
            refsetPicklistOutputFilter = refsetPicklistOutputFilter.getSuccessor();

            pickOutputList = refsetPicklistOutputFilter.getAvailableRefsetOutput(refsetOutputRequest, pickOutputList);
        }

        ArrayList<RefsetOutputConfiguration> refsetOutputConfigurations = new ArrayList<RefsetOutputConfiguration>();

        if (pickOutputList != null) {
            for (PicklistOutputDTO p : pickOutputList) {
                PicklistRefsetOutputConfiguration refsetOutputConfiguration = new PicklistRefsetOutputConfiguration();

                refsetOutputConfiguration.setRefsetOutputId(refsetOutputRequest.getRefsetOutputId());
                refsetOutputConfiguration.setDisplayName(p.getName());
                refsetOutputConfiguration.setPicklistOutputId(p.getPicklistOutputId());

                refsetOutputConfigurations.add(refsetOutputConfiguration);
            }
        }

        List<Supplement> supplements = savedRefsetSupplementOutputFilter.getAvailableRefsetOutput(refsetOutputRequest);

        if (supplements != null) {
            for (Supplement s : supplements) {
                SupplementRefsetOutputConfiguration refsetOutputConfiguration = new SupplementRefsetOutputConfiguration();

                refsetOutputConfiguration.setRefsetOutputId(refsetOutputRequest.getRefsetOutputId());
                refsetOutputConfiguration.setDisplayName(s.getName());
                refsetOutputConfiguration.setSupplementId(s.getElementIdentifier().getElementId());

                refsetOutputConfigurations.add(refsetOutputConfiguration);
            }
        }

        return refsetOutputConfigurations;
    }

    @Override
    @Transactional
    public void addRefsetOutputConfiguration(RefsetOutputSetting refsetOutputSetting) {
        getRefsetOutputDAOServiceByType(refsetOutputSetting.getType())
                .addRefsetOutputConfiguration(refsetOutputSetting);
    }

    @Override
    public List<RefsetOutputConfiguration> getRefsetOutputConfigurationById(Long contextId, Integer refsetOutputId) {
        List<RefsetOutputConfiguration> refsetOutputConfigurationList = new ArrayList<RefsetOutputConfiguration>();

        for (RefsetOutputDAOService refsetOutputDAOService : refsetOutputDAOServiceList) {
            List<RefsetOutputConfiguration> refsetOutput = refsetOutputDAOService
                    .getRefsetOutputConfigurationByOutputId(contextId, refsetOutputId);

            if (refsetOutput == null) {
                continue;
            }

            refsetOutputConfigurationList.addAll(refsetOutput);
        }

        return refsetOutputConfigurationList;
    }

    @Override
    @Transactional
    public void deleteRefsetOutputConfiguration(Integer refsetOutputId, Long outputId, String type) {
        getRefsetOutputDAOServiceByType(type).deleteRefsetOutputConfiguration(refsetOutputId, outputId);
    }

    /**
     * Get Refset Output DAO Service by output type.
     * 
     * @param type
     *            the output type, either 'picklist' or 'supplement'.
     * @return the Refset Output DAO Service.
     */
    private RefsetOutputDAOService getRefsetOutputDAOServiceByType(String type) {
        return refsetOutputDAOServiceList.stream().filter(x -> x.getType().equals(type)).findFirst().get();
    }

    /**
     * Get Refset Export Service by output type.
     * 
     * @param type
     *            the output type, either 'picklist' or 'supplement'.
     * @return the Refset Export Service.
     */
    private RefsetExportService getRefsetExportServiceByType(String type) {
        return refsetExportServiceList.stream().filter(x -> x.getType().equals(type)).findFirst().get();
    }

    @Override
    public RefsetOutputContent getRefsetOutputContent(Integer refsetOutputId) {
        RefsetOutputDTO refsetOutput = RefsetFactory.getRefsetOutputConfigByRefsetOutputId(refsetOutputId);

        if (refsetOutput == null) {
            return null;
        }

        RefsetOutputContent refsetOutputContent = new RefsetOutputContent();

        refsetOutputContent.setOutputFilename(refsetOutput.getFilename());

        XSSFWorkbook wb = new XSSFWorkbook();
        refsetOutputContent.setWorkbook(wb);

        List<RefsetPicklistOutputDTO> refsetPicklistOutputList = RefsetFactory
                .getRefsetPicklistOutputByRefsetOutputId(refsetOutput.getRefsetOutputId());

        if (refsetPicklistOutputList == null) {
            return refsetOutputContent;
        }

        List<RefsetOutputConfiguration> refsetOutputConfigurationList = new ArrayList<RefsetOutputConfiguration>();

        for (RefsetPicklistOutputDTO p : refsetPicklistOutputList) {
            PicklistOutputDTO picklist = RefsetFactory.getPicklistOutputConfigByOutputId(p.getPicklistOutputId());

            if (picklist == null) {
                continue;
            }

            PicklistRefsetOutputConfiguration refsetOutputConfiguration = new PicklistRefsetOutputConfiguration();

            refsetOutputConfiguration.setRefsetOutputId(refsetOutputId);
            refsetOutputConfiguration.setPicklistOutputId(p.getPicklistOutputId());
            refsetOutputConfiguration.setDisplayName(picklist.getName());
            refsetOutputConfiguration.setOrderNumber(p.getOrderNumber());
            refsetOutputConfiguration.setPicklistId(picklist.getPicklistId());
            refsetOutputConfiguration.setRefsetContextId(refsetOutput.getRefsetContextId());

            refsetOutputConfigurationList.add(refsetOutputConfiguration);
        }

        List<RefsetSupplementOutputDTO> refsetSupplementOutputList = RefsetFactory
                .getRefsetSupplementOutputByRefsetOutputId(refsetOutput.getRefsetOutputId());

        if (refsetSupplementOutputList != null) {
            for (RefsetSupplementOutputDTO s : refsetSupplementOutputList) {
                ElementIdentifier elementIdentifier = new ElementIdentifier();

                elementIdentifier.setElementId(s.getSupplementId());
                elementIdentifier.setElementVersionId(null);

                Supplement supplement = RefsetFactory.getSupplement(s.getRefsetContextId(), elementIdentifier,
                        ConceptLoadDegree.MINIMAL);

                SupplementRefsetOutputConfiguration refsetOutputConfiguration = new SupplementRefsetOutputConfiguration();

                refsetOutputConfiguration.setSupplementId(s.getSupplementId());
                refsetOutputConfiguration.setOrderNumber(s.getOrderNumber());
                refsetOutputConfiguration.setRefsetOutputId(refsetOutputId);
                refsetOutputConfiguration.setDisplayName(supplement.getName());
                refsetOutputConfiguration.setRefsetContextId(refsetOutput.getRefsetContextId());

                refsetOutputConfigurationList.add(refsetOutputConfiguration);
            }
        }

        Collections.sort(refsetOutputConfigurationList);

        RefsetOutputTitleDTO refsetOutputTitle = RefsetFactory.getRefsetOutputTitleByRefsetOutputId(refsetOutputId);

        if (refsetOutputTitle != null) {
            String titleProcessorType = refsetOutputTitle.getTitle() != null ? "picklist" : "supplement";

            getRefsetExportServiceByType(titleProcessorType).createExportTitle(refsetOutput.getRefsetContextId(),
                    refsetOutputTitle, wb, refsetOutput.getLanguageCode(), refsetOutputConfigurationList.size());
        }

        /**
         * Create Table of Content Sheet.
         */
        Sheet tableContent = wb
                .createSheet(RefsetExportUtils.getTableContentByLanguageCode(refsetOutput.getLanguageCode()));

        List<String> sheetNameList = new ArrayList<String>();

        for (RefsetOutputConfiguration refsetOutputConfiguration : refsetOutputConfigurationList) {
            RefsetExportService refsetExportService = getRefsetExportServiceByType(refsetOutputConfiguration.getType());

            String sheetName = refsetExportService.getSheetName(refsetOutputConfiguration);

            if (sheetName == null) {
                continue;
            }

            String exportSheetName = refsetExportService.processExport(refsetOutputConfiguration, wb, sheetName,
                    refsetOutput.getLanguageCode(), true);
            sheetNameList.add(exportSheetName);
        }

        Row row = tableContent.createRow((short) 0);
        Cell cell = row.createCell(0);
        cell.setCellValue(RefsetExportUtils.getTableContentDescByLanguageCode(refsetOutput.getLanguageCode()));

        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Font headerFont = wb.createFont();
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerFont.setFontHeightInPoints((short) 11);
        headerFont.setFontName("Calibri");

        headerStyle.setFont(headerFont);
        cell.setCellStyle(headerStyle);

        Row row2 = tableContent.createRow((short) 1);
        Cell cell2 = row2.createCell(0);
        cell2.setCellValue(RefsetExportUtils.getTableContentByLanguageCode(refsetOutput.getLanguageCode()));

        CellStyle headerStyle2 = wb.createCellStyle();

        Font headerFont2 = wb.createFont();
        headerFont2.setFontHeightInPoints((short) 11);
        headerFont2.setFontName("Calibri");

        headerStyle2.setFont(headerFont2);
        cell2.setCellStyle(headerStyle2);

        int i = 0;

        for (String sheetName : sheetNameList) {
            Row rowContent = tableContent.createRow((short) 2 + i);

            Cell cellContnet = rowContent.createCell(0);
            cellContnet.setCellValue(sheetName);

            CreationHelper createHelper = wb.getCreationHelper();

            Hyperlink link = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
            link.setAddress("'" + sheetName + "'!A1");
            cellContnet.setHyperlink(link);

            CellStyle hlinkStyle = wb.createCellStyle();
            Font hlinkFont = wb.createFont();
            hlinkFont.setUnderline(Font.U_SINGLE);
            hlinkFont.setColor(IndexedColors.BLUE.getIndex());
            hlinkStyle.setFont(hlinkFont);

            cellContnet.setCellStyle(hlinkStyle);

            i++;
        }

        return refsetOutputContent;
    }

    @Override
    @Transactional
    public void deleteRefsetOutput(Integer refsetOutputId) {
        List<RefsetPicklistOutputDTO> refsetPicklistOutputs = RefsetFactory
                .getRefsetPicklistOutputByRefsetOutputId(refsetOutputId);

        if (refsetPicklistOutputs != null) {
            for (RefsetPicklistOutputDTO p : refsetPicklistOutputs) {
                RefsetFactory.deleteRefsetPicklistOutputConfig(refsetOutputId, p.getPicklistOutputId());
            }
        }

        List<RefsetSupplementOutputDTO> refsetSupplementOutputs = RefsetFactory
                .getRefsetSupplementOutputByRefsetOutputId(refsetOutputId);

        if (refsetSupplementOutputs != null) {
            for (RefsetSupplementOutputDTO s : refsetSupplementOutputs) {
                RefsetFactory.deleteRefsetSupplementOutputConfig(refsetOutputId, s.getSupplementId());
            }
        }

        RefsetFactory.deleteRefsetOutputTitlePage(refsetOutputId);
        RefsetFactory.deleteRefsetOutputConfig(refsetOutputId);
    }
}
