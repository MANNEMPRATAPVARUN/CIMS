package ca.cihi.cims.refset.service.concept;

import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:13:19 PM
 */
public interface Supplement extends RefsetConcept {

	public String getCode();

	public byte[] getContent(Language language);

	public String getFilename();

	public String getName();

	public void setContent(byte[] content, Language language);

	/**
	 *
	 * @param filename
	 */
	public void setFilename(String filename);

	/**
	 *
	 * @param name
	 * @throws DuplicateNameException
	 */
	public void setName(String name) throws DuplicateCodeNameException;

}