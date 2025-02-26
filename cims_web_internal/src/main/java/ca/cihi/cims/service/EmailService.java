package ca.cihi.cims.service;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.prodpub.GenerateReleaseTablesCriteria;
import ca.cihi.cims.model.prodpub.PublicationRelease;

public interface EmailService {
	/*
	 * get called when use click the email link on the release history page
	 */
	void emailReleaseNotification(PublicationRelease publicationRelease);

	void sendGenerateTableFailedEmail(GenerateReleaseTablesCriteria generateTablesCritria, User currentUser, Exception e);

	void sendGenerateTableSuccessEmail(GenerateReleaseTablesCriteria GenerateReleaseTablesCriteria, User currentUser);

	void sendMissingReferenceValueEmail(GenerateReleaseTablesCriteria generateTablesCriteria, User currentUser,
			String key, String referenceValue, String code, String dataHolding);

	void sendReleaseTableFailedEmail(GenerateReleaseTablesCriteria releaseTablesCritria, User currentUser, Exception e);

	/*
	 * The notification sent to DL-Internal Release, DL-Preliminary Release and DL-Official Release are via email while
	 * the notification sent to DL-English Content Developer, DL-French Content Developer and DL-Administrator are
	 * posted on CIMS home page. get called when release finishes
	 */
	void sendReleaseTableNotificationEmail(GenerateReleaseTablesCriteria releaseTablesModel, User currentUser);

	void sendReleaseTableSuccessEmail(GenerateReleaseTablesCriteria releaseTablesCritria, User currentUser);

}
