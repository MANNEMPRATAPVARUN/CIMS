package ca.cihi.cims.web.customtag;

import java.util.List;

import ca.cihi.cims.model.User;
import ca.cihi.cims.model.resourceaccess.AccessCode;
import ca.cihi.cims.model.resourceaccess.ResourceAccess;
import ca.cihi.cims.model.resourceaccess.ResourceCode;

public final class Functions {
	private Functions() {}

	public static boolean hasReadAccess(User currentUser, String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasReadAccessToResource(currentUser.getResourceAccesses(), resourceCodeEnum);
	}


	public static boolean hasWriteAccess(User currentUser, String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasWriteAccessToResource(currentUser.getResourceAccesses(), resourceCodeEnum);
		//return true;
	}

	public static boolean hasExecuteAccess(User currentUser, String resourceCode) {
		ResourceCode resourceCodeEnum = ResourceCode.fromString(resourceCode);
		return hasExecuteAccessToResource(currentUser.getResourceAccesses(), resourceCodeEnum);
	}






	private static boolean hasReadAccessToResource(List<ResourceAccess> resourceAccesses ,ResourceCode resourceCode) {
		boolean hasReadAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if (resourceAccess.getResourceCode() == resourceCode && resourceAccess.getAccessCode() == AccessCode.READ) {
				hasReadAccess = true;
				break;
			}
		}
		return hasReadAccess;
	}


	private static boolean hasWriteAccessToResource(List<ResourceAccess> resourceAccesses ,ResourceCode resourceCode) {
		boolean hasWriteAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if (resourceAccess.getResourceCode() == resourceCode && resourceAccess.getAccessCode() == AccessCode.WRITE) {
				hasWriteAccess = true;
				break;
			}
		}

		return hasWriteAccess;
	}

	private static boolean hasExecuteAccessToResource(List<ResourceAccess> resourceAccesses ,ResourceCode resourceCode) {
		boolean hasExecuteAccess = false;
		for (ResourceAccess resourceAccess : resourceAccesses) {
			if (resourceAccess.getResourceCode() == resourceCode
					&& resourceAccess.getAccessCode() == AccessCode.EXECUTE) {
				hasExecuteAccess = true;
				break;
			}
		}

		return hasExecuteAccess;
	}


}
