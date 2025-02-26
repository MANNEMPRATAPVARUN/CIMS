package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.exception.AlreadyInUseException;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.ClassificationDiagram;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;


public class AdminServiceImpl implements AdminService {

	private static final Log LOGGER = LogFactory.getLog(AdminServiceImpl.class);

	// private AdminDao adminDao;
	private AdminMapper adminMapper;

	@Override
	@Transactional
	public void addUserToRecipients(final Long userId, final Long distributionId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("distributionId", distributionId);
		parameters.put("userId", userId);
		adminMapper.insertDisListUser(parameters);
	}

	@Override
	@Transactional
	public void createDistribution(Distribution distribution) {
		adminMapper.insertDistribution(distribution);
	}

	@Override
	@Transactional
	public void createUser(User user) {
		adminMapper.insertUser(user);
	}

	@Override
	@Transactional
	public void deleteAux(Long auxTableValueId) throws AlreadyInUseException {
		try {
			adminMapper.deleteAux(auxTableValueId);
		} catch (DataAccessException dae) {
			LOGGER.error("DataAccessException encountered :", dae);
			if (dae.getMessage().contains("SQLIntegrityConstraintViolationException")) {
				throw new AlreadyInUseException(dae);
			}
			throw dae;
		}
	}

	@Override
	@Transactional
	public void deleteDistribution(Long distributionId) {
		adminMapper.deleteDistribution(distributionId);

	}

	@Override
	@Transactional
	public void deleteUser(Long userId) {
		adminMapper.deleteUser(userId);
	}

	@Override
	public List<AuxTableValue> findAllActiveAuxValuesForAuxCode(String auxiliaryCode) {
		return null;
	}

	@Override
	public List<Long> findDistinctUserIdsInDistributionListIds(List<Long> distributionIds) {
		return adminMapper.findDistinctUserIdsInDistributionListIds(distributionIds);
	}

	@Override
	public List<User> findDistinctUsersInDistributionListIds(List<Long> distributionIds) {
		return adminMapper.findDistinctUsersInDistributionListIds(distributionIds);
	}

	public AdminMapper getAdminMapper() {
		return adminMapper;
	}

	@Override
	public List<Distribution> getAdvisorDistributionList() {
		return adminMapper.getAdvisorDistributionList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<String> getAuxTableCodes() {
		return adminMapper.getAuxTableCodes();
	}

	@Override
	public String getAuxTableIdByCode(final String auxCode) {
		return adminMapper.getAuxTableIdByCode(auxCode);
	}

	@Override
	@Transactional(readOnly = true)
	// @Cacheable(cacheName = "AUX_CODE_VALUES")
	public List<AuxTableValue> getAuxTableValues(final String auxCode) {
		return adminMapper.getAuxTableValues(auxCode);
	}

	@Override
	@Transactional(readOnly = true)
	public AuxTableValue getAuxTableValueByID(final Long valueId) {
		return adminMapper.getAuxTableValueByID(valueId);
	}



	@SuppressWarnings("unchecked")
	@Override
	public List<ClassificationDiagram> getDiagrams(String versionCode, String baseClassification) {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classificationYear", versionCode);
		params.put("baseClassification", baseClassification);

		adminMapper.getDiagrams(params);

		List<ClassificationDiagram> diagramsList = (ArrayList<ClassificationDiagram>) params.get("myData");

		return diagramsList;
	}

	@Override
	public Distribution getDistributionByCode(final String code) {
		return adminMapper.getDistributionByCode(code);
	}

	@Override
	@Transactional(readOnly = true)
	public Distribution getDistributionById(Long distributionId) {
		return adminMapper.getDistributionById(distributionId);
	}

	@Override
	@Transactional(readOnly = true)
	public String getDistributionCodeById(final Long distributionId) {
		return adminMapper.getDistributionCodeById(distributionId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<Distribution> getDistributionList() {
		return adminMapper.getDistributionList();
	}

	@Override
	@Transactional(readOnly = true)
	public Long getDistributionListUserCountByDistributionId(final Long distributionId) {
		return adminMapper.getDistributionListUserCountByDistributionId(distributionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getDistributionListUserCountByUserId(final Long userId) {
		return adminMapper.getDistributionListUserCountByUserId(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getIdleUsersByDistributionId(final Long distributionId) {
		return adminMapper.getIdleUsersByDistributionId(distributionId);
	}

	@Override
	@Transactional(readOnly = true)
	public Long getMaxDistributionId() {
		return adminMapper.getMaxDistributionId() == null ? 0 : adminMapper.getMaxDistributionId();
	}

	@Override
	@Transactional(readOnly = true)
	public Long getMaxUserId() {
		return adminMapper.getMaxUserId() == null ? 0 : adminMapper.getMaxUserId();
	}

	@Override
	@Cacheable("NON_REVIEW_GROUPS")
	public List<Distribution> getNonReviewGroupDistributionList() {
		return adminMapper.getNonReviewGroupDistributionList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getRecipientsByDistributionId(final Long distributionId) {
		return adminMapper.getRecipientsByDistributionId(distributionId);
	}

	@Override
	// @Cacheable(cacheName = "REVIEW_GROUPS")
	public List<Distribution> getReviewGroupList() {
		return adminMapper.getReviewGroupList();
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserById(Long userId) {
		return adminMapper.getUserById(userId);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByUserName(final String username) {
		User user = adminMapper.getUserByUserName(username);
		if (user != null) {
			List<Distribution> inGroups = adminMapper.getUserGroupsByUserId(user.getUserId());
			user.setInGroups(inGroups);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUsers() {
		return adminMapper.getUsers();
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUsersReport() {
		return adminMapper.getUsersReport();
	}

	@Override
	@Transactional
	public long insertAuxTableValue(final AuxTableValue auxTableValue) {
		adminMapper.insertAuxTableValue(auxTableValue);
		return adminMapper.getMaxAuxTableValueId();
	}

	public boolean isDistributionInUse(long distributionId) {
		int count = adminMapper.getActiveChangeRequestCountForDistribution(distributionId);
		return count != 0;
	}

	@Override
	@Transactional
	public void removeUserFromRecipientList(Long distributionId, Long userId) {
		final Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("distributionId", distributionId);
		parameters.put("userId", userId);
		adminMapper.removeUserFromRecipientList(parameters);

	}

	@Override
	@Transactional
	public void removeUserFromRecipients(final Long userId, final Long distributionId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("distributionId", distributionId);
		parameters.put("userId", userId);
		adminMapper.removeFromDisListUser(parameters);
	}

	public void setAdminMapper(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}

	@Override
	@Transactional
	public void updateAuxTableValue(final AuxTableValue auxTableValue) {
		adminMapper.updateAuxTableValue(auxTableValue);
	}

	@Override
	@Transactional
	public void updateDistribution(Distribution distribution) {
		adminMapper.updateDistribution(distribution);
	}

	@Override
	@Transactional
	public void updateUser(User user) {
		adminMapper.updateUser(user);
	}

	@Override
	public boolean isRefsetCodeNotUnique(String auxValueCode) {		
		return adminMapper.getRefsetCodeCount(auxValueCode) > 0 ? true : false;
	}
	
	@Override
	public boolean isRefsetNameNotUnique(String auxEngLable) {
		return adminMapper.getRefsetNameCount(auxEngLable) > 0 ? true : false;
	}

}