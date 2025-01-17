package ca.cihi.cims.web.controller.changerequest;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.Model;

import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.service.ChangeRequestHistoryService;
import ca.cihi.cims.service.ChangeRequestService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestHistoryControllerTest {
	ChangeRequestHistoryController changeRequestHistoryController;
	@Mock
	ChangeRequestHistoryService changeRequestHistoryService;

	@Mock
	ChangeRequestService changeRequestService;
	@Mock
	protected Model model;
	@Mock
	protected HttpServletRequest request;
	@Mock
	protected HttpSession session;

	@Before
	public void initializeMocks() throws Exception {
		MockitoAnnotations.initMocks(this);
		changeRequestHistoryController = new ChangeRequestHistoryController();
		changeRequestHistoryController.setChangeRequestHistoryService(changeRequestHistoryService);
		changeRequestHistoryController.setChangeRequestService(changeRequestService);
		when(changeRequestService.findCourseGrainedChangeRequestDTOById(nullable(Long.class))).thenReturn(mockChangeRequest());
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

	@Test
	public void testFindChangeRequestHistory() {
		Model test_model = model;
		Long changeRequestId = 1L;
		HttpSession test_session = session;
		changeRequestHistoryController.findChangeRequestHistory(test_model, changeRequestId, test_session);
		verify(changeRequestService, times(1)).findCourseGrainedChangeRequestDTOById(nullable(Long.class));
		verify(changeRequestHistoryService, times(1)).findChangeRequestHistoryByChangeRequestId(nullable(Long.class));
	}
}
