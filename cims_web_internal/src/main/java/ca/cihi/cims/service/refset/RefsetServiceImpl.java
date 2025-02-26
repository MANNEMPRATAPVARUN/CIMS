package ca.cihi.cims.service.refset;

import static java.util.stream.Collectors.toList;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.framework.domain.Context;
import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.ContextStatus;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.model.AuxTableValue;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.Status;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.refset.ClassificationCodeSearchRequest;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.model.sct.SCTVersion;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.factory.RefsetFactory;
import ca.cihi.cims.service.AdminService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.service.ViewService;
import ca.cihi.cims.service.sct.SnomedSCTService;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.validator.refset.RefsetValidator;
import ca.cihi.cims.web.bean.refset.ContextBaseBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;
import ca.cihi.cims.web.bean.refset.RefsetLightBean;
import ca.cihi.cims.web.bean.refset.SupplementViewBean;

/**
 * @author lzhu
 */
public class RefsetServiceImpl implements RefsetService {
	private static final Log LOGGER = LogFactory.getLog(RefsetServiceImpl.class);

	@Autowired
	private AdminService adminService;
	@Autowired
	private LookupService lookupService;
	@Autowired
	private SnomedSCTService snomedSCTService;
	@Autowired
	private RefsetValidator refsetValidator;

	@Autowired
	private ViewService viewService;

	@Override
	@Transactional
	public void assignTo(RefsetConfigDetailBean viewBean, String newAssignee) {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		refset.assign(newAssignee);
	}

	@Override
	public void checkPermission(LdapUserDetails user, RefsetConfigDetailBean viewBean) {
		String userName = user.getUsername();

		if (userName.equalsIgnoreCase(viewBean.getAssignee())) {
			viewBean.setCheckAssignee(true);
			if (!viewBean.getVersionStatus().equals(RefsetUtils.REFSET_VERSION_STATUS_CLOSED)) {
				viewBean.setReadOnly(false);
			} else {
				viewBean.setReadOnly(true);
			}
		} else {
			viewBean.setCheckAssignee(false);
			viewBean.setReadOnly(true);
		}

		viewBean.setAdminRole(isRoleAssigned(user, SecurityRole.ROLE_ADMINISTRATOR));
		viewBean.setRefsetDeveloperRole(isRoleAssigned(user, SecurityRole.ROLE_REFSET_DEVELOPER));
	}
	
	@Override
	public boolean isRefreshAllowed(Long contextId, Long elementId, Long elementVersionId){
		Refset refset = getRefset(contextId, elementId, elementVersionId);
		return (refset.getVersionStatus().toString().equals("OPEN") && refset.getStatus().equals("ACTIVE"));
	}

	// @Transactional
	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public Refset createRefset(RefsetConfigDetailBean viewBean, String userName) throws DuplicateCodeNameException {
		Refset refset = RefsetFactory.createRefset(viewBean.getRefsetCode(), viewBean.getRefsetNameENG());
		LOGGER.debug("userName=" + userName);
		refset.setName(viewBean.getRefsetNameFRE(), Language.FRA);
		// refset.setCCIYear(viewBean.getCCIYear());
		refset.setCCIYear(viewBean.getCCIContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[1]);
		refset.setCCIContextId(
				Long.parseLong(viewBean.getCCIContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[0]));
		refset.setDefinition(viewBean.getDefinition());
		refset.setEffectiveYearFrom(viewBean.getEffectiveYearFrom().shortValue());
		if (viewBean.getEffectiveYearTo() != null) {
			refset.setEffectiveYearTo(viewBean.getEffectiveYearTo().shortValue());
		}
		// refset.setICD10CAYear(viewBean.getICD10CAYear());
		refset.setICD10CAYear(viewBean.getICD10CAContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[1]);
		refset.setICD10CAContextId(
				Long.parseLong(viewBean.getICD10CAContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[0]));
		refset.setSCTVersionCode(viewBean.getSCTVersionCode());
		refset.setCategoryId(viewBean.getCategoryId());
		refset.assign(userName);
		refset.enableRefset();
		return refset;
	}

