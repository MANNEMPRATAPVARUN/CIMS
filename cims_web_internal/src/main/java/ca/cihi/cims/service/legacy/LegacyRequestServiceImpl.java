package ca.cihi.cims.service.legacy;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import ca.cihi.cims.data.mapper.legacy.LegacyRequestMapper;
import ca.cihi.cims.model.changerequest.legacy.ChangeNature;
import ca.cihi.cims.model.changerequest.legacy.ChangeType;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestDetailModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestResultsModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestSearchModel;
import ca.cihi.cims.model.changerequest.legacy.RequestStatus;
import ca.cihi.cims.model.changerequest.legacy.Section;

import ca.cihi.cims.model.changerequest.legacy.Language;


public class LegacyRequestServiceImpl implements LegacyRequestService {

	private LegacyRequestMapper legacyRequestMapper;

	public void setLegacyRequestMapper(LegacyRequestMapper legacyRequestMapper) {
		this.legacyRequestMapper = legacyRequestMapper;
	}

	@Override
	@Cacheable("VERSION_CODE_CACHE")
	public List<String>  findVersionCodes(){
		return legacyRequestMapper.findVersionCodes();
	}

	@Override
	@Cacheable("CLASSIFICATION_TITLE_CACHE")
	public List<String>  findClassificationTitleCodes(){
		return legacyRequestMapper.findClassificationTitleCodes();
	}

	@Override
	@Cacheable("LANGUAGE_CACHE")
	public List<Language>  findLanguages(){
		return legacyRequestMapper.findLanguages();
	}

	@Override
	@Cacheable("REQUEST_STATUS_CACHE")
	public List<RequestStatus>  findDispositions(){
		return legacyRequestMapper.findDispositions();
	}

	@Override
	@Cacheable("SECTION_CACHE")
	public List<Section>  findSections() {
		return legacyRequestMapper.findSections();
	}

	@Override
	@Cacheable("CHANGE_NATURE_CACHE")
	public List<ChangeNature>  findChangeNatures() {
		return legacyRequestMapper.findChangeNatures();
	}

	@Override
	@Cacheable("CHANGE_TYPE_CACHE")
	public List<ChangeType>  findChangeTypes() {
		return legacyRequestMapper.findChangeTypes();
	}

	
	@Override
	public List<LegacyRequestResultsModel> findLegacyChangeRequestsBySearchModel(LegacyRequestSearchModel legacyRequestSearchModel) {
        return legacyRequestMapper.findLegacyChangeRequestsBySearchModel(legacyRequestSearchModel);
    }

	@Override
	public Integer findNumOfLegacyChangeRequestsBySearchModel(LegacyRequestSearchModel legacyRequestSearchModel) {
        return legacyRequestMapper.findNumOfLegacyChangeRequestsBySearchModel(legacyRequestSearchModel);
    }
	
	@Override
	public List<LegacyRequestDetailModel> findLegacyChangeRequestByRequestId(Long requestId) {
        return legacyRequestMapper.findLegacyChangeRequestByRequestId(requestId);
	}

	@Override
	public List<String> findLegacyChangeRequestAttachmentsByRequestId(Long requestId) {
        return legacyRequestMapper.findLegacyChangeRequestAttachmentsByRequestId(requestId);
	}

	@Override
	public List<String> findLegacyChangeRequestQueryRefNumsByRequestId(Long requestId) {
        return legacyRequestMapper.findLegacyChangeRequestQueryRefNumsByRequestId(requestId);
	}

	
}
