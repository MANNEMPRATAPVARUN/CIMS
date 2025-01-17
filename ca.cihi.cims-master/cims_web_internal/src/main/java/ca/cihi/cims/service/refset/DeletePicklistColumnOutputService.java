package ca.cihi.cims.service.refset;

import org.springframework.stereotype.Component;

import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

/**
 * This picklist column output service is responsible for new picklist column
 * output record.
 *
 */
@Component
public class DeletePicklistColumnOutputService implements PicklistColumnOutputService {
    /**
     * Processor Id.
     */
    public static final String PROCESSOR_ID = "deletePicklistColumnOutputService";

    @Override
    public String getProcessorId() {
        return PROCESSOR_ID;
    }

    @Override
    public void processPicklistColumnOutput(PicklistColumnOutputDTO picklistColumnOutput) {
        RefsetFactory.deletePicklistColumnOutputConfig(picklistColumnOutput.getPickListColumnOutputId());
    }
}
