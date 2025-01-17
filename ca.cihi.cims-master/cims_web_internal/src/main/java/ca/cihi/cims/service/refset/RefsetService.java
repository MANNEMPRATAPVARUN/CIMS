package ca.cihi.cims.service.refset;

import java.util.List;

import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.model.refset.ClassificationCodeSearchRequest;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.model.sct.SCTVersion;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.web.bean.refset.ContextBaseBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;
import ca.cihi.cims.web.bean.refset.RefsetLightBean;
import ca.cihi.cims.web.bean.refset.SupplementViewBean;

/**
 * RefsetService interface
 *
 * @author lzhu
 *
 */
public interface RefsetService {
	
	boolean isRefreshAllowed(Long contextId, Long elementId, Long elementVersionId);

	void assignTo(RefsetConfigDetailBean viewBean, String newAssignee);

	void checkPermission(LdapUserDetails user, RefsetConfigDetailBean viewBean);

	void checkPickListContent(RefsetConfigDetailBean viewBean);

	Refset createRefset(RefsetConfigDetailBean viewBean, String userName) throws DuplicateCodeNameException;

	List<AuxTableValue> getCategoryList();

	List<String> getCCIYearList();

	List<ContextBaseBean> getCCIContextInfoList();

	Integer getDefaultEffectiveYearFrom();

	Integer getDefaultEffectiveYearTo();

	List<Integer> getEffectiveYearFromList(int range);

	List<Integer> getEffectiveYearToList(int range);

	List<String> getICD10CAYearList();

	List<ContextBaseBean> getICD10CAContextInfoList();

	List<PickList> getPickLists(Refset refset);

	List<Supplement> getSupplements(Refset refset);

	Supplement getSupplement(Long contextId, Long elementId, Long elementVersionId);

	Refset getRefset(Long contextId, Long elementId, Long elementVersionId);

	List<String> getRefsetAssigneeRecipents(String assignee);

	/**
	 * Get refset versions for active ones only
	 *
	 * @return
	 */
	List<RefsetVersion> getRefsetVersions();

	/**
	 * Get refset versions for open and active ones only
	 */
	List<RefsetVersion> getOpenActiveRefsetVersions();

	/**
	 * Get refset versions for active ones only and specific category id
	 *
	 * @return
	 */
	List<RefsetVersion> getRefsetVersions(Long categoryId);

	/**
	 * Get all refsets regardless version
	 *
	 * @param status
	 *            TODO
	 *
	 * @return
	 */
	List<RefsetVersion> getAllRefsets(String status);

	List<SCTVersion> getSCTVersionList();

	PickList insertPickList(PickListViewBean viewBean) throws DuplicateCodeNameException;

	Supplement insertSupplement(SupplementViewBean viewBean) throws DuplicateCodeNameException;

	void populateDataFromRefset(Long contextId, Long elementId, Long elementVersionId, RefsetConfigDetailBean viewBean);

	void removeRefset(RefsetConfigDetailBean viewBean);

	void setContextElementInfo(RefsetResponse refsetResponse, Refset refset);

	void updateRefset(RefsetConfigDetailBean viewBean) throws DuplicateCodeNameException;

	String getVersionDescByCode(String code);

	boolean isRoleAssigned(LdapUserDetails user, SecurityRole role);

	boolean isAssigneeRevoked(RefsetLightBean viewBean, String assignee);

	/**
	 * Get search result of active classifications by code.
	 *
	 * @param classificationCodeSearchRequest
	 *            the classification search request.
	 * @return list of classification code search response.
	 */
	List<ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse> getActiveClassificationByCode(
			ClassificationCodeSearchRequest classificationCodeSearchRequest);

	void updateRefsetStatus(Long contextId, ElementIdentifier elementIdentifier, Status status,
			final BindingResult result);

	void closeRefsetVersion(RefsetConfigDetailBean viewBean);

	boolean picklistExists(RefsetConfigDetailBean viewBean);

	boolean isInceptionVersion(Long contextId);

	void createNewVersion(RefsetConfigDetailBean viewBean);

	boolean isOpenRefsetVersion(Long contextId);

	boolean isLatestClosedRefsetVersion(Long contextId);

	String getVersionCode(Long contextId);

}