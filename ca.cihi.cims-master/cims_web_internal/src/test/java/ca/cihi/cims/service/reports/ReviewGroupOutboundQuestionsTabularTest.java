package ca.cihi.cims.service.reports;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.anyMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ca.cihi.cims.data.mapper.ReportMapper;
import ca.cihi.cims.model.changerequest.ReviewGroupOutboundQuestionForReviewer;
import ca.cihi.cims.web.bean.report.ReviewGroupReportViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ReviewGroupOutboundQuestionsTabularTest {

	@Mock
	ReportMapper reportMapper;

	ReviewGroupReportViewBean bean;
    ReportGenerator reportGenerator;
	
    private List<ReviewGroupOutboundQuestionForReviewer> mockReviewGroupOutboundQuestionForReviewer() {
		List<ReviewGroupOutboundQuestionForReviewer> results = new ArrayList<ReviewGroupOutboundQuestionForReviewer>();
        
		ReviewGroupOutboundQuestionForReviewer q1 = new ReviewGroupOutboundQuestionForReviewer();
        q1.setChangeRequestId(1L);
        q1.setChangeRequestName("Test1");
        q1.setQuestionForReviewerId(1L);
        q1.setQuestionForReviewerTxt("Question1");
		results.add(q1);

		ReviewGroupOutboundQuestionForReviewer q2 = new ReviewGroupOutboundQuestionForReviewer();
        q2.setChangeRequestId(2L);
        q2.setChangeRequestName("Test2");
        q2.setQuestionForReviewerId(2L);
        q2.setQuestionForReviewerTxt("Question2");
		results.add(q2);

		ReviewGroupOutboundQuestionForReviewer q3 = new ReviewGroupOutboundQuestionForReviewer();
        q3.setChangeRequestId(3L);
        q3.setChangeRequestName("Test3");
        q3.setQuestionForReviewerId(3L);
        q3.setQuestionForReviewerTxt("Question3");
		results.add(q3);

		return results;

    }
    
	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new ReviewGroupOutboundQuestionsTabular();
		reportGenerator.setReportMapper(reportMapper);
		bean = new ReviewGroupReportViewBean();
		bean.setClassification("ICD-10-CA");
		bean.setYear("2018");
		bean.setLanguage("ENG");
		bean.setLanguageDesc("English");
		bean.setReviewGroup("16");  // DL-WHO
		bean.setReviewGroupName("DL-WHO");
		bean.setCodeFrom("A00");
		bean.setCodeTo("Z00");
		bean.setRequestCategory("Tabular");

		String classification = bean.getClassification();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", classification);
		params.put("year", bean.getYear());
		params.put("language", bean.getLanguage());
		params.put("reviewGroup", bean.getReviewGroup());
		params.put("patternTopic", "");
		params.put("codeFrom", bean.getCodeFrom());
		params.put("codeTo", bean.getCodeTo() + "Z");
		
		when(reportMapper.findReviewGroupOutboundQuestionsTabularList(anyMap())).thenReturn(mockReviewGroupOutboundQuestionForReviewer());
		when(reportMapper.findReviewGroupOutboundQuestionsWithoutCodeValueTabularList(anyMap())).thenReturn(mockReviewGroupOutboundQuestionForReviewer());
		
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
		assertEquals("2018", reportData.get("year"));
		assertEquals("English", reportData.get("language"));
		assertEquals("DL-WHO", reportData.get("reviewGroup"));

		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>)reportData.get("detail1");

		assertNotNull(detailDataList);
		assertEquals(3, detailDataList.size());

        Map<String, Object> detailData1 = (Map<String, Object>)detailDataList.get(0);
		assertEquals("1", detailData1.get("changeRequestId"));
		assertEquals("Test1", detailData1.get("changeRequestName"));
		assertEquals("1", detailData1.get("questionForReviewerId"));
		assertEquals("Question1", detailData1.get("questionForReviewerTxt"));

        Map<String, Object> detailData2 = (Map<String, Object>)detailDataList.get(1);
		assertEquals("2", detailData2.get("changeRequestId"));
		assertEquals("Test2", detailData2.get("changeRequestName"));
		assertEquals("2", detailData2.get("questionForReviewerId"));
		assertEquals("Question2", detailData2.get("questionForReviewerTxt"));

        Map<String, Object> detailData3 = (Map<String, Object>)detailDataList.get(2);
		assertEquals("3", detailData3.get("changeRequestId"));
		assertEquals("Test3", detailData3.get("changeRequestName"));
		assertEquals("3", detailData3.get("questionForReviewerId"));
		assertEquals("Question3", detailData3.get("questionForReviewerTxt"));

	}
}
