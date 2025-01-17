package ca.cihi.cims.refset.service;

import java.util.ArrayList;
import java.util.List;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.refset.dto.SCTDescriptionChangeDTO;
import ca.cihi.cims.refset.enums.ColumnType;

public class SNOMEDChangedPreferredValueRefresh extends SNOMEDChangedValueRefresh {

	List<Concept> preferredColumnIds;
	static final String PREFERRED_NAME = "Preferred";

	@Override
	List<SCTDescriptionChangeDTO> findChangedValues(String oldSCTVersionCode, String newSCTVersionCode,
			Context context) {
		List<String> preferredColumnTypes = new ArrayList<>();
		preferredColumnTypes.add(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());
		preferredColumnTypes.add(ColumnType.SCT_DESCRIPTION_ID.getColumnTypeDisplay());
		preferredColumnTypes.add(ColumnType.SCT_DESCRIPTION_TYPE.getColumnTypeDisplay());
		preferredColumnTypes.add(ColumnType.SCT_PREFFERED_TERM.getColumnTypeDisplay());
		preferredColumnTypes.add(ColumnType.SCT_PREFFERED_TERM_ID.getColumnTypeDisplay());
		columnIds = findColumns(context, preferredColumnTypes);
		columnValueCheck = PREFERRED_NAME;
		return snomedHandler.findChangedPreferreds(oldSCTVersionCode, newSCTVersionCode, context.getContextId(),
				context.getBaseClassificationName());
	}
}
