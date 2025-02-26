package ca.cihi.cims.model.notification;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class NotificationUserProfileTest {
	private NotificationUserProfile bean;

	@Before
	public void setUp() {
		bean = new NotificationUserProfile();
	}

	@Test
	public void testGetsAndSets() {
		bean.setNotificationId(0L);
		bean.setNotificationUserProfileId(0L);
		bean.setUserProfileId(0L);
		assertTrue("Should have  the expected NotificationId", bean.getNotificationId() == 0L);
		assertTrue("Should have  the expected NotificationUserProfileId", bean.getNotificationUserProfileId() == 0L);
		assertTrue("Should have  the expected UserProfileId", bean.getUserProfileId() == 0L);

	}
}
