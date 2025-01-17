package ca.cihi.cims.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import ca.cihi.cims.data.mapper.AdminMapper;
import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;
import ca.cihi.cims.model.prodpub.ReleaseType;

public class EmailServiceImpl implements EmailService {
	public static final String CIMS_GENERATE_TABLE_FAILED_MESSAGE = "cims.notification.generatetable.failed.message";

	public static final String CIMS_GENERATE_TABLE_FAILED_SUBJECT = "cims.notification.generatetable.failed.subject";

	public static final String CIMS_GENERATE_TABLE_SUCCESS_MESSAGE = "cims.notification.generatetable.success.message";
	public static final String CIMS_GENERATE_TABLE_SUCCESS_SUBJECT = "cims.notification.generatetable.success.subject";

	public static final String CIMS_RELEASE_NOTIFICATION_SUBJECT = "cims.notification.release.subject";
	public static final String CIMS_RELEASE_TABLE_FAILED_MESSAGE = "cims.notification.releasetable.failed.message";

	public static final String CIMS_RELEASE_TABLE_FAILED_SUBJECT = "cims.notification.releasetable.failed.subject";
	public static final String CIMS_RELEASE_TABLE_MESSAGE_Official = "cims.notification.releasetable.officialPackage";

	public static final String CIMS_RELEASE_TABLE_MESSAGE_OfficialInternalQA = "cims.notification.releasetable.officialInternalQAPackage";
	public static final String CIMS_RELEASE_TABLE_MESSAGE_Preliminary = "cims.notification.releasetable.preliminaryPackage";
	public static final String CIMS_RELEASE_TABLE_MESSAGE_PreliminaryInternalQA = "cims.notification.releasetable.preliminaryInternalQAPackage";
	public static final String CIMS_RELEASE_TABLE_SUCCESS_MESSAGE = "cims.notification.releasetable.success.message";
	public static final String CIMS_RELEASE_TABLE_SUCCESS_SUBJECT = "cims.notification.releasetable.success.subject";
	private static final Log LOGGER = LogFactory.getLog(EmailServiceImpl.class);

	private AdminMapper adminMapper;

	private String bccAccount;
	private String cihiEmailHost;
	private String fromAccount;
	private String fromName;
	private JavaMailSender mailSender;
	private MessageSource messageSource;

	private String replyToAccount;
	private String replyToName;
	private boolean sendEmail;

	private String createEmailMsg(final String key, GenerateReleaseTablesCriteria generateTablesCritria) {
		String paras[] = new String[1];
		paras[0] = generateTablesCritria.getClassification();
		String result = messageSource.getMessage(key, paras, Locale.getDefault());
		return result;
	}

	private String createMissingReferenceValueEmailMsg(final String key, String referenceValue, String code,
			String dataHolding) {
		String paras[] = new String[3];
		paras[0] = referenceValue;
		paras[1] = code;
		paras[2] = dataHolding;
		String result = messageSource.getMessage(key, paras, Locale.getDefault());
		return result;
	}

	private String createReleaseEmailMsg(final String key, GenerateReleaseTablesCriteria releaseTablesCritria) {
		String paras[] = new String[1];
		paras[0] = releaseTablesCritria.getReleaseType();
		String result = messageSource.getMessage(key, paras, Locale.getDefault());
		return result;
	}

	private String createReleaseNotificationSubject(final String key, PublicationRelease publicationRelease) {
		String paras[] = new String[1];
		paras[0] = publicationRelease.getFiscalYear();
		String result = messageSource.getMessage(key, paras, Locale.getDefault());
		return result;
	}

	private String createReleaseSubject(final String key, GenerateReleaseTablesCriteria releaseTablesCritria) {
		String paras[] = new String[2];
		paras[0] = String.valueOf(releaseTablesCritria.getCurrentOpenYear());
		paras[1] = releaseTablesCritria.getReleaseType();
		String result = messageSource.getMessage(key, paras, Locale.getDefault());
		return result;
	}

	private String createSubject(final String key) {
		String result = messageSource.getMessage(key, null, Locale.getDefault());
		return result;
	}

	private String createSubject(final String key, GenerateReleaseTablesCriteria generateTablesCritria) {
		String paras[] = new String[2];
		paras[0] = String.valueOf(generateTablesCritria.getCurrentOpenYear());
		paras[1] = generateTablesCritria.getClassification();
		String result = messageSource.getMessage(key, paras, Locale.getDefault());
		return result;
	}