	public AdminService getAdminService() {
		return adminService;
	}

	@Override
	public List<AuxTableValue> getCategoryList() {
		return adminService.getAuxTableValues(AuxTableValue.AUX_CODE_REFSET_CATEGORY);
	}

	@Override
	public List<String> getCCIYearList() {
		List<ContextIdentifier> contextIdentifierList = lookupService
				.findClosedBaseContextIdentifiersReport(CIMSConstants.CCI, null);
		return contextIdentifierList.stream().map(ContextIdentifier::getVersionCode).collect(toList());
	}

	@Override
	public Integer getDefaultEffectiveYearFrom() {
		return Year.now().getValue();
	}

	@Override
	public Integer getDefaultEffectiveYearTo() {
		return Year.now().getValue();
	}

	@Override
	public List<Integer> getEffectiveYearFromList(int range) {
		List<Integer> yearList = new ArrayList<Integer>();
		int currentYear = Year.now().getValue();
		for (int i = -range; i <= range; i++) {
			yearList.add(currentYear + i);
		}
		return yearList;
	}

	@Override
	public List<Integer> getEffectiveYearToList(int range) {
		return getEffectiveYearFromList(range);
	}

	@Override
	public List<String> getICD10CAYearList() {
		List<ContextIdentifier> contextIdentifierList = lookupService
				.findClosedBaseContextIdentifiersReport(CIMSConstants.ICD_10_CA, null);
		return contextIdentifierList.stream().map(ContextIdentifier::getVersionCode).collect(toList());
	}

	public LookupService getLookupService() {
		return lookupService;
	}

	@Override
	public List<PickList> getPickLists(Refset refset) {
		return refset.listPickLists();
	}

	@Override
	public List<Supplement> getSupplements(Refset refset) {
		return refset.listSupplements();
	}

	@Override
	public Supplement getSupplement(Long contextId, Long elementId, Long elementVersionId) {
		ElementIdentifier elementIdentifier = new ElementIdentifier(elementId, elementVersionId);
		return RefsetFactory.getSupplement(contextId, elementIdentifier, ConceptLoadDegree.REGULAR);
	}

	@Override
	public Refset getRefset(Long contextId, Long elementId, Long elementVersionId) {
		ElementIdentifier elementIdentifier = new ElementIdentifier(elementId, elementVersionId);
		return RefsetFactory.getRefset(contextId, elementIdentifier, ConceptLoadDegree.REGULAR);
	}

	@Override
	public List<String> getRefsetAssigneeRecipents(String assignee) {
		List<String> assigneeRecipents = new ArrayList<>();
		List<User> users1 = adminService.getRecipientsByDistributionId(Distribution.DL_ID_ADMINISTRATOR);
		for (User user : users1) {
			if (!user.getUsername().equals(assignee)) {
				assigneeRecipents.add(user.getUsername());
			}
		}
		List<User> users2 = adminService.getRecipientsByDistributionId(Distribution.DL_ID_RefsetDeveloper);
		for (User user : users2) {
			if (!user.getUsername().equals(assignee)) {
				assigneeRecipents.add(user.getUsername());
			}
		}
		return RefsetUtils.dedupeList(assigneeRecipents);
	}

	@Override
	public List<RefsetVersion> getRefsetVersions() {
		return RefsetFactory.getRefsetVersions(null, RefsetStatus.ACTIVE.getStatus(), null);
	}

	@Override
	public List<RefsetVersion> getRefsetVersions(Long categoryId) {
		return RefsetFactory.getRefsetVersions(categoryId, RefsetStatus.ACTIVE.getStatus(), null);
	}

	@Override
	public List<RefsetVersion> getAllRefsets(String status) {
		return RefsetFactory.getAllRefsets(status);
	}

	@Override
	public List<SCTVersion> getSCTVersionList() {
		return snomedSCTService.getVersionsByStatus(CIMSConstants.SCT_VERSION_STATUS_CODE_ACTIVE);
	}

