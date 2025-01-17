package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ca.cihi.cims.model.refset.RefsetOutputSetting;
import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.dto.PicklistRefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetPicklistOutputDTO;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

@Component
public class RefsetPicklistOutputDAOProcessor implements RefsetOutputDAOService {
    /**
     * Processor Type.
     */
    public static final String PROCESSOR_TYPE = "picklist";

    @Override
    public void addRefsetOutputConfiguration(RefsetOutputSetting refsetOutputSetting) {
        RefsetPicklistOutputDTO refsetPicklistOutput = new RefsetPicklistOutputDTO();

        refsetPicklistOutput.setRefsetOutputId(refsetOutputSetting.getRefsetOutputId());
        refsetPicklistOutput.setPicklistOutputId(refsetOutputSetting.getOutputId().intValue());
        refsetPicklistOutput.setOrderNumber(refsetOutputSetting.getOrderNumber());

        RefsetFactory.addRefsetPicklistOutput(refsetPicklistOutput);
    }

    @Override
    public List<RefsetOutputConfiguration> getRefsetOutputConfigurationByOutputId(Long contextId, Integer refsetOutputId) {  
        List<RefsetPicklistOutputDTO> refsetPicklistOutputList = RefsetFactory.getRefsetPicklistOutputByRefsetOutputId(refsetOutputId);
        
        if (refsetPicklistOutputList == null) {
            return null;
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
            refsetOutputConfiguration.setRefsetContextId(contextId);
            
            refsetOutputConfigurationList.add(refsetOutputConfiguration);
        }
        
        return refsetOutputConfigurationList;
    }
    
    @Override
    public void deleteRefsetOutputConfiguration(Integer refsetOutputId, Long outputId) {
        RefsetFactory.deleteRefsetPicklistOutputConfig(refsetOutputId, outputId.intValue());        
    }

    @Override
    public String getType() {
        return PROCESSOR_TYPE;
    }    
}
