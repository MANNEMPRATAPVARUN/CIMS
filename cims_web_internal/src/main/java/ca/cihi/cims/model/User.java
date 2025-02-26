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
 * @author szhang
 */
@Entity
public class User implements Serializable {
	private static final long serialVersionUID = -4808362978980767028L;
	public static final Long USER_ID_SYSTEM = 0L;

	private Long userId;
	private Long distributionId;
	private String type;
	// @Column(unique = true)
	private String username;
	private String firstname;
	private String lastname;
	private String department;
	private String email;
	private String status;
	private String languagepreference;
	private String title;
	private Set<SecurityRole> roles;
	private List<ResourceAccess> resourceAccesses;
	private List<Distribution> inGroups; // the user can be in multiple groups
	private Date createdDate;

	// --------------------------------------------------

	public Date getCreatedDate() {
		return createdDate;
	}

	public String getDepartment() {
		return department;
	}

	public Long getDistributionId() {
		return distributionId;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstname() {
		return firstname;
	}

	public List<Distribution> getInGroups() {
		return inGroups;
	}

	public String getLanguagepreference() {
		return languagepreference;
	}

	public String getLastname() {
		return lastname;
	}

	public List<ResourceAccess> getResourceAccesses() {
		return resourceAccesses;
	}

	public Set<SecurityRole> getRoles() {
		return roles;
	}

	public String getStatus() {
		return status;
	}

	public String getTitle() {
		return title;
	}

	public String getType() {
		return type;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public boolean hasExecuteAccessToResource(ResourceCode resourceCode) {
		boolean hasExecuteAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if ((resourceAccess.getResourceCode() == resourceCode)
					&& (resourceAccess.getAccessCode() == AccessCode.EXECUTE)) {
				hasExecuteAccess = true;
				break;
			}
		}

		return hasExecuteAccess;
	}

	public boolean hasExecuteAccessToResource(String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasExecuteAccessToResource(resourceCodeEnum);
	}

	public boolean hasReadAccessToResource(ResourceCode resourceCode) {
		boolean hasReadAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if ((resourceAccess.getResourceCode() == resourceCode)
					&& (resourceAccess.getAccessCode() == AccessCode.READ)) {
				hasReadAccess = true;
				break;
			}
		}

		return hasReadAccess;
	}

	public boolean hasReadAccessToResource(String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasReadAccessToResource(resourceCodeEnum);
	}

	public boolean hasWriteAccessToResource(ResourceCode resourceCode) {
		boolean hasWriteAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if ((resourceAccess.getResourceCode() == resourceCode)
					&& (resourceAccess.getAccessCode() == AccessCode.WRITE)) {
				hasWriteAccess = true;
				break;
			}
		}

		return hasWriteAccess;
	}

	public boolean hasWriteAccessToResource(String resourceCode) {
		// ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		// return hasWriteAccessToResource(resourceCodeEnum);
		return true;
	}

	// ROLE_ADMINISTRATOR
	public boolean isAdministrator() {
		return roles.contains(SecurityRole.ROLE_ADMINISTRATOR);
	}

	// INITIATOR
	public boolean isInitiator() {
		return roles.contains(SecurityRole.ROLE_INITIATOR);
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setDistributionId(Long distributionId) {
		this.distributionId = distributionId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setInGroups(List<Distribution> inGroups) {
		this.inGroups = inGroups;
	}

	public void setLanguagepreference(String languagepreference) {
		this.languagepreference = languagepreference;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setResourceAccesses(List<ResourceAccess> resourceAccesses) {
		this.resourceAccesses = resourceAccesses;
	}

	public void setRoles(Set<SecurityRole> roles) {
		this.roles = roles;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}
