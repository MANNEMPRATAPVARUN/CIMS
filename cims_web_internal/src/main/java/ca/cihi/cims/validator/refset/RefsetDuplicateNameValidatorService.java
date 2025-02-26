package ca.cihi.cims.validator.refset;

/**
 * Refset Duplicate Name Validator Service.
 * 
 */
public interface RefsetDuplicateNameValidatorService {
    /**
     * Get Service Id.
     * 
     * @return the Service Id.
     */
    public String getId();

    /**
     * Check if has duplicate name or not.
     * 
     * @param id
     *            the refset/picklist Id.
     * @param name
     *            the name.
     * @return true: has duplicate name, false: no duplicate name found.
     */
    public boolean isDuplicateName(Long id, String name);

    /**
     * Get Display Error Message.
     * 
     * @return the Error Message.
     */
    public String getErrorMessage();
}
