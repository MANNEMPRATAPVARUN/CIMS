package ca.cihi.cims.refset.service.factory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.ObjectError;

import ca.cihi.cims.framework.enums.ConceptLoadDegree;
import ca.cihi.cims.framework.enums.Language;
import ca.cihi.cims.refset.dto.ClassificationCodeSearchReponse;
import ca.cihi.cims.refset.dto.PicklistColumnOutputDTO;
import ca.cihi.cims.refset.enums.ColumnType;
import ca.cihi.cims.refset.enums.RefsetStatus;
import ca.cihi.cims.refset.exception.ColumnTypeWrongException;
import ca.cihi.cims.refset.exception.DuplicateCodeNameException;
import ca.cihi.cims.refset.service.concept.Column;
import ca.cihi.cims.refset.service.concept.LightRecord;
import ca.cihi.cims.refset.service.concept.PickList;
import ca.cihi.cims.refset.service.concept.Record;
import ca.cihi.cims.refset.service.concept.Refset;
import ca.cihi.cims.refset.service.concept.RefsetVersion;
import ca.cihi.cims.refset.service.concept.Sublist;
import ca.cihi.cims.refset.service.concept.Supplement;
import ca.cihi.cims.refset.service.concept.Value;

@Rollback
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath:spring/applicationContext-test.xml" })
public class RefsetFactoryTest {

