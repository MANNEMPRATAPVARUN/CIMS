package ca.cihi.cims.refset.service.concept;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:12:50 PM
 */
public interface Value extends RefsetConcept {

	public Long getIdValue();

	public String getTextValue();

	/**
	 *
	 * @param idValue
	 */
	public void setIdValue(Long idValue);

	/**
	 *
	 * @param textValue
	 */
	public void setTextValue(String textValue);

	/**
	 * Return the column elementId which this value is describedBy
	 * 
	 * @return
	 */
	Long getDescribedBy();

}