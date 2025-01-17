package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.model.Concept;
/**
 * Service tier for the tree view
 * 
 * @author szhang
 */
public interface ViewService {
	List<Concept> getTree(String parentKey,String chapterId);
	//List<ViewableConcept> getTreeNodes(String elementDomainId);
}