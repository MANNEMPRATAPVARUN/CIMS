package ca.cihi.cims.service.refset;

import java.util.List;

import ca.cihi.cims.model.refset.RefsetOutputContent;
import ca.cihi.cims.model.refset.RefsetOutputRequest;
import ca.cihi.cims.model.refset.RefsetOutputSetting;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;

public interface RefsetOutputService {
    /**
     * Get List of Available Refset Picklist/Supplement Output Configurations.
     * 
     * @param refsetOutputRequest
     *            the refset output request.
     * @return List of Available Refset Picklist/Supplement Output
     *         Configurations.
     */
    public List<RefsetOutputConfiguration> getAvailableRefsetOutput(RefsetOutputRequest refsetOutputRequest);

    /**
     * Add New Refset Output Setting.
     * 
     * @param refsetOutputSetting
     *            the refset output setting.
     */
    public void addRefsetOutputConfiguration(RefsetOutputSetting refsetOutputSetting);

    /**
     * Get List of Refset Output Configuration.
     *
     * @Param context Id the refset context id.
     * @param refsetOutputId
     *            the refset output id.
     * @return list of refset output configurations.
     */
    public List<RefsetOutputConfiguration> getRefsetOutputConfigurationById(Long contextId, Integer refsetOutputId);

    /**
     * Delete Refset Output.
     * 
     * @param refsetOutputId
     *            the refset output id.
     */
    public void deleteRefsetOutput(Integer refsetOutputId);

    /**
     * Delete Refset Supplement/Picklist Output Configuration.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @param outputId
     *            the output id.
     * @param type
     *            either 'supplement' or 'picklist'.
     */
    public void deleteRefsetOutputConfiguration(Integer refsetOutputId, Long outputId, String type);

    /**
     * Get Refset Output Content.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @return the refset output content.
     */
    public RefsetOutputContent getRefsetOutputContent(Integer refsetOutputId);
}
