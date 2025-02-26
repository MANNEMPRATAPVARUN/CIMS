package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.model.reports.ModifiedValidationsModel;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ICDModifiedValidationsTest {

	@Mock
	ReportMapper reportMapper;

	@Mock
	LookupService lookupService;

	@Autowired
	ConceptService conceptService;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<ModifiedValidationsModel> mockResults() {
		List<ModifiedValidationsModel> results = new ArrayList<ModifiedValidationsModel>();
		ModifiedValidationsModel model1 = new ModifiedValidationsModel();
		model1.setCodeValue("A00.1");
		model1.setDataHolding("DAD - Acute Care");
		model1.setCurrentYear("2016");
		model1.setPriorYear("2015");
		model1.setCurrentStatus("ACTIVE");
		model1.setPriorStatus("ACTIVE");
		model1.setCurrentXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>16660</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>Y</NEW_BORN></validation>");
		model1.setPriorXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>16660</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>1-99</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>N</NEW_BORN></validation>");

		ModifiedValidationsModel model2 = new ModifiedValidationsModel();
		model2.setCodeValue("D00.1");
		model2.setDataHolding("DAD - Acute Care");
		model2.setCurrentYear("2016");
		model2.setPriorYear("2015");
		model2.setCurrentStatus("DISABLED");
		model2.setPriorStatus("ACTIVE");
		model2.setCurrentXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>16660</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>Y</NEW_BORN></validation>");
		model2.setPriorXml("<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE validation SYSTEM \"/dtd/cihi_cims_validation.dtd\"><validation classification=\"ICD-10-CA\" language=\"\"><ELEMENT_ID>16660</ELEMENT_ID><GENDER_CODE>A</GENDER_CODE><GENDER_DESC_ENG>Male, Female &amp; Other</GENDER_DESC_ENG><GENDER_DESC_FRA>Homme, Femme &amp; Autre</GENDER_DESC_FRA><AGE_RANGE>0-130</AGE_RANGE><MRDX_MAIN>N</MRDX_MAIN><DX_TYPE_1>N</DX_TYPE_1><DX_TYPE_2>N</DX_TYPE_2><DX_TYPE_3>Y</DX_TYPE_3><DX_TYPE_4>N</DX_TYPE_4><DX_TYPE_6>N</DX_TYPE_6><DX_TYPE_9>N</DX_TYPE_9><DX_TYPE_W>N</DX_TYPE_W><DX_TYPE_X>N</DX_TYPE_X><DX_TYPE_Y>N</DX_TYPE_Y><NEW_BORN>Y</NEW_BORN></validation>");

		results.add(model1);
		results.add(model2);

		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new ICDModifiedValidations();
		reportGenerator.setReportMapper(reportMapper);
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setConceptService(conceptService);
		bean = new ReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setCurrentYear("2016");
		bean.setPriorYear("2015");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentYear", bean.getCurrentYear());
		params.put("priorYear", bean.getPriorYear());
		params.put("currentContextId", 2l);
		params.put("priorContextId", 1l);
		params.put("validationCPVClassId",
				conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationICDCPV"));
		params.put("validationClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "ValidationICD"));
		params.put("catRubricClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("validationFacilityClassId",
				conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "ValidationFacility"));
		params.put("facilityTypeClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "FacilityType"));
		params.put("domainValueCodeClassId", conceptService.getICDClassID("TextPropertyVersion", "DomainValueCode"));
		params.put("codeClassId", conceptService.getICDClassID("TextPropertyVersion", "Code"));
		params.put("narrowClassId", conceptService.getICDClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));

		when(reportMapper.findICDModifiedValidations(params)).thenReturn(mockResults());
		when(lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", "2016")).thenReturn(
				new ContextIdentifier(2l, "2016", "ICD-10-CA", null, "OPEN", null, true, null, null));
		when(lookupService.findBaseContextIdentifierByClassificationAndYear("ICD-10-CA", "2015")).thenReturn(
				new ContextIdentifier(1l, "2015", "ICD-10-CA", null, "CLOSED", null, true, null, null));
	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("ICD-10-CA", reportData.get("classification"));
		assertEquals("2016", reportData.get("currentYear"));
		assertEquals("2015", reportData.get("priorYear"));
		assertEquals("2", reportData.get("codeCount"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(4, detailDataList.size());
	}
}
