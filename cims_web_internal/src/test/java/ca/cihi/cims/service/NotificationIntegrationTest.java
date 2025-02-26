package ca.cihi.cims.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.model.notification.NotificationTypeCode;
import ca.cihi.cims.model.notification.NotificationUserProfile;

// FIXME: hard-coded IDs

@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Transactional
@Rollback
public class NotificationIntegrationTest {

	@Autowired
	private NotificationService notificationService;

	@Test
	public void testFindNotifcationById() {
		NotificationDTO notificationDTO = notificationService.findNotifcationById(1L);
		notificationDTO.getRecipient();
		notificationDTO.getSender();
		notificationDTO.getDlRecipients();
	}

	@Test
	public void testFindNotificationsByUserId() {
		List<NotificationDTO> notificationDTOs = notificationService.findNotificationsByUserId(5L);
		notificationDTOs.size();
	}

	@Ignore
	public void testPostNotificationToManyRecipient() {
		List<NotificationUserProfile> notificationUserProfiles = new ArrayList<NotificationUserProfile>();
		NotificationUserProfile notificationUserProfile1 = new NotificationUserProfile();
		notificationUserProfile1.setNotificationId(2L);
		notificationUserProfile1.setUserProfileId(4L);
		NotificationUserProfile notificationUserProfile2 = new NotificationUserProfile();
		notificationUserProfile2.setNotificationId(2L);
		notificationUserProfile2.setUserProfileId(2L);
		notificationUserProfiles.add(notificationUserProfile1);
		notificationUserProfiles.add(notificationUserProfile2);
		// notificationService.insertNotificationUserProfiles(notificationUserProfiles);
	}

	@Test
	public void testPostNotificationToOneRecipient() {
		NotificationDTO notificationDTO = new NotificationDTO();
		notificationDTO.setNotificationTypeCode(NotificationTypeCode.NCR);
		notificationDTO.setFiscalYear("2015");
		notificationDTO.setSubject("post notication to user");
		notificationDTO.setMessage("to test post notification to one recipient");
		notificationDTO.setSenderId(5L);
		notificationDTO.setChangeRequestId(43L);
		notificationDTO.setCompletionRequiredInd(false);
		notificationDTO.setCompletionInd(false);
		notificationDTO.setOriginalNotificationId(1L);
		notificationDTO.setCreatedDate(Calendar.getInstance().getTime());
		User recipient = new User();
		recipient.setUserId(1L);
		notificationDTO.setRecipient(recipient);

		notificationService.postNotificationToOneRecipient(notificationDTO);
	}

	@Test
	public void testRemoveMyNotification() {
		notificationService.removeMyNotification(5L, 1L);
	}

	@Test
	public void testRemoveNotification() {
		notificationService.removeNotification(1L);
	}

	@Test
	public void testReviewChangeRquestComplete() {
		notificationService.reviewChangeRquestTaskComplete(1L, 5L);
	}

}
