package ca.cihi.cims.service.refset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;

import ca.cihi.cims.CIMSConstants;
import ca.cihi.cims.framework.ElementIdentifier;
import ca.cihi.cims.model.refset.RefsetResponse;
import ca.cihi.cims.refset.concept.ColumnImpl;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.exception.ColumnTypeWrongException;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.util.RefsetUtils;
import ca.cihi.cims.web.bean.refset.AvailableColumnTypeResponse;
import ca.cihi.cims.web.bean.refset.PickListColumnBean;
import ca.cihi.cims.web.bean.refset.PickListViewBean;
import ca.cihi.cims.web.bean.refset.RefsetConfigDetailBean;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class RefsetServiceImplTest {

	@Autowired
	private RefsetService refsetService;

	@Autowired
	private PicklistService picklistService;

	private RefsetConfigDetailBean refsetConfigDetailBean;

	@Mock
	private LdapUserDetails ldapUserDetails;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		// contextId=5945427&elementId=2767598&elementVersionId=5945428
		refsetConfigDetailBean = new RefsetConfigDetailBean();
		refsetConfigDetailBean.setAdminRole(false);
		refsetConfigDetailBean.setAssignee("tyang");
		refsetConfigDetailBean.setCategoryId(new Long(1));
		refsetConfigDetailBean.setCategoryName("test");
		refsetConfigDetailBean.setCCIYear("2017");
		refsetConfigDetailBean.setContextId(5945427L);
		refsetConfigDetailBean.setDefinition("this is for testing only");
		refsetConfigDetailBean.setEffectiveYearFrom(2016);
		refsetConfigDetailBean.setEffectiveYearTo(2018);
		refsetConfigDetailBean.setElementId(2767598L);
		refsetConfigDetailBean.setElementVersionId(5945428L);
		refsetConfigDetailBean.setICD10CAYear("2018");
		refsetConfigDetailBean.setNewAssignee("lzhu");
		refsetConfigDetailBean.setNotes("will do it later");
		refsetConfigDetailBean.setReadOnly(false);
		refsetConfigDetailBean.setRefsetCode("first test");
		refsetConfigDetailBean.setRefsetNameENG("refset1eng");
		refsetConfigDetailBean.setRefsetNameFRE("refset1fra");
		refsetConfigDetailBean.setRefsetVersionName("1.0");
		refsetConfigDetailBean.setSCTVersionCode("IE20160331");
		refsetConfigDetailBean.setSCTVersionDesc("International Edition 20160331");
		refsetConfigDetailBean.setStatus("OPEN");
		refsetConfigDetailBean.setVersionCode("dwwwewewew1.0");
		refsetConfigDetailBean.setVersionName("versionstatus");
		refsetConfigDetailBean.setVersionStatus("versionStatus");

		refsetConfigDetailBean.setICD10CAContextInfo("2_2017");
		refsetConfigDetailBean.setCCIContextInfo("1_2018");

	}

	@Test
	public void testCheckPermission() {
		when(ldapUserDetails.getUsername()).thenReturn("user1");
		refsetService.checkPermission(ldapUserDetails, refsetConfigDetailBean);
		assertFalse(refsetConfigDetailBean.getAdminRole());
	}

	@Test
	public void testCreateRefset() throws Exception {
		Refset refset1 = null;
		Refset refset2 = null;
		Refset refset3 = null;
		DuplicateCodeNameException dupCodeExp = null;

		refsetConfigDetailBean.setRefsetCode("LyWintest001");
		refsetConfigDetailBean.setRefsetNameENG("LyWintest001 English Name");
		refset1 = refsetService.createRefset(refsetConfigDetailBean, "lzhu");

		assertTrue(refset1.getCode().endsWith("LyWintest001"));

		try {
			RefsetConfigDetailBean viewBean2 = new RefsetConfigDetailBean();
			viewBean2.setRefsetCode("LyWintest001");
			viewBean2.setRefsetNameENG("LyWintest001 English Name");
			refset2 = refsetService.createRefset(viewBean2, "lzhu");
		} catch (DuplicateCodeNameException e) {
			dupCodeExp = new DuplicateCodeNameException("LyWintest001 is duplicated code");
		}
		assertTrue(refset1.getCode().equals("LyWintest001"));
		assertNull(refset2);
		assertTrue(dupCodeExp.getMessage().equals("LyWintest001 is duplicated code"));
		// assertTrue(dupNameExp.getMessage().equals("LyWintest001 English Name is duplicated name"));

		try {
			RefsetConfigDetailBean viewBean3 = new RefsetConfigDetailBean();
			viewBean3.setRefsetCode("LyWintest003");
			viewBean3.setRefsetNameENG("LyWintest001 English Name");
			refset3 = refsetService.createRefset(viewBean3, "lzhu");
		} catch (DuplicateCodeNameException e) {
			dupCodeExp = new DuplicateCodeNameException("LyWintest001 English Name is duplicated name");
		}
		assertNull(refset3);
		// assertTrue(dupCodeExp.getMessage().equals("LyWintest003 is duplicated code"));
		assertTrue(dupCodeExp.getMessage().equals("LyWintest001 English Name is duplicated name"));
		///////////////////////////////////////////////////////////////////////////////
		refsetConfigDetailBean.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		refsetConfigDetailBean.setElementId(refset1.getElementIdentifier().getElementId());
		refsetConfigDetailBean.setElementVersionId(refset1.getElementIdentifier().getElementVersionId());
		refsetService.assignTo(refsetConfigDetailBean, "lzhu");
		assertTrue(refsetService.getRefset(refsetConfigDetailBean.getContextId(), refsetConfigDetailBean.getElementId(),
				refsetConfigDetailBean.getElementVersionId()).getAssigneeName().equals("lzhu"));

		RefsetConfigDetailBean viewBean = new RefsetConfigDetailBean();
		refsetService.populateDataFromRefset(refset1.getContextElementIdentifier().getElementVersionId(),
				refset1.getElementIdentifier().getElementId(), refset1.getElementIdentifier().getElementVersionId(),
				viewBean);
		assertTrue(viewBean.getRefsetCode() != null);

		RefsetResponse refsetResponse = new RefsetResponse();
		Refset refset = refsetService.getRefset(refset1.getContextElementIdentifier().getElementVersionId(),
				refset1.getElementIdentifier().getElementId(), refset1.getElementIdentifier().getElementVersionId());
		refsetService.setContextElementInfo(refsetResponse, refset);
		assertTrue(refsetResponse.getContextId().longValue() == refset1.getContextElementIdentifier()
				.getElementVersionId().longValue());
		assertTrue(
				refsetResponse.getElementId().longValue() == refset1.getElementIdentifier().getElementId().longValue());
		assertTrue(refsetResponse.getElementVersionId().longValue() == refset1.getElementIdentifier()
				.getElementVersionId().longValue());

		refsetConfigDetailBean.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		refsetConfigDetailBean.setElementId(refset1.getElementIdentifier().getElementId());
		refsetConfigDetailBean.setElementVersionId(refset1.getElementIdentifier().getElementVersionId());
		refsetService.updateRefset(refsetConfigDetailBean);
		Refset refsetUpdated = refsetService.getRefset(refset1.getContextElementIdentifier().getElementVersionId(),
				refset1.getElementIdentifier().getElementId(), refset1.getElementIdentifier().getElementVersionId());
		assertTrue(refsetUpdated.getCode().equals("LyWintest001"));

		refsetService.removeRefset(refsetConfigDetailBean);
		Refset refsetToRemove = refsetService.getRefset(refset1.getContextElementIdentifier().getElementVersionId(),
				refset1.getElementIdentifier().getElementId(), refset1.getElementIdentifier().getElementVersionId());
		assertTrue(refsetToRemove.getVersionStatus().toString().equals("DELETED"));

		////////////////////////////////////////////////////////////////////////////////
		PickListViewBean picklistViewBean = new PickListViewBean();
		picklistViewBean.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		picklistViewBean.setElementId(refset1.getElementIdentifier().getElementId());
		picklistViewBean.setElementVersionId(refset1.getElementIdentifier().getElementVersionId());
		picklistViewBean.setName("Test picklist");
		picklistViewBean.setCode("Test picklist Code");
		picklistViewBean.setClassificationStandard(CIMSConstants.CCI);

		PickList pickList = refsetService.insertPickList(picklistViewBean);
		assertNotNull(pickList);
		assertTrue(pickList.listColumns().size() == 1);
		assertTrue(pickList.getName().equals("Test picklist"));
		assertTrue(pickList.getCode().equals("Test picklist Code"));
		assertTrue(pickList.getClassificationStandard().equals(CIMSConstants.CCI));
		pickList.setName("Test picklist name1");
		assertTrue(pickList.getName().equals("Test picklist name1"));

		PickListViewBean picklistViewBeanDup = new PickListViewBean();
		picklistViewBeanDup.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		picklistViewBeanDup.setElementId(refset1.getElementIdentifier().getElementId());
		picklistViewBeanDup.setElementVersionId(refset1.getElementIdentifier().getElementVersionId());
		picklistViewBeanDup.setName("Test picklist");
		picklistViewBeanDup.setCode("Test picklist Code");
		picklistViewBeanDup.setClassificationStandard(CIMSConstants.CCI);

		try {
			refsetService.insertPickList(picklistViewBeanDup);
		} catch (DuplicateCodeNameException e) {
			DuplicateCodeNameException e1 = new DuplicateCodeNameException(e.getMessage());
			assertTrue(e1.getMessage().equals("The code: Test picklist Code aleady exists."));
		}

		PickListViewBean picklistViewBean1 = new PickListViewBean();
		picklistViewBean1.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		picklistViewBean1.setElementId(refset1.getElementIdentifier().getElementId());
		picklistViewBean1.setElementVersionId(refset1.getElementIdentifier().getElementVersionId());
		picklistViewBean1.setName("Test picklist1");
		picklistViewBean1.setCode("Test picklist Code1");
		picklistViewBean1.setClassificationStandard(CIMSConstants.ICD_10_CA);
		PickList pickList1 = refsetService.insertPickList(picklistViewBean1);

		try {
			pickList1.setName("Test picklist");
		} catch (DuplicateCodeNameException e) {
			DuplicateCodeNameException e1 = new DuplicateCodeNameException(e.getMessage());
			assertTrue(e1.getMessage().equals("The name: Test picklist aleady exists."));
		}

		PickListViewBean picklistViewBeanICD = new PickListViewBean();
		picklistViewBeanICD.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		picklistViewBeanICD.setElementId(refset1.getElementIdentifier().getElementId());
		picklistViewBeanICD.setElementVersionId(refset1.getElementIdentifier().getElementVersionId());
		picklistViewBeanICD.setPicklistElementId(pickList1.getElementIdentifier().getElementId());
		picklistViewBeanICD.setPicklistElementVersionId(pickList1.getElementIdentifier().getElementVersionId());

		PickList pickListICD = picklistService.getPickList(picklistViewBeanICD);
		assertTrue(pickListICD != null);
		assertTrue(pickListICD.getClassificationStandard().equals(CIMSConstants.ICD_10_CA));

		List<Column> defaultColumns = pickListICD.listColumns();
		assertTrue(defaultColumns.size() == 1);
		Column icdColumn = defaultColumns.get(0);
		PickListColumnBean columnICD = new PickListColumnBean();
		columnICD.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		columnICD.setColumnElementId(icdColumn.getElementIdentifier().getElementId());
		columnICD.setColumnElementVersionId(icdColumn.getElementIdentifier().getElementVersionId());

		picklistViewBeanICD.setName("New Test Name");
		picklistService.savePicklist(picklistViewBeanICD);

		PickListColumnBean sublistColumnBean = new PickListColumnBean();
		sublistColumnBean.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		sublistColumnBean.setPicklistElementId(pickListICD.getElementIdentifier().getElementId());
		sublistColumnBean.setPicklistElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());
		sublistColumnBean.setContainerElementId(pickListICD.getElementIdentifier().getElementId());
		sublistColumnBean.setContainerElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());

		AvailableColumnTypeResponse response = picklistService.getAvailableColumnTypes(sublistColumnBean);
		assertTrue((response != null) && (response.getAvailableColumnTypes().size() == 35));
		assertTrue(!response.isMultipleColumnSublistExists());

		sublistColumnBean.setColumnOrder(2);
		sublistColumnBean.setColumnType("Dummy Column Type");
		sublistColumnBean.setRevisedColumnName("Sublist Column");
		try {
			picklistService.addColumn(sublistColumnBean);
		} catch (ColumnTypeWrongException e) {
			assertTrue(e.getMessage().equals("Column type: Dummy Column Type not exists."));
		}
		sublistColumnBean.setColumnType(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());
		ElementIdentifier sublistColumnEId = picklistService.addColumn(sublistColumnBean);

		sublistColumnBean.setColumnElementId(sublistColumnEId.getElementId());
		sublistColumnBean.setColumnElementVersionId(sublistColumnEId.getElementVersionId());
		sublistColumnBean.setColumnOrder(3);
		sublistColumnBean.setRevisedColumnName("Sublist 1");

		picklistService.saveColumn(sublistColumnBean);

		Column column1 = picklistService.getColumn(sublistColumnBean);
		assertTrue(column1.getColumnName().equals("Sublist 1"));
		assertTrue(column1.getColumnOrder().equals((short) 3));
		assertTrue(column1.getColumnType().equals(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay()));

		PickListColumnBean columnBeanSCTConceptID = new PickListColumnBean();
		columnBeanSCTConceptID.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		columnBeanSCTConceptID.setPicklistElementId(pickListICD.getElementIdentifier().getElementId());
		columnBeanSCTConceptID.setPicklistElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());
		columnBeanSCTConceptID.setContainerElementId(column1.getElementIdentifier().getElementId());
		columnBeanSCTConceptID.setContainerElementVersionId(column1.getElementIdentifier().getElementVersionId());
		columnBeanSCTConceptID.setContainerSublist(true);

		columnBeanSCTConceptID.setColumnOrder(4);
		columnBeanSCTConceptID.setColumnType(ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay());
		columnBeanSCTConceptID.setRevisedColumnName(ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay());

		ElementIdentifier columnSCTConceptIDEId = picklistService.addColumn(columnBeanSCTConceptID);
		columnBeanSCTConceptID.setColumnElementId(columnSCTConceptIDEId.getElementId());
		columnBeanSCTConceptID.setColumnElementVersionId(columnSCTConceptIDEId.getElementVersionId());

		PickListColumnBean columnBean2 = new PickListColumnBean();
		columnBean2.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		columnBean2.setPicklistElementId(pickListICD.getElementIdentifier().getElementId());
		columnBean2.setPicklistElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());
		columnBean2.setContainerElementId(column1.getElementIdentifier().getElementId());
		columnBean2.setContainerElementVersionId(column1.getElementIdentifier().getElementVersionId());
		columnBean2.setContainerSublist(true);

		columnBean2.setColumnOrder(5);
		columnBean2.setColumnType(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());
		columnBean2.setRevisedColumnName(ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay());

		ElementIdentifier columnSubEid = picklistService.addColumn(columnBean2);
		columnBean2.setColumnElementId(columnSubEid.getElementId());
		columnBean2.setColumnElementVersionId(columnSubEid.getElementVersionId());

		// regular column
		picklistService.checkColumnRemovable(sublistColumnBean);

		// ICD Column
		ObjectError errorICD = picklistService.checkColumnRemovable(columnICD);
		assertTrue((errorICD != null) && errorICD.getDefaultMessage().equals(ColumnImpl.CIMS_COLUMN_CANNOT_DELETE));

		// SCT_CONCEPT_ID
		ObjectError error = picklistService.checkColumnRemovable(columnBeanSCTConceptID);
		assertTrue((error != null) && error.getDefaultMessage().equals(ColumnImpl.SCT_CONCEPT_ID_CANNOT_DELETE));

		PickListColumnBean columnSublist2 = new PickListColumnBean();
		columnSublist2.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		columnSublist2.setPicklistElementId(pickListICD.getElementIdentifier().getElementId());
		columnSublist2.setPicklistElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());
		columnSublist2.setContainerElementId(pickListICD.getElementIdentifier().getElementId());
		columnSublist2.setContainerElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());

		columnSublist2.setColumnOrder(6);
		columnSublist2.setColumnType(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());
		columnSublist2.setRevisedColumnName("Sublist Column2");

		ElementIdentifier sublist2Eid = picklistService.addColumn(columnSublist2);
		columnSublist2.setElementId(sublist2Eid.getElementId());
		columnSublist2.setElementVersionId(sublist2Eid.getElementVersionId());

		picklistService.getAvailableColumnTypes(columnSublist2);

		PickListColumnBean subColumn1 = new PickListColumnBean();
		subColumn1.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		subColumn1.setPicklistElementId(pickListICD.getElementIdentifier().getElementId());
		subColumn1.setPicklistElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());
		subColumn1.setContainerElementId(sublist2Eid.getElementId());
		subColumn1.setContainerElementVersionId(sublist2Eid.getElementVersionId());
		subColumn1.setColumnOrder(7);
		subColumn1.setColumnType(ColumnType.NOTE_ENG.getColumnTypeDisplay());
		subColumn1.setRevisedColumnName(ColumnType.NOTE_ENG.getColumnTypeDisplay());
		subColumn1.setContainerSublist(true);

		ElementIdentifier subColumn1SavedEid = picklistService.addColumn(subColumn1);
		subColumn1.setColumnElementId(subColumn1SavedEid.getElementId());
		subColumn1.setColumnElementVersionId(subColumn1SavedEid.getElementVersionId());
		picklistService.getAvailableColumnTypes(subColumn1);

		picklistService.generatePicklistTable(picklistViewBeanICD);

		PickListViewBean dummyBean = new PickListViewBean();
		dummyBean.setContextId(picklistViewBeanICD.getContextId());
		picklistService.generatePicklistTable(dummyBean);

		picklistService.deleteColumn(columnBean2);

		// SCT_CONCEPT_ID removable
		ObjectError error1 = picklistService.checkColumnRemovable(columnBeanSCTConceptID);
		assertTrue((error1 == null));

		PickListColumnBean sublistColumnBeanNested = new PickListColumnBean();
		sublistColumnBeanNested.setContextId(refset1.getContextElementIdentifier().getElementVersionId());
		sublistColumnBeanNested.setPicklistElementId(pickListICD.getElementIdentifier().getElementId());
		sublistColumnBeanNested.setPicklistElementVersionId(pickListICD.getElementIdentifier().getElementVersionId());
		sublistColumnBeanNested.setContainerElementId(column1.getElementIdentifier().getElementId());
		sublistColumnBeanNested.setContainerElementVersionId(column1.getElementIdentifier().getElementVersionId());

		sublistColumnBeanNested.setColumnOrder(2);
		sublistColumnBeanNested.setColumnType(ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay());
		sublistColumnBeanNested.setRevisedColumnName("Sublist Column");

		try {
			picklistService.addColumn(sublistColumnBeanNested);
		} catch (ColumnTypeWrongException e) {
			assertTrue(e.getMessage().equals("Sublist column type can not be nested."));
		}

		picklistService.deleteColumn(sublistColumnBean);

		picklistViewBean.setPicklistElementId(pickList.getElementIdentifier().getElementId());
		picklistViewBean.setPicklistElementVersionId(pickList.getElementIdentifier().getElementVersionId());
		picklistService.deletePickList(picklistViewBean);

	}

	@Test
	public void testGetCategoryList() throws Exception {
		assertTrue(refsetService.getCategoryList().size() > 0);
	}

	@Test
	public void testGetCCIYearList() throws Exception {
		assertTrue(refsetService.getCCIYearList().size() > 0);
	}

	@Test
	public void testGetDefaultEffectiveYearFrom() throws Exception {
		assertTrue(refsetService.getDefaultEffectiveYearFrom() > 2000);
	}

	@Test
	public void testGetDefaultEffectiveYearTo() throws Exception {
		assertTrue(refsetService.getDefaultEffectiveYearTo() > 2000);
	}

	@Test
	public void testGetEffectiveYearFromList() throws Exception {
		List<Integer> fromYearList = refsetService.getEffectiveYearFromList(5);
		assertTrue(fromYearList.size() == 11);
	}

	@Test
	public void testGetEffectiveYearToList() throws Exception {
		assertTrue(refsetService.getEffectiveYearToList(5).size() == 11);
	}

	@Test
	public void testGetICD10CAYearList() throws Exception {
		assertTrue(refsetService.getICD10CAYearList().size() > 0);
	}

	@Test
	public void testGetRefsetAssigneeRecipents() {
		assertTrue(refsetService.getRefsetAssigneeRecipents("lzhu").size() >= 0);
	}

	@Test
	public void testGetRefsetVersionName() {
		assertTrue(RefsetUtils.getRefsetVersionName("aacode", 2016, 2017, "v1.0").equals("aacode 2016-2017 v1.0"));
		assertTrue(RefsetUtils.getRefsetVersionName("aacode", 2016, null, "v1.0").equals("aacode 2016 v1.0"));
	}

	@Test
	public void testGetRefsetVersions() throws Exception {
		List<RefsetVersion> refsetVersions = refsetService.getRefsetVersions();
		assertTrue(refsetVersions.size() >= 0);
		for (RefsetVersion version : refsetVersions) {
			assertTrue(!version.getRefsetName().isEmpty());
		}
	}

	@Test
	public void testGetRefsetVersions2() throws Exception {
		List<RefsetVersion> refsetVersion = refsetService.getRefsetVersions(new Long(0));
		assertTrue(refsetVersion.size() == 0);
	}

	@Test
	public void testGetSCTVersionList() throws Exception {
		assertTrue(refsetService.getSCTVersionList().size() >= 0);
	}

	@Test
	public void testGetVersionDescByCode() throws Exception {
		assertTrue(refsetService.getVersionDescByCode("IE20160131").equals("International Edition 20160131"));
	}

	/*
	@Test
	public void testIsLatestClosedRefsetVersion(){
		assertFalse(refsetService.isLatestClosedRefsetVersion(new Long(0)));
	}
	*/
	
}
