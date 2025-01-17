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
import ca.cihi.cims.model.changerequest.UserComment;
import ca.cihi.cims.web.bean.report.ReviewGroupReportViewBean;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ReviewGroupCompiledResponsesTabularTest {

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

        List<UserComment> userComments1 = new ArrayList<UserComment>();
        UserComment userComment1 = new UserComment();
        userComment1.setUserCommentId(1L);
        userComment1.setUserCommentTxt("Test User Response 1");
        userComments1.add(userComment1);
        UserComment userComment2 = new UserComment();
        userComment2.setUserCommentId(2L);
        userComment2.setUserCommentTxt("Test User Response 2");
        userComments1.add(userComment2);
        UserComment userComment3 = new UserComment();
        userComment3.setUserCommentId(3L);
        userComment3.setUserCommentTxt("Test User Response 3");
        userComments1.add(userComment3);
        q1.setQuestionComments(userComments1);

		results.add(q1);

		ReviewGroupOutboundQuestionForReviewer q2 = new ReviewGroupOutboundQuestionForReviewer();
        q2.setChangeRequestId(2L);
        q2.setChangeRequestName("Test2");
        q2.setQuestionForReviewerId(2L);
        q2.setQuestionForReviewerTxt("Question2");

        List<UserComment> userComments2 = new ArrayList<UserComment>();
        UserComment userComment4 = new UserComment();
        userComment4.setUserCommentId(4L);
        userComment4.setUserCommentTxt("Test User Response 4");
        userComments2.add(userComment4);
        UserComment userComment5 = new UserComment();
        userComment5.setUserCommentId(5L);
        userComment5.setUserCommentTxt("Test User Response 5");
        userComments2.add(userComment5);
        UserComment userComment6 = new UserComment();
        userComment6.setUserCommentId(6L);
        userComment6.setUserCommentTxt("Test User Response 6");
        userComments2.add(userComment6);
        q2.setQuestionComments(userComments2);

		results.add(q2);

		ReviewGroupOutboundQuestionForReviewer q3 = new ReviewGroupOutboundQuestionForReviewer();
        q3.setChangeRequestId(3L);
        q3.setChangeRequestName("Test3");
        q3.setQuestionForReviewerId(3L);
        q3.setQuestionForReviewerTxt("Question3");

        List<UserComment> userComments3 = new ArrayList<UserComment>();
        UserComment userComment7 = new UserComment();
        userComment7.setUserCommentId(7L);
        userComment7.setUserCommentTxt("Test User Response 7");
        userComments3.add(userComment7);
        
        UserComment userComment8 = new UserComment();
        userComment8.setUserCommentId(8L);
        userComment8.setUserCommentTxt("Test User Response 8");
        userComments3.add(userComment8);

        UserComment userComment9 = new UserComment();
        userComment9.setUserCommentId(9L);
        userComment9.setUserCommentTxt("Test User Response 9");
        userComments3.add(userComment9);
        
        q3.setQuestionComments(userComments3);

		results.add(q3);

		return results;

    }

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		reportGenerator = new ReviewGroupCompiledResponsesTabular();
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
        bean.setPatternTopic("Test Pattern Topic");

		String classification = bean.getClassification();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("classification", classification);
		params.put("year", bean.getYear());
		params.put("language", bean.getLanguage());
		params.put("reviewGroup", bean.getReviewGroup());
		params.put("patternTopic", bean.getPatternTopic());
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
		assertEquals("Test Pattern Topic", reportData.get("patternTopic"));

		List<Map<String, Object>> detailDataList = (List<Map<String, Object>>)reportData.get("detail1");

		assertNotNull(detailDataList);
		assertEquals(9, detailDataList.size());

        Map<String, Object> detailData1 = (Map<String, Object>)detailDataList.get(0);
		assertEquals("1", detailData1.get("changeRequestId"));
		assertEquals("Test1", detailData1.get("changeRequestName"));
		assertEquals("1", detailData1.get("questionForReviewerId"));
		assertEquals("Question1", detailData1.get("questionForReviewerTxt"));
		assertEquals("1", detailData1.get("responseId"));
		assertEquals("Test User Response 1", detailData1.get("response"));

        Map<String, Object> detailData2 = (Map<String, Object>)detailDataList.get(1);
		assertEquals("1", detailData2.get("changeRequestId"));
		assertEquals("Test1", detailData2.get("changeRequestName"));
		assertEquals("1", detailData2.get("questionForReviewerId"));
		assertEquals("Question1", detailData2.get("questionForReviewerTxt"));
		assertEquals("2", detailData2.get("responseId"));
		assertEquals("Test User Response 2", detailData2.get("response"));

        Map<String, Object> detailData3 = (Map<String, Object>)detailDataList.get(2);
		assertEquals("1", detailData3.get("changeRequestId"));
		assertEquals("Test1", detailData3.get("changeRequestName"));
		assertEquals("1", detailData3.get("questionForReviewerId"));
		assertEquals("Question1", detailData3.get("questionForReviewerTxt"));
		assertEquals("3", detailData3.get("responseId"));
		assertEquals("Test User Response 3", detailData3.get("response"));

        Map<String, Object> detailData4 = (Map<String, Object>)detailDataList.get(3);
		assertEquals("2", detailData4.get("changeRequestId"));
		assertEquals("Test2", detailData4.get("changeRequestName"));
		assertEquals("2", detailData4.get("questionForReviewerId"));
		assertEquals("Question2", detailData4.get("questionForReviewerTxt"));
		assertEquals("4", detailData4.get("responseId"));
		assertEquals("Test User Response 4", detailData4.get("response"));

        Map<String, Object> detailData5 = (Map<String, Object>)detailDataList.get(4);
		assertEquals("2", detailData5.get("changeRequestId"));
		assertEquals("Test2", detailData5.get("changeRequestName"));
		assertEquals("2", detailData5.get("questionForReviewerId"));
		assertEquals("Question2", detailData5.get("questionForReviewerTxt"));
		assertEquals("5", detailData5.get("responseId"));
		assertEquals("Test User Response 5", detailData5.get("response"));

        Map<String, Object> detailData6 = (Map<String, Object>)detailDataList.get(5);
		assertEquals("2", detailData6.get("changeRequestId"));
		assertEquals("Test2", detailData6.get("changeRequestName"));
		assertEquals("2", detailData6.get("questionForReviewerId"));
		assertEquals("Question2", detailData6.get("questionForReviewerTxt"));
		assertEquals("6", detailData6.get("responseId"));
		assertEquals("Test User Response 6", detailData6.get("response"));

        Map<String, Object> detailData7 = (Map<String, Object>)detailDataList.get(6);
		assertEquals("3", detailData7.get("changeRequestId"));
		assertEquals("Test3", detailData7.get("changeRequestName"));
		assertEquals("3", detailData7.get("questionForReviewerId"));
		assertEquals("Question3", detailData7.get("questionForReviewerTxt"));
		assertEquals("7", detailData7.get("responseId"));
		assertEquals("Test User Response 7", detailData7.get("response"));

        Map<String, Object> detailData8 = (Map<String, Object>)detailDataList.get(7);
		assertEquals("3", detailData8.get("changeRequestId"));
		assertEquals("Test3", detailData8.get("changeRequestName"));
		assertEquals("3", detailData8.get("questionForReviewerId"));
		assertEquals("Question3", detailData8.get("questionForReviewerTxt"));
		assertEquals("8", detailData8.get("responseId"));
		assertEquals("Test User Response 8", detailData8.get("response"));

        Map<String, Object> detailData9 = (Map<String, Object>)detailDataList.get(8);
		assertEquals("3", detailData9.get("changeRequestId"));
		assertEquals("Test3", detailData9.get("changeRequestName"));
		assertEquals("3", detailData9.get("questionForReviewerId"));
		assertEquals("Question3", detailData9.get("questionForReviewerTxt"));
		assertEquals("9", detailData9.get("responseId"));
		assertEquals("Test User Response 9", detailData9.get("response"));

	}
}
