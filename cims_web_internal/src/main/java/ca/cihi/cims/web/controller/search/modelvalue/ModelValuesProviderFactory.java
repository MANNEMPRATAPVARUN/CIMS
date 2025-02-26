package ca.cihi.cims.web.controller.search.modelvalue;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Factory interface for creating {@link ModelValuesProvider} instances
 * 
 * @author rshnaper
 * 
 */
public interface ModelValuesProviderFactory {
	/**
	 * Returns an instance of {@link ModelValuesProvider} object for the specified search type
	 * 
	 * @param type
	 * @return
	 */
	public ModelValuesProvider getModelValuesProviderFor(SearchTypes type);
}
