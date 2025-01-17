package ca.cihi.cims.sct.web.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.sct.web.domain.ConceptType;
import ca.cihi.cims.sct.web.domain.SCTVersion;
import ca.cihi.cims.sct.web.domain.Term;
 
/**
 * User Data Access Object.
 * @author Dong Han
 */
public interface DescriptionMapper {
	
    //@Select("SELECT * FROM SNOMEDCT_CIHI_QUERY_ALL_DESC WHERE descriptionid = #{id}")
    public List<Term> getDespByConceptId(@Param("conceptId") long conceptId, @Param("version") String version);

    public Term getDespByDespId(@Param("despId") long conceptId, @Param("version") String version);
    
    //	@Select("SELECT * FROM SNOMEDCT_CIHI_QUERY_ALL_DESC WHERE lower(term) like CONCAT(#{term}, '%')")
    public List<Term> getDespByTerm(@Param("terms") List<String> terms, @Param("version") String version, @Param("conceptType") String conceptType);

	public List<Term> getDespByConceptIdOrDespId(@Param("id") long id, @Param("version") String version, @Param("conceptType") String conceptType);

	public List<String> getAllVersions();
	
	public String getVersionDescByCode();
	
	public List<SCTVersion> getVersionsByStatus(String status);
	
	public List<ConceptType> getConceptTypes();
	
}
