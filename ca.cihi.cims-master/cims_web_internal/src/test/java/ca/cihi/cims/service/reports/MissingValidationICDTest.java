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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ca.cihi.cims.dal.ContextIdentifier;
import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.model.reports.MissingValidationHierarchy;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class MissingValidationICDTest {

	@Mock
	LookupService lookupService;
	@Mock
	ReportMapper reportMapper;
	@Mock
	ConceptService conceptService;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private ContextIdentifier mockContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2015", "ICD-10-CA", 1l, "OPEN", null, true,
				0l, null);
		return contextIdentifier;
	}

	private List<MissingValidationHierarchy> mockMissingValidationCodes() {
		List<MissingValidationHierarchy> missingValidationCodes = new ArrayList<MissingValidationHierarchy>();
		MissingValidationHierarchy codeHierarchy = new MissingValidationHierarchy();
		codeHierarchy.setCode("A11.1");
		codeHierarchy.setElementId(345l);
		codeHierarchy.setElementIdPath("345,123");
		missingValidationCodes.add(codeHierarchy);
		return missingValidationCodes;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new MissingValidationICD();
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setReportMapper(reportMapper);
		reportGenerator.setConceptService(conceptService);
		bean = new ReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setCodeFrom("A00");
		bean.setCodeTo("Z99");
		bean.setYear("2015");
		bean.setDataHolding("DAD - Acute Care");
		bean.setDataHoldingCode("1");
		String classification = bean.getClassification();

		when(lookupService.findBaseContextIdentifierByClassificationAndYear(classification, "2015")).thenReturn(
				mockContextIdentifier());

		when(conceptService.getICDClassID("ConceptPropertyVersion", "ValidationICDCPV")).thenReturn(33l);
		when(conceptService.getICDClassID("ConceptVersion", "ValidationICD")).thenReturn(31l);
		when(conceptService.getICDClassID("ConceptVersion", "Category")).thenReturn(5l);
		when(conceptService.getICDClassID("ConceptPropertyVersion", "ValidationFacility")).thenReturn(34l);
		when(conceptService.getICDClassID("ConceptVersion", "FacilityType")).thenReturn(25l);
		when(conceptService.getICDClassID("TextPropertyVersion", "DomainValueCode")).thenReturn(27l);
		when(conceptService.getICDClassID("TextPropertyVersion", "Code")).thenReturn(6l);
		when(conceptService.getICDClassID("ConceptPropertyVersion", "Narrower")).thenReturn(10l);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", 1l);
		params.put("codeFrom", "A00");
		params.put("codeTo", "Z99Z");
		params.put("dhCode", "1");
		params.put("validationCPVClassId", 33l);
		params.put("validationClassId", 31l);
		params.put("catRubricClassId", 5l);
		params.put("validationFacilityClassId", 34l);
		params.put("facilityTypeClassId", 25l);
		params.put("domainValueCodeClassId", 27l);
		params.put("codeClassId", 6l);
		params.put("narrowClassId", 10l);

		when(reportMapper.getICDMissingValidationCodes(params)).thenReturn(mockMissingValidationCodes());

		Map<String, Object> params1 = new HashMap<String, Object>();
		params1.put("contextId", 1l);
		params1.put("conceptId", 123l);
		params1.put("dhCode", "1");
		when(reportMapper.getHasActiveValidationRuleDH(params1)).thenReturn("N");
	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("ICD-10-CA", reportData.get("classification"));
		assertEquals("2015", reportData.get("year"));
		assertEquals("A00", reportData.get("codeFrom"));
		assertEquals("Z99", reportData.get("codeTo"));
		assertEquals("DAD - Acute Care", reportData.get("dataHolding"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());

		Map<String, Object> data = detailDataList.get(0);
		assertNotNull(data);
		String codeValue = (String) data.get("codeValue");
		assertEquals("A11.1", codeValue);
	}
}
