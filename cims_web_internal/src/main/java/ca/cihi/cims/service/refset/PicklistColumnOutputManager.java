package ca.cihi.cims.service.refset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.refset.PicklistColumnOutput;
import ca.cihi.cims.model.refset.PicklistColumnOutputRequest;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;

/**
 * This Manager will be responsible for picklist column output configuration
 * change.
 *
 */
@Component
public class PicklistColumnOutputManager {
    @Autowired
    List<PicklistColumnOutputService> picklistColumnOutputServiceList;

    @Transactional
    public void savePicklistColumnOutputConfiguration(PicklistColumnOutputRequest picklistColumnOutputRequest) {
        List<PicklistColumnOutput> picklistColumnOutputList = picklistColumnOutputRequest.getPicklistColumnOutputList();

        if (picklistColumnOutputList == null) {
            return;
        }

        /**
         * Process of picklist column output records.
         */
        for (PicklistColumnOutput po : picklistColumnOutputList) {
            PicklistColumnOutputDTO picklistColumnOutputDTO = new PicklistColumnOutputDTO();

            picklistColumnOutputDTO.setRefsetContextId(picklistColumnOutputRequest.getRefsetContextId());
            picklistColumnOutputDTO.setPicklistOutputId(picklistColumnOutputRequest.getPicklistOutputId());
            picklistColumnOutputDTO.setPickListColumnOutputId(po.getPicklistColumnOutputId());
            picklistColumnOutputDTO.setColumnId(po.getElementId());
            picklistColumnOutputDTO.setOrderNumber(po.getOrderNumber());
            picklistColumnOutputDTO.setDisplayModeCode(po.getDisplayMode());

            /**
             * Process of new added record.
             */
            if (po.getPicklistColumnOutputId() == null && po.isChecked()) {
                getPicklistColumnOutputServiceById(NewPicklistColumnOutputService.PROCESSOR_ID)
                        .processPicklistColumnOutput(picklistColumnOutputDTO);

            }

            /**
             * Process change of existing record.
             */
            if (po.getPicklistColumnOutputId() != null && po.isChecked()) {
                getPicklistColumnOutputServiceById(UpdatePicklistColumnOutputService.PROCESSOR_ID)
                        .processPicklistColumnOutput(picklistColumnOutputDTO);
            }

            List<PicklistColumnOutput> sublistPO = po.getSublist();

            if (sublistPO != null) {
                for (PicklistColumnOutput subPO : sublistPO) {
                    PicklistColumnOutputDTO subPicklistColumnOutputDTO = new PicklistColumnOutputDTO();

                    subPicklistColumnOutputDTO.setRefsetContextId(picklistColumnOutputRequest.getRefsetContextId());
                    subPicklistColumnOutputDTO.setPicklistOutputId(picklistColumnOutputRequest.getPicklistOutputId());
                    subPicklistColumnOutputDTO.setPickListColumnOutputId(subPO.getPicklistColumnOutputId());
                    subPicklistColumnOutputDTO.setColumnId(subPO.getElementId());
                    subPicklistColumnOutputDTO.setOrderNumber(subPO.getOrderNumber());
                    subPicklistColumnOutputDTO.setDisplayModeCode(subPO.getDisplayMode());
                    subPicklistColumnOutputDTO
                            .setParentPickListColumnOutputId(picklistColumnOutputDTO.getPickListColumnOutputId());

                    /**
                     * Process of new added sublist record.
                     */
                    if (subPO.getPicklistColumnOutputId() == null && subPO.isChecked()) {
                        getPicklistColumnOutputServiceById(NewPicklistColumnOutputService.PROCESSOR_ID)
                                .processPicklistColumnOutput(subPicklistColumnOutputDTO);
                    }

                    /**
                     * Process of change of existing sublist record.
                     */
                    if (subPO.getPicklistColumnOutputId() != null && subPO.isChecked()) {
                        getPicklistColumnOutputServiceById(UpdatePicklistColumnOutputService.PROCESSOR_ID)
                                .processPicklistColumnOutput(subPicklistColumnOutputDTO);
                    }

                    /**
                     * Process of delete of existing sublist record.
                     */
                    if (subPO.getPicklistColumnOutputId() != null && !subPO.isChecked()) {
                        getPicklistColumnOutputServiceById(DeletePicklistColumnOutputService.PROCESSOR_ID)
                                .processPicklistColumnOutput(subPicklistColumnOutputDTO);
                    }
                }
            }

            /**
             * Process delete of existing record.
             */
            if (po.getPicklistColumnOutputId() != null && !po.isChecked()) {
                getPicklistColumnOutputServiceById(DeletePicklistColumnOutputService.PROCESSOR_ID)
                        .processPicklistColumnOutput(picklistColumnOutputDTO);
            }
        }
    }

    /**
     * Get Picklist Column Output Processor using the processor Id.
     * 
     * @param processorId
     *            the processor Id.
     * @return the Picklist Column Output Processor.
     */
    private PicklistColumnOutputService getPicklistColumnOutputServiceById(String processorId) {
        return picklistColumnOutputServiceList.stream().filter(x -> x.getProcessorId().equals(processorId)).findFirst()
                .get();
    }
}
