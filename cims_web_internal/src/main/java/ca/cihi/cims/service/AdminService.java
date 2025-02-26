package ca.cihi.cims.service;

import java.util.List;

import ca.cihi.cims.exception.AlreadyInUseException;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.ClassificationDiagram;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;

public interface AdminService {

	void addUserToRecipients(Long userId, Long distributionId);

	void createDistribution(Distribution distribution);

	void createUser(User user);

	void deleteAux(Long auxTableValueId) throws AlreadyInUseException;

	void deleteDistribution(Long distributionId);

	void deleteUser(Long userId);

	List<AuxTableValue> findAllActiveAuxValuesForAuxCode(String auxiliaryCode);

	List<Long> findDistinctUserIdsInDistributionListIds(List<Long> distributionIds);

	List<User> findDistinctUsersInDistributionListIds(List<Long> distributionIds);

	List<Distribution> getAdvisorDistributionList();

	List<String> getAuxTableCodes();
	
	String getAuxTableIdByCode(String auxCode);

	List<AuxTableValue> getAuxTableValues(String auxCode);

	AuxTableValue getAuxTableValueByID(Long valueId);

	List<ClassificationDiagram> getDiagrams(String versionCode, String baseClassification);

	Distribution getDistributionByCode(String code);

	Distribution getDistributionById(Long distributionId);

	String getDistributionCodeById(Long distributionId);

	List<Distribution> getDistributionList();

	Long getDistributionListUserCountByDistributionId(Long distributionId);

	Long getDistributionListUserCountByUserId(Long userId);

	List<User> getIdleUsersByDistributionId(Long distributionId);

	Long getMaxDistributionId();

	Long getMaxUserId();

	List<Distribution> getNonReviewGroupDistributionList();

	List<User> getRecipientsByDistributionId(Long distributionId);

	List<Distribution> getReviewGroupList();

	User getUserById(Long userId);

	User getUserByUserName(String username);

	List<User> getUsers();

	List<User> getUsersReport();

	long insertAuxTableValue(AuxTableValue auxTableValue);

	boolean isDistributionInUse(long distributionId);

	//
	// RequestDetails getRequestById( Integer requestId);
	//
	// ConceptDetails getConceptById( Integer conceptId,String
	// fiscalYear,String language,String requestId);
	//

	//
	// void createConceptChangeCommand( ConceptDetails ctDetails);
	//
	// List<String> getAllVersions();
	//
	// void deleteChangeRequest( Integer requestId)throws
	// AlreadyInUseException;
	//
	//
	//
	// void updateChangeRequestStatus( Integer requestId,String status);
	//
	// void populateChanges( RequestDetails rsDetails, String version,
	// String language);
	// List<Long> findDistinctUserIdsInDistributionListCodes(List<String> distributionCodes);

	void removeUserFromRecipientList(Long distributionId, Long userId);

	void removeUserFromRecipients(Long userId, Long distributionId);

	void updateAuxTableValue(AuxTableValue auxTableValue);

	void updateDistribution(Distribution distribution);

	void updateUser(User user);
	
	boolean isRefsetCodeNotUnique(String auxValueCode);
	
	boolean isRefsetNameNotUnique(String auxEngLable);

}
