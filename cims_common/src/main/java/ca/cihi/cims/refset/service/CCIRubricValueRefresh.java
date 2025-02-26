package ca.cihi.cims.refset.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import ca.cihi.cims.framework.domain.Concept;
import ca.cihi.cims.refset.service.concept.Refset;

public class CCIRubricValueRefresh extends CCIValueRefresh {

	private static final String RUBRIC = "Rubric";

	@Override
	List<Long> findDisabledConceptIds(Refset oldRefset, Refset newRefset) {
		return Concept.findDisabledConceptIds(oldRefset.getCCIContextId(), newRefset.getCCIContextId(), RUBRIC).stream()
				.map(concept -> concept.getElementId()).collect(toList());
	}

}
