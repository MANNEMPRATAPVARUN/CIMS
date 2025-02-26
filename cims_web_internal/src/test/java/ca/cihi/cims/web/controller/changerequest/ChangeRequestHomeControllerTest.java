package ca.cihi.cims.web.controller.changerequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;

import ca.cihi.cims.WebConstants;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.UserSearchCriteria;
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.notification.NotificationDTO;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.NotificationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestHomeControllerTest {

	ChangeRequestHomeController changeRequestHomeController;
	@Mock
	ChangeRequestService changeRequestService;
	@Mock
	NotificationService notificationService;
	@Mock
	protected Model model;
	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpSession session;
	@Mock
	HttpServletResponse response;
	@Mock
	protected BindingResult result;
	@Mock
	WebDataBinder binder;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		changeRequestHomeController = new ChangeRequestHomeController();
		changeRequestHomeController.setChangeRequestService(changeRequestService);
		changeRequestHomeController.setNotificationService(notificationService);
		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());

	}

	private ChangeRequestDTO mockChangeRequest() {
		ChangeRequestDTO changeRequest = new ChangeRequestDTO();
		changeRequest.setChangeRequestId(1L);
		changeRequest.setAssignedTo("DL_01");
		changeRequest.setBaseClassification("CCI");
		changeRequest.setBaseVersionCode("2018");
		changeRequest.setBaseContextId(1L);
		changeRequest.setDeferredToBaseContextId(2L);
		changeRequest.setAdviceRecipient("DL_01");
		changeRequest.setOwnerId(1L);
		changeRequest.setTransferedTo(0L);
		changeRequest.setLanguageCode("ENG");
		changeRequest.setStatus(ChangeRequestStatus.NEW_WITH_OWNER);
		return changeRequest;
	}

	private List<ChangeRequestDTO> mockChangeRequests() {
		List<ChangeRequestDTO> changeRequests = new ArrayList<ChangeRequestDTO>();
		changeRequests.add(mockChangeRequest());
		return changeRequests;
	}

	private List<Long> mockIds() {
		List<Long> ids = new ArrayList<Long>();
		ids.add(1L);
		return ids;
	}

	private NotificationDTO mockNotificationDTO() {
		NotificationDTO mockNotificationDTO = new NotificationDTO();
		mockNotificationDTO.setNotificationId(0L);
		mockNotificationDTO.setSenderId(1L);
		mockNotificationDTO.setNotificationIds(mockIds());

		return mockNotificationDTO;
	}

	private List<NotificationDTO> mockNotificationDTOs() {

		List<NotificationDTO> notifications = new ArrayList<NotificationDTO>();
		NotificationDTO mockNotificationDTO1 = mockNotificationDTO();

		notifications.add(mockNotificationDTO1);

		return notifications;
	}

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		return currentUser;
	}

	@Test
	public void testCompleteTask() {
		Long notificationId = 1L;
		HttpSession test_session = session;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestHomeController.completeTask(notificationId, test_model, test_session, test_request);
		verify(notificationService, times(1)).completeTask(nullable(Long.class), nullable(Long.class));
	}

	@Test
	public void testFindAllChangeRequests() {
		Model test_model = model;
		changeRequestHomeController.findAllChangeRequests(test_model);
		verify(changeRequestService, times(1)).findAllChangeRequests();
	}

	@Test
	public void testFindMyChangeRequests() {
		HttpSession test_session = session;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestHomeController.findMyChangeRequests(test_session, test_model, test_request);
		verify(changeRequestService, times(1)).findChangeRequestsBySearchCriteria(nullable(UserSearchCriteria.class));
	}

	@Test
	public void testFindMyNotices() {
		HttpSession test_session = session;
		Model test_model = model;
		HttpServletRequest test_request = request;
		changeRequestHomeController.findMyNotices(test_model, test_session, test_request);
		verify(notificationService, times(1)).findNotificationsByUserSerachCriteria(nullable(UserSearchCriteria.class));

	}

	@Test
	public void testFindMyNoticesAndRequest() {
		Model test_model = model;
		HttpServletRequest test_request = request;
		String rtnPage = changeRequestHomeController.findMyNoticesAndRequest(test_model, test_request);
		String expectedPage = ChangeRequestHomeController.MY_HOME;
		assertEquals("Should get same view", rtnPage, expectedPage);
	}

	@Test
	public void testRemoveMyNotification() {
		NotificationDTO notificationDTO = mockNotificationDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		HttpSession test_session = session;
		changeRequestHomeController.removeMyNotification(notificationDTO, test_result, test_model, test_session,
				test_request);
		verify(notificationService, times(1)).removeMyNotifications(nullable(User.class), nullable(List.class));
	}

	@Test
	public void testRemoveOneNotification() {
		NotificationDTO notificationDTO = mockNotificationDTO();
		final BindingResult test_result = result;
		Model test_model = model;
		HttpServletRequest test_request = request;
		HttpSession test_session = session;
		changeRequestHomeController.removeOneNotification(notificationDTO, test_result, test_model, test_session,
				test_request);
		verify(notificationService, times(1)).removeMyNotification(nullable(Long.class), nullable(Long.class));
	}

}
