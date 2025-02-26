package ca.cihi.cims.validator.refset;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RefsetDuplicateNameManager {
    @Autowired
    List<RefsetDuplicateNameValidatorService> refsetDuplicateNameValidatorServiceList;
    
    /**
     * Validate Duplicate Name.
     * 
     * @param validatorId
     *            the validator id.
     * @param id
     *            refset/picklist Id.
     * @param name
     *            the output name.
     * @return true: duplicate name, false: not.
     */
    public boolean isDuplicateName(String validatorId, Long id, String name) {
        return getRefsetDuplicateNameValidatorServiceById(validatorId).isDuplicateName(id, name);
    }
    
    /**
     * Get Duplicate Error Message.
     * 
     * @param validatorId
     *            the validator Id.
     * @return the Duplicate Error Message.
     */
    public String getDuplicateErrorMessage(String validatorId) {
        return getRefsetDuplicateNameValidatorServiceById(validatorId).getErrorMessage();
    }
   
    /**
     * Get the Duplicate Name Validator by Validator Id.
     * 
     * @param validatorId
     *            the validator Id.
     * @return the Duplicate Name Validator.
     */
    private RefsetDuplicateNameValidatorService getRefsetDuplicateNameValidatorServiceById(String validatorId) {
        return refsetDuplicateNameValidatorServiceList.stream().filter(x -> x.getId().equals(validatorId)).findFirst()
                .get();
    }
}
