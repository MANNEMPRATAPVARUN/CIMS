package ca.cihi.cims.service.refset;

import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;

public interface PicklistColumnOutputService {
    /**
     * Get the processor id.
     * 
     * @return the processor id.
     */
    public String getProcessorId();

    /**
     * Process picklist column output, either insert, update or delete the
     * record.
     * 
     * @param picklistColumnOutput
     *            the picklist column output.
     */
    public void processPicklistColumnOutput(PicklistColumnOutputDTO picklistColumnOutput);
}
