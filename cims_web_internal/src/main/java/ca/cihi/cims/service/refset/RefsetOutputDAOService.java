package ca.cihi.cims.service.refset;

import java.util.List;

import ca.cihi.cims.model.refset.RefsetOutputSetting;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;

public interface RefsetOutputDAOService {
    /**
     * Add New Refset Output Configuration.
     * 
     * @param refsetOutputSetting
     *            the refset output setting.
     */
    public void addRefsetOutputConfiguration(RefsetOutputSetting refsetOutputSetting);

    /**
     * Get List of Refset Output Configuration.
     * 
     * @param contextId
     *            the refset context id.
     * @param refsetOutputId
     *            the refset output id.
     * @return list of reset output configuration.
     */
    public List<RefsetOutputConfiguration> getRefsetOutputConfigurationByOutputId(Long contextId, Integer refsetOutputId);
    
    /**
     * Delete Refset Supplement/Picklist Output Configuration.
     * 
     * @param refsetOutputId
     *            the refset output id.
     * @param outputId
     *            the output id.
     */
    public void deleteRefsetOutputConfiguration(Integer refsetOutputId, Long outputId);

    /**
     * Get Processor Type.
     * 
     * @return either 'picklist' or 'supplement'
     */
    public String getType();
}
