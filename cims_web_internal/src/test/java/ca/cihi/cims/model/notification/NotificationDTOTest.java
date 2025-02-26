package ca.cihi.cims.model.notification;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ca.cihi.cims.model.User;

public class NotificationDTOTest {
	private NotificationDTO bean;

	@Before
	public void setUp() {
		bean = new NotificationDTO();
	}

	@Test
	public void testGetsAndSets() {
		User sender = new User();
		sender.setUserId(0L);
		bean.setSender(sender);
		User recipient = new User();
		recipient.setUserId(0L);
		bean.setRecipient(recipient);
		bean.setDlRecipients(null);
		bean.setChangeRequest(null);
		bean.setNotificationIds(null);
		assertTrue("Should have  the expected sender", bean.getSender().getUserId() == 0L);
		assertTrue("Should have  the expected recipient", bean.getRecipient().getUserId() == 0L);
		assertTrue("Should have  the expected DlRecipients", bean.getDlRecipients() == null);
		assertTrue("Should have  the expected ChangeRequest", bean.getChangeRequest() == null);
		assertTrue("Should have  the expected NotificationIds", bean.getNotificationIds() == null);

	}
}
