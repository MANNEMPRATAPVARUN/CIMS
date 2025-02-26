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
import ca.cihi.cims.model.reports.CodeValueChangeRequest;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class CCINewTableCodesWithCodingDirectivesTest {

	@Mock
	ReportMapper reportMapper;

	@Mock
	LookupService lookupService;
	@Autowired
	ConceptService conceptService;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<CodeValueChangeRequest> mockResult() {
		List<CodeValueChangeRequest> results = new ArrayList<CodeValueChangeRequest>();
		CodeValueChangeRequest result = new CodeValueChangeRequest();
		result.setChangeRequestId(23l);
		result.setChangeRequestName("Test keep");
		result.setCodeValue("1.AA.02.HD");
		results.add(result);
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new CCINewTableCodesWithCodingDirectives();
		reportGenerator.setReportMapper(reportMapper);
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setConceptService(conceptService);

		bean = new ReportViewBean();
		bean.setClassification("CCI");
		bean.setCurrentYear("2016");
		bean.setPriorYear("2015");

		when(lookupService.findBaseContextIdentifierByClassificationAndYear("CCI", "2015")).thenReturn(
				new ContextIdentifier(1l, "2015", "CCI", null, "CLOSED", null, true, null, null));
		when(lookupService.findBaseContextIdentifierByClassificationAndYear("CCI", "2016")).thenReturn(
				new ContextIdentifier(2l, "2016", "CCI", null, "OPEN", null, true, null, null));

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("baseContextId", 1l);
		params.put("contextId", 2l);
		params.put("catRubricClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "Rubric"));
		params.put("cciCodeClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_VERSION, "CCICODE"));
		params.put("narrowClassId", conceptService.getCCIClassID(WebConstants.CONCEPT_PROPERTY_VERSION, "Narrower"));
		params.put("includePresentationClassId",
				conceptService.getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "IncludePresentation"));
		params.put("excludePresentationClassId",
				conceptService.getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "ExcludePresentation"));
		params.put("codeAlsoPresentationClassId",
				conceptService.getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "CodeAlsoPresentation"));
		params.put("notePresentationClassId",
				conceptService.getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "NotePresentation"));
		params.put("omitCodePresentationClassId",
				conceptService.getCCIClassID(WebConstants.XML_PROPERTY_VERSION, "OmitCodePresentation"));
		params.put("tablePresentationClassId",
				conceptService.getCCIClassID(WebConstants.HTML_PROPERTY_VERSION, "TablePresentation"));

		when(reportMapper.findCCINewTableCodesWithCodingDirectives(params)).thenReturn(mockResult());

	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("2016", reportData.get("year"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());

		Map<String, Object> data = detailDataList.get(0);
		assertNotNull(data);
		String changeRequestId = (String) data.get("changeRequestId");
		assertEquals("23", changeRequestId);
	}
}
