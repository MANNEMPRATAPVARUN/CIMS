package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.model.changerequest.IncompleteProperty;

public interface IncompleteReportService {

	List<IncompleteProperty> checkIndexConcept(final Long contextId, final Long conceptId, final String indexDesc);

	List<IncompleteProperty> checkSupplementConcept(final Long contextId, final Long conceptId, final String indexDesc);

	List<IncompleteProperty> checkTabularConcept(final Long contextId, final Long conceptId,
			final boolean isVersionYear, final String conceptCode);

}
