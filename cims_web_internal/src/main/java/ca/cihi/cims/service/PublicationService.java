package ca.cihi.cims.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.ComponentAndAttributeElementModel;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.PublicationSnapShot;
import ca.cihi.cims.model.prodpub.ReleaseType;

public interface PublicationService {

	boolean areBothClassificationFixedWidthFilesGenerated(GenerateReleaseTablesCriteria releaseTablesModel);

	boolean areBothClassificationTabFilesGenerated(GenerateReleaseTablesCriteria releaseTablesModel);

	@Transactional
	List<Long> closeYear(Long currentOpenYear, User currentUser);

	@Transactional
	void createPublicationRelease(PublicationRelease publicationRelease);

	@Transactional
	void createPublicationSnapShot(PublicationSnapShot publicationSnapShot);
	
	List<Long> addSingleYearContext(String icd, String cci);

	List<PublicationSnapShot> findAllLatestSnapShots();

	List<PublicationRelease> findAllReleases();

	List<PublicationSnapShot> findAllSnapShotsByContextId(Long contextId);

	List<PublicationSnapShot> findAllSuccessLatestSnapShots();

	PublicationRelease findLatestHighestSuccessPublicationReleaseByFiscalYear(String fiscalYear);

	PublicationRelease findLatestPublicationReleaseByFiscalYear(String fiscalYear);

	PublicationSnapShot findLatestSnapShotByContextId(Long contextId);

	PublicationRelease findLatestSuccessPublicationReleaseByFiscalYear(String fiscalYear);

	Integer findNextVersionNumber(String fiscalYear, ReleaseType releaseType, PublicationSnapShot icdSnapShot,
			PublicationSnapShot cciSnapShot);

	PublicationRelease findPublicationReleaseAndReleaseMsgTmpById(Long releaseId);

	PublicationRelease findPublicationReleaseById(Long releaseId);

	String findReleaseZipFileName(Long releaseId);

	PublicationSnapShot findSnapShotById(Long snapShotId);

	String findSnapShotZipFileName(Long snapShotId);

	List<ComponentAndAttributeElementModel> findUnusedComponentElements(Long openBaseContextId, Long closedBaseContextId);

	List<ComponentAndAttributeElementModel> findUnusedGenericAttributes(Long openBaseContextId, Long closedBaseContextId);

	List<ComponentAndAttributeElementModel> findUnusedReferenceValues(Long openBaseContextId, Long closedBaseContextId);

	@Transactional
	void generateClassificationTables(GenerateReleaseTablesCriteria generateTablesModel, User currentUser,
			String sessionId) throws Exception;

	String getCurrentProcessingFile(String sessionId);

	String getCurrentProcessingYear();

	boolean isGenerateFileProcessRunning(GenerateReleaseTablesCriteria generateTablesModel);

	@Transactional
	void notifyUsersToWrapupWork(GenerateReleaseTablesCriteria generateTablesModel, User currentUser);

	@Transactional
	void releaseClassificationTables(GenerateReleaseTablesCriteria releaseTablesModel, User currentUser,
			String sessionId) throws Exception;

	@Transactional
	void sendReleaseEmailNotification(PublicationRelease publicationRelease);

	@Transactional
	void unfreezeTabularChanges(Long baseContextId);

	@Transactional
	void updatePublicationRelease(PublicationRelease publicationRelease);

	@Transactional
	void updatePublicationSnapShot(PublicationSnapShot publicationSnapShot);

	@Transactional
	void updatePublicationSnapShotQANote(PublicationSnapShot publicationSnapShot);

}
