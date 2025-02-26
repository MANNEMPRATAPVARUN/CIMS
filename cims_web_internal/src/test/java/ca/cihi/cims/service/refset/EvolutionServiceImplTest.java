package ca.cihi.cims.service.refset;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;

import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse;
import ca.cihi.cims.refset.dto.PicklistColumnConfigEvolutionDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionRequestDTO;
import ca.cihi.cims.refset.dto.PicklistColumnEvolutionResultDTO;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.exception.ColumnTypeWrongException;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.refset.service.concept.Sublist;
import ca.cihi.cims.refset.service.concept.Value;
import ca.cihi.cims.refset.service.factory.RefsetFactory;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class EvolutionServiceImplTest {

	private Refset refset;
	private Refset refset2;
	private long baseRefsetContextId;
	private long refsetContextId;
	private long picklistElementId;
	@Autowired
	private EvolutionService evolutionService;
	
	private PicklistColumnEvolutionRequestDTO picklistColumnEvolutionRequest;
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		prepareData();
	    baseRefsetContextId = refset.getContextElementIdentifier().getElementVersionId();
		refsetContextId = refset2.getContextElementIdentifier().getElementVersionId();
		picklistElementId = refset2.listPickLists().get(0).getElementIdentifier().getElementId();
		picklistColumnEvolutionRequest = new PicklistColumnEvolutionRequestDTO();
		
		//picklistColumnEvolutionRequest.setBaseRefsetContextId(new Long(5915819));
		//picklistColumnEvolutionRequest.setPicklistElementId(new Long(2728620));
		//picklistColumnEvolutionRequest.setRefsetContextId(new Long(5916005));
		
		picklistColumnEvolutionRequest.setBaseRefsetContextId(baseRefsetContextId);
		picklistColumnEvolutionRequest.setPicklistElementId(picklistElementId);
		picklistColumnEvolutionRequest.setRefsetContextId(refsetContextId);
		picklistColumnEvolutionRequest.setPicklistOutputId(new Long(262));
	}
	
	@Test
	public void testGetPicklistColumnEvolutionList(){
		List<PicklistColumnEvolutionResultDTO> resultList = evolutionService.getPicklistColumnEvolutionList(picklistColumnEvolutionRequest);
		assertTrue(resultList.size()>=0);
	}
	
	@Test
	public void testGetPicklistColumnConfigEvolutionList(){
		List<PicklistColumnConfigEvolutionDTO> resultList = evolutionService.getPicklistColumnConfigEvolutionList(picklistColumnEvolutionRequest);
		assertTrue(resultList.size()>=0);
	}
	
	@Test
	public void testVerifyPicklistOutputConfig(){
		assertFalse(evolutionService.verifyPicklistOutputConfig(refsetContextId, refset2.getElementIdentifier().getElementId(), refset2.getElementIdentifier().getElementVersionId(), picklistElementId));
	}
	
	
	private void prepareData() throws Exception {

		refset = RefsetFactory.createRefset("lytest_un", "lytest Refset Name_un");
		refset.setCategoryId(1L);
		refset.getCategoryName(1L);
		refset.assign(0L);
		refset.setICD10CAContextId(3103461L);
		refset.setICD10CAYear("2015");
		refset.setDefinition("This is test");
		refset.setEffectiveYearFrom((short) 2015);
		refset.setEffectiveYearTo((short) 2018);
		refset.setCCIContextId(4205295L);
		refset.setCCIYear("2015");
		refset.setNotes("Note test");
		refset.setSCTVersionCode("IE20120131");
		List<RefsetVersion> versions = RefsetFactory.getRefsetVersions(new Long(1), "ACTIVE", "OPEN");
		Long contextId = refset.getContextElementIdentifier().getElementVersionId();
		String code = "pkcode";
		String classificationStandard = "ICD-10-CA";
		String name = "First picklist";
		PickList picklist = RefsetFactory.createPickList(refset, code, name, classificationStandard);
		picklist.setName("First picklist");

		PickList picklist2 = RefsetFactory.getPickList(contextId, picklist.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);
		List<PickList> pickLists = refset.listPickLists();

		// Column configuration
		Column column1 = RefsetFactory.createColumn(picklist, ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay(),
				"column1", (short) 1);

		ObjectError error = column1.removable();

		Column column2 = RefsetFactory.createColumn(picklist, ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay(),
				"column2", (short) 2);
		Column column3 = RefsetFactory.createColumn(column2, ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay(),
				"column3", (short) 3);
		column3.setColumnOrder((short) 3);

		Column column4 = RefsetFactory.createColumn(column2, ColumnType.DESC_COMMON_TERM_ENG.getColumnTypeDisplay(),
				"Desc ENG", (short) 4);
		column4.setColumnName("Desc (ENG)");

		Column column5 = RefsetFactory.createColumn(column2, ColumnType.SCT_DESCRIPTION.getColumnTypeDisplay(),
				"SCT Description", (short) 5);
		Column column6 = RefsetFactory.createColumn(picklist,
				ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getColumnTypeDisplay(),
				ColumnType.CIMS_ICD10CA_DESCRIPTION_ENG.getColumnTypeDisplay(), (short) 6);
		ObjectError error1 = column5.removable();

		ObjectError error2 = column3.removable();
		
		try {
			RefsetFactory.createColumn(picklist, "Dummy Type", "Name", (short) 5);
		} catch (ColumnTypeWrongException e) {
			assertTrue(e.getMessage().equals("Column type: Dummy Type not exists."));
		}
		try {
			RefsetFactory.createColumn(column2, ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay(), "Name", (short) 5);
		} catch (ColumnTypeWrongException e) {
			assertTrue(e.getMessage().equals("Sublist column type can not be nested."));
		}
		try {
			RefsetFactory.createColumn(refset, ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay(), "Name", (short) 5);
		} catch (ColumnTypeWrongException e) {
			assertTrue(
					e.getMessage().equals("Container type wrong: " + ColumnType.SUBLIST_COLUMN.getColumnTypeDisplay()));
		}

		Column columnLoaded = RefsetFactory.getColumn(contextId, column3.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);
		// common record
		Record record = RefsetFactory.createRecord(picklist, "B95.2");
		Value value1 = RefsetFactory.createValue(record, column1);
		value1.setTextValue("B95.2");
		value1.setIdValue(16309L);
	
		Value value6 = RefsetFactory.createValue(record, column6);
		value6.setTextValue("B95.2 Text");
		value6.setIdValue(16309L);

		RefsetFactory.createColumn(picklist, ColumnType.ICD10CA_CODE.getColumnTypeDisplay(), "ICD Code", (short) 7);

		// sublist

		Sublist sublist = RefsetFactory.createSublist(record, column2);

		Record record1 = RefsetFactory.createRecord(sublist, null);

		Record record2 = RefsetFactory.getRecord(contextId, record1.getElementIdentifier(), ConceptLoadDegree.REGULAR);

		Value value2 = RefsetFactory.createValue(record1, column3);
		value2.setTextValue("713024005");
		value2.setIdValue(713024005L);

		Value value3 = RefsetFactory.getValue(contextId, value2.getElementIdentifier(), ConceptLoadDegree.REGULAR);

		Value value4 = RefsetFactory.getValueByRecordAndColumn(contextId, record1.getElementIdentifier().getElementId(),
				column3.getElementIdentifier().getElementId());

		Value value5 = RefsetFactory.createValue(record1, column4);
		value5.setTextValue("Text to Search");
		value5.setIdValue(3103461L);

		List<String> commonTermSearch = RefsetFactory.searchCommonTerm("Tex", column4.getColumnType(), 3103461L, 10);
	
		List<ClassificationCodeSearchReponse> searchResponse = RefsetFactory.getActiveClassificationByCode(3103461L, 6L,
				"ICD-10-CA", "A0%", 10);

		List<Value> values = record2.listValues();

		List<Column> columns = picklist.listColumns();

		Record recordSecond = RefsetFactory.createRecord(picklist, "A03.9");

		Value value1Second = RefsetFactory.createValue(recordSecond, column1);
		value1Second.setTextValue("A03.9");
		value1Second.setIdValue(458L);

		Sublist sublistSecond = RefsetFactory.createSublist(recordSecond, column2);

		Record record1Second = RefsetFactory.createRecord(sublistSecond, null);

		Value value2Second = RefsetFactory.createValue(record1Second, column3);
		value2Second.setTextValue("712824002");
		value2Second.setIdValue(712824002L);

		Value value3Second = RefsetFactory.createValue(record1Second, column5);
		value3Second.setTextValue("adfadf");
		value3Second.setIdValue(712824052L);

		List<LightRecord> records = picklist.listRecords();

		List<Value> values1 = record.listValues();

		Sublist sublist1 = RefsetFactory.getSublist(contextId, sublist.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		Sublist sublist2 = RefsetFactory.getSublist(contextId, column2.getElementIdentifier(),
				record.getElementIdentifier(), ConceptLoadDegree.MINIMAL);

		List<LightRecord> sublistRecords = sublist.listRecords();

		List<LightRecord> sublistRecords1 = sublistSecond.listRecords();

		PickList pickCCI = RefsetFactory.createPickList(refset, "pickCCI", "Pick CCI Name", "CCI");

		Column cciCodeColumn = RefsetFactory.createColumn(pickCCI, ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay(),
				ColumnType.CIMS_CCI_CODE.getColumnTypeDisplay(), (short) 1);

		Record cciRecord = RefsetFactory.createRecord(pickCCI, "1.FE.35.JA-P2");

		Value cciValue = RefsetFactory.createValue(cciRecord, cciCodeColumn);
		cciValue.setIdValue(1199168L);
		cciValue.setTextValue("1.FE.35.JA-P2");

		refset.closeRefsetVersion();

		Refset refsetNewVersion = refset.createNewVersion("v1.1", 5245284L, 5245260L, "IE20150731");
		refsetNewVersion.setCCIYear("2018");
		refsetNewVersion.setICD10CAYear("2018");
		refset2 = RefsetFactory.getRefset(refsetNewVersion.getContextElementIdentifier().getElementVersionId(),
				refsetNewVersion.getElementIdentifier(), ConceptLoadDegree.COMPLETE);

		PickList pickListICDNewVersion = RefsetFactory.getPickList(
				refset2.getContextElementIdentifier().getElementVersionId(), picklist.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		Sublist sublistICDNewVersion = RefsetFactory.getSublist(
				refset2.getContextElementIdentifier().getElementVersionId(), sublistSecond.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		PickList pickCCINewVersion = RefsetFactory.getPickList(
				refset2.getContextElementIdentifier().getElementVersionId(), pickCCI.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		refset2.disableRefset();
		refset2.enableRefset();
	}

}
