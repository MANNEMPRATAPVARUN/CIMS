package ca.cihi.cims.web.controller.refset;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ca.cihi.cims.model.refset.RefsetOutputContent;
import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.model.refset.RefsetOutputSetting;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputDTO;
import ca.cihi.cims.refset.dto.RefsetOutputTitleDTO;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.service.refset.RefsetOutputService;
import ca.cihi.cims.service.refset.RefsetService;
import ca.cihi.cims.web.bean.CIMSWebResponse;
import ca.cihi.cims.web.bean.refset.RefsetBaseBean;
import ca.cihi.cims.web.bean.refset.SupplementOutputBean;

@Controller
public class ProductOutputConfigurationController {
    /**
     * Reference to logger.
     */
    private static final Log LOGGER = LogFactory.getLog(ProductOutputConfigurationController.class);

    /**
     * Refset Output Configuration View Name.
     */
    public static final String REFSET_OUTPUT_CONFIG_VIEW = "productOutputConfig";

    /**
     * Refset Output Configuration Detail View Name.
     */
    public static final String REFSET_OUTPUT_CONFIG_DETAIL_VIEW = "productOutputConfigDetail";

    /**
     * Reference to refset service.
     */
    @Autowired
    private RefsetService refsetService;

    /**
     * Reference to refset output service.
     */
    @Autowired
    private RefsetOutputService refsetOutputService;

