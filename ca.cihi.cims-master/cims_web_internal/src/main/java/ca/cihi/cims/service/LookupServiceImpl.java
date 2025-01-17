package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.LookupMapper;
import ca.cihi.cims.data.mapper.NotificationMapper;
import ca.cihi.cims.model.UserProfile;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguageDTO;
import ca.cihi.cims.model.meta.ChangeRequestAssignment;
import ca.cihi.cims.model.meta.NotificationTypeMeta;
import ca.cihi.cims.model.notification.NotificationTypeCode;



public class LookupServiceImpl implements LookupService {

	private LookupMapper lookupMapper;
	private NotificationMapper notificationMapper;

	// ----------------------------------------------------------

	@Override
	public List<String> findBaseClassifications() {
		return lookupMapper.findBaseClassifications();
	}

	@Override
	public ContextIdentifier findBaseContextIdentifierByClassificationAndYear(String baseClassification,
			String versionCode) {
		return lookupMapper.findBaseContextIdentifierByClassificationAndYear(baseClassification, versionCode);
	}

	@Override
	public List<ContextIdentifier> findBaseContextIdentifiers(String baseClassification) {
		return lookupMapper.findBaseContextIdentifiers(baseClassification);
	}

	@Override
	public List<String> findBaseContextYears(String baseClassification) {
		return lookupMapper.findBaseContextYears(baseClassification);
	}

	@Override
	public List<String> findBaseContextYearsReport(String baseClassification) {
		return lookupMapper.findBaseContextYearsReport(baseClassification);
	}

	@Override
	public Long findCCICurrentOpenYear() {
		return lookupMapper.findCCICurrentOpenYear();
	}

	@Override
	@Cacheable("CHANGE_REQUEST_LANGUAGE_CACHE")
	// public List<Map<String, Object>> findChangeRequestLanguages() {
	public List<ChangeRequestLanguageDTO> findChangeRequestLanguages() {
		return lookupMapper.findChangeRequestLanguages();
	}

	@Override
	public List<ContextIdentifier> findClosedBaseContextIdentifiersReport(String classification, String isVersionYear) {
		return lookupMapper.findClosedBaseContextIdentifiersReport(classification, isVersionYear);
	}

	@Override
	public ContextIdentifier findContextIdentificationById(Long baseContextId) {
		return lookupMapper.findContextIdentificationById(baseContextId);
	}

	@Override
	public Long findCurrentOpenContextByClassification(String classification) {
		return lookupMapper.findCurrentOpenContextByClassification(classification);
	}

	@Override
	public Long findCurrentOpenYear(String classification) {
		if ("CCI".equals(classification)) {
			return findCCICurrentOpenYear();
		} else {
			return findICDCurrentOpenYear();
		}
	}

	@Override
	public Long findICDCurrentOpenYear() {
		return lookupMapper.findICDCurrentOpenYear();
	}

	@Override
	public List<ContextIdentifier> findNonClosedBaseContextIdentifiers(String baseClassification) {
		return lookupMapper.findNonClosedBaseContextIdentifiers(baseClassification);
	}

	@Override
	public List<ContextIdentifier> findNonClosedBaseContextIdentifiersIndex(String baseClassification) {
		return lookupMapper.findNonClosedBaseContextIdentifiersIndex(baseClassification);
	}

	@Override
	public List<ContextIdentifier> findNonClosedBaseContextIdentifiersReport(String baseClassification,
			String isVersionYear) {
		return lookupMapper.findNonClosedBaseContextIdentifiersReport(baseClassification, isVersionYear);
	}

	@Override
	public ContextIdentifier findOpenContextByChangeRquestId(Long changeRequestId) {
		ContextIdentifier id = lookupMapper.findOpenContextByChangeRquestId(changeRequestId);
		return id;
	}

	@Override
	public List<String> findOpenVersionYears(String baseClassification) {
		return lookupMapper.findOpenVersionYears(baseClassification);
	}

