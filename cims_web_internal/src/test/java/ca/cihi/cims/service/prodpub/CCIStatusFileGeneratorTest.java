package ca.cihi.cims.service.prodpub;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.data.mapper.PublicationMapper;
import ca.cihi.cims.model.prodpub.CCIGenericAttribute;
import ca.cihi.cims.model.prodpub.CCIReferenceAttribute;
import ca.cihi.cims.model.prodpub.FileFormat;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationStatistics;
import ca.cihi.cims.service.ConceptService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CCIStatusFileGeneratorTest {

	@Mock
	PublicationMapper publicationMapper;

	@Autowired
	ConceptService conceptService;

	@Value("${cims.publication.classification.tables.dir}")
	private String pubDirectory;

	CCIStatusFileGenerator fileGenerator = null;

	private List<CCIReferenceAttribute> mockCCIReferenceAttributes() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("E12");
		attribute1.setCodeType("Status");
		attribute1.setMandatoryIndicator("N");
		List<CCIGenericAttribute> attributes1 = new ArrayList<CCIGenericAttribute>();
		attribute1.setGenericAttributes(attributes1);

		CCIReferenceAttribute attribute2 = new CCIReferenceAttribute();
		attribute2.setCode("E13");
		attribute2.setCodeType("Status");
		attribute2.setMandatoryIndicator("Y");
		List<CCIGenericAttribute> attributes2 = new ArrayList<CCIGenericAttribute>();
		attribute2.setGenericAttributes(attributes2);
		CCIGenericAttribute genericAttribute1 = new CCIGenericAttribute();
		genericAttribute1.setCode("A");
		genericAttribute1.setDescription("Test1");
		attributes2.add(genericAttribute1);

		CCIGenericAttribute genericAttribute2 = new CCIGenericAttribute();
		genericAttribute2.setCode("B");
		genericAttribute2.setDescription("Test2");
		attributes2.add(genericAttribute2);

		CCIReferenceAttribute attribute3 = new CCIReferenceAttribute();
		attribute3.setCode("E14");
		attribute3.setCodeType("Status");
		attribute3.setMandatoryIndicator("N");
		List<CCIGenericAttribute> attributes3 = new ArrayList<CCIGenericAttribute>();
		attribute3.setGenericAttributes(attributes3);
		CCIGenericAttribute genericAttribute3 = new CCIGenericAttribute();
		genericAttribute3.setCode("LT");
		genericAttribute3.setDescription("Test3");
		attributes3.add(genericAttribute3);

		attributes.add(attribute3);
		attributes.add(attribute2);
		attributes.add(attribute1);

		return attributes;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		fileGenerator = new CCIStatusFileGenerator();
		fileGenerator.setPublicationMapper(publicationMapper);
		fileGenerator.setPubDirectory(pubDirectory);
		fileGenerator.setConceptService(conceptService);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("referenceAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params.put("genericAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params.put("attributeTypeIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "AttributeTypeIndicator"));
		params.put("attributeCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params.put("attributeDescriptionClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params.put("domainValueCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "DomainValueCode"));
		params.put("attributeMandatoryIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params.put("contextId", 1l);
		params.put("attributeType", "S");
		params.put("languageCode", "ENG");

		when(publicationMapper.getCCIReferenceAttributes(params)).thenReturn(mockCCIReferenceAttributes());

		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("referenceAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ReferenceAttributeCPV"));
		params1.put("genericAttributeCPVClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "GenericAttributeCPV"));
		params1.put("attributeTypeIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "AttributeTypeIndicator"));
		params1.put("attributeCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeCode"));
		params1.put("attributeDescriptionClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "AttributeDescription"));
		params1.put("domainValueCodeClassId",
				conceptService.getCCIClassID(WebConstants.TEXT_PROPERTY_VERSION, "DomainValueCode"));
		params1.put("attributeMandatoryIndicatorClassId",
				conceptService.getCCIClassID(WebConstants.BOOLEAN_PROPERTY_VERSION, "AttributeMandatoryIndicator"));
		params1.put("contextId", 1l);
		params1.put("attributeType", "S");
		params1.put("languageCode", "FRA");
		when(publicationMapper.getCCIReferenceAttributes(params1)).thenReturn(mockCCIReferenceAttributes());
	}

	@Test
	public void testGenerateEnglishFile() throws IOException {
		List<PublicationStatistics> statisticsSummary = new ArrayList<PublicationStatistics>();
		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification("CCI");
		generateTablesModel.setCurrentOpenYear(2016l);
		generateTablesModel.setFileFormat(FileFormat.TAB);

		fileGenerator.generateEnglishFile(1l, generateTablesModel, statisticsSummary);

		File folder = fileGenerator.prepareFolder("CCI", "Validation", "ENG");
		String fileName = CimsFileUtils.buildAsciiFileName("CCI", "Status", "Eng",
				String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", null);

		File cciReferenceDesc = new File(folder.getCanonicalPath() + System.getProperty("file.separator") + fileName);

		assertEquals(true, cciReferenceDesc.isFile());

		assertEquals(1, statisticsSummary.size());

		PublicationStatistics statistics = statisticsSummary.get(0);
		assertEquals(5, statistics.getCount());
	}

	@Test
	public void testGenerateFrenchFile() throws IOException {
		List<PublicationStatistics> statisticsSummary = new ArrayList<PublicationStatistics>();
		GenerateReleaseTablesCriteria generateTablesModel = new GenerateReleaseTablesCriteria();
		generateTablesModel.setClassification("CCI");
		generateTablesModel.setCurrentOpenYear(2016l);
		generateTablesModel.setFileFormat(FileFormat.TAB);

		fileGenerator.generateFrenchFile(1l, generateTablesModel, statisticsSummary);

		File folder = fileGenerator.prepareFolder("CCI", "Validation", "FRA");
		String fileName = CimsFileUtils.buildAsciiFileName("CCI", "Status", "Fra",
				String.valueOf(generateTablesModel.getCurrentOpenYear()), null, "tab", null);

		File cciReferenceDesc = new File(folder.getCanonicalPath() + System.getProperty("file.separator") + fileName);

		assertEquals(true, cciReferenceDesc.isFile());

		assertEquals(1, statisticsSummary.size());

		PublicationStatistics statistics = statisticsSummary.get(0);
		assertEquals(5, statistics.getCount());
	}

	@Test
	public void testGetCCIReferenceAttributes() {
		List<CCIReferenceAttribute> attributes = new ArrayList<CCIReferenceAttribute>();
		CCIReferenceAttribute attribute1 = new CCIReferenceAttribute();
		attribute1.setCode("E12");
		attribute1.setCodeType("Status");
		attribute1.setMandatoryIndicator("N");
		List<CCIGenericAttribute> attributes1 = new ArrayList<CCIGenericAttribute>();
		attribute1.setGenericAttributes(attributes1);

		CCIReferenceAttribute attribute2 = new CCIReferenceAttribute();
		attribute2.setCode("E13");
		attribute2.setCodeType("Status");
		attribute2.setMandatoryIndicator("Y");
		List<CCIGenericAttribute> attributes2 = new ArrayList<CCIGenericAttribute>();
		attribute2.setGenericAttributes(attributes2);
		CCIGenericAttribute genericAttribute1 = new CCIGenericAttribute();
		genericAttribute1.setCode("A");
		genericAttribute1.setDescription("Test1");
		attributes2.add(genericAttribute1);

		CCIGenericAttribute genericAttribute2 = new CCIGenericAttribute();
		genericAttribute2.setCode("B");
		genericAttribute2.setDescription("Test2");
		attributes2.add(genericAttribute2);

		CCIReferenceAttribute attribute3 = new CCIReferenceAttribute();
		attribute3.setCode("E14");
		attribute3.setCodeType("Status");
		attribute3.setMandatoryIndicator("N");
		List<CCIGenericAttribute> attributes3 = new ArrayList<CCIGenericAttribute>();
		attribute3.setGenericAttributes(attributes3);
		CCIGenericAttribute genericAttribute3 = new CCIGenericAttribute();
		genericAttribute3.setCode("LT");
		genericAttribute3.setDescription("Test3");
		attributes3.add(genericAttribute3);

		attributes.add(attribute3);
		attributes.add(attribute2);
		attributes.add(attribute1);

		List<CCIReferenceAttribute> attributesToTest = fileGenerator.getCCIReferenceAttributes("ENG", 1l);
		assertEquals(attributes, attributesToTest);
	}

	@Test
	public void testGetDisabledHeaderDescs() {
		String[] headerDescs = new String[3];
		headerDescs[0] = "Reference";
		headerDescs[1] = "Status";
		headerDescs[2] = "English Disabled Description 2016";

		String[] headerDescsToTest = fileGenerator.getDisabledHeaderDescs("ENG", 2016l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[3];
		headerDescsFra[0] = "Référence";
		headerDescsFra[1] = "Situation";
		headerDescsFra[2] = "Description 2016";

		String[] headerDescsToTestFra = fileGenerator.getDisabledHeaderDescs("FRA", 2016l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetDisabledTitleValue() {

		String titleValue = "2016 Disabled CCI Status English Description";
		String titleValueToTest = fileGenerator.getDisabledTitleValue("ENG", "2016");
		assertEquals(titleValue, titleValueToTest);

		String titleValueFra = "2016 Situation Description désactivées";
		String titleValueToTestFra = fileGenerator.getDisabledTitleValue("FRA", "2016");
		assertEquals(titleValueFra, titleValueToTestFra);

	}

	@Test
	public void testGetNewHeaderDescs() {
		String[] headerDescs = new String[3];
		headerDescs[0] = "Reference";
		headerDescs[1] = "Status";
		headerDescs[2] = "English New Description 2016";

		String[] headerDescsToTest = fileGenerator.getNewHeaderDescs("ENG", 2016l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[3];
		headerDescsFra[0] = "Référence";
		headerDescsFra[1] = "Situation";
		headerDescsFra[2] = "Description 2016";

		String[] headerDescsToTestFra = fileGenerator.getNewHeaderDescs("FRA", 2016l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetNewTitleValue() {

		String titleValue = "2016 New CCI Status English Description";
		String titleValueToTest = fileGenerator.getNewTitleValue("ENG", "2016");
		assertEquals(titleValue, titleValueToTest);

		String titleValueFra = "2016 Situation Description nouveaux";
		String titleValueToTestFra = fileGenerator.getNewTitleValue("FRA", "2016");
		assertEquals(titleValueFra, titleValueToTestFra);

	}

	@Test
	public void testGetRevisedHeaderDescs() {
		String[] headerDescs = new String[4];
		headerDescs[0] = "Reference";
		headerDescs[1] = "Status";
		headerDescs[2] = "English New Description 2016";
		headerDescs[3] = "English Old Description 2015";

		String[] headerDescsToTest = fileGenerator.getRevisedHeaderDescs("ENG", 2016l, 2015l);

		assertArrayEquals(headerDescs, headerDescsToTest);

		String[] headerDescsFra = new String[4];
		headerDescsFra[0] = "Référence";
		headerDescsFra[1] = "Situation";
		headerDescsFra[2] = "Description 2016";
		headerDescsFra[3] = "Description 2015";

		String[] headerDescsToTestFra = fileGenerator.getRevisedHeaderDescs("FRA", 2016l, 2015l);

		assertArrayEquals(headerDescsFra, headerDescsToTestFra);
	}

	@Test
	public void testGetRevisedTitleValue() {

		String titleValue = "2016 CCI Status English Description Revisions";
		String titleValueToTest = fileGenerator.getRevisionsTitleValue("ENG", "2016");
		assertEquals(titleValue, titleValueToTest);

		String titleValueFra = "2016 Situation Description révisées";
		String titleValueToTestFra = fileGenerator.getRevisionsTitleValue("FRA", "2016");
		assertEquals(titleValueFra, titleValueToTestFra);

	}

	@Test
	public void testGetWorksheetName() {
		String worksheetName = "CCI_Status_Eng";
		String worksheetNameToTest = fileGenerator.getWorksheetName("ENG");
		assertEquals(worksheetName, worksheetNameToTest);

		String worksheetNameFra = "CCI_Situation_Fra";
		String worksheetNameToTestFra = fileGenerator.getWorksheetName("FRA");
		assertEquals(worksheetNameFra, worksheetNameToTestFra);
	}
}