	public SnomedSCTService getSnomedSCTService() {
		return snomedSCTService;
	}

	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public PickList insertPickList(PickListViewBean viewBean) throws DuplicateCodeNameException {
		Refset refset = RefsetFactory.getRefset(viewBean.getContextId(),
				new ElementIdentifier(viewBean.getElementId(), viewBean.getElementVersionId()),
				ConceptLoadDegree.REGULAR);
		PickList picklist = RefsetFactory.createPickList(refset, viewBean.getCode(), viewBean.getName(),
				viewBean.getClassificationStandard());

		if (CIMSConstants.ICD_10_CA.equals(viewBean.getClassificationStandard())) {
			RefsetFactory.createColumn(picklist, ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay(),
					ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay(), (short) 1);
		} else {
			RefsetFactory.createColumn(picklist, ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay(),
					ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay(), (short) 1);
		}

		return picklist;
	}

	@Override
	@Transactional(rollbackFor = { DuplicateCodeNameException.class })
	public Supplement insertSupplement(SupplementViewBean viewBean) throws DuplicateCodeNameException {
		Refset refset = RefsetFactory.getRefset(viewBean.getContextId(),
		        new ElementIdentifier(viewBean.getElementId(), viewBean.getElementVersionId()),
		        ConceptLoadDegree.REGULAR);
		Supplement supplement = RefsetFactory.createSupplement(refset,
		        getDecoratedSupplementCode(refset.getElementIdentifier().getElementId(), viewBean.getCode()),
		        viewBean.getName(), viewBean.getFileName(), viewBean.getContent());
		return supplement;
	}

	@Override
	public void populateDataFromRefset(Long contextId, Long elementId, Long elementVersionId,
			RefsetConfigDetailBean viewBean) {
		Refset refset = getRefset(contextId, elementId, elementVersionId);
		viewBean.setRefsetCode(refset.getCode());
		viewBean.setRefsetNameENG(refset.getName(Language.ENG));
		viewBean.setRefsetNameFRE(refset.getName(Language.FRA));
		viewBean.setEffectiveYearFrom(refset.getEffectiveYearFrom().intValue());
		if (refset.getEffectiveYearTo() != null) {
			viewBean.setEffectiveYearTo(refset.getEffectiveYearTo().intValue());
		}
		viewBean.setCategoryName(refset.getCategoryName(refset.getCategoryId()));
		viewBean.setCategoryId(refset.getCategoryId());
		viewBean.setAssignee(refset.getAssigneeName());
		viewBean.setStatus(refset.getStatus());
		viewBean.setVersionCode(refset.getVersionCode());
		String versionName = RefsetUtils.getRefsetVersionName(viewBean.getRefsetCode(), viewBean.getEffectiveYearFrom(),
				viewBean.getEffectiveYearTo(), viewBean.getVersionCode());
		viewBean.setVersionName(versionName);
		viewBean.setICD10CAYear(refset.getICD10CAYear());
		viewBean.setOldICD10CAYear(refset.getICD10CAYear());
		viewBean.setOldCCIYear(refset.getCCIYear());
		viewBean.setOldSCTVersionCode(refset.getSCTVersionCode());
		viewBean.setOldEffectiveYearFrom(refset.getEffectiveYearFrom().intValue());
		StringBuffer sb1 = new StringBuffer();
		sb1.append(refset.getICD10CAContextId()).append(RefsetUtils.CONTEXT_INFO_SEPERATOR)
				.append(viewBean.getICD10CAYear());

		viewBean.setICD10CAContextInfo(sb1.toString());
		viewBean.setCCIYear(refset.getCCIYear());
		StringBuffer sb2 = new StringBuffer();
		sb2.append(refset.getCCIContextId()).append(RefsetUtils.CONTEXT_INFO_SEPERATOR).append(viewBean.getCCIYear());
		viewBean.setCCIContextInfo(sb2.toString());
		viewBean.setDefinition(refset.getDefinition());
		viewBean.setVersionStatus(refset.getVersionStatus().toString());
		viewBean.setSCTVersionCode(refset.getSCTVersionCode());
		viewBean.setSCTVersionDesc(getVersionDescByCode(viewBean.getSCTVersionCode()));
		viewBean.setNotes(refset.getNotes());
		viewBean.setContextId(contextId);
		viewBean.setElementId(elementId);
		viewBean.setElementVersionId(elementVersionId);
		viewBean.setDisplayAssignee(refset.getAssigneeName());
		viewBean.setOpenVersionExists(refset.isOpenVersionExists());
		viewBean.setLatestClosedVersion(refset.isLatestClosedVersion());
		LOGGER.debug("categoryName from refset=" + refset.getCategoryName(refset.getCategoryId()));
	}