	Logger LOGGER = LogManager.getLogger(this.getClass());

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Test
	public void refsetFactoryIntegrationTest() throws Exception {

		Refset refset = RefsetFactory.createRefset("lytest_un", "lytest Refset Name_un");
		assertTrue(refset.getCode().equals("lytest_un"));
		assertTrue(refset.getName(Language.ENG).equals("lytest Refset Name_un"));
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
		assertTrue(refset.getAssignee() == 0);
		List<RefsetVersion> versions = RefsetFactory.getRefsetVersions(new Long(1), "ACTIVE", "OPEN");
		assertTrue(versions.size() == 1);

		Long contextId = refset.getContextElementIdentifier().getElementVersionId();

		String code = "pkcode";
		String classificationStandard = "ICD-10-CA";
		String name = "First picklist";
		PickList picklist = RefsetFactory.createPickList(refset, code, name, classificationStandard);
		picklist.setName("First picklist");

		PickList picklist2 = RefsetFactory.getPickList(contextId, picklist.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);
		assertTrue(picklist2.getCode().equals("pkcode"));
		assertTrue(picklist2.getClassificationStandard().equals("ICD-10-CA"));
		assertTrue(picklist2.getName().equals("First picklist"));

		List<PickList> pickLists = refset.listPickLists();
		assertTrue(pickLists.size() == 1);

		// Column configuration
		Column column1 = RefsetFactory.createColumn(picklist, ColumnType.CIMS_ICD10CA_CODE.getColumnTypeDisplay(),
				"column1", (short) 1);
		assertTrue(column1 != null);

		ObjectError error = column1.removable();

		assertTrue(error.getDefaultMessage().equals("CIMS Classification Code Column is a mandatory Column"));

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
		assertTrue(error1 == null);

		ObjectError error2 = column3.removable();
		assertTrue(error2.getDefaultMessage().equals(
				"The SCT Concept ID Column Type cannot be deleted unless the associated SNOMED CT Terms are deleted"));

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
		assertTrue(columnLoaded.getColumnName().equals("column3"));
		assertTrue(columnLoaded.getColumnOrder() == (short) 3);
		assertTrue(columnLoaded.getColumnType().equals(ColumnType.SCT_CONCEPT_ID.getColumnTypeDisplay()));

		// common record
		Record record = RefsetFactory.createRecord(picklist, "B95.2");
		Value value1 = RefsetFactory.createValue(record, column1);
		value1.setTextValue("B95.2");
		value1.setIdValue(16309L);
		assertTrue(value1.getTextValue().equals("B95.2"));
		assertTrue(value1.getIdValue() == 16309L);

		Value value6 = RefsetFactory.createValue(record, column6);
		value6.setTextValue("B95.2 Text");
		value6.setIdValue(16309L);

		RefsetFactory.createColumn(picklist, ColumnType.ICD10CA_CODE.getColumnTypeDisplay(), "ICD Code", (short) 7);

		// sublist

		Sublist sublist = RefsetFactory.createSublist(record, column2);

		Record record1 = RefsetFactory.createRecord(sublist, null);

		Record record2 = RefsetFactory.getRecord(contextId, record1.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		assertTrue(record2 != null);

		Value value2 = RefsetFactory.createValue(record1, column3);
		value2.setTextValue("713024005");
		value2.setIdValue(713024005L);

		Value value3 = RefsetFactory.getValue(contextId, value2.getElementIdentifier(), ConceptLoadDegree.REGULAR);
		assertTrue(value3 != null);

		Value value4 = RefsetFactory.getValueByRecordAndColumn(contextId, record1.getElementIdentifier().getElementId(),
				column3.getElementIdentifier().getElementId());
		assertTrue(value4.getElementIdentifier().equals(value3.getElementIdentifier()));

		Value value5 = RefsetFactory.createValue(record1, column4);
		value5.setTextValue("Text to Search");
		value5.setIdValue(3103461L);

		List<String> commonTermSearch = RefsetFactory.searchCommonTerm("Tex", column4.getColumnType(), 3103461L, 10);
		assertTrue((commonTermSearch != null) && (commonTermSearch.size() == 1)
				&& commonTermSearch.get(0).equals("Text to Search"));

		List<ClassificationCodeSearchReponse> searchResponse = RefsetFactory.getActiveClassificationByCode(3103461L, 6L,
				"ICD-10-CA", "A0%", 10);

		assertTrue(searchResponse.size() == 10);

		List<Value> values = record2.listValues();
		assertTrue(values.size() == 2);

		List<Column> columns = picklist.listColumns();
		assertTrue(columns.size() == 4);

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

		assertTrue(records.size() == 2);

		List<Value> values1 = record.listValues();
		assertTrue(values1.size() == 3);

		Sublist sublist1 = RefsetFactory.getSublist(contextId, sublist.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		assertTrue(sublist1 != null);

		Sublist sublist2 = RefsetFactory.getSublist(contextId, column2.getElementIdentifier(),
				record.getElementIdentifier(), ConceptLoadDegree.MINIMAL);

		assertTrue((sublist2 != null) && sublist2.getElementIdentifier().equals(sublist1.getElementIdentifier()));

		List<LightRecord> sublistRecords = sublist.listRecords();
		assertTrue(sublistRecords.get(0).getValues().values().size() == 2);

		List<LightRecord> sublistRecords1 = sublistSecond.listRecords();
		assertTrue(sublistRecords1.get(0).getValues().values().size() == 2);

		byte[] content = null;
		try {
			Resource schemaFile = new ClassPathResource("/stylesheet/cims_index.xsl", this.getClass());
			content = IOUtils.toByteArray(schemaFile.getInputStream());
			// supplement.setContent(content, Language.ENG);

		} catch (IOException e) {

			e.printStackTrace();
		}

		Supplement supplement = RefsetFactory.createSupplement(refset, "agreementdoc1", "Test Supplement", "file1.xlsx",
				content);
		assertTrue(supplement.getCode().equals("agreementdoc1"));
		supplement.setName("Test Supplement");
		supplement.setFilename("file1.xlsx");
		supplement.setContent(content, Language.ENG);

		Supplement supplementLoaded = RefsetFactory.getSupplement(
				refset.getContextElementIdentifier().getElementVersionId(), supplement.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);
		assertTrue(supplementLoaded.getName().equals("Test Supplement"));
		assertTrue(supplementLoaded.getFilename().equals("file1.xlsx"));
		byte[] contentLoaded = supplementLoaded.getContent(Language.ENG);
		assertTrue(Arrays.equals(contentLoaded, content));

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
		assertTrue(refsetNewVersion.getVersionCode().equals("v1.1"));
		assertTrue(refsetNewVersion.getCode().equals("lytest_un"));
		assertTrue(refsetNewVersion.getName(Language.ENG).equals("lytest Refset Name_un"));
		assertTrue(refsetNewVersion.getCCIContextId() == 5245260L);
		assertTrue(refsetNewVersion.getICD10CAContextId() == 5245284L);

		Refset refset2 = RefsetFactory.getRefset(refsetNewVersion.getContextElementIdentifier().getElementVersionId(),
				refsetNewVersion.getElementIdentifier(), ConceptLoadDegree.COMPLETE);

		assertTrue(refset2.getAssignee() == 0);
		assertTrue(refset2.getAssigneeName().equals("system"));
		assertTrue(refset2.getStatus().equals(RefsetStatus.ACTIVE.getStatus()));
		assertTrue(refset2.getCategoryId() == 1);

		assertTrue(refset2.getCCIContextId() == 5245260L);
		assertTrue(refset2.getCCIYear().equals("2018"));
		assertTrue(refset2.getDefinition().equals("This is test"));
		assertTrue(refset2.getEffectiveYearFrom() == 2015);
		assertTrue(refset2.getEffectiveYearTo() == 2018);
		assertTrue(refset2.getICD10CAContextId() == 5245284L);
		assertTrue(refset2.getICD10CAYear().equals("2018"));
		assertTrue(refset2.getNotes().equals("Note test"));
		assertTrue(refset2.getSCTVersionCode().equals("IE20150731"));

		assertTrue(refset2.isOpenVersionExists());
		assertTrue(!refset2.isLatestClosedVersion());

		PickList pickListICDNewVersion = RefsetFactory.getPickList(
				refset2.getContextElementIdentifier().getElementVersionId(), picklist.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		assertTrue(pickListICDNewVersion.listRecords().size() == 2);

		Sublist sublistICDNewVersion = RefsetFactory.getSublist(
				refset2.getContextElementIdentifier().getElementVersionId(), sublistSecond.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);
		assertTrue(sublistICDNewVersion.listRecords().size() == 0);

		PickList pickCCINewVersion = RefsetFactory.getPickList(
				refset2.getContextElementIdentifier().getElementVersionId(), pickCCI.getElementIdentifier(),
				ConceptLoadDegree.REGULAR);

		refset2.disableRefset();
		refset2.enableRefset();
	}

	@Test
	public void testDuplicateCodeException() throws DuplicateCodeNameException {

		thrown.expect(DuplicateCodeNameException.class);
		RefsetFactory.createRefset("lytest_un", "lytest Refset Name_un");
		RefsetFactory.createRefset("lytest_un", "lytest New Name");
	}

	@Test
	public void testDuplicateNameException() throws DuplicateCodeNameException {

		thrown.expect(DuplicateCodeNameException.class);
		RefsetFactory.createRefset("lytest_un", "lytest Refset Name_un");
		RefsetFactory.createRefset("lytest1", "lytest Refset Name_un");
	}

	@Test
	public void testGetPicklistColumnOutputConfig() {
		List<PicklistColumnOutputDTO> testList = RefsetFactory.getPicklistColumnOutputConfig(0l, 0l);
		assertTrue(testList.size() == 0);
	}
	
	@Test
	public void testGetConceptStatus(){
		String status = RefsetFactory.getConceptStatus("A00",3103461l, 3103461l);
		assertFalse(status.equals("ACTIVE1"));
	}
}
