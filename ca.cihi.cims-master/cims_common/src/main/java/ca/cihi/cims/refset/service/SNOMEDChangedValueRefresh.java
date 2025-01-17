package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;

import org.springframework.util.CollectionUtils;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.framework.domain.ConceptQueryCriteria;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.refset.dto.SCTDescriptionChangeDTO;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.Value;

public abstract class SNOMEDChangedValueRefresh extends SNOMEDValueRefresh {

	List<SCTDescriptionChangeDTO> changedValues;
	List<Concept> columnIds;
	String columnValueCheck;

	@Override
	void process(Refset oldRefset, Refset newRefset) {
		List<SCTDescriptionChangeDTO> changedValues = findChangedValues(oldRefset.getSCTVersionCode(),
				newRefset.getSCTVersionCode(), context);
		changedValues.stream().filter(changed -> changed.getNewId() != null)
				.forEach(changed -> processChangedValue(changed));
		processExpiredTerm(changedValues.stream().filter(changed -> changed.getNewId() == null)
				.map(changed -> changed.getOldId()).collect(toList()));

	}

	void processExpiredTerm(List<Long> expiredTermIds) {
        if (columnIds==null || columnIds.size()==0 ){
        	return;
        }
		if (!CollectionUtils.isEmpty(expiredTermIds)) {
			ConceptQueryCriteria expiredValueQuery = buildValueQuery(columnIds, expiredTermIds);
			List<Concept> expiredValues = Concept.findConceptsByClassAndValues(context.getContextId(),
					expiredValueQuery);

			for (Concept expiredValue : expiredValues) {
				expiredValue.remove();
			}

		}
	}

	abstract List<SCTDescriptionChangeDTO> findChangedValues(String oldSctVersionCode, String newSctVersionCode2,
			Context context);

	void processChangedValue(SCTDescriptionChangeDTO dto) {
		if (columnIds==null || columnIds.size()==0 || dto==null || dto.getOldId()==null) {
			return;
		}
		ConceptQueryCriteria fsnValueQuery = buildValueQuery(columnIds, Arrays.asList(dto.getOldId()));

		List<Concept> fsnValues = Concept.findConceptsByClassAndValues(context.getContextId(), fsnValueQuery);
		fsnValues.forEach(value -> {
			Value realValue = (Value) value;
			if (!realValue.getTextValue().equals(columnValueCheck)) {
				// sct_description and sct_fully_specified_name values
				if (!realValue.getIdValue().toString().equals(realValue.getTextValue())) {
					realValue.setTextValue(dto.getNewDescription());
				} else {
					realValue.setTextValue(dto.getNewId().toString());
				}
			}
			realValue.setIdValue(dto.getNewId());

		});
	}
}