	@Override
	@Transactional
	public void removeRefset(RefsetConfigDetailBean viewBean) {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		refset.remove();
	}

	public void setAdminService(AdminService adminService) {
		this.adminService = adminService;
	}

	@Override
	public void setContextElementInfo(RefsetResponse refsetResponse, Refset refset) {
		refsetResponse.setContextId(refset.getContextElementIdentifier().getElementVersionId());
		refsetResponse.setElementId(refset.getElementIdentifier().getElementId());
		refsetResponse.setElementVersionId(refset.getElementIdentifier().getElementVersionId());
		refsetResponse.setCategoryName(refset.getCategoryName(refset.getCategoryId()));
	}

	public void setLookupService(LookupService lookupService) {
		this.lookupService = lookupService;
	}

	public void setSnomedSCTService(SnomedSCTService snomedSCTService) {
		this.snomedSCTService = snomedSCTService;
	}

	@Override
	@Transactional
	public void updateRefset(RefsetConfigDetailBean viewBean) throws DuplicateCodeNameException {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		refset.setCategoryId(viewBean.getCategoryId());
		refset.setName(viewBean.getRefsetNameFRE(), Language.FRA);
		refset.setDefinition(viewBean.getDefinition());
		refset.setNotes(viewBean.getNotes());
	}

	@Override
	public String getVersionDescByCode(String code) {
		return snomedSCTService.getVersionDescByCode(code);
	}

