package ca.cihi.cims.web.bean;

import java.util.List;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import ca.cihi.cims.model.User;

/**
 * @author szhang
 */
public class RecipientListViewBean extends BaseSerializableCloneableObject {
	private static final long serialVersionUID = 2287702285708569598L;

	private String actionType;
	private String distribution_id;
	private String distribution_name;
	private String distribution_code;
	private String distribution_description;

	private List<User> users;

	private List<User> recipients;

	public String getActionType() {
		return actionType;
	}

	public String getDistribution_code() {
		return distribution_code;
	}

	public String getDistribution_description() {
		return distribution_description;
	}

	public String getDistribution_id() {
		return distribution_id;
	}

	public String getDistribution_name() {
		return distribution_name;
	}

	public List<User> getRecipients() {
		return recipients;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setDistribution_code(String distribution_code) {
		this.distribution_code = distribution_code;
	}

	public void setDistribution_description(String distribution_description) {
		this.distribution_description = distribution_description;
	}

	public void setDistribution_id(String distribution_id) {
		this.distribution_id = distribution_id;
	}

	public void setDistribution_name(String distribution_name) {
		this.distribution_name = distribution_name;
	}

	public void setRecipients(List<User> recipients) {
		this.recipients = recipients;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

}