	@Override
	public void emailReleaseNotification(PublicationRelease publicationRelease) {

		String subject = createReleaseNotificationSubject(CIMS_RELEASE_NOTIFICATION_SUBJECT, publicationRelease);

		ReleaseType releaseType = publicationRelease.getReleaseType();
		List<User> toUsers = adminMapper.getRecipientsByDistributionId(releaseType.getEmailDLId());

		String[] toAccounts = new String[toUsers.size()];
		String[] toNames = new String[toUsers.size()];
		int i = 0;
		for (User toUser : toUsers) {
			toAccounts[i] = toUser.getEmail();
			toNames[i] = toUser.getUsername();
			i++;
		}

		if (sendEmail) {
			notify(subject, toAccounts, toNames, publicationRelease.getReleaseNote(), true);
		}
	}

	public AdminMapper getAdminMapper() {
		return adminMapper;
	}

	public String getBccAccount() {
		return bccAccount;
	}

	public String getCihiEmailHost() {
		return cihiEmailHost;
	}

	public String getFromAccount() {
		return fromAccount;
	}

	public String getFromName() {
		return fromName;
	}

	public JavaMailSender getMailSender() {
		return mailSender;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public String getReplyToAccount() {
		return replyToAccount;
	}

	public String getReplyToName() {
		return replyToName;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	private void notify(final String subject, final String[] accounts, final String[] names, final String emailTxt,
			boolean isHtml) {

		MimeMessage msg = mailSender.createMimeMessage();

		LOGGER.debug("cihiMailHost:" + this.getCihiEmailHost());
		((JavaMailSenderImpl) mailSender).setHost(cihiEmailHost);

		try {
			MimeMessageHelper helper = new MimeMessageHelper(msg, true);

			helper.setSubject(subject);
			helper.setFrom(getFromAccount(), getFromName());
			helper.setBcc(getBccAccount());
			helper.setReplyTo(getReplyToAccount(), getReplyToName());

			helper.setText(emailTxt, isHtml);

			for (int i = 0; i < accounts.length; i++) {
				helper.addTo(new InternetAddress(accounts[i], names[i]));
			}

			mailSender.send(msg);

			LOGGER.info(new StringBuilder("Successfully sent notification to ").append(Arrays.toString(accounts))
					.append(" regarding ").append(subject).toString());
		} catch (Exception e) {
			LOGGER.error(new StringBuilder("Failed to send email notification."), e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void sendGenerateTableFailedEmail(GenerateReleaseTablesCriteria generateTablesCritria, User currentUser,
			Exception e) {
		String subject = createSubject(CIMS_GENERATE_TABLE_FAILED_SUBJECT, generateTablesCritria);
		String[] toAccounts = new String[1];
		String[] toNames = new String[1];
		toAccounts[0] = currentUser.getEmail();
		toNames[0] = currentUser.getUsername();
		String emailTxt = createEmailMsg(CIMS_GENERATE_TABLE_FAILED_MESSAGE, generateTablesCritria);
		if (sendEmail) {
			notify(subject, toAccounts, toNames, emailTxt, false);
		}
	}

	@Override
	// The <classification: ICD-10-CA, CCI, ICD-10-CA & CCI> classification tables package has been successfully
	// generated.
	public void sendGenerateTableSuccessEmail(GenerateReleaseTablesCriteria generateTablesCritria, User currentUser) {
		String subject = createSubject(CIMS_GENERATE_TABLE_SUCCESS_SUBJECT, generateTablesCritria);
		String[] toAccounts = new String[1];
		String[] toNames = new String[1];
		toAccounts[0] = currentUser.getEmail();
		toNames[0] = currentUser.getUsername();
		String emailTxt = createEmailMsg(CIMS_GENERATE_TABLE_SUCCESS_MESSAGE, generateTablesCritria);
		if (sendEmail) {
			notify(subject, toAccounts, toNames, emailTxt.toString(), false);
		}
	}

	@Override
	public void sendMissingReferenceValueEmail(GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser,
			String key, String referenceValue, String code, String dataHolding) {
		String subject = createSubject(CIMS_GENERATE_TABLE_FAILED_SUBJECT, generateTablesCriteria);
		String[] toAccounts = new String[1];
		String[] toNames = new String[1];
		toAccounts[0] = currentUser.getEmail();
		toNames[0] = currentUser.getUsername();
		String emailTxt = createMissingReferenceValueEmailMsg(key, referenceValue, code, dataHolding);
		if (sendEmail) {
			notify(subject, toAccounts, toNames, emailTxt, false);
		}
	}

	@Override
	public void sendReleaseTableFailedEmail(GenerateReleaseTablesCriteria releaseTablesCritria, User currentUser,
			Exception e) {
		String subject = createReleaseSubject(CIMS_RELEASE_TABLE_FAILED_SUBJECT, releaseTablesCritria);
		String[] toAccounts = new String[1];
		String[] toNames = new String[1];
		toAccounts[0] = currentUser.getEmail();
		toNames[0] = currentUser.getUsername();
		String emailTxt = createReleaseEmailMsg(CIMS_RELEASE_TABLE_FAILED_MESSAGE, releaseTablesCritria);
		if (sendEmail) {
			notify(subject, toAccounts, toNames, emailTxt.toString(), false);
		}
	}

	/*
	 * 1. For the ""preliminary internal QA"" or ""official - internal QA"" release, the notification should be sent to
	 * DL-Internal Release, DL-English Content Developer and DL-French Content Developer (for resuming work). See
	 * EP-RU194 for message to content developers. 2. For the ""preliminary"" release, the notification should be sent
	 * to DL-Preliminary Release, DL-English Content Developer and DL-French Content Developer (for resuming work). See
	 * EP-RU194 for message to content developers. 3. For the ""official"" release, the notification should be sent to
	 * DL-Official Release and DL-Administrator (for year closure). (non-Javadoc)
	 *
	 * @see ca.cihi.cims.service.EmailService#sendReleaseTableNotificationEmail(ca.cihi.cims.model.prodpub.
	 * GenerateReleaseTablesCriteria, ca.cihi.cims.model.User)
	 */
	@Override
	public void sendReleaseTableNotificationEmail(GenerateReleaseTablesCriteria releaseTablesModel, User currentUser) {

		String subject = null;
		String message = null;

		List<User> toUsers = null;
		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY_INTERNAL_QA
				.equals(releaseTablesModel.getReleaseType())) {
			toUsers = adminMapper.getRecipientsByDistributionId(Distribution.DL_ID_InternalRelease);
			subject = createSubject(CIMS_RELEASE_TABLE_MESSAGE_PreliminaryInternalQA);
			message = subject;

		}
		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL_INTERNAL_QA
				.equals(releaseTablesModel.getReleaseType())) {
			toUsers = adminMapper.getRecipientsByDistributionId(Distribution.DL_ID_InternalRelease);
			subject = createSubject(CIMS_RELEASE_TABLE_MESSAGE_OfficialInternalQA);
			message = subject;
		}

		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_PRELIMINARY.equals(releaseTablesModel.getReleaseType())) {
			toUsers = adminMapper.getRecipientsByDistributionId(Distribution.DL_ID_PreliminaryRelease);
			subject = createSubject(CIMS_RELEASE_TABLE_MESSAGE_Preliminary);
			message = subject;

		}
		if (GenerateReleaseTablesCriteria.RELEASE_TYPE_OFFICIAL.equals(releaseTablesModel.getReleaseType())) {
			toUsers = adminMapper.getRecipientsByDistributionId(Distribution.DL_ID_OfficialRelease);
			subject = createSubject(CIMS_RELEASE_TABLE_MESSAGE_Official);
			message = subject;
		}
		if ((toUsers != null) && (toUsers.size() > 0)) {
			String[] toAccounts = new String[toUsers.size()];
			String[] toNames = new String[toUsers.size()];
			int i = 0;
			for (User toUser : toUsers) {
				toAccounts[i] = toUser.getEmail();
				toNames[i] = toUser.getUsername();
				i++;
			}
			if (sendEmail) {
				notify(subject, toAccounts, toNames, message, false);
			}

		}

	}

	@Override
	public void sendReleaseTableSuccessEmail(GenerateReleaseTablesCriteria releaseTablesCritria, User currentUser) {
		String subject = createReleaseSubject(CIMS_RELEASE_TABLE_SUCCESS_SUBJECT, releaseTablesCritria);
		String[] toAccounts = new String[1];
		String[] toNames = new String[1];
		toAccounts[0] = currentUser.getEmail();
		toNames[0] = currentUser.getUsername();
		String emailTxt = createReleaseEmailMsg(CIMS_RELEASE_TABLE_SUCCESS_MESSAGE, releaseTablesCritria);
		if (sendEmail) {
			notify(subject, toAccounts, toNames, emailTxt, false);
		}
	}

	public void setAdminMapper(AdminMapper adminMapper) {
		this.adminMapper = adminMapper;
	}

	public void setBccAccount(String bccAccount) {
		this.bccAccount = bccAccount;
	}

	public void setCihiEmailHost(String cihiEmailHost) {
		this.cihiEmailHost = cihiEmailHost;
	}

	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setReplyToAccount(String replyToAccount) {
		this.replyToAccount = replyToAccount;
	}

	public void setReplyToName(String replyToName) {
		this.replyToName = replyToName;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

}
