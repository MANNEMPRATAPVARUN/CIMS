package ca.cihi.cims.web.controller.search.modelvalue;

import org.springframework.ui.Model;

import ca.cihi.cims.model.search.Search;

/**
 * Interface that represents an object who's primary role is to populate default values into the {@link Model} object
 * for a specific search type
 * 
 * @author rshnaper
 * 
 */
public interface ModelValuesProvider {

	/**
	 * Populates the {@link Model} object with various default values that will be used for rendering
	 * 
	 * @param model
	 * @param search
	 */
	public void populate(Model model, Search search);
}
