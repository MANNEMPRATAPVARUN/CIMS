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
import ca.cihi.cims.model.reports.ModifiedValidCodeModel;
import ca.cihi.cims.service.ConceptService;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ICDModifiedValidCodeTest {

	@Mock
	ReportMapper reportMapper;

	@Mock
	LookupService lookupService;

	@Autowired
	ReportGeneratorFactory reportGeneratorFactory;

	@Autowired
	ConceptService conceptService;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<ModifiedValidCodeModel> mockResults() {
		List<ModifiedValidCodeModel> results = new ArrayList<ModifiedValidCodeModel>();
		ModifiedValidCodeModel model = new ModifiedValidCodeModel();
		model.setCodeValue("A00.0");
		model.setCurrentFlag("N");
		model.setPriorFlag("Y");
		results.add(model);
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = reportGeneratorFactory.createReportGenerator("ICDModifiedValidCode");
		reportGenerator.setReportMapper(reportMapper);
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setConceptService(conceptService);

		bean = new ReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setCurrentYear("2016");

		String currentYear = bean.getCurrentYear();
		Long priorYear = Long.parseLong(currentYear) - 1;

		String classification = bean.getClassification();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("currentYear", currentYear);
		params.put("currentContextId", 2l);

		params.put("priorYear", priorYear);
		params.put("priorContextId", 1l);
		params.put("catRubricClassId", conceptService.getICDClassID(WebConstants.CONCEPT_VERSION, "Category"));
		params.put("codeClassId", conceptService.getICDClassID("TextPropertyVersion", "Code"));

		when(lookupService.findBaseContextIdentifierByClassificationAndYear(classification, currentYear)).thenReturn(
				new ContextIdentifier(2l, "2016", "ICD-10-CA", null, "OPEN", null, true, null, null));
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(classification, priorYear.toString()))
				.thenReturn(new ContextIdentifier(1l, "2015", "ICD-10-CA", null, "CLOSED", null, true, null, null));

		when(reportMapper.findICDModifiedValidCodes(params)).thenReturn(mockResults());

	}

	@Test
	public void testGenerateReportData() {
		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("ICD-10-CA", reportData.get("classification"));
		assertEquals("2016", reportData.get("currentYear"));
		assertEquals(1, reportData.get("codeCount"));

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());
	}
}
