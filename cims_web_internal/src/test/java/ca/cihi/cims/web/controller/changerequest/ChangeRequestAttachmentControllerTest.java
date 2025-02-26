package ca.cihi.cims.web.controller.changerequest;

import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.DataInputStream;
import java.io.File;
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
import ca.cihi.cims.model.changerequest.ChangeRequestDTO;
import ca.cihi.cims.model.changerequest.ChangeRequestStatus;
import ca.cihi.cims.model.changerequest.DocumentReference;
import ca.cihi.cims.model.changerequest.DocumentReferenceType;
import ca.cihi.cims.service.ChangeRequestService;
import ca.cihi.cims.service.FileService;
import ca.cihi.cims.service.PublicationService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
public class ChangeRequestAttachmentControllerTest {

	ChangeRequestAttachmentController changeRequestAttachmentController;
	@Mock
	ChangeRequestService changeRequestService;
	@Mock
	FileService fileService;
	@Mock
	PublicationService publicationService;

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
	@Mock
	File file;
	@Mock
	DataInputStream inputStream;

	@Before
	public void initializeMocks() throws Exception {
		MockitoAnnotations.initMocks(this);
		changeRequestAttachmentController = new ChangeRequestAttachmentController();
		changeRequestAttachmentController.setChangeRequestService(changeRequestService);
		changeRequestAttachmentController.setFileService(fileService);
		changeRequestAttachmentController.setPublicationService(publicationService);
		when(session.getAttribute(WebConstants.CURRENT_USER)).thenReturn(mockUser());
		when(changeRequestService.findCourseGrainedChangeRequestDTOById(nullable(Long.class))).thenReturn(mockChangeRequest());
		when(fileService.getFile(nullable(String.class))).thenReturn(file);
		when(publicationService.findReleaseZipFileName(nullable(Long.class))).thenReturn("test.zip");
		when(publicationService.findSnapShotZipFileName(nullable(Long.class))).thenReturn("test.zip");

		// when(new DataInputStream(any(FileInputStream.class))).thenReturn(inputStream);
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

		changeRequest.setOtherAttachments(mockOtherAttachments());
		changeRequest.setUrcAttachments(mockURLAttachments());
		// changeRequest.setUrcLinks(mockUrcLinks());
		return changeRequest;
	}

	private List<DocumentReference> mockOtherAttachments() {
		List<DocumentReference> otherAttachments = new ArrayList<DocumentReference>();
		DocumentReference documentReference = new DocumentReference();
		documentReference.setChangeRequestId(1L);
		documentReference.setFileName("fileName");
		documentReference.setReferenceType(DocumentReferenceType.OTHER_FILE);
		otherAttachments.add(documentReference);
		return otherAttachments;
	}

	private List<DocumentReference> mockURLAttachments() {
		List<DocumentReference> otherAttachments = new ArrayList<DocumentReference>();
		DocumentReference documentReference = new DocumentReference();
		documentReference.setChangeRequestId(1L);
		documentReference.setFileName("fileName");
		documentReference.setReferenceType(DocumentReferenceType.URC_FILE);
		documentReference.setDocumentReferenceId(1L);
		otherAttachments.add(documentReference);
		return otherAttachments;
	}

	private User mockUser() {
		User currentUser = new User();
		currentUser.setUserId(1l);
		return currentUser;
	}

	// as there is no file , so we expect runtime exception
	@Test(expected = RuntimeException.class)
	public void testOpenChangeRequestFile() {
		Long changeRequestId = 1L;
		Long attachmentId = 1L;
		String attachmentType = "urc";
		HttpServletResponse test_response = response;
		HttpServletRequest test_request = request;
		changeRequestAttachmentController.openChangeRequestFile(changeRequestId, attachmentId, attachmentType,
				test_request, test_response);
		verify(changeRequestService, times(1)).findCourseGrainedChangeRequestDTOById(nullable(Long.class));

	}

	@Test(expected = RuntimeException.class)
	public void testOpenReleaseZipFile() {
		Long releaseId = 1L;
		HttpServletResponse test_response = response;
		HttpServletRequest test_request = request;
		changeRequestAttachmentController.openReleaseZipFile(releaseId, test_request, test_response);
		verify(publicationService, times(1)).findReleaseZipFileName(nullable(Long.class));
	}

	@Test(expected = RuntimeException.class)
	public void testOpenSnapShotZipFile() {
		Long snapShotId = 1L;
		HttpServletResponse test_response = response;
		HttpServletRequest test_request = request;
		changeRequestAttachmentController.openSnapShotZipFile(snapShotId, test_request, test_response);
		verify(publicationService, times(1)).findSnapShotZipFileName(nullable(Long.class));

	}
}
