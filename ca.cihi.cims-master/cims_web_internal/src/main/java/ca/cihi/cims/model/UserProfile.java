package ca.cihi.cims.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;

import ca.cihi.cims.model.resourceaccess.AccessCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceCode;

/**
 * @author
 */
@Entity
public class UserProfile implements Serializable {
	private static final long serialVersionUID = 201502041626L;
	//public static final Long USER_ID_SYSTEM = 0L;

	private Long userProfileId;
	private String userName;
	private String title;
	private String firstName;
	private String lastName;
	private String department;
	private String email;
	private String userTypeCode;
	private String preferredLanguageCode;
	private String userStatusCode;
	private Date lastUpdateDate;


	public Long getUserProfileId() {
		return userProfileId;
	}
	public void setUserProfileId(Long userProfileId) {
		this.userProfileId = userProfileId;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getDepartment() {
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserTypeCode() {
		return userTypeCode;
	}
	public void setUserTypeCode(String userTypeCode) {
		this.userTypeCode = userTypeCode;
	}

	public String getPreferredLanguageCode() {
		return preferredLanguageCode;
	}
	public void setPreferredLanguageCode(String preferredLanguageCode) {
		this.preferredLanguageCode = preferredLanguageCode;
	}

	public String getUserStatusCode() {
		return userStatusCode;
	}
	public void setUserStatusCode(String userStatusCode) {
		this.userStatusCode = userStatusCode;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}
