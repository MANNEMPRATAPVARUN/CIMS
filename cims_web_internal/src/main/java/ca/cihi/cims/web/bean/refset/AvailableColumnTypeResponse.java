package ca.cihi.cims.web.bean.refset;

import java.io.Serializable;
import java.util.List;

import ca.cihi.cims.refset.enums.ColumnType;

public class AvailableColumnTypeResponse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -7295767817821069117L;
	private List<ColumnType> availableColumnTypes;
	private boolean multipleColumnSublistExists;

	public List<ColumnType> getAvailableColumnTypes() {
		return availableColumnTypes;
	}

	public void setAvailableColumnTypes(List<ColumnType> availableColumnTypes) {
		this.availableColumnTypes = availableColumnTypes;
	}

	public boolean isMultipleColumnSublistExists() {
		return multipleColumnSublistExists;
	}

	public void setMultipleColumnSublistExists(boolean multipleColumnSublistExists) {
		this.multipleColumnSublistExists = multipleColumnSublistExists;
	}
}
