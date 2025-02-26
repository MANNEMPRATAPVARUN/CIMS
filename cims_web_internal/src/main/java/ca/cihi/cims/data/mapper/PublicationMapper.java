package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.ComponentAndAttributeElementModel;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.CodeDescriptionPublication;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.ReleaseType;
import ca.cihi.cims.model.prodpub.ValidationRuleSet;

public interface PublicationMapper {

	List<PublicationSnapShot> findAllLatestSnapShots();

	List<PublicationRelease> findAllReleases();

	List<PublicationSnapShot> findAllSnapShotsByContextId(Long contextId);

	List<PublicationRelease> findAllSuccessDescentOrderPublicationReleasesByFiscalYear(String fiscalYear);

	List<PublicationSnapShot> findAllSuccessLatestSnapShots();

	List<CodeDescriptionPublication> findCCIBlkDesc(@Param("contextId") Long contextId,
			@Param("cciClassIdCode") Long cciClassIdCode, @Param("cciClassIdShortTitle") Long cciClassIdShortTitle,
			@Param("cciClassIdLongTitle") Long cciClassIdLongTitle, @Param("cciClassIdSection") Long cciClassIdSection,
			@Param("cciClassIdBlock") Long cciClassIdBlock, @Param("cciClassIdGroup") Long cciClassIdGroup,
			@Param("languageCode") String languageCode);

	List<ValidationRuleSet> findCCIChildCodes(Map<String, Object> params);

	List<CodeDescriptionPublication> findCCICodeDesc(@Param("contextId") Long contextId,
			@Param("cciClassIdCode") Long cciClassIdCode, @Param("cciClassIdShortTitle") Long cciClassIdShortTitle,
			@Param("cciClassIdLongTitle") Long cciClassIdLongTitle, @Param("cciClassIdCCICode") Long cciClassIdCCICode,
			@Param("languageCode") String languageCode);

	List<CodeDescriptionPublication> findCCIRubricDesc(@Param("contextId") Long contextId,
			@Param("cciClassIdCode") Long cciClassIdCode, @Param("cciClassIdShortTitle") Long cciClassIdShortTitle,
			@Param("cciClassIdLongTitle") Long cciClassIdLongTitle, @Param("cciClassIdRubric") Long cciClassIdRubric,
			@Param("languageCode") String languageCode);

	List<ValidationRuleSet> findCCIValidationRules(Map<String, Object> params);

	Long findConceptIdForChapter22(@Param("contextId") Long contextId, @Param("icdClassIdCode") Long icdClassIdCode,
			@Param("icdClassIdChapter") Long icdClassIdChapter, @Param("languageCode") String languageCode);

	/*
	 * List<CodeDescriptionPublication> findICDBlkDesc(@Param("contextId") Long contextId,
	 * 
	 * @Param("icdClassIdCode") Long icdClassIdCode, @Param("icdClassIdShortTitle") Long icdClassIdShortTitle,
	 * 
	 * @Param("icdClassIdLongTitle") Long icdClassIdLongTitle, @Param("icdClassIdChapter") Long icdClassIdChapter,
	 * 
	 * @Param("icdClassIdBlock") Long icdClassIdBlock, @Param("languageCode") String languageCode);
	 */
	List<CodeDescriptionPublication> findICDBlkDescWithoutChapter22(@Param("contextId") Long contextId,
			@Param("icdClassIdCode") Long icdClassIdCode, @Param("icdClassIdShortTitle") Long icdClassIdShortTitle,
			@Param("icdClassIdLongTitle") Long icdClassIdLongTitle, @Param("icdClassIdChapter") Long icdClassIdChapter,
			@Param("icdClassIdBlock") Long icdClassIdBlock, @Param("chapter22ConceptId") Long chapter22ConceptId,
			@Param("languageCode") String languageCode);

	List<CodeDescriptionPublication> findICDCat1Desc(@Param("contextId") Long contextId,
			@Param("icdClassIdCode") Long icdClassIdCode, @Param("icdClassIdShortTitle") Long icdClassIdShortTitle,
			@Param("icdClassIdLongTitle") Long icdClassIdLongTitle,
			@Param("icdClassIdCategory") Long icdClassIdCategory, @Param("languageCode") String languageCode);

