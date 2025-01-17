package ca.cihi.cims.refset.dto;

public interface RefsetOutputConfiguration extends Comparable<RefsetOutputConfiguration> {
    /**
     * Get Refset Context Id.
     * 
     * @return the refset context Id.
     */
    public Long getRefsetContextId();

    /**
     * Get Picklist/Supplement Output Id.
     * 
     * @return the Picklist/Supplement Output Id.
     */
    public Long getOutputId();

    /**
     * Get Refset Output Id.
     * 
     * @return the Refset Output Id.
     */
    public Integer getRefsetOutputId();

    /**
     * Name shown in the dropdown selection.
     * 
     * @return the element display name.
     */
    public String getDisplayName();

    /**
     * Type - either 'picklist' or 'supplement'.
     * 
     * @return Output Element Type.
     */
    public String getType();

    /**
     * Get Supplement/Picklist Order Number;
     * 
     * @return Supplement/Picklist Order Number.
     */
    public Integer getOrderNumber();

    /**
     * Get Picklist/Supplement Id.
     * 
     * @return the picklist/supplement id.
     */
    public Long getId();
}
