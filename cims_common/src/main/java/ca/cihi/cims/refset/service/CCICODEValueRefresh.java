package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.refset.service.concept.Refset;

public class CCICODEValueRefresh extends CCIValueRefresh {

	private static final String CCICODE = "CCICODE";

	@Override
	List<Long> findDisabledConceptIds(Refset oldRefset, Refset newRefset) {
		return Concept.findDisabledConceptIds(oldRefset.getCCIContextId(), newRefset.getCCIContextId(), CCICODE)
				.stream().map(concept -> concept.getElementId()).collect(toList());
	}

}
