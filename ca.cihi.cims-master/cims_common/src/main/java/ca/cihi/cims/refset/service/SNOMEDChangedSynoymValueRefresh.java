package ca.cihi.cims.refset.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.refset.dto.SCTDescriptionChangeDTO;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.service.concept.Value;

public class SNOMEDChangedSynoymValueRefresh extends SNOMEDChangedValueRefresh {
	List<Concept> synonymPartColumnIds;

	@Override
	List<SCTDescriptionChangeDTO> findChangedValues(String oldSCTVersionCode, String newSCTVersionCode,
			Context context) {
		List<String> synonymColumnTypes = new ArrayList<>();
		synonymColumnTypes.add(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());
		synonymColumnTypes.add(ColumnType.SCT_DESCRIPTION_ID.getColumnTypeDisplay());
		synonymColumnTypes.add(ColumnType.SCT_DESCRIPTION_TYPE.getColumnTypeDisplay());
		synonymColumnTypes.add(ColumnType.SCT_SYNONYM_NAME.getColumnTypeDisplay());
		synonymColumnTypes.add(ColumnType.SCT_SYNONYM_ID.getColumnTypeDisplay());
		columnIds = findColumns(context, synonymColumnTypes);

		List<String> synonymPartColumnTypes = new ArrayList<>();
		synonymPartColumnTypes.add(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());
		synonymPartColumnTypes.add(ColumnType.SCT_SYNONYM_NAME.getColumnTypeDisplay());
		synonymPartColumnIds = findColumns(context, synonymPartColumnTypes);
		return snomedHandler.findChangedSynonym(oldSCTVersionCode, newSCTVersionCode, context.getContextId(),
				context.getBaseClassificationName());
	}

	@Override
	void processChangedValue(SCTDescriptionChangeDTO dto) {
		if (dto==null){
			return;
		}
		if (synonymPartColumnIds==null || synonymPartColumnIds.size()==0 || dto.getOldId()==null){
			return;
		}
		
		ConceptQueryCriteria synonymValueQuery = buildValueQuery(synonymPartColumnIds, Arrays.asList(dto.getOldId()));

		List<Concept> synonymValues = Concept.findConceptsByClassAndValues(context.getContextId(), synonymValueQuery);
		synonymValues.forEach(value -> {
			Value realValue = (Value) value;
			realValue.setTextValue(dto.getNewDescription());
		});
	}

}
