package ca.cihi.cims.model.resourceaccess;

import java.io.Serializable;

public class ResourceAccess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ResourceCode resourceCode;
	private AccessCode accessCode;

	public ResourceCode getResourceCode() {
		return resourceCode;
	}

	public void setResourceCode(ResourceCode resourceCode) {
		this.resourceCode = resourceCode;
	}

	public AccessCode getAccessCode() {
		return accessCode;
	}

	public void setAccessCode(AccessCode accessCode) {
		this.accessCode = accessCode;
	}

}
