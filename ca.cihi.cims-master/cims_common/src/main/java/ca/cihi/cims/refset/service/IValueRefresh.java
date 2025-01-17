package ca.cihi.cims.refset.service;

import ca.cihi.cims.refset.service.concept.Refset;

@FunctionalInterface
public interface IValueRefresh {

	/**
	 * take old and new refset and refresh CIMS and SNOMED CT values
	 * 
	 * @param oldRefset
	 * @param newRefset
	 */
	void refreshValues(Refset oldRefset, Refset newRefset);
}