    /**
     * Refset Output Configuration Page.
     * 
     * @param model
     *            the model.
     * @param contextId
     *            the refset context id.
     * @param elementId
     *            the refset element id.
     * @param elementVersionId
     *            the refset element version id.
     * @return the view page.
     */
    @RequestMapping(value = "/refset/productOutputConfig", method = RequestMethod.GET)
    public String getProductOutputConfig(final Model model, @RequestParam("contextId") Long contextId,
            @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {
        RefsetBaseBean refsetBaseBean = new RefsetBaseBean();

        refsetBaseBean.setContextId(contextId);
        refsetBaseBean.setElementId(elementId);
        refsetBaseBean.setElementVersionId(elementVersionId);

        model.addAttribute("viewBean", refsetBaseBean);
        model.addAttribute("activeTab", "productOutputConfig");

        return REFSET_OUTPUT_CONFIG_VIEW;
    }

    /**
     * Refset Output Configuration Detail.
     * 
     * @param model
     *            the model.
     * @param contextId
     *            the refset context id.
     * @param elementId
     *            the refset element id.
     * @param elementVersionId
     *            the refset element version id.
     * @param refsetOutputId
     *            the refset output id.
     * @return the view page.
     */
    @RequestMapping(value = "/refset/productOutputConfigDetail", method = RequestMethod.GET)
    public String getProductOutputConfigDetail(final Model model, @RequestParam("contextId") Long contextId,
            @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId,
            @RequestParam("refsetOutputId") Integer refsetOutputId) {
        RefsetBaseBean refsetBaseBean = new RefsetBaseBean();

        refsetBaseBean.setContextId(contextId);
        refsetBaseBean.setElementId(elementId);
        refsetBaseBean.setElementVersionId(elementVersionId);

        model.addAttribute("viewBean", refsetBaseBean);
        model.addAttribute("activeTab", "productOutputConfig");

        RefsetOutputDTO refsetOutput = RefsetFactory.getRefsetOutputConfigByRefsetOutputId(refsetOutputId);

        model.addAttribute("refsetOutput", refsetOutput);

        return REFSET_OUTPUT_CONFIG_DETAIL_VIEW;
    }

    /**
     * Add a new refset output configuration.
     * 
     * @param refsetOutput
     *            the refset output configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/addRefsetOutput", method = RequestMethod.POST)
    public @ResponseBody CIMSWebResponse addRefsetOutput(@RequestBody RefsetOutputDTO refsetOutput) {
        RefsetResponse response = new RefsetResponse();

        try {
            refsetOutput = RefsetFactory.addRefsetOutputConfig(refsetOutput);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully added.");
            webResponse.setResult(refsetOutput);

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("addRefsetOutput exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Get Refset Output Configuration.
     * 
     * @param refsetContextId
     *            the Refset Context Id.
     * @param refsetElementId
     *            the refset Element Id.
     * @return List of Refset Output Configuration.
     */
    @RequestMapping("/refset/getRefsetOutputConfig")
    public @ResponseBody List<RefsetOutputDTO> getRefsetOutputConfig(@RequestParam("contextId") Long contextId,
            @RequestParam("refsetElementId") Long refsetElementId) {
        return RefsetFactory.getRefsetOutputConfigById(contextId, refsetElementId);
    }

    /**
     * Save refset output configuration.
     * 
     * @param refsetOutput
     *            the refset output configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/saveRefsetOutput", method = RequestMethod.POST)
    public @ResponseBody CIMSWebResponse saveRefsetOutput(@RequestBody RefsetOutputDTO refsetOutput) {
        RefsetResponse response = new RefsetResponse();

        try {
            RefsetFactory.updateRefsetOutputConfig(refsetOutput);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully saved.");
            webResponse.setResult(refsetOutput);

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("saveRefsetOutput exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Delete refset output configuration.
     * 
     * @param refsetOutput
     *            the refset output configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/deleteRefsetOutput")
    public @ResponseBody CIMSWebResponse deleteRefsetOutput(@RequestParam("refsetOutputId") Integer refsetOutputId) {
        RefsetResponse response = new RefsetResponse();

        try {
            refsetOutputService.deleteRefsetOutput(refsetOutputId);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully deleted.");

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("deleteRefsetOutput exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Get refset supplements.
     * 
     * @param contextId
     *            the refset context id.
     * @param elementId
     *            the refset element id.
     * @param elementVersionId
     *            the refset element version id.
     * @return the refset supplements.
     */
    @RequestMapping("/refset/getSupplementList")
    public @ResponseBody List<SupplementOutputBean> getSupplementList(@RequestParam("contextId") Long contextId,
            @RequestParam("elementId") Long elementId, @RequestParam("elementVersionId") Long elementVersionId) {
        Refset refset = refsetService.getRefset(contextId, elementId, elementVersionId);

        if (refset == null) {
            return new ArrayList<SupplementOutputBean>();
        }

        List<Supplement> supplementList = refsetService.getSupplements(refset);

        List<SupplementOutputBean> supplementOutputList = new ArrayList<SupplementOutputBean>();

        if (supplementList == null) {
            return supplementOutputList;
        }

        for (Supplement supplement : supplementList) {
            SupplementOutputBean supplementOutput = new SupplementOutputBean();

            supplementOutput.setElementId(supplement.getElementIdentifier().getElementId());
            supplementOutput.setElementVersionId(supplement.getElementIdentifier().getElementVersionId());
            supplementOutput.setName(supplement.getName());
            supplementOutput.setCode(supplement.getCode());

            supplementOutputList.add(supplementOutput);
        }

        return supplementOutputList;
    }

    /**
     * Add New Refset Output Title Configuration.
     * 
     * @param refsetOutputTitle
     *            the refset output title configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/addRefsetOutputTitle", method = RequestMethod.POST)
    public @ResponseBody CIMSWebResponse addRefsetOutputTitle(@RequestBody RefsetOutputTitleDTO refsetOutputTitle) {
        RefsetResponse response = new RefsetResponse();

        try {
            RefsetFactory.addRefsetOutputTitle(refsetOutputTitle);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully added.");
            webResponse.setResult(refsetOutputTitle);

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("addRefsetOutput exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Update Refset Output Title Configuration.
     * 
     * @param refsetOutputTitle
     *            the refset output title configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/updateRefsetOutputTitle", method = RequestMethod.POST)
    public @ResponseBody CIMSWebResponse updateRefsetOutputTitle(@RequestBody RefsetOutputTitleDTO refsetOutputTitle) {
        RefsetResponse response = new RefsetResponse();

        try {
            RefsetFactory.updateRefsetOutputTitle(refsetOutputTitle);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully updated.");
            webResponse.setResult(refsetOutputTitle);

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("updateRefsetOutput exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Get Refset Output Title Configuration.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @return refset output title configuration.
     */
    @RequestMapping("/refset/getRefsetOutputTitle")
    public @ResponseBody CIMSWebResponse getRefsetOutputTitle(@RequestParam("refsetOutputId") Integer refsetOutputId) {
        RefsetOutputTitleDTO refsetOutputTitle = RefsetFactory.getRefsetOutputTitleByRefsetOutputId(refsetOutputId);

        RefsetResponse response = new RefsetResponse();

        CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response, "");
        webResponse.setResult(refsetOutputTitle);

        return webResponse;
    }

    /**
     * Get Available Refset Output Picklist/Supplement Configuration.
     *
     * @param contextId
     *            the refset context id.
     * @param elementId
     *            the refset element id.
     * @param elementVersionId
     *            the refset element version id.
     * @param refsetOutputId
     *            the refset output id.
     * @param languageCode
     *            the language code.
     * @return refset output title configuration.
     */
    @RequestMapping("/refset/getAvailableRefsetOutputConfiguration")
    public @ResponseBody CIMSWebResponse getAvailableRefsetOutputConfiguration(
            @RequestParam("contextId") Long contextId, @RequestParam("elementId") Long elementId,
            @RequestParam("elementVersionId") Long elementVersionId,
            @RequestParam("refsetOutputId") Integer refsetOutputId, @RequestParam("language") String languageCode) {
        RefsetOutputRequest refsetOutputRequest = new RefsetOutputRequest();

        refsetOutputRequest.setRefsetContextId(contextId);
        refsetOutputRequest.setElementId(elementId);
        refsetOutputRequest.setElementVersionId(elementVersionId);
        refsetOutputRequest.setRefsetOutputId(refsetOutputId);
        refsetOutputRequest.setLanguageCode(languageCode);

        List<RefsetOutputConfiguration> refsetOutputConfigurations = refsetOutputService
                .getAvailableRefsetOutput(refsetOutputRequest);

        RefsetResponse response = new RefsetResponse();

        CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response, "");
        webResponse.setResult(refsetOutputConfigurations);

