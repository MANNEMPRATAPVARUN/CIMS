package ca.cihi.cims.refset.service.concept;

import java.util.List;

/**
 * @author lzhu
 * @version 1.0
 * @created 23-Jun-2016 1:39:54 PM
 */
public interface Record extends RefsetConcept {

	public List<Value> listValues() throws Exception;

	String getConceptCode();

}