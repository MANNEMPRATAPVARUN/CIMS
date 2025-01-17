package ca.cihi.cims.data.mapper;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.exception.AlreadyInUseException;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.ClassificationDiagram;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.folioclamlexport.HtmlOutputLog;

public interface AdminMapper {

	void deleteAux(Long auxTableValueId) throws AlreadyInUseException;

	void deleteDistribution(Long distributionId);

	void deleteUser(Long userId);

	List<Long> findDistinctUserIdsInDistributionListCodes(List<String> distributionCodes);

	List<Long> findDistinctUserIdsInDistributionListIds(List<Long> distributionIds);

	List<User> findDistinctUsersInDistributionListIds(List<Long> distributionIds);

	int getActiveChangeRequestCountForDistribution(long distributionId);

	List<Distribution> getAdvisorDistributionList();

	List<String> getAuxTableCodes();

	String getAuxTableIdByCode(final String auxCode);

	List<AuxTableValue> getAuxTableValues(String auxCode);

	AuxTableValue getAuxTableValueByID(Long valueId);

	List<ClassificationDiagram> getDiagrams(Map<String, Object> map);

	Distribution getDistributionByCode(String code);

	Distribution getDistributionById(Long distributionId);

	String getDistributionCodeById(Long distributionId);

	List<Distribution> getDistributionList();

	Long getDistributionListUserCountByDistributionId(Long distributionId);

	Long getDistributionListUserCountByUserId(Long userId);

	List<User> getIdleUsersByDistributionId(Long distributionId);

	long getMaxAuxTableValueId();

	Long getMaxDistributionId();

	Long getMaxUserId();

	List<Distribution> getNonReviewGroupDistributionList();

	List<User> getRecipientsByDistributionId(Long distributionId);

	List<Distribution> getReviewGroupList();

	User getUserById(Long userId);

	User getUserByUserName(String username);

	List<Distribution> getUserGroupsByUserId(Long userId);

	List<User> getUsers();

	List<User> getUsersReport();

	void insertAuxTableValue(AuxTableValue auxTableValue);

	void insertDisListUser(Map<String, Object> parameters);

	void insertDistribution(Distribution distribution);

	void insertUser(User user);

	void removeFromDisListUser(Map<String, Object> parameters);

	void removeUserFromRecipientList(Map<String, Object> parameters);

	void updateAuxTableValue(AuxTableValue auxTableValue);

	void updateDistribution(Distribution distribution);

	void updateUser(User user);
	
	List<HtmlOutputLog> getHtmlOutputLogs();
	
	void updateHtmlOutputLog(HtmlOutputLog log);
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	void insertHtmlOutputLog(HtmlOutputLog log);

	Long getRefsetCodeCount(String auxValueCode);
	
	Long getRefsetNameCount(String auxEngLable);
}
