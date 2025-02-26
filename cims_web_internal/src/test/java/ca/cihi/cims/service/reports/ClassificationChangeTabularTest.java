package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
import ca.cihi.cims.model.changerequest.ConceptModification;
import ca.cihi.cims.service.LookupService;
import ca.cihi.cims.web.bean.report.ReportViewBean;

public class ClassificationChangeTabularTest {
	@Mock
	LookupService lookupService;
	@Mock
	ReportMapper reportMapper;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<ConceptModification> mockConceptModifications() {
		List<ConceptModification> results = new ArrayList<ConceptModification>();
		ConceptModification cm1 = new ConceptModification();
		cm1.setCode("A01");
		cm1.setVersionCode("2015");
		cm1.setChangeRequestId(1l);
		results.add(cm1);

		ConceptModification cm6 = new ConceptModification();
		cm6.setCode("A01");
		cm6.setVersionCode("2015");
		cm6.setChangeRequestId(6l);
		results.add(cm6);

		ConceptModification cm2 = new ConceptModification();
		cm2.setCode("A01");
		cm2.setVersionCode("2016");
		cm2.setChangeRequestId(2l);
		results.add(cm2);

		ConceptModification cm3 = new ConceptModification();
		cm3.setCode("A03.5");
		cm3.setVersionCode("2017");
		cm3.setChangeRequestId(3l);
		results.add(cm3);

		ConceptModification cm4 = new ConceptModification();
		cm4.setCode("C11");
		cm4.setVersionCode("2018");
		cm4.setChangeRequestId(4l);
		results.add(cm4);

		ConceptModification cm5 = new ConceptModification();
		cm5.setCode("D09");
		cm5.setVersionCode("2016");
		cm5.setChangeRequestId(5l);
		results.add(cm5);
		return results;
	}

	private List<ContextIdentifier> mockContextIdentifiers() {
		List<ContextIdentifier> results = new ArrayList<ContextIdentifier>();
		results.add(new ContextIdentifier(1l, "2015", "ICD-10-CA", 1l, "OPEN", null, true, 0l, null));
		results.add(new ContextIdentifier(2l, "2016", "ICD-10-CA", 2l, "OPEN", null, false, 0l, null));
		results.add(new ContextIdentifier(3l, "2017", "ICD-10-CA", 3l, "OPEN", null, false, 0l, null));
		results.add(new ContextIdentifier(4l, "2018", "ICD-10-CA", 4l, "OPEN", null, false, 0l, null));
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new ClassificationChangeTabular();
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setReportMapper(reportMapper);
		bean = new ReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setCodeFrom("A00");
		bean.setCodeTo("Z00");
		bean.setRequestCategory("Tabular");
		String classification = bean.getClassification();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("codeFrom", bean.getCodeFrom());
		params.put("codeTo", bean.getCodeTo() + "Z");
		params.put("classification", classification);
		when(lookupService.findNonClosedBaseContextIdentifiersReport(classification, null)).thenReturn(
				mockContextIdentifiers());
		when(reportMapper.findClassificationChangeTabularList(params)).thenReturn(mockConceptModifications());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateReportData() {

		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("A00", reportData.get("valueFrom"));
		assertEquals("Z00", reportData.get("valueTo"));
		assertEquals("ICD-10-CA", reportData.get("classification"));
		assertEquals("Tabular", reportData.get("requestCategory"));

		Map<String, Object> detailHeader = (Map<String, Object>) reportData.get("detailHeader");
		assertNotNull(detailHeader);
		assertEquals(4, detailHeader.size());
		assertEquals(2015, detailHeader.get("year1"));
		assertNull(detailHeader.get("year5"));

		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(4, detailDataList.size());
	}
}
