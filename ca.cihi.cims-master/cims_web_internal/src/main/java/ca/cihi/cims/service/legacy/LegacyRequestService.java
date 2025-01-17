package ca.cihi.cims.service.legacy;

import java.util.List;

import ca.cihi.cims.data.mapper.legacy.LegacyRequestMapper;
import ca.cihi.cims.model.changerequest.legacy.ChangeNature;
import ca.cihi.cims.model.changerequest.legacy.ChangeType;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestDetailModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestSearchModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestResultsModel;
import ca.cihi.cims.model.changerequest.legacy.RequestStatus;
import ca.cihi.cims.model.changerequest.legacy.Language;
import ca.cihi.cims.model.changerequest.legacy.Section;

public interface LegacyRequestService {


	void setLegacyRequestMapper(LegacyRequestMapper legacyRequestMapper);

	public List<String>  findVersionCodes();
	public List<String>  findClassificationTitleCodes();
	public List<Language>  findLanguages();
	public List<RequestStatus>  findDispositions();
	public List<Section>  findSections();
	public List<ChangeNature>  findChangeNatures();
	public List<ChangeType>  findChangeTypes();
	
	public List<LegacyRequestResultsModel> findLegacyChangeRequestsBySearchModel(LegacyRequestSearchModel legacyRequestSearchModel);
	public Integer findNumOfLegacyChangeRequestsBySearchModel(LegacyRequestSearchModel legacyRequestSearchModel);
	public List<LegacyRequestDetailModel> findLegacyChangeRequestByRequestId(Long requestId);
	public List<String> findLegacyChangeRequestAttachmentsByRequestId(Long requestId);
	public List<String> findLegacyChangeRequestQueryRefNumsByRequestId(Long requestId);

}
