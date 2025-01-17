package ca.cihi.cims.refset.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.handler.SNOMEDHandler;
import ca.cihi.cims.refset.service.concept.Refset;

public abstract class SNOMEDValueRefresh extends AbstractValueRefresh {
	protected List<Concept> columnIds;
	protected SNOMEDHandler snomedHandler;

	@Override
	boolean readyForRefresh(Refset oldRefset, Refset newRefset) {
		snomedHandler = ApplicationContextProvider.getApplicationContext().getBean(SNOMEDHandler.class);
		List<String> columnTypes = new ArrayList<>();
		columnTypes.add(ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay());
		columnIds = findColumns(context, columnTypes);
		return (!oldRefset.getSCTVersionCode().equals(newRefset.getSCTVersionCode())
				&& !CollectionUtils.isEmpty(columnIds));
	}

}
