package ca.cihi.cims.service.refset;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.model.refset.RefsetOutputSetting;
import ca.cihi.cims.refset.dto.RefsetOutputConfiguration;
import ca.cihi.cims.refset.dto.RefsetSupplementOutputDTO;
import ca.cihi.cims.refset.dto.SupplementRefsetOutputConfiguration;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

@Component
public class RefsetSupplementOutputDAOProcessor implements RefsetOutputDAOService {
    /**
     * Processor Type.
     */
    public static final String PROCESSOR_TYPE = "supplement";

    @Override
    public void addRefsetOutputConfiguration(RefsetOutputSetting refsetOutputSetting) {
        RefsetSupplementOutputDTO refsetSupplementOutput = new RefsetSupplementOutputDTO();

        refsetSupplementOutput.setRefsetContextId(refsetOutputSetting.getRefsetContextId());
        refsetSupplementOutput.setRefsetOutputId(refsetOutputSetting.getRefsetOutputId());
        refsetSupplementOutput.setSupplementId(refsetOutputSetting.getOutputId());
        refsetSupplementOutput.setOrderNumber(refsetOutputSetting.getOrderNumber());

        RefsetFactory.addRefsetSupplementOutput(refsetSupplementOutput);
    }

    @Override
    public List<RefsetOutputConfiguration> getRefsetOutputConfigurationByOutputId(Long contextId,
            Integer refsetOutputId) {
        List<RefsetSupplementOutputDTO> refsetSupplements = RefsetFactory
                .getRefsetSupplementOutputByRefsetOutputId(refsetOutputId);

        if (refsetSupplements == null) {
            return null;
        }

        List<RefsetOutputConfiguration> refsetOutputConfigurations = new ArrayList<RefsetOutputConfiguration>();

        for (RefsetSupplementOutputDTO s : refsetSupplements) {
            ElementIdentifier elementIdentifier = new ElementIdentifier();

            elementIdentifier.setElementId(s.getSupplementId());
            elementIdentifier.setElementVersionId(null);

            Supplement supplement = RefsetFactory.getSupplement(contextId, elementIdentifier,
                    ConceptLoadDegree.MINIMAL);

            SupplementRefsetOutputConfiguration refsetOutputConfiguration = new SupplementRefsetOutputConfiguration();

            refsetOutputConfiguration.setSupplementId(s.getSupplementId());
            refsetOutputConfiguration.setOrderNumber(s.getOrderNumber());
            refsetOutputConfiguration.setRefsetOutputId(refsetOutputId);
            refsetOutputConfiguration.setDisplayName(supplement.getName());
            refsetOutputConfiguration.setRefsetContextId(s.getRefsetContextId());            

            refsetOutputConfigurations.add(refsetOutputConfiguration);
        }

        return !refsetOutputConfigurations.isEmpty() ? refsetOutputConfigurations : null;
    }
    
    @Override
    public void deleteRefsetOutputConfiguration(Integer refsetOutputId, Long outputId) {
        RefsetFactory.deleteRefsetSupplementOutputConfig(refsetOutputId, outputId);        
    }

    @Override
    public String getType() {
        return PROCESSOR_TYPE;
    }
}
