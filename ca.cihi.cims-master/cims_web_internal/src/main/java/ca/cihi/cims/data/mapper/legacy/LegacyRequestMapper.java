package ca.cihi.cims.data.mapper.legacy;

import java.util.List;

import ca.cihi.cims.model.changerequest.legacy.ChangeNature;
import ca.cihi.cims.model.changerequest.legacy.ChangeType;
import ca.cihi.cims.model.changerequest.legacy.Language;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestDetailModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestSearchModel;
import ca.cihi.cims.model.changerequest.legacy.LegacyRequestResultsModel;
import ca.cihi.cims.model.changerequest.legacy.RequestStatus;
import ca.cihi.cims.model.changerequest.legacy.Section;

public interface LegacyRequestMapper {

	List<String>  findVersionCodes();
	List<String>  findClassificationTitleCodes();
	List<Language>  findLanguages();
	List<RequestStatus>  findDispositions();
	List<Section>  findSections();
	List<ChangeNature>  findChangeNatures();
	List<ChangeType>  findChangeTypes();
	
	List<LegacyRequestResultsModel> findLegacyChangeRequestsBySearchModel(LegacyRequestSearchModel legacyRequestSearchModel);
	Integer findNumOfLegacyChangeRequestsBySearchModel(LegacyRequestSearchModel legacyRequestSearchModel);
	List<LegacyRequestDetailModel> findLegacyChangeRequestByRequestId(Long requestId);
	List<String> findLegacyChangeRequestAttachmentsByRequestId(Long requestId);
	List<String> findLegacyChangeRequestQueryRefNumsByRequestId(Long requestId);
}