	List<ValidationRuleSet> findICDChildCodes(Map<String, Object> params);

	/*
	 * CSRE-1088 List<CodeDescriptionPublication> findICDCodeAndMorphologyDesc(@Param("contextId") Long contextId,
	 * 
	 * @Param("icdClassIdCode") Long icdClassIdCode, @Param("icdClassIdShortTitle") Long icdClassIdShortTitle,
	 * 
	 * @Param("icdClassIdLongTitle") Long icdClassIdLongTitle,
	 * 
	 * @Param("icdClassIdCategory") Long icdClassIdCategory, @Param("languageCode") String languageCode);
	 */

	List<CodeDescriptionPublication> findICDCodeDesc(@Param("contextId") Long contextId,
			@Param("icdClassIdCode") Long icdClassIdCode, @Param("icdClassIdShortTitle") Long icdClassIdShortTitle,
			@Param("icdClassIdLongTitle") Long icdClassIdLongTitle,
			@Param("icdClassIdCategory") Long icdClassIdCategory, @Param("languageCode") String languageCode);

	/*
	 * List<CodeDescriptionPublication> findICDCodeDesc(@Param("contextId") Long contextId,
	 * 
	 * @Param("icdClassIdCode") Long icdClassIdCode, @Param("icdClassIdShortTitle") Long icdClassIdShortTitle,
	 * 
	 * @Param("icdClassIdLongTitle") Long icdClassIdLongTitle,
	 * 
	 * @Param("icdClassIdCategory") Long icdClassIdCategory, @Param("languageCode") String languageCode);
	 */
	List<ValidationRuleSet> findICDValidationRules(Map<String, Object> params);

	PublicationRelease findLatestPublicationReleaseByFiscalYear(String fiscalYear);

	PublicationRelease findLatestPublicationReleaseByFiscalYearAndReleaseType(@Param("fiscalYear") String fiscalYear,
			@Param("releaseType") ReleaseType releaseType);

	PublicationSnapShot findLatestSnapShotByContextId(Long contextId);

	PublicationSnapShot findLatestSuccessFixedWidthSnapShotByContextId(Long contextId);

	PublicationRelease findLatestSuccessPublicationReleaseByFiscalYear(String fiscalYear);

	PublicationSnapShot findLatestSuccessTabSnapShotByContextId(Long contextId);

	PublicationRelease findPublicationReleaseById(Long releaseId);

	PublicationSnapShot findSnapShotById(Long snapShotId);

	Integer findSnapShotSeqNumber(Long contextId);

	List<ComponentAndAttributeElementModel> findUnusedComponentElements(
			@Param("openBaseContextId") Long openBaseContextId, @Param("closedBaseContextId") Long closedBaseContextId);

	List<ComponentAndAttributeElementModel> findUnusedGenericAttributes(
			@Param("openBaseContextId") Long openBaseContextId, @Param("closedBaseContextId") Long closedBaseContextId);

	List<ComponentAndAttributeElementModel> findUnusedReferenceValues(
			@Param("openBaseContextId") Long openBaseContextId, @Param("closedBaseContextId") Long closedBaseContextId);

	Integer findVersionCodeNumber(@Param("fiscalYear") String fiscalYear, @Param("releaseType") ReleaseType releaseType);

	CCIReferenceAttribute getCCIReferenceAttribute(Map<String, Object> params);

	List<CCIReferenceAttribute> getCCIReferenceAttributes(Map<String, Object> params);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	int insertPublicationRelease(PublicationRelease publicationRelease);

	void insertPublicationReleaseSnapShot(PublicationRelease publicationRelease);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	int insertPublicationSnapShot(PublicationSnapShot publicationSnapShot);

	Long synchronizeCloseYear();

	Long synchronizeRelease();

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	void updatePublicationRelease(PublicationRelease publicationRelease);

	void updatePublicationReleaseNote(PublicationRelease publicationRelease);

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	void updatePublicationSnapShot(PublicationSnapShot publicationSnapShot);

	void updatePublicationSnapShotQANote(PublicationSnapShot publicationSnapShot);

}
