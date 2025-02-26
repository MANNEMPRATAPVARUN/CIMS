package ca.cihi.cims.web.bean.search;

import ca.cihi.cims.model.search.SearchTypes;

/**
 * Object factory for search criteria bean objects
 * 
 * @author rshnaper
 * 
 */
public interface SearchCriteriaBeanFactory {
	/**
	 * Creates an object instance of type {@link SearchCriteriaBean} for the specified search type
	 * 
	 * @param type
	 * @return
	 */
	public <T extends SearchCriteriaBean> T createBean(SearchTypes type);

	/**
	 * Returns the class that maps to the specified search type
	 * 
	 * @param type
	 * @return
	 */
	public Class<? extends SearchCriteriaBean> getBeanClass(SearchTypes type);
}
