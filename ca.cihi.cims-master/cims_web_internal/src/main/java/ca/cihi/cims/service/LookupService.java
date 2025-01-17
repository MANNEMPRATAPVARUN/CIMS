package ca.cihi.cims.service;

import java.util.List;
import java.util.Map;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.LookupMapper;
import ca.cihi.cims.model.UserProfile;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguageDTO;
import ca.cihi.cims.model.meta.ChangeRequestAssignment;
import ca.cihi.cims.model.meta.NotificationTypeMeta;
import ca.cihi.cims.model.notification.NotificationTypeCode;

public interface LookupService {

	List<String> findBaseClassifications();

	ContextIdentifier findBaseContextIdentifierByClassificationAndYear(String baseClassification, String versionCode);

	List<ContextIdentifier> findBaseContextIdentifiers(String baseClassification);

	List<String> findBaseContextYears(String baseClassification);

	/**
	 * Used in QA Summary Metrics Report, find all context years open first and closed after
	 * 
	 * @param baseClassification
	 * @return
	 */
	List<String> findBaseContextYearsReport(String baseClassification);

	Long findCCICurrentOpenYear();

	// List<Map<String, Object>> findChangeRequestLanguages();
	List<ChangeRequestLanguageDTO> findChangeRequestLanguages();

	List<ContextIdentifier> findClosedBaseContextIdentifiersReport(String classification, String isVersionYear);

	ContextIdentifier findContextIdentificationById(Long baseContextId);

	/**
	 * Finds the context id of the current open year for the specified classification
	 * 
	 * @param classification
	 * @return
	 */
	public Long findCurrentOpenContextByClassification(String classification);

	Long findCurrentOpenYear(String classification);

	Long findICDCurrentOpenYear();

	List<ContextIdentifier> findNonClosedBaseContextIdentifiers(String baseClassification);

	List<ContextIdentifier> findNonClosedBaseContextIdentifiersIndex(String baseClassification);

	List<ContextIdentifier> findNonClosedBaseContextIdentifiersReport(String baseClassification, String isVersionYear);

	ContextIdentifier findOpenContextByChangeRquestId(Long changeRequestId);

	List<String> findOpenVersionYears(String baseClassification);

	List<ContextIdentifier> findPriorBaseContextIdentifiers(String baseClassification, String currentYear);

	/**
	 * Finds all the prior base contexts for the specified classification and context id. The contexts are ordered by
	 * year in descending order
	 * 
	 * @param classification
	 * @param contextId
	 * @param versionYearsOnly
	 * @return
	 */
	List<ContextIdentifier> findPriorBaseContextIdentifiersByClassificationAndContext(String classification,
			Long contextId, boolean versionYearsOnly);

	/**
	 * Used by viewservice to find the prior context id
	 * 
	 * @param classification
	 * @param contextId
	 * @return
	 */
	Long findPriorContextId(String classification, Long contextId, boolean changeRequest);

	List<UserProfile> findUserProfiles();

	List<String> findVersionYears(String baseClassification);

	List<String> getSearchPatternTopics(String classification, String searchString, int maxAutocompleteSearchResults);

	List<String> getSearchPatternTopics2(String classification, String searchString, int maxAutocompleteSearchResults);

	Map<String, List<ChangeRequestAssignment>> loadAllChangeRequestAssignmentMetaDataToMap();

	Map<NotificationTypeCode, NotificationTypeMeta> loadAllNotificationTypeMetaToMap();

	void setLookupMapper(LookupMapper lookupMapper);

}
