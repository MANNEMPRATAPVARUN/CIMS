package ca.cihi.cims.refset.dto;

import java.io.Serializable;
import java.util.Map;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.refset.service.concept.LightRecord;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:24:12 PM
 */
public class LightRecordDTO implements LightRecord, Serializable {

	private static final long serialVersionUID = 1L;

	private ElementIdentifier recordIdentifier;

	private Map<Long, ValueDTO> values;

	public LightRecordDTO() {

	}

	@Override
	public ElementIdentifier getRecordIdentifier() {
		return recordIdentifier;
	}

	@Override
	public Map<Long, ValueDTO> getValues() {
		return values;
	}

	public void setRecordIdentifier(ElementIdentifier recordIdentifier) {
		this.recordIdentifier = recordIdentifier;
	}

	public void setValues(Map<Long, ValueDTO> values) {
		this.values = values;
	}

}