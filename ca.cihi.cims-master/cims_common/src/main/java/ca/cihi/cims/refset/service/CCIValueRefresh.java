package ca.cihi.cims.refset.service;

import java.util.List;

import ca.cihi.cims.refset.dto.ValueDTO;
import ca.cihi.cims.refset.enums.ColumnCategory;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.service.concept.Refset;

public abstract class CCIValueRefresh extends CIMSValueRefresh {

	@Override
	List<ValueDTO> findChangedValues(Refset oldRefset, Refset newRefset, Long idValueClasssId) {
		return refsetControlHandler.findChangedCIMSValues(oldRefset.getCCIContextId(), newRefset.getCCIContextId(),
				context.getContextId(), idValueClasssId);
	}

	@Override
	boolean readyForRefresh(Refset oldRefset, Refset newRefset) {
		columnTypes.add(ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay());
		columnCategory = ColumnCategory.CCIREFRESHABLE;
		return ((oldRefset.getCCIContextId() != newRefset.getCCIContextId()));
	}

}