	@Override
	public void checkPickListContent(RefsetConfigDetailBean viewBean) {
		// TODO: need to implement this method later when requirement is complete.
		// Currently set pickListContent to false to disable the Close Refset Version Button
		viewBean.setPickListContent(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean isRoleAssigned(LdapUserDetails user, SecurityRole role) {
		Collection<GrantedAuthority> grantedAuthorities = (Collection<GrantedAuthority>) user.getAuthorities();
		return grantedAuthorities.stream().anyMatch(t -> t.getAuthority().equalsIgnoreCase(role.getRole()));
	}

	@Override
	public boolean isAssigneeRevoked(RefsetLightBean viewBean, String assignee) {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		return (refset.getAssigneeName().equals(assignee) ? false : true);
	}

	@Override
	public List<ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse> getActiveClassificationByCode(
			ClassificationCodeSearchRequest classificationCodeSearchRequest) {
		String searchConceptCode = classificationCodeSearchRequest.getSearchConceptCode();
		searchConceptCode += !searchConceptCode.contains("%") ? "%" : "";

		return RefsetFactory.getActiveClassificationByCode(classificationCodeSearchRequest.getContextId(),
				getCodeClassId(classificationCodeSearchRequest.getClassificationCode()),
				classificationCodeSearchRequest.getClassificationCode(), searchConceptCode,
				classificationCodeSearchRequest.getMaxResults());
	}

	private Long getCodeClassId(String classificationCode) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("classification", classificationCode);

		return viewService.getContentDisplayMapper().getCodeClassId(parameters);
	}

	@Override
	public List<ContextBaseBean> getCCIContextInfoList() {
		return getContextInfoList(CIMSConstants.CCI);
	}

	private List<ContextBaseBean> getContextInfoList(String contextType) {
		List<ContextIdentifier> contextIdentifierList = lookupService
				.findClosedBaseContextIdentifiersReport(contextType, null);
		contextIdentifierList.addAll(lookupService.findNonClosedBaseContextIdentifiersReport(contextType, null));
		List<ContextBaseBean> beanList = new ArrayList<ContextBaseBean>();
		for (ContextIdentifier contextIdentifier : contextIdentifierList) {
			ContextBaseBean bean = new ContextBaseBean();
			bean.setContextId(contextIdentifier.getContextId());
			bean.setVersionCode(contextIdentifier.getVersionCode());
			StringBuffer sb = new StringBuffer();
			sb.append(bean.getContextId()).append(RefsetUtils.CONTEXT_INFO_SEPERATOR).append(bean.getVersionCode());
			bean.setContextBaseInfo(sb.toString());
			beanList.add(bean);
		}
		return beanList;
	}

	@Override
	public List<ContextBaseBean> getICD10CAContextInfoList() {
		return getContextInfoList(CIMSConstants.ICD_10_CA);
	}

	@Override
	public void updateRefsetStatus(Long contextId, ElementIdentifier elementIdentifier, Status status,
			final BindingResult result) {
		Refset refset = RefsetFactory.getRefset(contextId, elementIdentifier, ConceptLoadDegree.MINIMAL);
		refsetValidator.validateDisabledStatus(refset, status, result);
		if (!result.hasErrors()) {
			if (Status.DISABLED == status) {
				refset.disableRefset();
			} else if (Status.ACTIVE == status) {
				refset.enableRefset();
			}
		}
	}

	@Override
	@Transactional
	public void closeRefsetVersion(RefsetConfigDetailBean viewBean) {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		refset.closeRefsetVersion();
	}

	@Override
	public boolean picklistExists(RefsetConfigDetailBean viewBean) {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		return (refset.listPickLists().size() > 0 ? true : false);
	}

	@Override
	public List<RefsetVersion> getOpenActiveRefsetVersions() {
		return RefsetFactory.getRefsetVersions(null, RefsetStatus.ACTIVE.getStatus(), ContextStatus.OPEN.getStatus());
	}

	@Override
	public boolean isInceptionVersion(Long contextId) {
		return Context.findById(contextId).getBaseContextId() == null ? true : false;
	}

	@Override
	@Transactional
	public void createNewVersion(RefsetConfigDetailBean viewBean) {
		Refset refset = getRefset(viewBean.getContextId(), viewBean.getElementId(), viewBean.getElementVersionId());
		String versionType = viewBean.getVersionType();

		String versionCode = ca.cihi.cims.refset.util.RefsetUtils.generateVersionCode(refset.getVersionCode(),
				versionType);

		Refset newVersion = refset.createNewVersion(versionCode,
				Long.parseLong(viewBean.getICD10CAContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[0]),
				Long.parseLong(viewBean.getCCIContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[0]),
				viewBean.getSCTVersionCode());
		newVersion.setCCIYear(viewBean.getCCIContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[1]);
		newVersion.setICD10CAYear(viewBean.getICD10CAContextInfo().split(RefsetUtils.CONTEXT_INFO_SEPERATOR)[1]);
		if ("major".equals(versionType)) {
			newVersion.setEffectiveYearFrom(viewBean.getEffectiveYearFrom().shortValue());
			newVersion.setEffectiveYearTo(
					viewBean.getEffectiveYearTo() != null ? viewBean.getEffectiveYearTo().shortValue() : null);
		}
		viewBean.setContextId(newVersion.getContextElementIdentifier().getElementVersionId());
		viewBean.setElementVersionId(newVersion.getElementIdentifier().getElementVersionId());
	}

	@Override
	public boolean isOpenRefsetVersion(Long contextId) {
		String status = Context.findById(contextId).getContextStatus().getStatus();
		return (ContextStatus.OPEN.getStatus().equals(status) ? true : false);
	}

	@Override
	public boolean isLatestClosedRefsetVersion(Long contextId) {
		Long versionId = Context.findById(contextId).getLatestClosedVersion();
		return (contextId.longValue() == versionId.longValue() ? true : false);
	}

	@Override
	public String getVersionCode(Long contextId) {
		return Context.findById(contextId).getVersionCode();
	}

	private String getDecoratedSupplementCode(Long refsetElementId, String code) {
		return refsetElementId + "_" + code;
	}
}