        return webResponse;
    }

    /**
     * Add New Refset Output Configuration.
     * 
     * @param refsetOutputConfiguration
     *            the refset output configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/addRefsetOutputConfiguration", method = RequestMethod.POST)
    public @ResponseBody CIMSWebResponse addRefsetOutputConfiguration(
            @RequestBody RefsetOutputSetting refsetOutputSetting) {
        RefsetResponse response = new RefsetResponse();

        try {
            refsetOutputService.addRefsetOutputConfiguration(refsetOutputSetting);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully added.");

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("addRefsetOutputConfiguration exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Save Refset Output Configuration.
     * 
     * @param refsetOutputConfiguration
     *            the refset output configuration.
     * @return the result response.
     */
    @RequestMapping(value = "/refset/saveRefsetOutputConfiguration", method = RequestMethod.POST)
    public @ResponseBody CIMSWebResponse saveRefsetOutputConfiguration(
            @RequestBody RefsetOutputSetting refsetOutputSetting) {
        RefsetResponse response = new RefsetResponse();

        try {
            refsetOutputService.deleteRefsetOutputConfiguration(refsetOutputSetting.getRefsetOutputId(),
                    refsetOutputSetting.getOrigOutputId(), refsetOutputSetting.getOrigType());
            refsetOutputService.addRefsetOutputConfiguration(refsetOutputSetting);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The refset output configuration is successfully updated.");

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("saveRefsetOutputConfiguration exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Get Saved Refset Output Picklist/Supplement Configuration.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @return refset output title configuration.
     */
    @RequestMapping("/refset/getRefsetOutputConfiguration")
    public @ResponseBody CIMSWebResponse getRefsetOutputConfiguration(@RequestParam("contextId") Long contextId,
            @RequestParam("refsetOutputId") Integer refsetOutputId) {
        List<RefsetOutputConfiguration> refsetOutputConfigurations = refsetOutputService
                .getRefsetOutputConfigurationById(contextId, refsetOutputId);

        RefsetResponse response = new RefsetResponse();

        CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response, "");
        webResponse.setResult(refsetOutputConfigurations);

        return webResponse;
    }

    /**
     * Delete refset output configuration detail.
     * 
     * @param refsetOutput
     *            the refset output id
     * @param outputId
     *            either supplement id or picklist output id
     * @param type
     *            either 'supplement' or 'picklist'
     * @return the result response.
     */
    @RequestMapping(value = "/refset/deleteRefsetOutputConfigDetail")
    public @ResponseBody CIMSWebResponse deleteRefsetOutputConfigDetail(
            @RequestParam("refsetOutputId") Integer refsetOutputId, @RequestParam("outputId") Long outputId,
            @RequestParam("type") String type) {
        RefsetResponse response = new RefsetResponse();

        try {
            refsetOutputService.deleteRefsetOutputConfiguration(refsetOutputId, outputId, type);

            CIMSWebResponse webResponse = CIMSWebResponse.buildSuccessResponse(response,
                    "The new refset output configuration is successfully deleted.");

            return webResponse;
        } catch (Exception e) {
            LOGGER.error("deleteRefsetOutputConfigDetail exception: " + e);

            return CIMSWebResponse.buildFailureResponse(e);
        }
    }

    /**
     * Download Excel Export.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @param response
     *            the http response.
     */
    @RequestMapping(value = "/refset/exportExcel.htm")
    public void exportExcel(@RequestParam("refsetOutputId") Integer refsetOutputId, HttpServletResponse response) {
        RefsetOutputContent refsetOutputContent = refsetOutputService.getRefsetOutputContent(refsetOutputId);

        try {
            String outputFilename = refsetOutputContent.getOutputFilename();

            outputFilename = outputFilename.endsWith(".xlsx") ? outputFilename : outputFilename + ".xlsx";

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=" + outputFilename);

            ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
            refsetOutputContent.getWorkbook().write(outByteStream);

            byte[] outArray = outByteStream.toByteArray();
            response.setContentLength(outArray.length);
            OutputStream outStream = response.getOutputStream();
            
            WritableByteChannel channel = Channels.newChannel(outStream);
			channel.write(ByteBuffer.wrap(outArray));

            outStream.flush();
            outStream.close();

            response.flushBuffer();

            return;
        } catch (IOException ex) {
            LOGGER.error("Error writing file to output stream." + ex);
        }
    }
}
