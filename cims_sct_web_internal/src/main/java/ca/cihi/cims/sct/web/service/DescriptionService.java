package ca.cihi.cims.sct.web.service;

import java.util.List;

import ca.cihi.cims.sct.web.domain.ConceptType;
import ca.cihi.cims.sct.web.domain.SCTVersion;
import ca.cihi.cims.sct.web.domain.Term;

public interface DescriptionService {

    Term getDesciptionById(long id, String version);

    List<Term> getDesciptionByConceptId(long conceptId, String version);
    
    List<Term> getDesciptionByTerm(List<String> words, String version, String conceptType);        

	List<Term> getDesciptionByConceptIdOrDespId(long id, String version, String conceptType);
	
	List<String> getAllVersons();
	
	List<SCTVersion> getSCTVersionList(String status) throws Exception;
	
	List<ConceptType> getConceptTypes();
	
}