	@Override
	public List<ContextIdentifier> findPriorBaseContextIdentifiers(String baseClassification, String currentYear) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentYear", currentYear);
		params.put("baseClassification", baseClassification);
		return lookupMapper.findPriorBaseContextIdentifiers(params);
	}

	@Override
	public List<ContextIdentifier> findPriorBaseContextIdentifiersByClassificationAndContext(String classification,
			Long contextId, boolean versionYearsOnly) {
		return lookupMapper.findPriorBaseContextIdentifiersByClassificationAndContext(classification, contextId,
				versionYearsOnly);
	}

	@Override
	public Long findPriorContextId(String classification, Long contextId, boolean changeRequest) {
		return lookupMapper.findPriorContextId(classification, contextId, changeRequest);
	}

	@Override
	public List<UserProfile> findUserProfiles() {
		return lookupMapper.findUserProfiles();
	}

	@Override
	public List<String> findVersionYears(String baseClassification) {
		return lookupMapper.findVersionYears(baseClassification);
	}

	public NotificationMapper getNotificationMapper() {
		return notificationMapper;
	}

	@Override
	public List<String> getSearchPatternTopics(String classification, String searchString,
			int maxAutocompleteSearchResults) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("searchString", searchString + "%");
		parameters.put("maxResults", maxAutocompleteSearchResults);
		return lookupMapper.getSearchPatternTopics(parameters);
	}

	@Override
	public List<String> getSearchPatternTopics2(String classification, String searchString,
			int maxAutocompleteSearchResults) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classification);
		parameters.put("searchString", searchString + "%");
		parameters.put("maxResults", maxAutocompleteSearchResults);
		return lookupMapper.getSearchPatternTopics2(parameters);
	}

	@Override
	@Cacheable("CHANGEREQUEST_ASSIGNMENT_META_CACHE")
	public Map<String, List<ChangeRequestAssignment>> loadAllChangeRequestAssignmentMetaDataToMap() {
		List<ChangeRequestAssignment> changeRequestAssignmentMetas = lookupMapper
				.findAllChangeRequestAssignmentMetaData();
		Map<String, List<ChangeRequestAssignment>> changeRequestAssignmentMetaMap = new HashMap<String, List<ChangeRequestAssignment>>();
		for (ChangeRequestAssignment changeRequestAssignmentMeta : changeRequestAssignmentMetas) {
			StringBuilder sb_key = new StringBuilder(String.valueOf(changeRequestAssignmentMeta.getStatus()
					.getStatusId()));
			sb_key.append(changeRequestAssignmentMeta.getLanguage().getCode());
			String key = sb_key.toString();

			if (changeRequestAssignmentMetaMap.containsKey(key)) {
				List<ChangeRequestAssignment> changeRequestAssignments = changeRequestAssignmentMetaMap.get(key);
				changeRequestAssignments.add(changeRequestAssignmentMeta);
			} else {
				List<ChangeRequestAssignment> changeRequestAssignments = new ArrayList<ChangeRequestAssignment>();
				changeRequestAssignments.add(changeRequestAssignmentMeta);
				changeRequestAssignmentMetaMap.put(key, changeRequestAssignments);
			}
		}

		return changeRequestAssignmentMetaMap;
	}

	@Override
	@Cacheable("NOTIFICATION_META_CACHE")
	public Map<NotificationTypeCode, NotificationTypeMeta> loadAllNotificationTypeMetaToMap() {
		List<NotificationTypeMeta> notificationTypeMetas = notificationMapper.findAllNotificationTypeMeta();
		Map<NotificationTypeCode, NotificationTypeMeta> notificationTypeMap = new HashMap<NotificationTypeCode, NotificationTypeMeta>();
		for (NotificationTypeMeta notificationTypeMeta : notificationTypeMetas) {
			notificationTypeMap.put(notificationTypeMeta.getNotificationTypeCode(), notificationTypeMeta);
		}

		return notificationTypeMap;
	}

	@Override
	public void setLookupMapper(LookupMapper lookupMapper) {
		this.lookupMapper = lookupMapper;
	}

	public void setNotificationMapper(NotificationMapper notificationMapper) {
		this.notificationMapper = notificationMapper;
	}
}
