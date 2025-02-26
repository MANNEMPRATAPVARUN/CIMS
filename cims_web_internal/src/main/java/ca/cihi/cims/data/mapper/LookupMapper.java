package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.model.UserProfile;
import ca.cihi.cims.model.changerequest.ChangeRequestLanguageDTO;
import ca.cihi.cims.model.meta.ChangeRequestAssignment;

public interface LookupMapper {

	List<ChangeRequestAssignment> findAllChangeRequestAssignmentMetaData();

	String findAuxTableValueById(Long auxTableValueId);

	List<String> findBaseClassifications();

	ContextIdentifier findBaseContextIdentifierByClassificationAndYear(
			@Param("baseClassification") String baseClassification, @Param("versionCode") String versionCode);

	List<ContextIdentifier> findBaseContextIdentifiers(String baseClassification);

	List<String> findBaseContextYears(String baseClassification);

	List<String> findBaseContextYearsReport(String baseClassification);

	Long findCCICurrentOpenYear();

	String findChangeRequestLanguageDescByCode(String languageCode);

	List<ChangeRequestLanguageDTO> findChangeRequestLanguages();

	Long findClassIdByClassificationAndClassName(@Param("baseClassification") String baseClassification,
			@Param("className") String className);

	List<ContextIdentifier> findClosedBaseContextIdentifiersReport(@Param("classification") String baseClassification,
			@Param("isVersionYear") String isVersionYear);

	ContextIdentifier findContextIdentificationById(Long baseContextId);

	/**
	 * Returns the context id for the current open year for the specified classification
	 * 
	 * @param classification
	 * @return
	 */
	public Long findCurrentOpenContextByClassification(String classification);

	String findDistributionNameById(Long distributionId);

	Long findICDCurrentOpenYear();

	ContextIdentifier findLargestOpenVersionContextIdentifier(@Param("baseClassification") String baseClassification);

	Long findLargestVersionOpenYear();

	List<ContextIdentifier> findNonClosedBaseContextIdentifiers(String baseClassification);

	List<ContextIdentifier> findNonClosedBaseContextIdentifiersIndex(String baseClassification);

	List<ContextIdentifier> findNonClosedBaseContextIdentifiersReport(
			@Param("classification") String baseClassification, @Param("isVersionYear") String isVersionYear);

	ContextIdentifier findOpenContextByChangeRquestId(Long changeRequestId);

	List<String> findOpenVersionYears(String baseClassification);

	List<ContextIdentifier> findPriorBaseContextIdentifiers(Map<String, Object> params);

	List<ContextIdentifier> findPriorBaseContextIdentifiersByClassificationAndContext(
			@Param("baseClassification") String classification, @Param("contextId") Long contextId,
			@Param("versionYearsOnly") boolean versionYearsOnly);

	Long findPriorContextId(@Param("baseClassification") String classification, @Param("contextId") Long contextId,
			@Param("changeRequest") boolean changeRequest);

	String findUserNameByUserId(Long userId);

	List<UserProfile> findUserProfiles();

	List<String> findVersionYears(String baseClassification);

	List<String> getSearchPatternTopics(Map<String, Object> parameters);

	List<String> getSearchPatternTopics2(Map<String, Object> parameters);

}
