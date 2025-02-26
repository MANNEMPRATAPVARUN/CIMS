package ca.cihi.cims.refset.service.concept;

import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.refset.dto.ValueDTO;

/**
 * @author lzhu
 * @version 1.0
 * @created 27-Jun-2016 1:25:01 PM
 */
public interface LightRecord {

	public ElementIdentifier getRecordIdentifier();

	/**
	 * Key is column elementIdentifier
	 *
	 * @return
	 */
	public Map<Long, ValueDTO> getValues();

}