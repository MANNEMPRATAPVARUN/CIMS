package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.enums.ColumnCategory;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.service.concept.Refset;

public class ICD10CAValueRefresh extends CIMSValueRefresh {

	private static final String CATEGORY = "Category";

	@Override
	boolean readyForRefresh(Refset oldRefset, Refset newRefset) {
		columnTypes.add(ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay());
		columnCategory = ColumnCategory.ICD10CAREFRESHABLE;
		return ((oldRefset.getICD10CAContextId() != newRefset.getICD10CAContextId()));
	}

	@Override
	List<Long> findDisabledConceptIds(Refset oldRefset, Refset newRefset) {
		return Concept
				.findDisabledConceptIds(oldRefset.getICD10CAContextId(), newRefset.getICD10CAContextId(), CATEGORY)
				.stream().map(concept -> concept.getElementId()).collect(toList());
	}

	@Override
	List<ValueDTO> findChangedValues(Refset oldRefset, Refset newRefset, Long idValueClasssId) {
		return refsetControlHandler.findChangedCIMSValues(oldRefset.getICD10CAContextId(),
				newRefset.getICD10CAContextId(), context.getContextId(), idValueClasssId);
	}

}
