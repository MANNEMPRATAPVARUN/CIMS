package ca.cihi.cims.refset.service;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.refset.dto.SCTDescriptionChangeDTO;
import ca.cihi.cims.refset.enums.ColumnType;

public class SNOMEDChangedFSNValueRefresh extends SNOMEDChangedValueRefresh {

	private static final String FSN_NAME = "Fully specified name";

	@Override
	List<SCTDescriptionChangeDTO> findChangedValues(String oldSCTVersionCode, String newSCTVersionCode,
			Context context) {
		List<String> fsnColumnTypes = new ArrayList<>();
		fsnColumnTypes.add(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());
		fsnColumnTypes.add(ColumnType.SCT_DESCRIPTION_ID.getColumnTypeDisplay());
		fsnColumnTypes.add(ColumnType.SCT_DESCRIPTION_TYPE.getColumnTypeDisplay());
		fsnColumnTypes.add(ColumnType.SCT_FULLY_SPECIFIED_NAME.getColumnTypeDisplay());
		fsnColumnTypes.add(ColumnType.SCT_FULLY_SPECIFIED_NAME_ID.getColumnTypeDisplay());
		columnIds = findColumns(context, fsnColumnTypes);
		columnValueCheck = FSN_NAME;
		return snomedHandler.findChangedFSNs(oldSCTVersionCode, newSCTVersionCode, context.getContextId(),
				context.getBaseClassificationName());
	}
}
