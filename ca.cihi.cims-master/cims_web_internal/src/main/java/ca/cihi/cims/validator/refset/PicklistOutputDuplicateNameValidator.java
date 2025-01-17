package ca.cihi.cims.validator.refset;

import java.util.List;

import org.springframework.stereotype.Component;

import ca.cihi.cims.refset.dto.PicklistOutputDTO;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

@Component
public class PicklistOutputDuplicateNameValidator implements RefsetDuplicateNameValidatorService {
    /**
     * Duplicate Error Message.
     */
    private static final String ERROR_MESSAGE = "Duplicate Picklist Output Configuration is not allowed";
    
    @Override
    public String getId() {        
        return RefsetDuplicateNameValidatorId.PICKLIST_OUTPUT.getId();
    }

    @Override
    public boolean isDuplicateName(Long id, String name) {
        List<PicklistOutputDTO> picklistOutputConfigList = RefsetFactory.getPicklistOutputConfigByName(id, name);
        
        return picklistOutputConfigList != null && !picklistOutputConfigList.isEmpty() ? true : false;
    }

    @Override
    public String getErrorMessage() {        
        return ERROR_MESSAGE;
    }
}
