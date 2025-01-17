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

public class ClassificationChangeIndexTest {

	@Mock
	LookupService lookupService;
	@Mock
	ReportMapper reportMapper;

	private ReportViewBean bean;
	private ReportGenerator reportGenerator;

	private List<ConceptModification> mockConceptModifications() {
		List<ConceptModification> results = new ArrayList<ConceptModification>();
		ConceptModification cm1 = new ConceptModification();
		cm1.setIndexTerm("Aarskog's syndrome-CR27");
		cm1.setVersionCode("2015");
		cm1.setChangeRequestId(22l);
		cm1.setIndexPath(" A > Aarskog's syndrome-CR27");
		results.add(cm1);

		return results;
	}

	private ContextIdentifier mockContextIdentifier() {
		ContextIdentifier contextIdentifier = new ContextIdentifier(1l, "2015", "ICD-10-CA", 1l, "OPEN", null, true,
				0l, null);
		return contextIdentifier;
	}

	private List<ContextIdentifier> mockContextIdentifiers() {
		List<ContextIdentifier> results = new ArrayList<ContextIdentifier>();
		results.add(new ContextIdentifier(1l, "2015", "ICD-10-CA", 1l, "OPEN", null, true, 0l, null));
		results.add(new ContextIdentifier(2l, "2016", "ICD-10-CA", 2l, "OPEN", null, false, 0l, null));
		return results;
	}

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new ClassificationChangeIndex();
		reportGenerator.setLookupService(lookupService);
		reportGenerator.setReportMapper(reportMapper);
		bean = new ReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setIndexBook("Section I -- Alphabetic Index to Diseases and Nature of Injury");
		bean.setLeadTerm("Aarskog's syndrome-CR27");
		bean.setLeadTermElementId(1l);
		bean.setRequestCategory("Index");
		String classification = bean.getClassification();

		when(lookupService.findCurrentOpenYear(classification)).thenReturn(2015l);
		when(lookupService.findBaseContextIdentifierByClassificationAndYear(classification, "2015")).thenReturn(
				mockContextIdentifier());
		when(lookupService.findNonClosedBaseContextIdentifiersReport(classification, "Y")).thenReturn(
				mockContextIdentifiers());
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("leadTermElementId", bean.getLeadTermElementId());
		params.put("classification", classification);
		params.put("contextId", 1l);

		when(reportMapper.findClassificationChangeIndexList(params)).thenReturn(mockConceptModifications());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGenerateReportData() {

		Map<String, Object> reportData = reportGenerator.generatReportData(bean);

		assertNotNull(reportData);
		assertEquals("Section I -- Alphabetic Index to Diseases and Nature of Injury", reportData.get("indexBook"));
		assertEquals("Aarskog's syndrome-CR27", reportData.get("leadIndexTerm"));
		assertEquals("ICD-10-CA", reportData.get("classification"));
		assertEquals("Index", reportData.get("requestCategory"));

		Map<String, Object> detailHeader = (Map<String, Object>) reportData.get("detailHeader");
		assertNotNull(detailHeader);
		assertEquals(2, detailHeader.size());
		assertEquals(2015, detailHeader.get("year1"));
		assertNull(detailHeader.get("year5"));

		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>) reportData.get("detail1");
		assertEquals(1, detailDataList.size());
	}
}
