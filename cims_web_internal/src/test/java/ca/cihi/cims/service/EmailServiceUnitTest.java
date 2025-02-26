package ca.cihi.cims.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ca.cihi.cims.CIMSTestConstants;
import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.model.SecurityRole;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.ReleaseType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/applicationContext-test.xml" })
@Rollback
@Transactional
public class EmailServiceUnitTest {

	EmailServiceImpl emailService;
	@Mock
	AdminMapper adminMapper;
	@Mock
	MimeMessage msg;
	@Mock
	JavaMailSenderImpl mailSender;
	@Autowired
	MessageSource ntfMessageSource;

	@Before
	public void initializeMocks() {
		MockitoAnnotations.initMocks(this);
		emailService = new EmailServiceImpl();
		emailService.setAdminMapper(adminMapper);
		emailService.setMailSender(mailSender);
		emailService.setBccAccount("bccAccount");
		emailService.setCihiEmailHost("cihiEmailHost");
		emailService.setFromAccount("fromAccount");
		emailService.setFromName("fromName");
		emailService.setReplyToAccount("replyToAccount");
		emailService.setReplyToName("replyToName");

		emailService.setMessageSource(ntfMessageSource);
		emailService.setSendEmail(true);
		doNothing().when(mailSender).send(any(MimeMessage.class));
		when(mailSender.createMimeMessage()).thenReturn(msg);

	}

	private User mockCurrentUser() {
		User currentUser = new User();
		currentUser.setUserId(0L);
		Set<SecurityRole> roles = new HashSet<SecurityRole>();
		SecurityRole role = SecurityRole.ROLE_ADMINISTRATOR;
		roles.add(role);
		currentUser.setRoles(roles);
		return currentUser;
	}

	private GenerateReleaseTablesCriteria mockGenerateReleaseTablesCriteria() {
		GenerateReleaseTablesCriteria generateReleaseTablesCriteria = new GenerateReleaseTablesCriteria();
		generateReleaseTablesCriteria.setCurrentOpenYear(Long.valueOf(CIMSTestConstants.TEST_VERSION));
		return generateReleaseTablesCriteria;
	}

	private PublicationRelease mockPublicationRelease() {
		PublicationRelease publicationRelease = new PublicationRelease();
		publicationRelease.setReleaseId(0L);
		publicationRelease.setFiscalYear(CIMSTestConstants.TEST_VERSION);
		publicationRelease.setReleaseType(ReleaseType.PRELIMINARY_INTERNAL_QA);
		publicationRelease.setCreatedDate(Calendar.getInstance().getTime());
		publicationRelease.setReleaseNote("releaseNote");
		return publicationRelease;
	}

	private List<User> mockUsers() {
		List<User> users = new ArrayList<User>();
		users.add(mockCurrentUser());
		return users;
	}

	@Test
	public void testEmailReleaseNotification() {
		PublicationRelease publicationRelease = mockPublicationRelease();
		when(adminMapper.getRecipientsByDistributionId(anyLong())).thenReturn(mockUsers());
		emailService.emailReleaseNotification(publicationRelease);
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}

	@Test
	public void testSendGenerateTableFailedEmail() {
		GenerateReleaseTablesCriteria generateTablesCritria = mockGenerateReleaseTablesCriteria();
		User currentUser = mockCurrentUser();
		Exception e = new Exception("error");
		emailService.sendGenerateTableFailedEmail(generateTablesCritria, currentUser, e);
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}

	@Test
	public void testSendGenerateTableSuccessEmail() {
		GenerateReleaseTablesCriteria generateTablesCritria = mockGenerateReleaseTablesCriteria();
		User currentUser = mockCurrentUser();
		emailService.sendGenerateTableSuccessEmail(generateTablesCritria, currentUser);
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}

	@Test
	public void testSendMissingReferenceValueEmail() {
		GenerateReleaseTablesCriteria generateTablesCriteria = mockGenerateReleaseTablesCriteria();
		User currentUser = mockCurrentUser();
		String key = "cims.notification.releasetable.missingreferencevalue";
		String referenceValue = "referenceValue";
		String code = "code";
		String dataHolding = "dataHolding";
		emailService.sendMissingReferenceValueEmail(generateTablesCriteria, currentUser, key, referenceValue, code,
				dataHolding);
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}

	@Test
	public void testSendReleaseTableFailedEmail() {
		GenerateReleaseTablesCriteria releaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		User currentUser = mockCurrentUser();
		Exception e = new Exception("error");
		emailService.sendReleaseTableFailedEmail(releaseTablesCriteria, currentUser, e);
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}

	@Test
	public void testsendReleaseTableNotificationEmail() {
		GenerateReleaseTablesCriteria releaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		User currentUser = mockCurrentUser();
		when(adminMapper.getRecipientsByDistributionId(anyLong())).thenReturn(mockUsers());
		releaseTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY_INTERNAL_QA);
		emailService.sendReleaseTableNotificationEmail(releaseTablesCriteria, currentUser);
		releaseTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY);
		emailService.sendReleaseTableNotificationEmail(releaseTablesCriteria, currentUser);
		releaseTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL_INTERNAL_QA);
		emailService.sendReleaseTableNotificationEmail(releaseTablesCriteria, currentUser);
		releaseTablesCriteria.setReleaseType(GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL);
		emailService.sendReleaseTableNotificationEmail(releaseTablesCriteria, currentUser);
		verify(mailSender, times(4)).send(any(MimeMessage.class));
	}

	@Test
	public void testSendReleaseTableSuccessEmail() {
		GenerateReleaseTablesCriteria releaseTablesCriteria = mockGenerateReleaseTablesCriteria();
		User currentUser = mockCurrentUser();
		emailService.sendReleaseTableSuccessEmail(releaseTablesCriteria, currentUser);
		verify(mailSender, times(1)).send(any(MimeMessage.class));
	}
}
