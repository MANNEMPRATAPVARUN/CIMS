package ca.cihi.cims.model.notification;

import static org.junit.Assert.assertTrue;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

public class NotificationTest {
	private Notification bean;

	@Before
	public void setUp() {
		bean = new Notification();
	}

	@Test
	public void testGetsAndSets() {
		bean.setAdviceId(0L);
		bean.setChangeRequestId(0L);
		bean.setCompletionInd(true);
		bean.setCompletionRequiredInd(true);
		bean.setCreatedDate(Calendar.getInstance().getTime());
		bean.setFiscalYear("2016");
		bean.setLastUpdatedTime(Calendar.getInstance().getTime());
		bean.setMessage("message");
		bean.setNotificationId(0L);
		bean.setNotificationTypeCode(NotificationTypeCode.AP);
		bean.setOriginalNotificationId(null);
		bean.setQuestionForReviewerId(null);
		bean.setSenderId(0L);
		bean.setSubject("subject");
		assertTrue("Should have  the expected AdviceId", bean.getAdviceId() == 0L);
		assertTrue("Should have  the expected ChangeRequestId", bean.getChangeRequestId() == 0L);
		assertTrue("Should have  the expected CompletionInd", bean.isCompletionInd());
		assertTrue("Should have  the expected CompletionRequiredInd", bean.isCompletionRequiredInd());
		assertTrue("CreatedDate is nit null", bean.getCreatedDate() != null);
		assertTrue("Should have  the expected FiscalYear", bean.getFiscalYear().equals("2016"));
		assertTrue("LastUpdatedTime is nit null", bean.getLastUpdatedTime() != null);
		assertTrue("Should have  the expected message", bean.getMessage().equals("message"));
		assertTrue("Should have  the expected NotificationId", bean.getNotificationId() == 0L);
		assertTrue("Should have  the expected NotificationTypeCode",
				bean.getNotificationTypeCode() == NotificationTypeCode.AP);
		assertTrue("OriginalNotificationId is null", bean.getOriginalNotificationId() == null);
		assertTrue("QuestionForReviewerId is null", bean.getQuestionForReviewerId() == null);
		assertTrue("SenderId is 0", bean.getSenderId() == 0L);
		assertTrue("Should have  the expected subject", bean.getSubject().equals("subject"));

	}
}
