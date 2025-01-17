package ca.cihi.cims.validator.refset;

public enum RefsetDuplicateNameValidatorId {
    /**
     * Picklist Output Id.
     */
    PICKLIST_OUTPUT("PICKLIST_OUTPUT"),

    /**
     * Picklist Output Configuration Code.
     */
    PICKLIST_OUTPUT_CONFIGURATION_CODE("PICKLIST_OUTPUT_CONFIGURATION_CODE"),

    /**
     * Refset Output Id.
     */
    REFSET_OUTPUT("REFSET_OUTPUT");

    /**
     * Id.
     */
    private String id;

    private RefsetDuplicateNameValidatorId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
