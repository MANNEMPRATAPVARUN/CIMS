package ca.cihi.cims.framework.domain;

import java.util.List;

import ca.cihi.cims.framework.ApplicationContextProvider;
import ca.cihi.cims.framework.dto.PropertyHierarchyDTO;
import ca.cihi.cims.framework.handler.SearchHandler;

public class Search {

	private Search() {

	}

	public static List<PropertyHierarchyDTO> searchHierarchyForProperties(Long startWithConceptId, Long contextId,
			Long relationshipId, List<Long> propertyClassIds, Integer level) {
		SearchHandler searchHandler = ApplicationContextProvider.getApplicationContext().getBean(SearchHandler.class);

		return searchHandler.searchHierarchyForProperties(startWithConceptId, contextId, relationshipId,
				propertyClassIds, level);
	}
}
