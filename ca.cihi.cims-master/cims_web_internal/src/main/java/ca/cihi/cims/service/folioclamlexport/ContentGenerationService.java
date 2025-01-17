package ca.cihi.cims.service.folioclamlexport;

import java.io.IOException;

import ca.cihi.cims.model.folioclamlexport.QueryCriteria;

public interface ContentGenerationService {

	/**
	 * Generate folio content based on context and concept provided
	 *
	 * @param request
	 *            See {@link QueryCriteria} for detail.
	 * @return The html URL for the content, could be a real html file or a html anchor
	 * @throws IOException
	 */
	String generateContent(QueryCriteria request) throws IOException;

	/**
	 * Cleanup output folder, initialize local cache
	 *
	 * @param year
	 * @throws IOException
	 */
	void initialize(String year, String classification, String language) throws IOException;
}
