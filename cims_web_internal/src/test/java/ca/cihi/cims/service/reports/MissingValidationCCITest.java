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

public class MissingValidationCCITest {

	@Mock
	LookupService lookupService;
	@Mock
	ReportMapper reportMapper;
	@Mock
	ConceptService conceptService;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private ContextIdentifier mockContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2015", "CCI", 1l, "OPEN", null, true, 0l, null);
		return contextIdentifier;
	}

	private List<MissingValidationHierarchy> mockMissingValidationCodes() {
		List<MissingValidationHierarchy> missingValidationCodes = new ArrayList<MissingValidationHierarchy>();
		MissingValidationHierarchy codeHierarchy = new MissingValidationHierarchy();
		codeHierarchy.setCode("1.AA.13");
		missingValidationCodes.add(codeHierarchy);
		return missingValidationCodes;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new MissingValidationCCI();
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setReportMapper(reportMapper);
		reportGenerator.setConceptService(conceptService);
		bean = new ReportViewBean();
		bean.setClassification("CCI");
		bean.setCodeFrom("1.AA.00");
		bean.setCodeTo("9.ZZ.99");
		bean.setYear("2015");
		bean.setDataHolding("DAD - Acute Care");
		bean.setDataHoldingCode("1");
		String classification = bean.getClassification();

		when(lookupService.findBaseContextIdentifierByClassificationAndYear(classification, "2015")).thenReturn(
				mockContextIdentifier());

		when(conceptService.getCCIClassID("ConceptPropertyVersion", "ValidationCCICPV")).thenReturn(116l);
		when(conceptService.getCCIClassID("ConceptVersion", "ValidationCCI")).thenReturn(113l);
		when(conceptService.getCCIClassID("ConceptVersion", "Rubric")).thenReturn(64l);
		when(conceptService.getCCIClassID("ConceptPropertyVersion", "ValidationFacility")).thenReturn(117l);
		when(conceptService.getCCIClassID("ConceptVersion", "FacilityType")).thenReturn(130l);
		when(conceptService.getCCIClassID("TextPropertyVersion", "DomainValueCode")).thenReturn(118l);
		when(conceptService.getCCIClassID("TextPropertyVersion", "Code")).thenReturn(66l);
		when(conceptService.getCCIClassID("ConceptVersion", "CCICODE")).thenReturn(65l);
		when(conceptService.getCCIClassID("ConceptPropertyVersion", "Narrower")).thenReturn(70l);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("contextId", 1l);
		params.put("codeFrom", "1.AA.00");
		params.put("codeTo", "9.ZZ.99Z");
		params.put("dhCode", "1");
		params.put("validationCPVClassId", 116l);
		params.put("validationClassId", 113l);
		params.put("catRubricClassId", 64l);
		params.put("validationFacilityClassId", 117l);
		params.put("facilityTypeClassId", 130l);
		params.put("domainValueCodeClassId", 118l);
		params.put("codeClassId", 66l);
		params.put("cciCodeClassId", 65l);
		params.put("narrowClassId", 70l);

		when(reportMapper.getCCIMissingValidationCodes(params)).thenReturn(mockMissingValidationCodes());
	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("CCI", reportData.get("classification"));
		assertEquals("2015", reportData.get("year"));
		assertEquals("1.AA.00", reportData.get("codeFrom"));
		assertEquals("9.ZZ.99", reportData.get("codeTo"));
		assertEquals("DAD - Acute Care", reportData.get("dataHolding"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());
	}
}
