package ca.cihi.cims.service;

public interface TransformQualifierlistService extends TransformationService {

	/**
	 * Transform the given the xml string
	 * 
	 * @param xmlString
	 *            String the given xml string
	 * @return String
	 */
	String transformQualifierlistString(final String xmlString);

}
