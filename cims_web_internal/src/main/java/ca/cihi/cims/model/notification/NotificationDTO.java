package ca.cihi.cims.model.notification;

import java.util.List;

import ca.cihi.cims.model.Distribution;
import ca.cihi.cims.model.User;
import ca.cihi.cims.model.changerequest.ChangeRequest;


public class NotificationDTO extends Notification {



	private User sender;

	//private boolean notificationToUser ;
	private User recipient;    // notification to a user

	private List<Distribution> dlRecipients;   // Distribution List recipients

	private ChangeRequest changeRequest;

	private List<Long> notificationIds;  // used for select multiple notifications for delete on the my notification list screen



	public User getRecipient() {
		return recipient;
	}

	public void setRecipient(User recipient) {
		this.recipient = recipient;
	}

	public List<Distribution> getDlRecipients() {
		return dlRecipients;
	}

	public void setDlRecipients(List<Distribution> dlRecipients) {
		this.dlRecipients = dlRecipients;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public List<Long> getNotificationIds() {
		return notificationIds;
	}

	public void setNotificationIds(List<Long> notificationIds) {
		this.notificationIds = notificationIds;
	}

	public ChangeRequest getChangeRequest() {
		return changeRequest;
	}

	public void setChangeRequest(ChangeRequest changeRequest) {
		this.changeRequest = changeRequest;
	}




}
