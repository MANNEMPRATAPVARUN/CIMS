package ca.cihi.cims.web.bean;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import ca.cihi.blueprint.core.domain.BaseSerializableCloneableObject;
import ca.cihi.cims.model.User;
import ca.cihi.cims.validator.Status;

/**
 * @author szhang
 */
public class UserViewBean extends BaseSerializableCloneableObject {
	private static final long serialVersionUID = 2287702285708569399L;
	private static final String ENG = "ENG";

	private List<User> users;
	private String actionType;
	private String user_id;

	@Size(min = 1, max = 100, message = "User Name must not be empty and unique.")
	private String username;
	private String userType;
	@NotNull()
	@Size(min = 1, max = 100, message = "First Name must not be empty")
	private String firstname;
	@NotNull()
	@Size(min = 1, max = 100, message = "Last Name must not be empty")
	private String lastname;
	private String department;
	@NotNull
	@Pattern(regexp = ".+@.+\\.[a-z]+", message = "Invalid email address.")
	private String email;
	@Status
	private String status;
	private String languagepreference;
	private String title;

	public void fromUser(final User user) {
		this.user_id = user.getUserId().toString();
		this.userType = user.getType();
		this.username = user.getUsername().toLowerCase();
		this.firstname = user.getFirstname();
		this.lastname = user.getLastname();
		this.email = user.getEmail();
		this.status = user.getStatus();
		this.languagepreference = user.getLanguagepreference();
		this.department = user.getDepartment();
		this.title = user.getTitle();
	}

	public User toUser() {
		User user = new User();
		user.setUserId(Long.valueOf(this.user_id));
		user.setType(this.userType);
		if (username == null || username.isEmpty()) {
			username = firstname.substring(0) + lastname;
		}
		user.setUsername(username.toLowerCase());
		user.setFirstname(this.firstname);
		user.setLastname(this.lastname);
		user.setEmail(this.email);
		user.setStatus(this.status);
		user.setDepartment(department);
		if (languagepreference == null || languagepreference.isEmpty()) {
			languagepreference = "ENG";
		}
		user.setLanguagepreference(languagepreference);
		user.setTitle(title);
		user.setCreatedDate(new Date());
		return user;
	}

	public void reset() {
		this.users = null;
		this.user_id = null;
		this.username = null;
		this.userType = null;
		this.firstname = null;
		this.lastname = null;
		this.department = null;
		this.email = null;
		this.status = null;
		this.languagepreference = null;
		this.title = null;

	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	@Valid
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getLanguagepreference() {
		return languagepreference;
	}

	public void setLanguagepreference(String languagepreference) {
		this.languagepreference = languagepreference;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
