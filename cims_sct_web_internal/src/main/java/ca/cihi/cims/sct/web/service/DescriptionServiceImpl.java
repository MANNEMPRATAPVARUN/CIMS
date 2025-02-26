package ca.cihi.cims.sct.web.service;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.sct.web.domain.ConceptType;
import ca.cihi.cims.sct.web.domain.SCTVersion;
import ca.cihi.cims.sct.web.domain.Term;
import ca.cihi.cims.sct.web.mapper.DescriptionMapper;

@Service
@Transactional
public class DescriptionServiceImpl implements DescriptionService{

    private DescriptionMapper descriptionMapper;

    public DescriptionMapper getDescriptionMapper() {
		return descriptionMapper;
	}

	public void setDescriptionMapper(DescriptionMapper descriptionMapper) {
		this.descriptionMapper = descriptionMapper;
	}

	public Term getDesciptionById(long id, String version) {
        return descriptionMapper.getDespByDespId(id, version);
    }

    public List<Term> getDesciptionByConceptId(long conceptId, String version) {
        return descriptionMapper.getDespByConceptId(conceptId, version);
    }
    
    
    public List<Term> getDesciptionByTerm(List<String> terms, String version, String conceptType) {
        return descriptionMapper.getDespByTerm(terms, version, conceptType.trim().equals("")?null:conceptType);
    }


	public List<Term> getDesciptionByConceptIdOrDespId(long id, String version, String conceptType) {
        return descriptionMapper.getDespByConceptIdOrDespId(id, version, conceptType.trim().equals("")?null:conceptType);
	}

	@Override
	public List<String> getAllVersons() {
        return descriptionMapper.getAllVersions();
	}

	@Override
	public List<SCTVersion> getSCTVersionList(String status) throws Exception {
		return descriptionMapper.getVersionsByStatus(status);
	}

	@Override
	public List<ConceptType> getConceptTypes() {
		return descriptionMapper.getConceptTypes();
	}

}
